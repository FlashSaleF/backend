# 플래시 세일 이커머스 프로젝트

🎇 **한 줄 소개:**

플래시 세일 이커머스 프로젝트는 짧은 시간 동안 한정된 수량의 상품을 할인된 가격에 판매하는 이벤트를 제공하는 서비스입니다.

<img src="https://github.com/FlashSaleF/backend/blob/dev/docs/images/flash%20sale.png?raw=true">

---

## ⚡ 서비스/프로젝트 소개

플래시 세일 이커머스 프로젝트는 짧은 시간 동안 한정된 수량의 상품을 할인된 가격에 판매하는 이벤트를 제공하는 서비스입니다.

사용자는 제한된 시간 내에 특별한 가격으로 상품을 구매할 수 있으며, 판매자는 이러한 이벤트를 활용해 효과적으로 상품을 판매할 수 있습니다.

즉, 본 서비스는 소비자에게 합리적인 구매 경험을 제공하고, 판매자에게는 단기적인 판매 촉진 기회를 제공합니다.

🎇 **플래시 세일(Flash Sale)이란?**  
→ 특정 상품들의 제한된 수량만을 짧은 기간 동안 할인된 가격으로 판매하는 프로모션 방식입니다.

---

## 🎯 서비스/프로젝트 목표

짧은 기간 동안 높은 할인율을 적용하여 상품을 제공하는 플래시 세일 상황에서는, 대규모 사용자가 동시에 시스템에 접속하여 한정된 상품을 구매하려고 시도하기 때문에 서버 성능, 데이터 일관성, 동시성 처리가 매우
중요합니다.

이 프로젝트의 목표는:

- **MSA**와 **DDD**를 적용하여 대규모 트래픽 환경에서도 서비스가 유연하고 확장 가능하게 설계되는 것입니다.
- **Redis 분산 락**과 같은 **동시성 제어 기법**을 통해 재고 관리의 정확성을 유지하고,
- **Kafka**와 같은 비동기 메시징 시스템을 활용해 **대규모 트래픽 처리**에 효율적으로 대응하는 것입니다.
- **Prometheus**와 **Grafana**를 통한 **실시간 모니터링**을 구축하여, 트래픽 증가와 서버 성능을 지속적으로 추적하고 관리하는 것입니다.

이를 통해 대규모 트래픽 상황에서도 시스템의 성능을 시각화하고, 장애 발생 시 신속하게 대응할 수 있는 안정적인 모니터링 환경을 제공함과 동시에 안정적으로 운영 가능한 확장성 있는 시스템을 구축하여 사용자 경험을
극대화하는 방안을 모색하고자 합니다.

---

## 🏃‍ 실행 방법

### 프로젝트 실행 방법 (Docker Compose)

이 프로젝트는 데이터베이스를 제외한 모든 서비스를 Docker Compose로 실행할 수 있습니다. 데이터베이스는 개인 환경에 맞춰 설정해주세요.

