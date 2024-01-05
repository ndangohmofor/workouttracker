package com.meufty.workoutplanner.repository;

import com.meufty.workoutplanner.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    List<LocalDateTime> getCheckInByIdIs(Long userId);
}
