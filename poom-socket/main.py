"""
Voice WebSocket server: FastAPI + Pipecat + Gemini Live (S2S).
Poom mobile app connects to ws://host:8765/ws for real-time voice assistant.
API key loaded from .env (same as poom-server GenAiApiKeyConfig).
"""

import asyncio
import os

from pathlib import Path

from dotenv import load_dotenv
from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from fastapi.responses import FileResponse
from loguru import logger

# websocket 관련 의존성
from pipecat.pipeline.pipeline import Pipeline
from pipecat.pipeline.runner import PipelineRunner
from pipecat.pipeline.task import PipelineParams, PipelineTask
from pipecat.transports.websocket.fastapi import (
    FastAPIWebsocketParams,
    FastAPIWebsocketTransport,
)
from pipecat.services.google.gemini_live.llm import (
    GeminiLiveLLMService,
    InputParams,
    GeminiVADParams,
    ContextWindowCompressionParams,
)
from pipecat.transcriptions.language import Language # 언어 설정(한국어)

# 오디오 시리얼라이저
from serializers import RawPCMWebSocketSerializer
# 파이프라인 내부 발화/응답 로깅용
from pipecat.frames.frames import (
    TranscriptionFrame,
    LLMTextFrame,
    LLMFullResponseEndFrame,
)
from pipecat.observers.base_observer import BaseObserver, FrameProcessed, FramePushed


class VoiceLogObserver(BaseObserver):
    """서버 터미널에 사용자 발화(user_text)·AI 응답(ai_text) 로그 출력."""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self._ai_text_buffer = []

    def _log_user(self, text: str):
        msg = (text or "").strip() or "(empty)"
        # 단일 문자열로 전달해 loguru 등에서 % 포맷 재적용 시 %s가 그대로 나오는 현상 방지
        logger.info("[voice] user_text: " + msg)

    def _log_ai(self):
        if self._ai_text_buffer:
            full = "".join(self._ai_text_buffer).strip()
            if full:
                logger.info("[voice] ai_text: " + full)
        self._ai_text_buffer = []

    async def on_process_frame(self, data: FrameProcessed):
        frame = data.frame
        if isinstance(frame, TranscriptionFrame) and getattr(frame, "text", None):
            self._log_user(frame.text)
        elif isinstance(frame, LLMTextFrame) and getattr(frame, "text", None):
            self._ai_text_buffer.append(frame.text)
        elif isinstance(frame, LLMFullResponseEndFrame):
            self._log_ai()

    async def on_push_frame(self, data: FramePushed):
        """같은 프레임이 push/process 양쪽으로 오므로 여기선 처리하지 않음. 중복 로그·버퍼 방지."""
        pass


load_dotenv()
GOOGLE_API_KEY = os.getenv("GOOGLE_API_KEY")
if not GOOGLE_API_KEY:
    logger.warning("GOOGLE_API_KEY not set; voice server will fail at runtime.")

# 어시스턴스 지시 사항
_ROOT = Path(__file__).resolve().parent
_SYSTEM_INSTRUCTION_FILE = _ROOT / "SYSTEM_INSTRUCTION"

# 없으면 기본 지시 사항 적용
if not _SYSTEM_INSTRUCTION_FILE.exists():
    _SYSTEM_INSTRUCTION_FILE = _ROOT / "SYSTEM_INSTRUCTION.txt"
SYSTEM_INSTRUCTION = _SYSTEM_INSTRUCTION_FILE.read_text(encoding="utf-8") if _SYSTEM_INSTRUCTION_FILE.exists() else (
    "You are a helpful voice assistant for new parents (Poom). "
    "Respond briefly and clearly. Support multiple languages when needed."
)

app = FastAPI(
    title="Poom Voice WebSocket",
    version="0.1.0",
    description="음성 비서 WebSocket 서버. **데이터 흐름·메시지 형식**은 `GET /api/voice/ws-spec` 응답 또는 `docs/WebSocket_데이터_형식.md` 참고.",
)

# ---------- WebSocket 프로토콜 스펙 ----------

