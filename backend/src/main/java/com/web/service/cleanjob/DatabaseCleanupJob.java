package com.web.service.cleanjob;

import com.web.repository.OrderRepository;
import com.web.repository.RefreshTokenRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import com.web.service.IOrderService;
import com.web.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DatabaseCleanupJob {
    
  private final OrderRepository orderRepository;
  private final IOrderService orderService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final Clock clock;

  @Scheduled(fixedDelay = 60 * 60 * 1000)
  public void expiresPendingOrders() {
    List<Long> orderIds = orderRepository.findExpiredPendingOrderIds(clock.instant());
    for(Long id :orderIds){
      orderService.makeExpiredOrder(id);
    }
  }

  @Scheduled(fixedDelay = 60 * 60 * 1000)
  @Transactional
  public void cleanExpiredRefreshToken() {
    refreshTokenRepository.deleteByExpiredAtBefore(clock.instant());
  }
  
}
