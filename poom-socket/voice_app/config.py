"""환경 변수·프로젝트 경로·Gemini 음성 모델 상수."""

from pathlib import Path

import os

from dotenv import load_dotenv
from loguru import logger

PROJECT_ROOT = Path(__file__).resolve().parent.parent
# 실행 cwd와 무관하게 poom-socket/.env 를 읽음 (SPRING_BASE_URL 등)
load_dotenv(PROJECT_ROOT / ".env")
load_dotenv()
STATIC_DIR = PROJECT_ROOT / "static"
PROMPT_DIR = STATIC_DIR / "prompt"

GOOGLE_API_KEY = os.getenv("GOOGLE_API_KEY")
if not GOOGLE_API_KEY:
    logger.warning("GOOGLE_API_KEY not set; voice server will fail at runtime.")

_SYSTEM_INSTRUCTION_FILE = PROMPT_DIR / "SYSTEM_INSTRUCTION"
if not _SYSTEM_INSTRUCTION_FILE.exists():
    _SYSTEM_INSTRUCTION_FILE = PROMPT_DIR / "SYSTEM_INSTRUCTION.txt"

SYSTEM_INSTRUCTION = (
    _SYSTEM_INSTRUCTION_FILE.read_text(encoding="utf-8")
    if _SYSTEM_INSTRUCTION_FILE.exists()
    else (
        "You are a helpful voice assistant for new parents (Poom). "
        "Respond briefly and clearly. Support multiple languages when needed."
    )
)

GEMINI_VOICE_MODEL = "models/gemini-2.5-flash-native-audio-preview-12-2025"
GEMINI_VOICE_ID = "Leda"
GEMINI_VAD_SILENCE_DURATION_MS = int(os.getenv("GEMINI_VAD_SILENCE_DURATION_MS", "500"))

# UserStoppedSpeaking 이후 늦게 도착하는 전사를 묶기 위한 지연(초).
USER_TURN_SETTLE_DELAY_SEC = float(os.getenv("USER_TURN_SETTLE_DELAY_SEC", "0.35"))

# 비어 있으면 user_text_final 시 Spring 동기화 비활성화.
SPRING_BASE_URL = (os.getenv("SPRING_BASE_URL") or "").strip()
# 음성 전사 저장은 LLM 없음 — 기본 타임아웃 짧게.
SPRING_CHAT_TIMEOUT_SEC = float(os.getenv("SPRING_CHAT_TIMEOUT_SEC", "60"))

if SPRING_BASE_URL:
    logger.info("[spring] 설정됨: SPRING_BASE_URL={}", SPRING_BASE_URL)
else:
    logger.info("[spring] 미설정: SPRING_BASE_URL 비어 있음")
