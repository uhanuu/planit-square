# Holiday Service

전 세계 공휴일 데이터를 관리하는 REST API 서비스입니다.
외부 API로부터 최근 5년(2021-2025) 공휴일 데이터를 수집하고, 다양한 조건으로 검색할 수 있습니다.

## 기술 스택

- **Java 21**
- **Spring Boot 4.0.0**
- **Spring Data JPA** - 영속성 관리
- **QueryDSL** - 동적 쿼리
- **H2 Database** - 인메모리 데이터베이스
- **Lombok** - 보일러플레이트 코드 제거
- **Springdoc OpenAPI** - API 문서화

## 아키텍처

이 프로젝트는 **헥사고날 아키텍처(Hexagonal Architecture)**와 **DDD 전술적 패턴**을 적용하여 설계되었습니다.

### 레이어 구조

```
src/main/java/com/planitsquare/miniservice/
├── domain/              # 순수 비즈니스 로직
│   ├── model/          # Entity, Aggregate
│   └── vo/             # Value Object
├── application/         # Use Case 오케스트레이션
│   ├── port/
│   │   ├── in/         # Use Case 인터페이스
│   │   └── out/        # Repository 인터페이스
│   └── service/        # Application Service
└── adapter/            # 외부 세계 연결
    ├── in/web/        # REST Controller
    └── out/           # JPA Repository, External API
        └── persistence/
```

## 빌드 & 실행 방법

### 1. 사전 요구사항

- Java 21 이상
- Gradle 7.x 이상

### 2. 프로젝트 클론

```bash
git clone <repository-url>
cd mini-service
```

### 3. 빌드

```bash
# 클린 빌드
./gradlew clean build

# 테스트 제외 빌드
./gradlew clean build -x test
```

### 4. 실행

```bash
# 개발 서버 실행
./gradlew bootRun

# 또는 JAR 파일 실행
java -jar build/libs/mini-service-0.0.1-SNAPSHOT.jar
```

### 5. 애플리케이션 실행 확인

```bash
# Health check
curl http://localhost:8080/actuator/health

# 또는 브라우저에서
http://localhost:8080/swagger-ui.html
```

## REST API 명세

### 엔드포인트

#### 1. 공휴일 검색 API

**Endpoint:** `GET /api/v1/holidays`

**설명:** 다양한 조건으로 공휴일을 검색하고 페이징 처리된 결과를 반환합니다.

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `year` | Integer | No | 연도 필터 | 2024 |
| `countryCode` | String | No | 국가 코드 (ISO 3166-1 alpha-2) | KR, US, JP |
| `from` | Date | No | 시작일 (yyyy-MM-dd) | 2024-01-01 |
| `to` | Date | No | 종료일 (yyyy-MM-dd) | 2024-12-31 |
| `type` | String | No | 공휴일 타입 | PUBLIC, BANK, OPTIONAL |
| `name` | String | No | 공휴일 이름 검색 (부분 일치) | 설날, New Year |
| `page` | Integer | No | 페이지 번호 (0-based, 기본값: 0) | 0 |
| `size` | Integer | No | 페이지 크기 (기본값: 20) | 20 |
| `sort` | String | No | 정렬 조건 (field,direction) | date,asc |

**정렬 가능 필드:**
- `date` - 날짜 기준 정렬
- `name` - 이름 기준 정렬
- `country` - 국가 코드 기준 정렬

**Response:**

```json
{
  "content": [
    {
      "id": 1,
      "countryCode": "KR",
      "countryName": "대한민국",
      "localName": "설날",
      "name": "New Year's Day",
      "date": "2024-01-01",
      "fixed": true,
      "global": true,
      "launchYear": 2021,
      "types": ["Public"],
      "applicableRegions": []
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "empty": false,
      "unsorted": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 100,
  "totalPages": 5,
  "last": false,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "empty": false,
    "unsorted": false
  },
  "numberOfElements": 20,
  "first": true,
  "empty": false
}
```

### 사용 예시

#### 1. 기본 검색 (전체 공휴일)

```bash
curl -X GET "http://localhost:8080/api/v1/holidays?page=0&size=20"
```

#### 2. 연도별 검색

```bash
curl -X GET "http://localhost:8080/api/v1/holidays?year=2024"
```

#### 3. 국가별 검색

```bash
curl -X GET "http://localhost:8080/api/v1/holidays?countryCode=KR"
```

#### 4. 연도 + 국가 검색

```bash
curl -X GET "http://localhost:8080/api/v1/holidays?year=2024&countryCode=KR"
```

