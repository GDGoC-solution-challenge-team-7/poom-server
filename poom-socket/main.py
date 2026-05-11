"""
진입점: `uvicorn main:app` 또는 `python main.py`로 실행.
앱 구성·라우트·음성 파이프라인은 `voice_app` 패키지에 분리한다.
"""
import os
import sys

from loguru import logger
import uvicorn

from voice_app.factory import create_app

# 기본 로그 레벨을 INFO로 고정해 민감한 DEBUG 로그(예: 시스템 지시문 출력)를 숨긴다.
logger.remove()
logger.add(sys.stderr, level=os.getenv("LOG_LEVEL", "INFO").upper())

app = create_app()

if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8765,
        reload=False,
    )
