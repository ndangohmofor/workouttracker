package com.meufty.workoutplanner.repository;

import com.meufty.workoutplanner.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    List<CheckIn> getCheckInById(Long userId);

    @Query("""
            SELECT C FROM CheckIn C WHERE C.userId = :userId AND C.checkOutTime = NULL 
            """)
    CheckIn getCurrentCheckInById(Long userId);
}
