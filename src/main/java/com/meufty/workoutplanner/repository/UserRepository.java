package com.meufty.workoutplanner.repository;

import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional()
public interface UserRepository extends JpaRepository<MyUser, Long> {

    Optional<MyUser> findByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE MyUser a " +
    "SET a.active = true WHERE a.username = ?1")
    int enableUser(String username);

    @Query("SELECT mu.id, mu.locked, mu.enabled, mu.username, mu.email, mu.password, mu.userRole FROM MyUser mu WHERE mu.userRole = ?1")
    Optional<List<MyUser>> findMyUserByRole(UserRole userRole);
}
