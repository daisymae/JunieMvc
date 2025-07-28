package com.cherylorcutt.juniemvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class JunieMvcApplicationTests {

  @Test
  void contextLoads() {
    // This test verifies that the application context loads successfully
    // No assertions needed - test will fail if context fails to load
  }

}