# 백엔드 인수인계 (Poom Server)

본 문서는 모바일·음성 소켓 서버·차기 백엔드 담당자가 **현재 구현과 API 계약**을 맞추기 위한 요약입니다.  
기준 시점: 레포 내 Spring Boot 애플리케이션 및 `poom-socket` 연동 코드 기준.

---

## 1. 공통 사항

### 1.1 Base URL

- 로컬 예시: `http://localhost:8080`
- 모든 REST API는 `/api/v1` 접두사를 사용합니다.

### 1.2 표준 응답 래퍼 (`ApiResponse`)

성공 시 본문 형태:

| 필드 | 설명 |
|------|------|
| `isSuccess` | `true` |
| `code` | 성공 코드 문자열 |
| `message` | 성공 메시지 |
| `result` | 실제 데이터 (없으면 `null` 생략 가능) |

실패 시 `isSuccess: false`, `code`/`message`로 사유를 전달합니다.

### 1.3 인증 (Access Token)

- 구글 로그인·회원가입 후 발급되는 **JWT Access Token**을 사용합니다.
- 보호가 필요한 엔드포인트 호출 시 헤더:

```http
Authorization: Bearer <access_token>
```

- `JwtFilter`가 `Authorization` 헤더의 Bearer 토큰을 읽어 회원 정보를 `SecurityContext`에 넣습니다.
- `SecurityConfig`에서 `/api/v1/voice/**`, `/api/v1/chat/**` 등은 `permitAll`로 열려 있으나, **컨트롤러가 `@AuthenticationPrincipal CustomUserDetails`로 회원 ID를 쓰는 API는 토큰 없이 호출하면 동작하지 않습니다.**  
  → 실사용 시 **Bearer 토큰을 반드시 보내는 것**을 계약으로 고정하는 것이 안전합니다.

---

## 2. 인증·회원

| 메서드 | 경로 | 설명 |
|--------|------|------|
| `GET` | `/api/v1/auth/callback` | 구글 OAuth 콜백 (`code` 쿼리) |
| `POST` | `/api/v1/auth/sign-up` | 회원가입 |
| `POST` | `/api/v1/auth/reissue` | Access Token 재발급 |

Swagger: `/swagger-ui.html` (또는 프로젝트에 설정된 UI 경로), OpenAPI: `/v3/api-docs`

---

## 3. 텍스트 챗봇 (채팅 API)

### 3.1 흐름 요약

1. 사용자가 로그인하여 Access Token을 확보한다.
2. **채팅 메시지 전송** `POST /api/v1/chat`으로 대화를 시작한다.
3. `chatRoomId`가 없으면 서버가 채팅방을 생성하고 ID를 부여한다 (상세는 `ChatHelperService` / `ChatCommandService`).
4. AI 응답은 DB에 메시지로 저장된다.
5. **채팅방 제목(`chatTitle`)** 은 텍스트 채팅 경로에서 LLM 응답에 포함된 `<chat_title>...</chat_title>` 구간을 파싱해 저장한다.  
   - 최초 생성 시 임시 제목은 `"새 대화"`이며, 파싱된 제목이 있으면 갱신된다.

### 3.2 주요 엔드포인트

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| `POST` | `/api/v1/chat` | Bearer 권장 | 메시지 전송·저장·AI 응답 |
| `GET` | `/api/v1/chat` | Bearer 권장 | 채팅방 목록 |
| `GET` | `/api/v1/chat/{chatRoomId}` | Bearer 권장 | 채팅방 메시지 조회 |
| `PATCH` | `/api/v1/chat/{chatRoomId}` | (구현상 설정 확인) | 채팅방 설정 변경 |
| `GET` | `/api/v1/chat/{chatRoomId}/settings` | | 설정 조회 |
| `DELETE` | `/api/v1/chat/{chatRoomId}` | Bearer 권장 | 채팅방 삭제 |

> **참고:** `<chat_title>` 파싱은 **HTTP 채팅(`ChatHelperService.chat`)** 에서만 적용됩니다. 음성 전사 저장 경로는 아래 §4와 같습니다.

---

## 4. 음성 봇

### 4.1 역할

- 실시간 음성 세션은 **별도 WebSocket 서버**(예: `poom-socket`, Gemini Live 등)에서 처리합니다.
- 확정된 **사용자/AI 전사 텍스트**를 메인 서버에 보내 **채팅과 동일한 메시지 테이블**에 쌓아, 앱의 채팅 목록·히스토리와 일치시킵니다.

### 4.2 연결 정보 (REST)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| `GET` | `/api/v1/voice/connection-info` | 음성 WebSocket URL·가용 여부 등 |

응답 본문 (`VoiceConnectionInfo`, `result` 내부):

