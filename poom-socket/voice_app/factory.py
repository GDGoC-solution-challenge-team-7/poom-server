"""FastAPI 앱 팩토리."""

from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles

from voice_app.config import STATIC_DIR
from voice_app.routers import health, pages, voice_spec, websocket_voice


def create_app() -> FastAPI:
    app = FastAPI(
        title="Poom Voice WebSocket",
        version="0.1.0",
        description=(
            "음성 비서 WebSocket 서버."
        ),
    )
    if STATIC_DIR.exists():
        app.mount("/static", StaticFiles(directory=str(STATIC_DIR)), name="static")

    app.include_router(health.router)
    app.include_router(pages.router)
    app.include_router(voice_spec.router)
    app.include_router(websocket_voice.router)
    return app
