# 공휴일 데이터 서비스 테스트 계획

## 개요
이 문서는 공휴일 데이터 서비스의 모든 테스트 케이스를 정의합니다.
TDD (Red-Green-Refactor) 방식으로 개발하며, 각 테스트 완료 시 체크박스를 표시합니다.

---

## Domain Model 테스트 (19개)

### CountryCode (Value Object) - 4개

- [x] 유효한_2자리_코드로_CountryCode를_생성한다
  - Given: 유효한 2자리 국가 코드 "US"
  - When: CountryCode 생성
  - Then: 성공적으로 생성됨

- [x] 소문자_코드를_대문자로_변환한다
  - Given: 소문자 국가 코드 "us"
  - When: CountryCode 생성
  - Then: "US"로 변환되어 저장됨

- [x] null_코드로_생성시_예외를_던진다
  - Given: null 국가 코드
  - When: CountryCode 생성 시도
  - Then: IllegalArgumentException 발생

- [x] 2자리가_아닌_코드로_생성시_예외를_던진다
  - Given: 1자리 또는 3자리 이상의 코드 ("U", "USA")
  - When: CountryCode 생성 시도
  - Then: IllegalArgumentException 발생

### HolidayDate (Value Object) - 3개

- [x] LocalDate로_HolidayDate를_생성한다
  - Given: LocalDate "2024-07-04"
  - When: HolidayDate 생성
  - Then: 성공적으로 생성됨

- [x] 생성시_year를_자동으로_추출한다
  - Given: LocalDate "2024-07-04"
  - When: HolidayDate 생성
  - Then: year 필드가 2024로 설정됨

- [x] 날짜가_범위_내에_있는지_확인한다
  - Given: HolidayDate "2024-07-04"
  - When: isBetween("2024-01-01", "2024-12-31") 호출
  - Then: true 반환

### Country (Aggregate Root) - 4개

- [ ] 유효한_국가코드와_이름으로_Country를_생성한다
  - Given: CountryCode "US", name "United States"
  - When: Country 생성
  - Then: 성공적으로 생성됨

- [ ] Country에_Holiday를_추가한다
  - Given: Country 객체
  - When: addHoliday() 호출
  - Then: holidays 리스트에 추가됨

- [ ] Country에서_날짜와_이름으로_Holiday를_찾는다
  - Given: Country에 여러 Holiday 추가
  - When: findHoliday(date, name) 호출
  - Then: 해당 Holiday 반환

- [ ] Country의_특정_연도_SyncStatus를_업데이트한다
  - Given: Country 객체
  - When: updateSyncStatus(2024, SUCCESS) 호출
  - Then: 해당 연도의 SyncStatus가 업데이트됨

### Holiday (Entity) - 4개

- [ ] 유효한_정보로_Holiday를_생성한다
  - Given: 유효한 날짜, 이름, 타입 등
  - When: Holiday 생성
  - Then: 성공적으로 생성됨

- [ ] Holiday가_특정_날짜인지_확인한다
  - Given: Holiday "2024-07-04"
  - When: isOnDate(LocalDate.of(2024, 7, 4)) 호출
  - Then: true 반환

- [ ] Holiday가_전국_공휴일인지_확인한다
  - Given: global=true인 Holiday
  - When: isGlobalHoliday() 호출
  - Then: true 반환

- [ ] Holiday_정보를_업데이트한다
  - Given: 기존 Holiday
  - When: update(newHoliday) 호출
  - Then: 정보가 업데이트됨

### HolidayDomainService - 3개

- [ ] 존재하지_않는_Holiday는_삽입한다
  - Given: Country에 해당 Holiday 없음
  - When: upsertHoliday() 호출
  - Then: 새 Holiday 삽입됨

- [ ] 이미_존재하는_Holiday는_업데이트한다
  - Given: Country에 동일한 날짜/이름의 Holiday 존재
  - When: upsertHoliday() 호출
  - Then: 기존 Holiday 업데이트됨

- [ ] 동일한_날짜와_이름을_가진_Holiday를_찾는다
  - Given: Country에 여러 Holiday
  - When: findHoliday(date, name) 호출
  - Then: 일치하는 Holiday 반환

