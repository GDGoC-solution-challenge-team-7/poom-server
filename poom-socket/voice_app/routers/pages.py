"""브라우저 시연용 HTML 페이지."""

from fastapi import APIRouter
from fastapi.responses import FileResponse

from voice_app.config import STATIC_DIR

router = APIRouter(include_in_schema=False)


@router.get("/demo")
async def voice_demo_page():
    """브라우저에서 마이크로 음성 비서 시연용 페이지."""
    path = STATIC_DIR / "demo.html"
    if not path.exists():
        return {"error": "demo.html not found"}
    return FileResponse(path)
