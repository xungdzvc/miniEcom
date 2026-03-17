package com.web.service.cleanjob;

import com.web.repository.OrderRepository;
import com.web.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DatabaseCleanupJob {
    
  private final OrderRepository orderRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  @Scheduled(fixedDelay = 60 * 60 * 1000)
  @Transactional
  public void expiresPendingOrders() {
    orderRepository.markExpired(LocalDateTime.now());
  }
  
  @Scheduled(fixedDelay = 60 * 60 * 1000)
  @Transactional
  public void cleanExpiredRefreshToken() {
    refreshTokenRepository.deleteByExpiredAtBefore(LocalDateTime.now());
  }
  
}
