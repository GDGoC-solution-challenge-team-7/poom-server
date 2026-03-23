"""
진입점: `uvicorn main:app` 또는 `python main.py`로 실행.
앱 구성·라우트·음성 파이프라인은 `voice_app` 패키지에 분리한다.
"""

import uvicorn

from voice_app.factory import create_app

app = create_app()

if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8765,
        reload=False,
    )
