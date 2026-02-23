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


## 목소리


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