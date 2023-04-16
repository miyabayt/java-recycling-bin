package com.bigtreetc.recycling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.bigtreetc.recycling")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
