"""Gemini Live + Pipecat 파이프라인으로 음성 세션 한 건 실행."""

import asyncio

from fastapi import WebSocket, WebSocketDisconnect
from loguru import logger
from pipecat.frames.frames import StartFrame
from pipecat.pipeline.pipeline import Pipeline
from pipecat.pipeline.runner import PipelineRunner
from pipecat.pipeline.task import PipelineParams, PipelineTask
from pipecat.services.google.gemini_live.llm import (
    ContextWindowCompressionParams,
    GeminiLiveLLMService,
    GeminiVADParams,
    InputParams,
)
from pipecat.transcriptions.language import Language
from pipecat.transports.websocket.fastapi import (
    FastAPIWebsocketParams,
    FastAPIWebsocketTransport,
)

from voice_app.config import (
    GEMINI_VOICE_ID,
    GEMINI_VOICE_MODEL,
    GOOGLE_API_KEY,
    SPRING_BASE_URL,
    SPRING_CHAT_TIMEOUT_SEC,
    SYSTEM_INSTRUCTION,
    USER_TURN_SETTLE_DELAY_SEC,
)
from voice_app.serializers.raw_pcm import RawPCMWebSocketSerializer
from voice_app.spring_chat import SpringChatSync
from voice_app.voice.observers import VoiceSessionObserver


def _spring_sync_from_websocket(websocket: WebSocket) -> SpringChatSync | None:
    """
    WebSocket 쿼리(연결 시 1회): token 또는 access_token, chatRoomId, characterType.
    SPRING_BASE_URL 환경 변수가 있고 토큰이 있을 때만 동기화 활성화.
    """
    qp = websocket.query_params
    qkeys = list(qp.keys())
    token = (qp.get("token") or qp.get("access_token") or "").strip()
    base_ok = bool(SPRING_BASE_URL)
    token_ok = bool(token)
    logger.info(
        "[spring] 연결 진단: SPRING_BASE_URL_설정={} ws_쿼리에_토큰={} 쿼리키={}",
        base_ok,
        token_ok,
        qkeys,
    )

    if not SPRING_BASE_URL:
        return None
    if not token:
        logger.warning(
            "[spring] WS에 token/access_token 없음 — 동기화 끔. "
            "데모는 http://호스트:포트/static/demo.html 에서 토큰 입력 후 연결하거나 "
            "ws://호스트/ws?token=JWT&characterType=EMPATHY 형태여야 함."
        )
        return None

    raw_room = (qp.get("chatRoomId") or "").strip()
    chat_room_id: int | None = None
    if raw_room:
        try:
            chat_room_id = int(raw_room)
        except ValueError:
            logger.warning("[spring] invalid chatRoomId query: {!r}", raw_room)

    ct = (qp.get("characterType") or "EMPATHY").strip().upper()
    if ct not in ("EMPATHY", "SOLUTION"):
        ct = "EMPATHY"

    return SpringChatSync(
        base_url=SPRING_BASE_URL,
        bearer_token=token,
        character_type=ct,
        initial_chat_room_id=chat_room_id,
        timeout_sec=SPRING_CHAT_TIMEOUT_SEC,
    )


async def run_voice_session(websocket: WebSocket) -> None:
    """WebSocket 연결 수락 후 Pipecat 파이프라인을 실행한다. 호출 전에 `accept()`는 이미 된 상태여야 한다."""
    if not GOOGLE_API_KEY:
        await websocket.close(code=1011, reason="GOOGLE_API_KEY not configured")
        return

    spring_sync = _spring_sync_from_websocket(websocket)
    if spring_sync is not None:
        logger.info("[spring] 이 WebSocket 세션: 메인 서버 전사 동기화 사용 중")
    elif not SPRING_BASE_URL:
        logger.warning(
            "[spring] 이 세션: 동기화 비활성 — poom-socket/.env 에 SPRING_BASE_URL 이 없거나 비어 있음"
        )
    else:
        logger.warning(
            "[spring] 이 세션: 동기화 비활성 — 연결 URL에 token 또는 access_token 쿼리가 없음 "
            "(예: ws://호스트/ws?token=JWT&characterType=EMPATHY). 음성만 동작하고 DB에는 안 쌓임."
        )

    try:
        params = FastAPIWebsocketParams(
            serializer=RawPCMWebSocketSerializer(),
            add_wav_header=False,
            session_timeout=None,
            audio_in_enabled=True,
            audio_out_enabled=True,
        )
        transport = FastAPIWebsocketTransport(websocket, params=params)
        llm = GeminiLiveLLMService(
            api_key=GOOGLE_API_KEY,
            model=GEMINI_VOICE_MODEL,
            voice_id=GEMINI_VOICE_ID,
            system_instruction=SYSTEM_INSTRUCTION,
            params=InputParams(
                temperature=0.7,
                max_tokens=2048,
                language=Language.KO_KR,
                vad=GeminiVADParams(silence_duration_ms=500),
                context_window_compression=ContextWindowCompressionParams(enabled=True),
            ),
        )

        pipeline = Pipeline([
            transport.input(),
            llm,
            transport.output(),
        ])

        pipeline_params = PipelineParams(
            audio_in_sample_rate=16000,
            audio_out_sample_rate=24000,
        )
        task = PipelineTask(
            pipeline,
            params=pipeline_params,
            enable_rtvi=False,
            observers=[
                VoiceSessionObserver(
                    websocket,
                    turn_settle_delay_sec=USER_TURN_SETTLE_DELAY_SEC,
                    spring_sync=spring_sync,
                ),
            ],
        )
        runner = PipelineRunner(handle_sigint=False)
        await task.queue_frame(StartFrame())
        await runner.run(task)
    except WebSocketDisconnect:
        logger.info("Client disconnected")
    except asyncio.CancelledError:
        logger.debug("Voice session cancelled")
    except Exception as e:
        logger.exception("Voice session error: %s", e)
        try:
            await websocket.close(code=1011, reason=str(e)[:123])
        except Exception:
            pass