#### 5. 날짜 범위 검색

```bash
curl -X GET "http://localhost:8080/api/v1/holidays?from=2024-01-01&to=2024-06-30"
```

#### 6. 공휴일 이름 검색

```bash
curl -X GET "http://localhost:8080/api/v1/holidays?name=설날&countryCode=KR"
```

#### 7. 타입별 검색

```bash
curl -X GET "http://localhost:8080/api/v1/holidays?type=PUBLIC&countryCode=US"
```

#### 8. 정렬 (날짜 내림차순)

```bash
curl -X GET "http://localhost:8080/api/v1/holidays?year=2024&sort=date,desc"
```

#### 9. 복합 조건 검색 + 페이징

```bash
curl -X GET "http://localhost:8080/api/v1/holidays?year=2024&countryCode=KR&type=PUBLIC&page=0&size=10&sort=date,asc"
```

## API 문서 확인 방법

### 1. Swagger UI

애플리케이션 실행 후 브라우저에서 다음 URL에 접속:

```
http://localhost:8080/swagger-ui.html
```

또는

```
http://localhost:8080/swagger-ui/index.html
```

Swagger UI에서 다음 기능을 사용할 수 있습니다:
- API 엔드포인트 목록 확인
- 각 API의 파라미터 및 응답 스키마 확인
- **Try it out** 버튼으로 직접 API 테스트
- 요청/응답 예시 확인

### 2. OpenAPI JSON

OpenAPI 3.0 스펙의 JSON 형식 문서:

```
http://localhost:8080/v3/api-docs
```

### 3. OpenAPI YAML

YAML 형식으로 받기:

```
http://localhost:8080/v3/api-docs.yaml
```

## 데이터베이스 스키마

### 주요 테이블

#### 1. `holiday` - 공휴일 정보

| Column | Type | Description |
|--------|------|-------------|
| holiday_id | BIGINT | 공휴일 ID (PK) |
| country_code | VARCHAR(2) | 국가 코드 (FK) |
| local_name | VARCHAR(200) | 현지 이름 |
| name | VARCHAR(200) | 영문 이름 |
| date | DATE | 공휴일 날짜 |
| fixed | BOOLEAN | 고정 휴일 여부 |
| global | BOOLEAN | 전역 휴일 여부 |
| launch_year | INTEGER | 휴일 시작 연도 |

#### 인덱스

성능 최적화를 위한 복합 인덱스:

- `idx_holiday_date` - 날짜 검색
- `idx_holiday_country` - 국가 검색
- `idx_holiday_country_date` - 국가 + 날짜 복합 검색
- `idx_holiday_date_country` - 날짜 + 국가 복합 검색

## H2 Console 접속

개발 환경에서 H2 데이터베이스 콘솔에 접속하여 데이터를 확인할 수 있습니다.

```
http://localhost:8080/h2-console
```

**접속 정보:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (없음)

## 테스트

### 전체 테스트 실행

```bash
./gradlew test
```

### 특정 테스트 실행

```bash
# Application Service 테스트
./gradlew test --tests HolidaySearchServiceTest

# 단위 테스트만 실행
./gradlew test --tests "*Test"
```

## 프로젝트 구조

```
mini-service/
├── build.gradle                 # Gradle 빌드 설정
├── CLAUDE.md                    # 프로젝트 가이드라인
├── README.md                    # 프로젝트 문서
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/planitsquare/miniservice/
    │   │       ├── domain/              # 도메인 계층
    │   │       ├── application/         # 애플리케이션 계층
    │   │       └── adapter/             # 어댑터 계층
    │   └── resources/
    │       └── application.yml          # 설정 파일
    └── test/
        └── java/                        # 테스트 코드
```

## 주요 기능

### 1. 동적 쿼리 지원 (QueryDSL)

- 여러 검색 조건을 조합하여 유연한 검색 가능
- 컴파일 타임 타입 안정성 보장
- 복잡한 조건문도 가독성 있게 작성

### 2. 페이징 처리

- Spring Data의 `Page` 객체 활용
- 페이지 번호, 크기, 전체 개수 등 메타데이터 제공
- 대량 데이터도 효율적으로 처리

### 3. 정렬 지원

- 날짜, 이름, 국가 코드 기준 정렬
- 오름차순/내림차순 선택 가능

### 4. 인덱스 최적화

- 자주 사용되는 검색 조건에 대한 복합 인덱스 설정
- 쿼리 성능 향상
