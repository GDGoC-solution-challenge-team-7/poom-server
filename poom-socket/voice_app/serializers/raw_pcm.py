# Raw PCM WebSocket serializer for mobile/client audio (no telephony protocol).
# Binary messages = PCM mono 16-bit; sample_rate from pipeline StartFrame.

import asyncio
from typing import Optional

from loguru import logger
from pipecat.frames.frames import (
    Frame,
    InputAudioRawFrame,
    OutputAudioRawFrame,
    StartFrame,
)
from pipecat.serializers.base_serializer import FrameSerializer


class RawPCMWebSocketSerializer(FrameSerializer):
    """Treats binary WebSocket messages as raw PCM audio (16kHz mono 16-bit by default)."""

    def __init__(self, params: Optional[FrameSerializer.InputParams] = None, **kwargs):
        super().__init__(params=params, **kwargs)
        self._sample_rate = 16000 # 샘플레이트 16000Hz: 1초에 16000개의 샘플을 측정
        self._num_channels = 1 # 채널 1: 모노(단일 채널)
        self._received_first_binary_audio = False
        self._no_audio_watchdog_task: asyncio.Task | None = None

    async def _cancel_no_audio_watchdog(self) -> None:
        if self._no_audio_watchdog_task and not self._no_audio_watchdog_task.done():
            self._no_audio_watchdog_task.cancel()
            try:
                await self._no_audio_watchdog_task
            except asyncio.CancelledError:
                pass
        self._no_audio_watchdog_task = None

    async def setup(self, frame: StartFrame):
        self._sample_rate = frame.audio_in_sample_rate
        self._received_first_binary_audio = False
        await self._cancel_no_audio_watchdog()

        async def _warn_if_no_audio_in_5s() -> None:
            try:
                await asyncio.sleep(5)
                if not self._received_first_binary_audio:
                    logger.warning(
                        "[voice][debug] WebSocket 연결 후 5초 동안 바이너리 오디오 프레임이 수신되지 않았습니다. "
                        "브라우저 마이크 권한/AudioContext 상태/ws.send 동작을 확인하세요."
                    )
            except asyncio.CancelledError:
                raise

        self._no_audio_watchdog_task = asyncio.create_task(_warn_if_no_audio_in_5s())

    async def serialize(self, frame: Frame) -> bytes | None:
        if isinstance(frame, OutputAudioRawFrame):
            return frame.audio
        return None

    async def deserialize(self, data: str | bytes) -> Frame | None:
        if isinstance(data, str):
            if data == "__DEBUG_NO_VOICE_5S__":
                logger.warning(
                    "[voice][debug] 클라이언트에서 5초 무음 감지됨. "
                    "마이크 입력 레벨/브라우저 권한/오디오 장치를 확인하세요."
                )
            elif data.startswith("__DEBUG_INFO__:"):
                _, _, rest = data.partition("__DEBUG_INFO__:")
                tag, _, body = rest.partition(":")
                logger.warning("[voice][debug] 클라이언트 {}: {}", tag or "info", body)
            return None
        if not data:
            return None
        if not self._received_first_binary_audio:
            self._received_first_binary_audio = True
            logger.info(
                "[voice][debug] 첫 바이너리 PCM 수신: {} bytes (sample_rate={}Hz, channels={})",
                len(data),
                self._sample_rate,
                self._num_channels,
            )
            await self._cancel_no_audio_watchdog()
        return InputAudioRawFrame(
            audio=bytes(data),
            sample_rate=self._sample_rate,
            num_channels=self._num_channels,
        )
