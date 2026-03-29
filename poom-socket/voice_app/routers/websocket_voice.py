"""실시간 음성 비서 WebSocket 엔드포인트."""

from fastapi import APIRouter, WebSocket

from voice_app.voice.session import run_voice_session

router = APIRouter()


@router.websocket("/ws")
async def voice_websocket(websocket: WebSocket):
    await websocket.accept()
    await run_voice_session(websocket)
