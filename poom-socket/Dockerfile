# Poom Voice WebSocket 서버 (FastAPI + Pipecat + Gemini Live)
FROM python:3.12.7-slim

# 타임존 설정 (메인 서버와 동일)
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && echo "Asia/Seoul" > /etc/timezone

WORKDIR /app

# 의존성 먼저 복사 후 설치 (레이어 캐시 활용)
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 앱 코드 및 리소스
COPY main.py serializers.py ./
COPY SYSTEM_INSTRUCTION* ./
COPY static/ ./static/

# 포트 (main.py와 동일)
ENV PORT=8765
EXPOSE ${PORT}

# 비밀은 런타임에 GOOGLE_API_KEY 등으로 주입
ENTRYPOINT ["python", "-m", "uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8765"]
