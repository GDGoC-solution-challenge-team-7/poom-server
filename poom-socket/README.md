# Poom Voice WebSocket Server

poom-socket/
├── main.py                          # 앱 생성 + uvicorn 진입
├── voice_app/
│   ├── __init__.py
│   ├── config.py                    # .env, PROJECT_ROOT, SYSTEM_INSTRUCTION, Gemini 상수
│   ├── factory.py                   # create_app(): FastAPI, static 마운트, 라우터 등록
│   ├── serializers/
│   │   ├── __init__.py
│   │   └── raw_pcm.py               # RawPCMWebSocketSerializer (기존 serializers.py)
│   ├── routers/
│   │   ├── __init__.py
│   │   ├── health.py                # GET /health
│   │   ├── pages.py                 # GET /demo, /docs/ws-inspect
│   │   ├── voice_spec.py            # GET /api/voice/ws-spec
│   │   └── websocket_voice.py       # WebSocket /ws
│   └── voice/
│       ├── __init__.py
│       ├── text_utils.py            # 스트리밍 텍스트 병합·중복 제거 (순수 함수)
│       ├── observers.py             # VoiceLogObserver, VoiceTextWebSocketObserver
│       └── session.py               # run_voice_session() — Pipecat + Gemini 파이프라인
├── static/ …
└── docs/ 

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


## 지원되는 목소리

```bash
여성 목소리 voice_id (13개)
voice_id	특성 (설명)
Zephyr	Bright – 밝고 선명한 여성
Kore	Firm – 단호하고 차분한 여성
Leda	Youthful – 젊고 활기찬 여성
Aoede	Breezy – 편안하고 자연스러운 여성
Callirrhoe	Easy-going – 친근하고 부드러운 여성
Autonoe	Bright – 밝고 밝은 여성
Despina	Smooth – 부드럽고 차분한 여성
Erinome	Clear – 또렷하고 명료한 여성
Laomedeia	Upbeat – 긍정적이고 경쾌한 여성
Achernar	Soft – 부드럽고 따뜻한 여성 (현재 사용 중)
Gacrux	Mature – 성숙하고 안정적인 여성
Vindemiatrix	Gentle – 부드럽고 섬세한 여성
Sulafat	Warm – 따뜻하고 다가가기 쉬운 여성
```