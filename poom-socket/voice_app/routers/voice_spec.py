"""WebSocket 프로토콜 스펙(JSON). Swagger에 WS가 없어 REST로 문서화."""

from fastapi import APIRouter

router = APIRouter(prefix="/api/voice", tags=["voice"])


@router.get(
    "/ws-spec",
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
                "user_text_final": "true면 해당 user_text가 VAD 턴 기준 확정본(Spring POST 등 저장용). false면 문장 누적(partial) 또는 AI 메시지.",
                "ai_text_final": "true면 해당 ai_text가 한 응답의 최종본.",
            },
        },
        "output_json_examples": {
            "comment": "서버→클라이언트 Text 메시지는 항상 user_text_final·ai_text_final 포함. Spring 연동 시 user_text_final==true / ai_text_final==true 만 저장 후보로 쓰면 됨.",
            "user_partial": {
                "user_text": "오늘 기분이",
                "ai_text": None,
                "user_text_final": False,
                "ai_text_final": False,
            },
            "user_final": {
                "user_text": "오늘 기분이 어때?",
                "ai_text": None,
                "user_text_final": True,
                "ai_text_final": False,
            },
            "ai_final": {
                "user_text": None,
                "ai_text": "저도 좋아요. 무엇을 도와드릴까요?",
                "user_text_final": False,
                "ai_text_final": True,
            },
        },
        "message_types": {
            "client_sends": "Binary only. 필드 없음. Raw PCM 16kHz mono 16bit.",
            "server_sends": "Binary = ai_voice(PCM 24kHz). Text = JSON(user_text, ai_text, user_text_final, ai_text_final).",
        },
        "docs_md": "상세: docs/WebSocket_데이터_형식.md",
    }
