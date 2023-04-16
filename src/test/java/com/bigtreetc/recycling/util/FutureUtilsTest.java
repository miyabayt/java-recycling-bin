package com.bigtreetc.recycling.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.bigtreetc.recycling.AppConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@SpringBootTest
@ContextConfiguration(classes = {AppConfig.class})
class FutureUtilsTest {

  @Autowired TaskExecutor taskExecutor;

  @BeforeAll
  static void beforeAll() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Test
  void test1() {
    MDC.put("test1", "1234");
    taskExecutor.execute(
        () -> {
          assertThat(MDC.get("test1")).isEqualTo("1234");
        });
  }
}
