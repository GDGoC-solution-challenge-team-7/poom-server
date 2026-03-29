"""VAD 턴 단위로 사용자 전사 문장을 모았다가, 턴 종료 후 짧은 지연 뒤 '최종'으로 확정한다."""

from __future__ import annotations

import asyncio
from collections.abc import Awaitable, Callable

FlushCallback = Callable[[str], Awaitable[None]]


class UserTurnCoordinator:
    """Gemini Live는 문장 단위 TranscriptionFrame을 보낸다. 여러 문장을 한 턴으로 묶고,

    UserStoppedSpeaking 이후 늦게 도착하는 전사를 포함하려면 짧은 settle 지연 뒤 플러시한다.
    새 턴(UserStartedSpeaking)이 먼저 오면 지연 작업을 취소하고 즉시 이전 턴을 확정한다.
    """

    def __init__(self, delay_sec: float = 0.35):
        self._delay_sec = delay_sec
        self._parts: list[str] = []
        self._task: asyncio.Task | None = None

    def _join_and_clear(self) -> str:
        out = " ".join(self._parts).strip()
        self._parts.clear()
        return out

    async def _cancel_task(self) -> None:
        if self._task is not None and not self._task.done():
            self._task.cancel()
            try:
                await self._task
            except asyncio.CancelledError:
                pass
        self._task = None

    def append_sentence(self, text: str) -> str:
        t = (text or "").strip()
        if not t:
            return self.current_text()
        if not self._parts or self._parts[-1] != t:
            self._parts.append(t)
        return self.current_text()

    def current_text(self) -> str:
        return " ".join(self._parts).strip()

    async def on_user_started(self, on_final: FlushCallback) -> None:
        await self._cancel_task()
        text = self._join_and_clear()
        if text:
            await on_final(text)

    async def flush_pending_now(self, on_final: FlushCallback) -> None:
        """UserStopped/VAD가 안 오거나 지연 전에 봇이 먼저 말하는 경우, 쌓인 사용자 전사를 즉시 확정한다."""
        await self._cancel_task()
        text = self._join_and_clear()
        if text:
            await on_final(text)

    async def on_user_stopped(self, on_final: FlushCallback) -> None:
        await self._cancel_task()

        async def _delayed() -> None:
            try:
                await asyncio.sleep(self._delay_sec)
                flushed = self._join_and_clear()
                if flushed:
                    await on_final(flushed)
            except asyncio.CancelledError:
                raise

        self._task = asyncio.create_task(_delayed())