1. **Docker 설치**

   Docker가 설치되어 있지 않다면, [Docker 공식 사이트](https://www.docker.com/)에서 설치하세요.

2. **프로젝트 클론**

    ```bash
    git clone https://github.com/FlashSaleF/backend.git
    cd backend
    ```

3. **.env 파일 설정**

   `.env.example` 파일을 복사해서 `.env` 파일을 생성한 후, 필요한 환경 변수를 설정합니다. 데이터베이스와 관련된 변수는 개인 DB 정보로 수정하세요.

    ```bash
    cp .env.example .env
    ```

4. **권한 부여**

    ```bash
    chmod +x build_and_run.sh
    ```

5. **스크립트 실행**

    ```bash
    ./build_and_run.sh
    ```

6. **프로젝트 실행**

---

## 🛠️ 인프라 설계도

### 서비스 아키텍처

<img src="https://github.com/FlashSaleF/backend/blob/dev/docs/images/service%20architecture.jpg?raw=true">



### 인프라 설계도

<img src="https://github.com/FlashSaleF/backend/blob/dev/docs/images/infrastructure.jpg?raw=true">

---

## 🚗 주요 기능

- [⭐ 비동기 처리와 이벤트 기반 대용량 주문 처리](https://spot-decade-fee.notion.site/1264f92830ab80f7b6bed71f7b2144d5)
- [⭐ Redis 분산락을 통한 동시성 재고 관리](https://creative-crane-389.notion.site/Redis-128707d279cc8132ad26e1bfd9c78140)
- [플래시 세일 알림 메일 발송](https://creative-crane-389.notion.site/128707d279cc810184b5d013b5ded7c4)
- [모니터링](https://creative-crane-389.notion.site/128707d279cc81e4bfa4c1590e86e710)
- [Redis 캐싱](https://creative-crane-389.notion.site/Redis-128707d279cc8185b2f4d97b71e86a9c)
- [플래시 세일 관리](https://creative-crane-389.notion.site/128707d279cc81b4aeddd30fd4bb1ebc)

---

## 🔨 적용 기술

| 기술명                   | 선택 근거/목적                                                                                                                                          |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| Redis                 | 부하를 줄이기 위한 캐싱, 동시성 제어를 위한 Redis Lock을 적용하기 위해 사용                                                                                                  |
| Prometheus            | 모니터링 및 성능 지표 수집을 위해 사용                                                                                                                            |
| Grafana               | Prometheus가 수집한 데이터를 시각화 하기 위해 사용                                                                                                                 |
| Kafka                 | 주문 처리, 재고 관리 등 이벤트 기반의 비동기 처리 및 실시간 데이터 흐름을 안정적으로 구현하기 위해 선택                                                                                      |
| GitHub Actions        | 자동화된 CI/CD 파이프라인을 통한 배포 프로세스의 신속성과 일관성을 보장하기 위해 선택                                                                                                |
| Swagger               | API 문서화를 통해 서비스 인터페이스의 효율적인 관리와 원활한 협업을 위해 사용                                                                                                     |
| Docker                | 컨테이너 기반으로 일관된 개발 및 배포 환경을 제공하여 환경 차이에 의한 문제를 최소화하고, 확장 가능한 마이크로서비스 아키텍처를 지원하기 위해 도입                                                               |
| Spring Security & JWT | Spring Security의 통합된 보안 관리 기능을 통해 인증 및 인가를 효율적으로 처리할 수 있기 때문에 도입. 추가로 JWT를 통해 stateless하게 사용자 인증 정보를 안전하게 검증하고, 확장성 있는 시스템 아키텍처를 유지할 수 있기 때문에 도입. |
| Eureka & Gateway      | 마이크로서비스 아키텍처에서 서비스 등록 및 발견 기능을 제공하여 각 서비스가 서로를 쉽게 찾고 통신할 수 있도록 지원. 이를 통해 서비스 간의 의존성을 최소화하고, 시스템의 확장성과 유연성을 높여 더 나은 장애 처리 및 로드 밸런싱을 가능하게 하므로 도입.   |
| Jmeter                | 분산 환경에서의 성능 및 부하 테스트를 위해 사용                                                                                                                       |
| open api              | PG사 결제 연동을 위한 아임포트 api 사용                                                                                                                         |
| Mulite module         | 각 마이크로서비스를 독립적인 모듈로 분리하여 관리할 수 있게 해 코드의 재사용성과 유지보수성을 향상시킴. 이 구조를 통해 각 모듈이 독립적으로 배포되고 개발될 수 있어 팀 간의 협업이 용이해지며, 서비스 간의 경계를 명확히 할 수 있음.              |

## 🤔 기술적 의사결정

- [Gateway에서 캐싱된 Access토큰 블랙리스트 및 화이트리스트 조회](https://creative-crane-389.notion.site/Gateway-Access-128707d279cc8197a074f9c1ad0f28c2)
- [대규모 트래픽에서 효율적인 락 관리](https://creative-crane-389.notion.site/128707d279cc818983eec6578e3d7a6d)
- [메일 발송을 위한 동적 스케줄러 구성](https://creative-crane-389.notion.site/128707d279cc81c5b17cf46059782498)
- [아키텍처](https://creative-crane-389.notion.site/128707d279cc81a8a5b6cac32605c527)
- [Kafka를 이용한 주문 생성](https://creative-crane-389.notion.site/Kafka-128707d279cc8141a020ca70790739a2)

---

## ⚽ 트러블슈팅

[Jmeter 부하테스트 ](https://spot-decade-fee.notion.site/Jmeter-1264f92830ab809dafadf52b1b7885d2)

[UUID 검증](https://creative-crane-389.notion.site/UUID-128707d279cc81fc8d4cc8968d7c8096)

[Feignclient 에러 핸들링](https://creative-crane-389.notion.site/Feignclient-128707d279cc814cb275dd112d4b2404)

[재고에 대한 분산 락 처리의 변경](https://creative-crane-389.notion.site/128707d279cc819bb343d4fdaf545e63)

---

## 👨‍👩‍👧‍👦 CONTRIBUTORS

| 팀원명 | 포지션 | 담당(개인별 기여점)                                                                                                                                                                                                                                                                                                                                                                                                                                                              | 깃허브 링크                                                         |
|-----|-----|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------|
| 이수정 | 리더  | ▶ 서비스 로직 구현 <br> - Vendor / Product 서비스의 CRUD 구현 <br> - Redis 분산 락을 이용한 재고 동시성 문제 해결 <br> - Kafka 토픽을 통한 상품 데이터 변경 이벤트 발행 <br> - FeignClient 예외 처리 <br> ▶ 배포 및 자동화 <br> - GitHub Actions를 활용한 CI/CD 구축 <br> - 서비스의 Docker 컨테이너화 <br> ▶ API 관리 및 문서화 <br> - Swagger 적용                                                                                                                                                                                                    | [https://github.com/Krystal-13](https://github.com/Krystal-13) |
| 김남혁 | 부리더 | ▶ User 서비스 <br> - CRUD <br> ▶ Auth 서비스 <br> - 토큰 발급 및 관리(Access-블랙/화이트, Refresh-화이트) <br> - 로그인, 로그아웃, 토큰 자동 재발급 <br> - 토큰 유효성 검사 <br> ▶ Alarm 서비스 <br> - 세일 상품 등록 시 메일 발송 스케줄러 구성 <br> - 사용자 알람 설정 시 메일 발송, 조회 <br> - 분산환경에서 중복 발송 방지를 위해 분산 락 적용 <br> ▶ Gateway <br> - Auth에서 저장한 토큰 캐싱 데이터 조회 <br> - 인증 절차 이후 각 서비스로 라우팅 <br> ▶ Eureka <br> - Eureka 서버 구성 및 각 서비스 Client 등록 <br> ▶ Base 모듈(Security + DB) <br> - Security 관련 설정 <br> - 엔드포인트 도달 전 SecurityContext 생성 | [https://github.com/knh9612](https://github.com/knh9612)       |
| 김정수 | 맴버  | ▶ flashsale 서비스 <br> - 스케쥴러를 통한 세일 시작, 종료 <br> - 진행 중인 세일 상품 캐싱 처리                                                                                                                                                                                                                                                                                                                                                                                                       | [https://github.com/dnjawm19](https://github.com/dnjawm19)     |
| 이성원 | 멤버  | ▶ Order 서비스 <br> - 주문, 결제 기능 구현 <br> - Kafka 이벤트 기반의 비동기 처리 <br> - 결제 PG사 연동 <br> ▶ Base 모듈(Exception) <br> - 전역 예외 처리 설정 <br> ▶ 부하 테스트 <br> - Jmeter 이용한 시스템 성능 및 부하 테스트                                                                                                                                                                                                                                                                                                | [https://github.com/lsw71311](https://github.com/lsw71311)     |
