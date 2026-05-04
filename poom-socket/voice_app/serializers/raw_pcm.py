# Raw PCM WebSocket serializer for mobile/client audio (no telephony protocol).
# Binary messages = PCM mono 16-bit; sample_rate from pipeline StartFrame.

from typing import Optional

from loguru import logger
from pipecat.frames.frames import (
    Frame,
    InputAudioRawFrame,
    OutputAudioRawFrame,
    StartFrame,
)
from pipecat.serializers.base_serializer import FrameSerializer

from voice_app.config import VOICE_DEBUG_AUDIO


class RawPCMWebSocketSerializer(FrameSerializer):
    """Treats binary WebSocket messages as raw PCM audio (16kHz mono 16-bit by default)."""

    def __init__(self, params: Optional[FrameSerializer.InputParams] = None, **kwargs):
        super().__init__(params=params, **kwargs)
        self._sample_rate = 16000 # 샘플레이트 16000Hz: 1초에 16000개의 샘플을 측정
        self._num_channels = 1 # 채널 1: 모노(단일 채널)
        self._logged_first_audio = False

    async def setup(self, frame: StartFrame):
        self._sample_rate = frame.audio_in_sample_rate

    async def serialize(self, frame: Frame) -> bytes | None:
        if isinstance(frame, OutputAudioRawFrame):
            return frame.audio
        return None

    async def deserialize(self, data: str | bytes) -> Frame | None:
        if isinstance(data, str) or not data:
            return None
        if VOICE_DEBUG_AUDIO and not self._logged_first_audio:
            self._logged_first_audio = True
            logger.info("[voice][debug] 첫 WebSocket 바이너리 오디오 청크: {} bytes", len(data))
        return InputAudioRawFrame(
            audio=bytes(data),
            sample_rate=self._sample_rate,
            num_channels=self._num_channels,
        )
