package com.bigtreetc.recycling;

import com.bigtreetc.recycling.util.FutureUtils;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AppConfig {

  @Bean
  public TaskExecutor taskExecutor() {
    val executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(40);
    executor.setTaskDecorator(FutureUtils::withContext);
    executor.initialize();
    return executor;
  }
}