### SyncStatusDomainService - 2개

- [ ] 새로운_SyncStatus를_생성한다
  - Given: Country와 year
  - When: createSyncStatus() 호출
  - Then: 새 SyncStatus 생성됨

- [ ] 기존_SyncStatus를_업데이트한다
  - Given: 기존 SyncStatus
  - When: updateStatus() 호출
  - Then: 상태가 업데이트됨

---

## Application Service 테스트 (12개)

### QueryHolidayService - 4개

- [ ] 유효한_국가코드와_연도로_공휴일을_조회한다
  - Given: Repository에 US/2024 공휴일 존재
  - When: getHolidaysByCountryAndYear("US", 2024)
  - Then: 해당 공휴일 리스트 반환

- [ ] 존재하지_않는_국가코드로_조회시_빈_리스트를_반환한다
  - Given: Repository에 XX 국가 없음
  - When: getHolidaysByCountryAndYear("XX", 2024)
  - Then: 빈 리스트 반환

- [ ] 날짜_범위로_공휴일을_검색한다
  - Given: Repository에 여러 공휴일 존재
  - When: searchHolidaysByDateRange("2024-01-01", "2024-12-31")
  - Then: 범위 내 공휴일 반환

- [ ] 잘못된_날짜_범위로_검색시_예외를_던진다
  - Given: to < from인 날짜 범위
  - When: searchHolidaysByDateRange() 호출
  - Then: IllegalArgumentException 발생

### SyncHolidayService - 8개

- [ ] 모든_국가의_공휴일을_동기화한다
  - Given: 외부 API에서 국가 및 공휴일 반환
  - When: syncAllHolidays() 호출
  - Then: 모든 국가의 공휴일이 저장됨

- [ ] 일부_국가_실패시_나머지_국가는_계속_동기화한다
  - Given: 일부 국가 API 호출 실패
  - When: syncAllHolidays() 호출
  - Then: 성공한 국가의 공휴일은 저장됨

- [ ] 동기화_성공시_SyncStatus를_SUCCESS로_업데이트한다
  - Given: 공휴일 동기화 성공
  - When: syncCountryYear() 완료
  - Then: SyncStatus.status = SUCCESS

- [ ] 동기화_실패시_SyncStatus를_FAILED로_업데이트한다
  - Given: API 호출 실패
  - When: syncCountryYear() 실패
  - Then: SyncStatus.status = FAILED

- [ ] 실패한_동기화만_재시도한다
  - Given: FAILED 상태의 SyncStatus 존재
  - When: retryFailedSyncs() 호출
  - Then: 실패한 것만 재시도됨

- [ ] API_호출_실패시_3회_재시도한다
  - Given: API 호출이 계속 실패
  - When: NagerApiPort 호출
  - Then: 총 3회 재시도 후 예외 발생

- [ ] 재시도시_지수_백오프를_적용한다
  - Given: API 호출 실패
  - When: 재시도 수행
  - Then: 1초, 2초, 4초 간격으로 대기

- [ ] 동기화_결과_요약을_반환한다
  - Given: 동기화 완료
  - When: syncAllHolidays() 완료
  - Then: SyncSummary(성공/실패 수) 반환

---

## Controller 통합 테스트 (8개)

### HolidayController - 4개

- [ ] GET_국가_연도로_공휴일_조회시_200_OK를_반환한다
  - Given: DB에 US/2024 공휴일 존재
  - When: GET /api/holidays/US/2024
  - Then: 200 OK, 공휴일 리스트 반환

- [ ] 존재하지_않는_국가_조회시_404_NOT_FOUND를_반환한다
  - Given: DB에 XX 국가 없음
  - When: GET /api/holidays/XX/2024
  - Then: 404 NOT_FOUND

- [ ] GET_날짜_범위로_공휴일_검색시_200_OK를_반환한다
  - Given: DB에 공휴일 존재
  - When: GET /api/holidays/search?from=2024-01-01&to=2024-12-31
  - Then: 200 OK, 공휴일 리스트 반환