| 필드 | 설명 |
|------|------|
| `enabled` | 기능 사용 설정 여부 |
| `webSocketUrl` | 설정값 기준 WebSocket URL (예: `application.yml`의 `voice.websocket-url`, 환경변수 `VOICE_WS_URL`) |
| `description` | 안내 문구 |
| `available` | 해당 URL 호스트:포트에 TCP 연결 가능 여부(시연용 체크) |
| `unavailableMessage` | 연결 불가 시 메시지 |

**중요:** Spring은 **토큰·캐릭터 타입을 URL에 붙여 주지 않습니다.**  
클라이언트 또는 음성 서버 규약에 따라 `webSocketUrl`에 `?token=...&characterType=...` 등을 **추가해 연결**하는지는 별도 합의 사항입니다.  
기본 설정 예: `ws://localhost:8765/ws` (`src/main/resources/application.yml` 참고).

### 4.3 음성 전사 저장 (REST)

| 메서드 | 경로 |
|--------|------|
| `POST` | `/api/v1/voice/transcript` |

**이전 경로** `POST /api/v1/chat/voice/transcript` 는 사용하지 않습니다.  
레포 내 Python 연동(`poom-socket/voice_app/spring_chat.py`)도 `/api/v1/voice/transcript` 로 맞춰 두었습니다.

#### 요청 헤더

```http
Authorization: Bearer <access_token>
Content-Type: application/json
```

#### 요청 본문 (`VoiceTranscriptRequest`)

| 필드 | 타입 | 필수 조건 | 설명 |
|------|------|-----------|------|
| `chatRoomId` | number \| null | 역할에 따라 다름 | 아래 규칙 참고 |
| `role` | string | 항상 | `USER` 또는 `AI` (`SenderType` enum과 동일) |
| `text` | string | 항상 | 확정 전사 문구 (공백만 불가) |
| `characterType` | string \| null | 신규 방 생성 시에만 필수 | `EMPATHY`, `SOLUTION` (`CharacterType`) |

**규칙 (서버: `VoiceTranscriptService`):**

1. **`role == USER`**
   - **`chatRoomId`가 `null`**: 새 채팅방 생성 (`ChatMode.VOICE`, 임시 제목 `"새 대화"`). 이때 **`characterType` 필수.**
   - **`chatRoomId` 지정**: 해당 방 소유 회원 검증 후 사용자 메시지로 저장.
2. **`role == AI`**
   - **`chatRoomId` 필수** (없으면 400).

**주의:** 신규 세션에서는 `chatRoomId`를 `0`으로 보내지 말고 **`null` 또는 필드 생략**을 사용하세요. `0`은 “ID가 0인 방”으로 해석될 수 있습니다.

#### 성공 응답 (`result`)

| 필드 | 설명 |
|------|------|
| `chatRoomId` | 저장이 반영된 채팅방 ID (신규 생성 시 새로 부여된 ID 포함) |

클라이언트·음성 서버는 이후 전사 요청에 동일 `chatRoomId`를 넣어 같은 스레드에 메시지를 이어 씁니다.

### 4.4 음성 채팅방 제목

- 음성으로 새 방을 만들 때 제목은 서버에서 **`"새 대화"`** 로 고정 저장됩니다.
- 텍스트 채팅과 달리 **`VoiceTranscriptService` 경로에서는 `<chat_title>` 파싱을 하지 않습니다.**  
  (제목 자동 요약이 필요하면 추후 요구사항으로 분리하는 것이 맞습니다.)

---

## 5. 열거형 값 (JSON)

- **`role`:** `USER`, `AI`
- **`characterType`:** `EMPATHY` (공감), `SOLUTION` (해결)

---

## 6. 운영·환경

| 항목 | 예시 |
|------|------|
| 음성 WS URL | 환경변수 `VOICE_WS_URL` → `voice.websocket-url` |

---

## 7. 관련 소스 (빠른 탐색)

| 영역 | 위치 |
|------|------|
| 보안 | `global/security/SecurityConfig.java`, `JwtFilter.java` |
| 인증 API | `domain/auth/controller/AuthController.java` |
| 채팅 API | `domain/chat/controller/ChatController.java` |
| 채팅 + 제목 파싱 | `domain/chat/service/ChatHelperService.java` |
| 음성 API | `domain/voice/controller/VoiceController.java` |
| 음성 전사 저장 | `domain/voice/service/VoiceTranscriptService.java` |
| 소켓 → Spring POST | `poom-socket/voice_app/spring_chat.py` |

---

문서와 실제 동작이 어긋나면 **Swagger 및 위 소스**를 우선 기준으로 삼고, 배포 환경의 `application-*.yml`을 함께 확인하시기 바랍니다.
