# CLAUDE.md

## Project Overview
Spring Boot 4.0.0 외부 API 두 개만으로 최근 **5 년(2020 ~ 2025)** 의 전 세계 공휴일 데이터를 **저장·조회·관리** 프로젝트. 
헥사고날 아키텍처와 DDD 전술적 패턴을 적용한 백엔드 시스템.
- Java 21
- Gradle build system
- Spring Data JPA
- Spring REST Client
- Lombok for boilerplate reduction
- H2 console (presumably for in-memory database during development)

**설치된 도구:**
- GitHub CLI (`gh`) - 이슈 생성 및 PR 요청 시 활용 가능

**Commands:**
```bash
./gradlew bootRun          # 개발 서버
./gradlew test             # 전체 테스트
./gradlew clean build      # 클린 빌드
```

---

## Architecture: Hexagonal (Ports & Adapters)

### Layer Structure
```text
src/main/java/com/argo/
├── domain/              # 순수 비즈니스 로직 (프레임워크 독립)
│   ├── model/          # Entity, Value Object, Aggregate
│   └── service/        # Domain Service (도메인 간 협업)
├── application/         # Use Case 오케스트레이션
│   ├── port/
│   │   ├── in/         # Use Case 인터페이스
│   │   └── out/        # Repository, External API 인터페이스
│   └── service/        # Application Service (Use Case 구현)
└── adapter/            # 외부 세계 연결
    ├── in/web/        # REST Controller
    └── out/persistence/ # JPA Repository 구현
```

### Dependency Rule
- **Domain** → 의존성 없음 (순수 Java)
- **Application** → Domain만 의존
- **Adapter** → Application, Domain 의존
- **역방향 의존 절대 금지**

### Interaction Flow
```text
Controller → Use Case (Port) → Application Service 
  → Domain Service → Domain Model → Repository (Port) → JPA Adapter
```

---

## DDD Tactical Patterns

### Entity
- 고유 식별자로 구분
- 비즈니스 로직 캡슐화
- **금지**: Anemic Model (getter/setter만 존재)

### Value Object
- 불변(immutable), 자가 검증
```java
@Value @Builder
public class Money {
    BigDecimal amount;
    Currency currency;
}
```

### Aggregate
- 일관성 경계, Root를 통해서만 접근
- **다른 Aggregate는 ID로만 참조** (직접 참조 금지)
- Repository는 Aggregate Root당 1개

### Repository
- **인터페이스**: `application/port/out`에 정의
- **구현체**: `adapter/out/persistence`에 배치

### Domain Service
- 여러 Aggregate에 걸친 비즈니스 로직
- **Application Service에서 import하여 사용**
```java
// domain/service/PricingService.java
public class PricingService {
    public Money calculateDiscount(Order order, Customer customer) { }
}

// application/service/OrderApplicationService.java
@Service
@RequiredArgsConstructor
public class OrderApplicationService implements CreateOrderUseCase {
    private final PricingService pricingService;  // Domain Service 주입
    
    @Transactional
    public OrderId createOrder(CreateOrderCommand cmd) {
        Money discount = pricingService.calculateDiscount(order, customer);
        // ...
    }
}
```

---

## Code Standards (Google Java Style)

### Formatting
- 들여쓰기: 2 spaces
- 컬럼 제한: 100자
- K&R 스타일 중괄호

### Naming
- 패키지: `lowercase`
- 클래스: `UpperCamelCase`
- 메서드/변수: `lowerCamelCase`
- 상수: `UPPER_SNAKE_CASE`
- **테스트 메서드**: `한글_snake_case`

### Core Rules
- Wildcard import 금지
- `@Override` 필수
- Optional 적극 활용 (null 반환 금지)
- Stream API 선호
- 중복 제거 우선
- 의도를 명확히 표현하는 네이밍

### Javadoc
- **프로덕션 코드**: 한글 (가독성)
- **테스트 코드**: 한글 (가독성)
- 모든 public/protected 요소 필수

```java
/**
 * Calculates order total with customer discounts.
 * 
 * @param order the order, must not be {@code null}
 * @param customer the customer, must not be {@code null}
 * @return calculated total amount
 * @throws InvalidOrderException if order is invalid
 * @since 1.0
 */
public Money calculateTotal(Order order, Customer customer) { }
```

---

## TDD & Tidy First Workflow

### Core Principles
- **Always follow TDD cycle**: Red → Green → Refactor
- **Write one test at a time**, make it run, then improve structure
- **Run all tests** after each change (except long-running tests)
- **Separate structural changes from behavioral changes** (Tidy First)