- [ ] 잘못된_날짜_범위로_검색시_400_BAD_REQUEST를_반환한다
  - Given: to < from
  - When: GET /api/holidays/search?from=2024-12-31&to=2024-01-01
  - Then: 400 BAD_REQUEST

### SyncController - 4개

- [ ] POST_수동_동기화_트리거시_202_ACCEPTED를_반환한다
  - Given: 애플리케이션 실행 중
  - When: POST /api/sync/trigger
  - Then: 202 ACCEPTED

- [ ] POST_실패한_동기화_재시도시_202_ACCEPTED를_반환한다
  - Given: FAILED 상태의 동기화 존재
  - When: POST /api/sync/retry-failed
  - Then: 202 ACCEPTED

- [ ] 동기화가_비동기로_실행된다
  - Given: POST /api/sync/trigger 호출
  - When: 응답 반환
  - Then: 동기화는 백그라운드에서 계속 실행됨

- [ ] 동기화_진행중_상태를_확인할_수_있다
  - Given: 동기화 진행 중
  - When: SyncStatus 조회
  - Then: IN_PROGRESS 상태 확인 가능

---

## Adapter 통합 테스트 (10개)

### HolidayJpaAdapter - 4개

- [ ] Domain_Holiday를_Entity로_변환하여_저장한다
  - Given: Domain Holiday 객체
  - When: save() 호출
  - Then: HolidayEntity로 변환되어 DB 저장

- [ ] Entity를_Domain_Holiday로_변환하여_조회한다
  - Given: DB에 HolidayEntity 존재
  - When: findById() 호출
  - Then: Domain Holiday로 변환되어 반환

- [ ] 국가코드와_연도로_Holiday를_조회한다
  - Given: DB에 여러 Holiday 존재
  - When: findByCountryCodeAndYear("US", 2024)
  - Then: US/2024 공휴일만 반환

- [ ] 날짜_범위로_Holiday를_조회한다
  - Given: DB에 여러 Holiday 존재
  - When: findByDateBetween(from, to)
  - Then: 범위 내 공휴일만 반환

### NagerApiAdapter - 6개

- [ ] 외부_API에서_국가_목록을_가져온다
  - Given: Nager API 정상 작동
  - When: fetchAvailableCountries() 호출
  - Then: 국가 목록 반환

- [ ] 외부_API에서_공휴일을_가져온다
  - Given: Nager API 정상 작동
  - When: fetchPublicHolidays(2024, "US") 호출
  - Then: US/2024 공휴일 반환

- [ ] API_호출_실패시_재시도한다
  - Given: API 첫 호출 실패, 두 번째 성공
  - When: fetchPublicHolidays() 호출
  - Then: 재시도 후 성공

- [ ] 3회_재시도_후_예외를_던진다
  - Given: API 호출 계속 실패
  - When: fetchPublicHolidays() 호출
  - Then: RetryExhaustedException 발생

- [ ] 재시도_간_대기_시간이_지수적으로_증가한다
  - Given: API 호출 실패
  - When: 재시도 수행
  - Then: 1초 → 2초 → 4초 대기

- [ ] API_응답을_Domain_Model로_변환한다
  - Given: API에서 CountryApiDto 반환
  - When: 변환 수행
  - Then: Domain Country 객체로 변환됨

---

## Edge Case 테스트 (17개)

### 데이터 검증 - 5개

- [ ] 빈_국가_목록을_처리한다
  - Given: API가 빈 배열 반환
  - When: syncAllHolidays() 호출
  - Then: 에러 없이 완료, 0개 동기화

- [ ] 빈_공휴일_목록을_처리한다
  - Given: 특정 국가/연도에 공휴일 없음
  - When: syncCountryYear() 호출
  - Then: 에러 없이 완료, 0개 저장

- [ ] null_필드가_있는_API_응답을_처리한다
  - Given: API 응답에 일부 필드 null
  - When: 데이터 변환
  - Then: 기본값 적용 또는 에러 처리

- [ ] 지역_공휴일_데이터를_처리한다
  - Given: global=false, counties 필드 존재
  - When: Holiday 저장
  - Then: counties 정보 저장됨

- [ ] 여러_타입을_가진_공휴일을_처리한다
  - Given: Holiday에 여러 HolidayType
  - When: 데이터 저장
  - Then: 첫 번째 타입을 primary로 저장

