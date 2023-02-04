package com.meufty.workoutplanner.repository;

import com.meufty.workoutplanner.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional()
public interface UserRepository extends JpaRepository<MyUser, Long> {
    Optional<MyUser> findByUserName(String userName);

    @Transactional
    @Modifying
    @Query("UPDATE MyUser a " +
    "SET a.active = true WHERE a.userName = ?1")
    int enableUser(String username);
}
