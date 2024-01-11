package com.meufty.workoutplanner.repository;

import com.meufty.workoutplanner.model.CheckIn;
import com.meufty.workoutplanner.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    List<CheckIn> getCheckInById(Long userId);

    @Query("""
            SELECT c FROM CheckIn c WHERE c.userId = :userId AND c.checkOutTime = NULL
            """)
    CheckIn getCurrentCheckInById(Long userId);
    @Query("""
            SELECT max(c.checkInTime) FROM CheckIn c where c.userId = :myUser.getId()
            """)
    LocalDateTime getFirstCheckinDateById(MyUser myUser);
}