### 동시성 - 3개

- [ ] 동시_동기화_요청을_처리한다
  - Given: 동시에 여러 동기화 요청
  - When: syncAllHolidays() 동시 호출
  - Then: 하나만 실행되거나 순차 처리

- [ ] 동시_Upsert시_중복이_생성되지_않는다
  - Given: 동일한 Holiday 동시 삽입
  - When: upsert 동시 실행
  - Then: 하나만 저장됨 (unique constraint)

- [ ] 데이터베이스_제약조건_위반을_처리한다
  - Given: unique constraint 위반
  - When: 저장 시도
  - Then: 적절한 예외 처리

### 오류 시나리오 - 5개

- [ ] API_404_오류를_처리한다
  - Given: 존재하지 않는 국가 코드
  - When: fetchPublicHolidays("XX", 2024)
  - Then: 적절한 예외 발생 및 로깅

- [ ] API_500_오류를_처리한다
  - Given: 서버 에러
  - When: API 호출
  - Then: 재시도 후 예외 발생

- [ ] 네트워크_타임아웃을_처리한다
  - Given: API 응답 지연
  - When: API 호출
  - Then: timeout 예외 처리

- [ ] 잘못된_JSON_응답을_처리한다
  - Given: API가 잘못된 JSON 반환
  - When: 데이터 파싱
  - Then: 파싱 에러 처리

- [ ] 데이터베이스_연결_실패를_처리한다
  - Given: DB 연결 끊김
  - When: 데이터 저장 시도
  - Then: 연결 에러 처리

### 경계 조건 - 4개

- [ ] 연도_경계를_처리한다_2019to2020
  - Given: 2019-12-31과 2020-01-01 공휴일
  - When: 날짜 범위 검색
  - Then: 두 연도의 공휴일 모두 반환

- [ ] 여러_연도에_걸친_날짜_범위를_처리한다
  - Given: 2020-2025 공휴일
  - When: searchHolidaysByDateRange("2020-01-01", "2025-12-31")
  - Then: 6년간의 모든 공휴일 반환

- [ ] 하루_날짜_범위를_처리한다
  - Given: from = to = "2024-07-04"
  - When: searchHolidaysByDateRange()
  - Then: 해당 날짜의 공휴일만 반환

- [ ] 매우_큰_날짜_범위를_처리한다
  - Given: from = "2000-01-01", to = "2099-12-31"
  - When: searchHolidaysByDateRange()
  - Then: 성능 저하 없이 결과 반환 (pagination 고려)

---

## 테스트 진행 상황

- **Domain Model 테스트**: 7/19 완료
- **Application Service 테스트**: 0/12 완료
- **Controller 통합 테스트**: 0/8 완료
- **Adapter 통합 테스트**: 0/10 완료
- **Edge Case 테스트**: 0/17 완료

**전체**: 7/66 완료 (11%)

---

## 테스트 도구

- **Unit Test Framework**: JUnit 5 (Jupiter)
- **Assertion Library**: AssertJ
- **Mocking Framework**: Mockito
- **Integration Test**: @SpringBootTest
- **Test Coverage**: JaCoCo

---

## 테스트 실행 명령어

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests CountryCodeTest

# 특정 테스트 메서드 실행
./gradlew test --tests CountryCodeTest.유효한_2자리_코드로_CountryCode를_생성한다

# 테스트 커버리지 확인
./gradlew test jacocoTestReport
```

---

## 주의사항

1. **TDD 원칙 준수**: 테스트를 먼저 작성하고 (RED), 최소한의 코드로 통과시킨 후 (GREEN), 리팩토링 (REFACTOR)
2. **테스트 격리**: 각 테스트는 독립적이어야 하며, 다른 테스트에 의존하지 않음
3. **테스트 이름**: 한글 snake_case로 작성하여 의도를 명확히 표현
4. **Given-When-Then**: 모든 테스트는 이 구조를 따름
5. **Mock 최소화**: 가능한 실제 객체 사용, 외부 의존성만 Mock
6. **Coverage 목표**: Domain 100%, Application 90%+, Adapter 70%+
