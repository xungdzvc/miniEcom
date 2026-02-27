/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.config;

/**
 *
 * @author ZZ
 */
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class MailAsyncConfig {

  @Bean(name = "mailExecutor")
  public Executor mailExecutor(
      @Value("${app.mail.async.corePoolSize:4}") int core,
      @Value("${app.mail.async.maxPoolSize:8}") int max,
      @Value("${app.mail.async.queueCapacity:500}") int queue
  ) {
    ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
    ex.setCorePoolSize(core);
    ex.setMaxPoolSize(max);
    ex.setQueueCapacity(queue);
    ex.setThreadNamePrefix("mail-");
    ex.initialize();
    return ex;
  }
}