### Red-Green-Refactor Cycle
1. **RED**: 실패하는 테스트 작성
    - 작은 단위의 기능 하나만 테스트
    - 명확하고 정보성 있는 실패 메시지
    - 테스트 이름으로 행동 설명 (`유효한_주문_생성시_주문ID를_반환한다`)

2. **GREEN**: 최소한의 코드로 테스트 통과
    - "동작할 수 있는 가장 단순한 해법" 사용
    - 중복 코드 허용 (일단 통과시키기)
    - 모든 테스트 실행하여 확인

3. **REFACTOR**: 중복 제거 및 구조 개선
    - 테스트가 통과한 상태에서만 리팩토링
    - 한 번에 하나의 리팩토링만 수행
    - 각 리팩토링 후 테스트 실행

### Test Structure
```java
@DisplayName("주문 서비스 테스트")
class OrderServiceTest {
    
    @Test
    @DisplayName("유효한 주문 생성 시 주문 ID를 반환한다")
    void 유효한_주문_생성시_주문ID를_반환한다() {
        // Given (준비)
        CreateOrderRequest request = ...;
        
        // When (실행)
        OrderId orderId = orderService.createOrder(request);
        
        // Then (검증)
        assertThat(orderId).isNotNull();
    }
}
```

### Tidy First Approach

**Change Separation:**
- **STRUCTURAL CHANGES** (구조적 변경): 동작 변경 없이 코드 재배치
    - 리네이밍 (변수, 메서드, 클래스)
    - 메서드 추출/인라인
    - 패키지 이동
    - 코드 포맷팅

- **BEHAVIORAL CHANGES** (행동 변경): 실제 기능 추가/수정
    - 새로운 기능 추가
    - 버그 수정
    - 로직 변경

**Rules:**
- **절대 섞지 않기**: 구조적 변경과 행동 변경을 같은 커밋에 포함 금지
- **구조 먼저**: 둘 다 필요한 경우 구조적 변경을 먼저 수행
- **검증 필수**: 구조적 변경 전후로 테스트 실행하여 동작 불변 확인

### Commit Discipline

**Commit 조건 (모두 충족 시에만):**

1. **[필수]** 모든 테스트 통과
2. **[필수]** 컴파일/린터 경고 0개
3. **[필수]** 단일 논리적 작업 단위
4. **[필수]** 커밋 메시지에 변경 유형 명시 (structural/behavioral)

**Commit 전략:**
- 작고 빈번한 커밋 선호
- 구조적 변경과 행동 변경 분리
- 각 커밋 후 `./gradlew test` 실행

**Commit Message Format:**
```text
feat(domain): Add Order aggregate validation logic
test(domain): Add Order creation test cases
refactor(structural): Extract OrderValidator from Order
refactor(structural): Rename calculatePrice to calculateTotal
```

### Coverage Requirements
- **Domain Model**: 100% (핵심 비즈니스 로직)
- **Application Service**: 90%+
- **Adapter**: 70%+

---

## Implementation Pattern

### Feature Development Flow
```text
For each feature:
1. Write failing test (Domain Model)
2. Implement minimum code to pass
3. Run all tests (Green)
4. Refactor if needed (structural changes)
5. Commit structural changes separately
6. Write next test (Application Service)
7. Implement Use Case
8. Run all tests (Green)
9. Commit behavioral changes separately
10. Write Integration Test (Adapter)
11. Implement Adapter
12. Run all tests (Green)
13. Refactor & Commit
```

### Layer Implementation Order
1. **Domain Model** (Entity, VO, Port 인터페이스)
    - Domain Test 작성 및 통과

2. **Application Service** (Use Case)
    - Application Test 작성 및 통과

3. **Adapter** (Controller, Repository)
    - Integration Test 작성 및 통과

### Code Quality Checklist
- [ ] 중복 제거 완료
- [ ] 의도가 명확한 네이밍
- [ ] 의존성 명시적 표현
- [ ] 메서드는 단일 책임
- [ ] 상태와 부작용 최소화
- [ ] 가장 단순한 해법 사용

---

## Guardrails (즉시 ESC)

> **경고**: 다음 항목 발견 시 즉시 작업 중단하고 수정해야 합니다.

### Architecture Violations

**[금지]** Domain 계층 오염
- Domain에 Spring 어노테이션 (`@Service`, `@Component` 등)
- Domain에 Port 인터페이스 정의 (반드시 Application에 정의)
- Aggregate 간 직접 참조 (ID로만 참조)

