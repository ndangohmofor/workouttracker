package com.meufty.workoutplanner.repository;

import com.meufty.workoutplanner.model.UserProfile;
import com.meufty.workoutplanner.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findUserProfileByUserId(Long userId);
    Optional<List<UserProfile>> findUserProfileByUserRole(UserRole role);
}
