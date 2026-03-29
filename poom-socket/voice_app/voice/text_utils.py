"""스트리밍 AI 텍스트 청크 병합·중복 완화."""


def append_ai_chunk_with_overlap_dedup(buffer: list[str], new_chunk: str) -> None:
    """버퍼 끝과 새 청크 앞이 겹치면 겹친 부분을 빼고 이어붙임. 스트리밍 중복 문장 완화."""
    if not new_chunk:
        return
    current = "".join(buffer)
    overlap = 0
    for i in range(1, min(len(new_chunk), len(current)) + 1):
        if current[-i:] == new_chunk[:i]:
            overlap = i
    to_append = new_chunk[overlap:]
    if to_append:
        buffer.append(to_append)


def dedupe_ai_text_full(text: str) -> str:
    """최종 AI 텍스트에서 뒤쪽에 반복된 구간(앞에 이미 나온 부분)을 제거."""
    text = (text or "").strip()
    if not text or len(text) < 20:
        return text
    n = len(text)
    for length in range(n // 2, 9, -1):
        tail = text[-length:]
        if tail in text[:-length]:
            return text[:-length].strip()
    return text
