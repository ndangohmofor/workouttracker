package com.meufty.workoutplanner.repository;

import com.meufty.workoutplanner.token.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);
    @Query("SELECT c.token FROM ConfirmationToken c WHERE c.myUser.id = ?1")
    Optional<ConfirmationToken> findTokenByUserId(Long userId);
    @Query("SELECT c.confirmedAt FROM ConfirmationToken c where c.id = ?1")
    Optional<String> findByMyUserIdAndConfirmedAtIsNull(Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c " +
    "SET c.confirmedAt = ?2 " +
    "WHERE c.token = ?1")
    int updateConfirmedAt(String token, LocalDateTime confirmedAt);
}
