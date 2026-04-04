"""음성 세션 STT를 메인 서버에 저장==> — 채팅 스레드와 동일한 메시지 목록 유지."""

from __future__ import annotations

import asyncio
from typing import Any

import httpx
from loguru import logger

VOICE_TRANSCRIPT_PATH = "/api/v1/voice/transcript"


async def post_voice_transcript(
    *,
    base_url: str,
    bearer_token: str,
    role: str,
    text: str,
    chat_room_id: int | None,
    character_type: str | None,
    timeout_sec: float = 60.0,
) -> tuple[bool, dict[str, Any] | None, str | None]:
    """Spring ApiResponse 파싱. role 은 USER | AI."""
    url = base_url.rstrip("/") + VOICE_TRANSCRIPT_PATH
    body: dict[str, Any] = {
        "chatRoomId": chat_room_id,
        "role": role,
        "text": text,
        "characterType": character_type,
    }
    headers = {
        "Authorization": f"Bearer {bearer_token}",
        "Content-Type": "application/json",
    }
    preview = (text[:50] + "…") if len(text) > 50 else text
    try:
        async with httpx.AsyncClient(timeout=timeout_sec) as client:
            resp = await client.post(url, json=body, headers=headers)
            try:
                data = resp.json()
            except Exception:
                data = {"_raw": (resp.text or "")[:500]}
    except Exception as e:
        logger.warning("[spring] voice transcript POST 실패 role={} url={} err={}", role, url, e)
        return False, None, str(e)

    if not resp.is_success:
        logger.warning(
            "[spring] voice transcript HTTP {} role={} body_preview={!r} resp={}",
            resp.status_code,
            role,
            preview,
            data,
        )
        return False, None, str(data)

    if not data.get("isSuccess"):
        msg = data.get("message") or str(data)
        logger.warning("[spring] voice transcript isSuccess=false role={} msg={}", role, msg)
        return False, None, msg

    result = data.get("result")
    if not isinstance(result, dict):
        logger.info(
            "[spring] 메인 서버 응답 OK HTTP {} role={} text_preview={!r}",
            resp.status_code,
            role,
            preview,
        )
        return True, {}, None

    logger.info(
        "[spring] 메인 서버 저장 OK HTTP {} role={} chatRoomId={} text_preview={!r}",
        resp.status_code,
        role,
        result.get("chatRoomId"),
        preview,
    )
    return True, result, None


class SpringChatSync:
    """user_text_final → USER 전사 저장, ai_text_final → AI(Gemini) 전사 저장."""

    def __init__(
        self,
        *,
        base_url: str,
        bearer_token: str,
        character_type: str,
        initial_chat_room_id: int | None = None,
        timeout_sec: float = 60.0,
    ) -> None:
        self._base_url = base_url.rstrip("/")
        self._token = bearer_token
        self._character_type = character_type
        self._timeout_sec = timeout_sec
        self._room_id: int | None = initial_chat_room_id
        self._lock = asyncio.Lock()

    async def persist_final_user_text(self, text: str) -> None:
        trimmed = (text or "").strip()
        if not trimmed:
            return

        async with self._lock:
            room_id = self._room_id

        ok, result, err = await post_voice_transcript(
            base_url=self._base_url,
            bearer_token=self._token,
            role="USER",
            text=trimmed,
            chat_room_id=room_id,
            character_type=self._character_type if room_id is None else None,
            timeout_sec=self._timeout_sec,
        )
        if not ok:
            logger.warning("[spring] USER 전사 저장 실패: {}", err)
            return

        new_id = (result or {}).get("chatRoomId")
        if new_id is not None:
            async with self._lock:
                self._room_id = int(new_id)

    async def persist_final_assistant_text(self, text: str) -> None:
        trimmed = (text or "").strip()
        if not trimmed:
            return

        async with self._lock:
            room_id = self._room_id

        if room_id is None:
            logger.warning("[spring] skip AI transcript: no chatRoomId yet (user turn not saved?)")
            return

        ok, result, err = await post_voice_transcript(
            base_url=self._base_url,
            bearer_token=self._token,
            role="AI",
            text=trimmed,
            chat_room_id=room_id,
            character_type=None,
            timeout_sec=self._timeout_sec,
        )
        if not ok:
            logger.warning("[spring] AI 전사 저장 실패: {}", err)
            return