@app.get(
    "/api/voice/ws-spec",
    summary="WebSocket 프로토콜 스펙",
    description="음성 비서 WebSocket(/ws)의 메시지 종류·데이터 형식·흐름. 프론트 연동 시 참고.",
)
async def voice_ws_spec():
    """Swagger에는 WebSocket 메시지가 안 나오므로, 데이터 흐름은 이 스펙으로 전달."""
    return {
        "websocket": {"path": "/ws", "description": "실시간 음성 비서."},
        "field_mapping": {
            "comment": "REST의 message/reply처럼, WebSocket에서 '어디에 뭐가 저장되는지' 매핑.",
            "input": {
                "user_voice": "클라이언트가 서버로 보낼 때 사용. WebSocket Binary 메시지 전체 = 사용자 음성(PCM). JSON 필드 없음.",
                "user_text": "서버가 인식한 사용자 발화 텍스트. 서버→클라이언트로 보낼 때만 존재. 아래 output.text 예시 참고.",
            },
            "output": {
                "ai_voice": "서버가 클라이언트로 보낼 때. WebSocket Binary 메시지 전체 = AI 음성(PCM). JSON 아님.",
                "ai_text": "서버가 클라이언트로 보낼 때. WebSocket Text 메시지(JSON) 안의 필드. 아래 예시 참고.",
                "user_text": "서버가 클라이언트로 보낼 때. WebSocket Text 메시지(JSON) 안의 필드. 전사 결과.",
            },
        },
        "output_json_examples": {
            "comment": "서버→클라이언트로 오는 Text 메시지는 아래 JSON 중 하나 형태. 직접 확인은 GET /docs/ws-inspect 페이지 사용.",
            "user_text_only": {"user_text": "오늘 기분이 어때?", "ai_text": None},
            "ai_text_only": {"user_text": None, "ai_text": "저도 좋아요. 무엇을 도와드릴까요?"},
            "both": {"user_text": "배가 아파요", "ai_text": "어디가 아프신가요? 병원 가보셨나요?"},
        },
        "message_types": {
            "client_sends": "Binary only. 필드 없음. Raw PCM 16kHz mono 16bit.",
            "server_sends": "Binary = ai_voice(PCM 24kHz). Text = JSON with user_text and/or ai_text.",
        },
        "docs_md": "상세: docs/WebSocket_데이터_형식.md. 수신 JSON 직접 확인: GET /docs/ws-inspect",
    }


@app.get("/health")
async def health():
    return {"status": "ok", "service": "poom-voice-ws"}


@app.get("/demo", include_in_schema=False)
async def voice_demo_page():
    """브라우저에서 마이크로 음성 비서 시연용 페이지."""
    path = Path(__file__).parent / "static" / "demo.html"
    if not path.exists():
        return {"error": "demo.html not found"}
    return FileResponse(path)


@app.get("/docs/ws-inspect", include_in_schema=False)
async def ws_inspect_page():
    """WebSocket 수신 메시지(필드·JSON 형식)를 직접 확인하는 페이지. 프론트 연동 참고용."""
    path = Path(__file__).parent / "static" / "ws-inspect.html"
    if not path.exists():
        return {"error": "ws-inspect.html not found"}
    return FileResponse(path)


@app.websocket("/ws")
async def voice_websocket(websocket: WebSocket):
    await websocket.accept()
    if not GOOGLE_API_KEY:
        await websocket.close(code=1011, reason="GOOGLE_API_KEY not configured")
        return

    try:
        params = FastAPIWebsocketParams(
            serializer=RawPCMWebSocketSerializer(),
            add_wav_header=False,
            session_timeout=None,
            audio_in_enabled=True,
            audio_out_enabled=True,
        )
        transport = FastAPIWebsocketTransport(websocket, params=params)
        # gemini-live-2.5-flash-native-audio 사용해도 될 듯, 
        llm = GeminiLiveLLMService(
            api_key=GOOGLE_API_KEY,
            model="models/gemini-2.5-flash-native-audio-preview-12-2025",
            voice_id="Puck", # 목소리 설정: Callirrhoe, Achernar .. 등
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
            observers=[VoiceLogObserver()],
        )
        runner = PipelineRunner(handle_sigint=False)
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


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8765,
        reload=False,
    )
