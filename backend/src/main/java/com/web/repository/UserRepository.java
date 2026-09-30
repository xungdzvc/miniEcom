package com.web.repository;

import com.web.entity.UserEntity; 
import java.time.Instant;
import java.util.List;
import java.util.Optional; 
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository; 

public interface UserRepository extends JpaRepository<UserEntity,Long> { 
    @EntityGraph(attributePaths = {"roles"})
    @Override
    List<UserEntity> findAll();
    UserEntity findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    UserEntity findByGoogleId(String googleId);
    boolean existsByGoogleId(String googleId);

    int countByCreatedAtBetween(Instant start,Instant end);
    
    @EntityGraph(attributePaths = {"roles"})
    Optional<UserEntity> findWithRolesById(Long id);
}
