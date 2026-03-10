package com.web.service.cleanjob;

import com.web.repository.OrderRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DatabaseCleanupJob {
    
  private final OrderRepository orderRepository;

  @Scheduled(fixedDelay = 60_000) // 60s
  @Transactional
  public void expiresPendingOrders() {
    orderRepository.markExpired(LocalDateTime.now());
  }
  
}
