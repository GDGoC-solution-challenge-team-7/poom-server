# Poom Voice WebSocket Server

FastAPI + Pipecat + Gemini Live(S2S) 기반 실시간 음성 비서 WebSocket 서버입니다.  
poom-server의 `/api/voice/connection-info`가 반환하는 `webSocketUrl`(기본 `ws://localhost:8765/ws`)에 연결하면 됩니다.

## 설정

1. **가상환경 및 의존성**
   ```bash
   python>=3.12
   ```


   ```bash
   python -m venv .venv
   .venv\Scripts\activate   # Windows
   pip install -r requirements.txt
   ```

## 실행

```bash
.venv\Scripts\activate
python main.py
```

- WebSocket: `ws://localhost:8765/ws`
- 헬스: `GET http://localhost:8765/health`
- **시연 페이지**: 브라우저에서 **http://localhost:8765/demo** 접속 → "마이크 켜고 연결" 클릭 → 마이크 허용 후 말하면 음성으로 답변 


