package com.meufty.workoutplanner.repository;

import com.meufty.workoutplanner.model.MyUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional()
public interface UserRepository extends JpaRepository<MyUserDetails, Long> {
    Optional<MyUserDetails> findByUserName(String userName);
}