**[금지]** 의존성 규칙 위반
- Application Service가 Domain Service 미사용
- 역방향 의존성 (Domain → Application/Adapter)

### Code Quality Violations

**[금지]** 문서화 누락
- Javadoc 없는 public/protected 메서드

**[금지]** 빈약한 도메인 모델
- 테스트 없는 비즈니스 로직
- Anemic Domain Model (getter/setter만 존재)

**[금지]** 잘못된 리팩토링
- 테스트가 실패한 상태에서 리팩토링
- 구조적/행동 변경을 같은 커밋에 포함

### TDD Violations

**[금지]** TDD 원칙 위반
- 테스트 없이 프로덕션 코드 작성
- 여러 테스트를 한 번에 작성
- 테스트 통과 이상의 코드 작성
- 실패하는 테스트 무시/비활성화

### Domain Layer Rules

```java
// [절대 금지] - 즉시 제거 필요
@Entity                    // JPA 어노테이션
@Service                   // Spring 어노테이션
import org.springframework  // Spring Framework import
interface OrderRepository  // Port 인터페이스 (Application에 정의)

// [허용] - 순수 Java만 사용
public class Order { }
public class PricingService { }
public class Money { }
```

---

## Refactoring Guidelines

### When to Refactor
- 테스트가 통과한 후 (GREEN 상태)
- 중복 코드 발견 시
- 의도가 불명확한 코드 발견 시

### How to Refactor
1. 한 번에 하나의 리팩토링만 수행
2. 확립된 리팩토링 패턴 사용 (이름 명시)
3. 각 리팩토링 후 테스트 실행
4. 구조적 변경을 별도 커밋

### Common Refactorings
| Pattern | Description |
|---------|-------------|
| Extract Method | 중복 코드를 별도 메서드로 추출 |
| Rename Variable/Method/Class | 의도를 명확히 하는 이름으로 변경 |
| Move Method/Class | 적절한 클래스/패키지로 이동 |
| Extract Class/Interface | 책임을 분리하여 새 클래스 생성 |
| Inline Method/Variable | 불필요한 간접 참조 제거 |

---

## Example Workflow

```text
Given: 주문 생성 기능 개발

1. [RED] Write failing test
   void 유효한_주문_생성시_주문ID를_반환한다() { }
   → Compile error: Order class doesn't exist

2. [GREEN] Create minimal Order class
   public class Order { }
   → Test compiles but fails

3. [GREEN] Add createOrder method
   public OrderId createOrder(...) { return new OrderId(1L); }
   → Test passes (hardcoded, but passes)

4. [REFACTOR] Extract validation logic
   private void validateOrder(Order order) { }
   → Run tests → All pass
   → Commit: "refactor(structural): Extract order validation"

5. [RED] Add next test
   void 재고가_부족하면_예외를_던진다() { }
   
6. [GREEN] Add stock validation
   if (item.getStock() < quantity) throw new OutOfStockException();
   → Run tests → All pass
   → Commit: "feat(domain): Add stock validation logic"

7. Repeat...
```

---

**핵심 원칙**:
- Domain은 순수하게
- Adapter는 유연하게
- Application은 명확하게
- **항상 테스트 먼저, 구조 변경 분리**
- # 📋 기능 개요

매년 1월 2일 01:00 KST에 전년도·금년도 공휴일 데이터를 자동으로 동기화한다.

## 🎯 목적 및 배경

새해가 되면 전년도와 금년도의 공휴일 데이터를 자동으로 최신 상태로 유지하여, 수동 작업 없이 데이터 정확성을 보장해야 한다.

## 📐 요구사항

### Functional Requirements
- [ ] 매년 1월 2일 01:00 KST에 배치 작업 실행
- [ ] 전년도(예: 2024)와 금년도(예: 2025)의 모든 국가 공휴일 데이터 동기화
- [ ] 배치 실행 결과 로깅 (성공/실패 건수, 소요 시간)

## 📎 참고 자료

**Spring Batch 또는 Spring Scheduler 사용**
```java
@Scheduled(cron = "0 0 1 2 1 ?", zone = "Asia/Seoul")
public void syncHolidaysAutomatically() {
  // 전년도, 금년도 동기화 로직
}
```

**Cron 표현식**
- `0 0 1 2 1 ?` = 매년 1월 2일 01:00
- `zone = "Asia/Seoul"` = KST 기준
 이 기능을 구현할라고 지금까지 작업했는데 컴파일 오류를 잡아주고 javadoc을 작성하지 않은 부분들을 작성해주고 개선할 수 있으면 개선해줘