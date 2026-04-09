# 🐾 pawNest : 실종 동물 제보 커뮤니티 플랫폼

 Google Vision API를 활용한 견종 식별과 WebSocket 기반의 실시간 커뮤니티 서비스


# 🚀 주요 기능
#### 1. AI 기반 견종 식별
- Google Vision API를 활용하여 업로드된 사진에서 견종 자동 추출

#### 2. 실시간 소통 (STOMP)
- WebSocket을 활용한 양방향 통신 구현
- 실시간 댓글/채팅 제공

#### 3. JWT & Security 강화
- JWT 기반의 Stateless 인증 체계 구축
- HTTP 요청 뿐만 아니라 WebSocket 채널 인터셉터를 
커스텀 하여 실시간 통신 보안 강화

# 🛠 기술 스택
**Backend**
- JAVA 17
- Spring boot 3.x
- Spring Security
- Spring Data JPA

**communication**
- WebSocket / STOMP

**AI & External API**
- Google Cloud Vision API

**DB & Tools**
- MYSQL / HeidiSQL
- ngrok
- postman

# 🏗 트러블 슈팅
#### 1. WebSocket 인증 정보 유실
- **Issue** : Spring Security의 OncePerRequestFilter는 HTTP 요청에만 반응하여, WebSocket 연결 시 @AuthenticationPrincipal에 인증 정보 대신 @Payload 데이터가 담기는 현상 발생
- **Solution** :  ChannelInterceptor를 구현하여 웹소켓 CONNECT 프레임에서 JWT를 검증하고, MessageHeaderAccessor를 통해 세션에 유지 정보를 수동 주입하여 해결

#### 2. AI 데이터 필터
- **Issue** : Vision API 결과에 'Dog','Pet'등 너무 일반적이거나 포괄적인 범위의 단어들이 출력 되는 문제
- **Solution** : 품종 외 불필요한 키워드를 걸러내는 필터링 리스트를 구축하고, getOrDefault 패턴을 활용하여 미등록 견종에 대한 예외 처리 구현

# 📂 프로젝트 구조

```
src/main/java/com/jihyeon/pawNest
-------------------------------------------------
//1. 도메인 별 비즈니스 로직 분리
├─ ai
│   ├─ controller
│   ├─ service
├─ board
│   ├─ controller
│   ├─ repository
│   ├─ service
├─ chat
│   ├─ controller
│   ├─ repository
│   ├─ service
├─ comment
│   ├─ controller
│   ├─ repository
│   ├─ service
├─ user
│   ├─ repository
│   ├─ service
-------------------------------------------------
├─ config //2. 각 종 설정 파일 분리
│   ├── SwaggerConfig.java
│   ├── WebConfig.java
│   ├── WebSocketConfig.java
├─ domain //3. 엔티티 분리
│   ├── board
│   ├── chat
│   ├── comment
│   ├── user
├─  dto //4. dto 분리
│   ├── request
│   ├── response
├─ handler //5. 로그인 성공 처리 핸들러 및 웹소켓 전용 핸들러
│   ├── OAuth2SuccessHandler.java
│   ├── StompHandler.java         // CONNECT 시점에 토큰 검증 및 Principal 주입
├─ security //6. 로그인 JWT 관련 로직 분리
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   ├── SecurityConfig.java
```
