# 🛡️ Spring Boot JWT Auth & Security Server

> **목적:** JWT 인증/인가 및 Redis 기반 보안 최적화 모듈 구현

## 🛠️ Tech Stack
- **Language:** Java 17
- **Framework:** Spring Boot 4.x, Spring Security
- **Database:** MySQL (Spring Data JPA)
- **In-Memory DB:** Redis (Docker)
- **Mail:** Spring Boot Starter Mail

---

## 💡 Key Features & Architecture

### 1. 보안 인증 (Authentication) 및 권한 부여 (Authorization)
- **Spring Security + JWT:** 세션(Session)에 의존하지 않는 Stateless 형태의 인증 시스템 구현.
- **Filter Chain 커스텀:** `OncePerRequestFilter`를 상속받은 커스텀 `JwtFilter`를 구현하여, 모든 API 요청 전 토큰의 유효성을 검증.
- **의존성 주입(DI) 제어:** `JwtFilter`를 글로벌 필터로 자동 등록하는 스프링 부트의 이중 실행 버그를 방지하기 위해, `@Component` 대신 `SecurityConfig`에서 `new` 키워드와 파라미터 주입을 통해 필터 생명주기를 수동으로 안전하게 통제.

### 2. Redis 기반 인프라 최적화 (보안 & 성능)
- **JWT 로그아웃 (Blacklist):** - JWT의 한계(발급 후 탈취 시 통제 불가)를 극복하기 위해 로그아웃된 토큰을 Redis에 블랙리스트로 등록.
  - Value를 1바이트 크기로 최소화하여 메모리 최적화.
  - 토큰의 남은 만료 시간(TTL)만큼만 Redis에 보관되도록 설정하여 메모리 누수 방지.
- **이메일 인증 시스템:** - 인증번호(6자리)를 Redis에 3분(TTL) 동안만 저장하여, 스케줄러(Timer) 없이 자동으로 파기되도록 RDBMS I/O 최적화.
  - 이메일 인증 통과 시 '인증 합격증(TTL 30분)'을 발급하여, API 직접 호출을 통한 회원가입 우회를 차단.

### 3. 보안 및 유지보수성 (Clean Code)
- **민감 정보 격리:** DB 접속 정보, 이메일 SMTP 앱 비밀번호, JWT Secret Key 등 모든 민감한 설정값을 `application.properties` 내 환경변수(`${...}`)로 분리하여 깃허브 하드코딩 노출 방지.
- **비밀번호 암호화:** `BCryptPasswordEncoder`를 적용하여 단방향 암호화 후 MySQL 저장.

---

## 🚨 Trouble Shooting & 팩트체크 기록

- **Issue:** Redis 연동 후 `JwtFilter`에서 발생하는 의존성 주입(DI) 누락 컴파일 에러.
- **Cause:** Lombok(`@RequiredArgsConstructor`)으로 인해 필수 매개변수가 늘어났으나, `SecurityConfig`에서 갱신되지 않음.
- **Solution:** `SecurityFilterChain` 빈(Bean) 생성 시 매개변수로 스프링 컨테이너의 `RedisTemplate`을 결재받아, 야생의 `new JwtFilter()`에 직접 전달하는 방식으로 스프링 제어의 역전(IoC) 흐름을 이해하고 해결함.

- **Issue:** `Failed to configure a DataSource` 에러 발생.
- **Cause:** build.gradle 내 DB 드라이버 존재 여부 확인 및 로컬 빌드 캐시 꼬임 현상.
- **Solution:** Gradle build 폴더 클린 및 캐시 삭제 후, 도커(Docker)를 활용한 DB/Redis 격리 환경 구축의 필요성.
