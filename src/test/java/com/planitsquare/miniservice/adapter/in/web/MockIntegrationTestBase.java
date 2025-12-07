package com.planitsquare.miniservice.adapter.in.web;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Mock을 사용하는 통합 테스트를 위한 기본 클래스.
 *
 * <p>모든 통합 테스트는 이 클래스를 상속받아 Spring Boot Test 컨텍스트를 공유합니다.
 * 이를 통해 테스트 실행 시 컨텍스트가 여러 번 재시작되는 것을 방지합니다.
 *
 * @since 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class MockIntegrationTestBase {
}
