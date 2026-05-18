"""파이프라인 프레임 관찰: 사용자 턴 최종 전사·AI 응답을 WebSocket JSON 및 로그로 전달."""

from __future__ import annotations

from fastapi import WebSocket
from loguru import logger
from pipecat.frames.frames import (
    BotStartedSpeakingFrame,
    Frame,
    LLMFullResponseEndFrame,
    LLMTextFrame,
    TranscriptionFrame,
    UserStartedSpeakingFrame,
    UserStoppedSpeakingFrame,
)
from pipecat.observers.base_observer import BaseObserver, FrameProcessed, FramePushed

try:
    from pipecat.frames.frames import InterimTranscriptionFrame as _InterimTranscriptionFrame
except ImportError:  # pragma: no cover
    _InterimTranscriptionFrame = None

from voice_app.spring_chat import SpringChatSync
from voice_app.voice.text_utils import append_ai_chunk_with_overlap_dedup, dedupe_ai_text_full
from voice_app.voice.user_turn_coordinator import UserTurnCoordinator


class VoiceSessionObserver(BaseObserver):
    """사용자: 문장 단위 전사는 UI용(partial), VAD 턴 종료 후 확정(final) — Spring POST 등에 사용.

    AI: LLMFullResponseEndFrame에서 한 번에 전송하며 ai_text_final=True.
    """

    def __init__(
        self,
        websocket: WebSocket,
        *,
        turn_settle_delay_sec: float = 0.35,
        spring_sync: SpringChatSync | None = None,
        **kwargs,
    ):
        super().__init__(**kwargs)
        self._websocket = websocket
        self._spring_sync = spring_sync
        self._user_turn = UserTurnCoordinator(delay_sec=turn_settle_delay_sec)
        self._ai_text_buffer: list[str] = []
        # 동일 프레임이 파이프라인에서 여러 프로세서를 거치며 observer가 중복 호출됨 → id로 1회만 처리
        self._handled_frame_ids: set[int] = set()

    def _consume_frame_once(self, frame: Frame) -> bool:
        fid = getattr(frame, "id", None)
        if fid is None:
            return True
        if fid in self._handled_frame_ids:
            return False
        self._handled_frame_ids.add(fid)
        if len(self._handled_frame_ids) > 8000:
            self._handled_frame_ids.clear()
        return True

    async def _send_payload(
        self,
        *,
        user_text: str | None,
        ai_text: str | None,
        user_text_final: bool,
        ai_text_final: bool,
    ) -> None:
        try:
            await self._websocket.send_json({
                "user_text": user_text,
                "ai_text": ai_text,
                "user_text_final": user_text_final,
                "ai_text_final": ai_text_final,
            })
        except Exception as e:
            logger.warning("VoiceSessionObserver send_json failed (클라이언트 전사 JSON 미전달): {}", e)

    async def _on_user_turn_final(self, text: str) -> None:
        logger.info("[voice] user_text_final: {}", text)
        await self._send_payload(
            user_text=text,
            ai_text=None,
            user_text_final=True,
            ai_text_final=False,
        )
        if self._spring_sync is not None:
            await self._spring_sync.persist_final_user_text(text)
        else:
            logger.info(
                "[spring] USER 전사는 확정됐으나 동기화 비활성 — 메인 미저장 "
                "(SPRING_BASE_URL·WS ?token= 확인)"
            )

    async def on_process_frame(self, data: FrameProcessed):
        frame = data.frame

        if isinstance(frame, BotStartedSpeakingFrame):
            if not self._consume_frame_once(frame):
                return
            await self._user_turn.flush_pending_now(self._on_user_turn_final)
            return

        if isinstance(frame, UserStartedSpeakingFrame):
            if not self._consume_frame_once(frame):
                return
            await self._user_turn.on_user_started(self._on_user_turn_final)
            return

        if isinstance(frame, UserStoppedSpeakingFrame):
            if not self._consume_frame_once(frame):
                return
            await self._user_turn.on_user_stopped(self._on_user_turn_final)
            return

        if _InterimTranscriptionFrame is not None and isinstance(frame, _InterimTranscriptionFrame):
            if not self._consume_frame_once(frame):
                return
            t = (getattr(frame, "text", None) or "").strip()
            if t:
                logger.trace("[voice] user_text_interim: {}", t)
                await self._send_payload(
                    user_text=t,
                    ai_text=None,
                    user_text_final=False,
                    ai_text_final=False,
                )
            return

        if isinstance(frame, TranscriptionFrame) and getattr(frame, "text", None):
            if not self._consume_frame_once(frame):
                return
            cumulative = self._user_turn.append_sentence(frame.text)
            if cumulative:
                logger.debug("[voice] user_text_partial: {}", cumulative)
                await self._send_payload(
                    user_text=cumulative,
                    ai_text=None,
                    user_text_final=False,
                    ai_text_final=False,
                )
            return

        if isinstance(frame, LLMTextFrame) and getattr(frame, "text", None):
            if not self._consume_frame_once(frame):
                return
            append_ai_chunk_with_overlap_dedup(self._ai_text_buffer, frame.text or "")
            return

        if isinstance(frame, LLMFullResponseEndFrame):
            if not self._consume_frame_once(frame):
                return
            if self._ai_text_buffer:
                full = dedupe_ai_text_full("".join(self._ai_text_buffer))
                if full:
                    logger.info("[voice] ai_text_final: {}", full)
                    await self._send_payload(
                        user_text=None,
                        ai_text=full,
                        user_text_final=False,
                        ai_text_final=True,
                    )
                    if self._spring_sync is not None:
                        await self._spring_sync.persist_final_assistant_text(full)
                    else:
                        logger.info(
                            "[spring] AI 전사는 확정됐으나 동기화 비활성 — 메인 미저장 "
                            "(SPRING_BASE_URL·WS ?token= 확인)"
                        )
            self._ai_text_buffer = []

    async def on_push_frame(self, data: FramePushed):
        pass
