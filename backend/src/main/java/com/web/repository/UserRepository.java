package com.web.repository;

import com.web.entity.UserEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    UserEntity findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    UserEntity findByEmail(String email);
    UserEntity findByGoogleId(String googleId);
    boolean existsByGoogleId(String googleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    UserEntity findUserById(Long userId);
}
