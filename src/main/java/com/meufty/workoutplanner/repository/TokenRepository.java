package com.meufty.workoutplanner.repository;

import com.meufty.workoutplanner.model.Token;
import com.meufty.workoutplanner.model.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query("""
            select t from Token t inner join MyUser u on t.user.id = u.id
            where u.id = :userId and (t.expired = false or t.revoked = false )
            """)
    List<Token> findAllValidTokensByUser(Long userId);

    @Query("""
            select t from Token  t inner join MyUser u on t.user.id = u.id
            where u.id = :userId and t.tokenType = 'BEARER' and (t.expired = false or t.revoked = false )
            """)
    List<Token> findAllValidAccessTokensByUser(Long userId);

    Optional<Token> findByToken(String token);
}
