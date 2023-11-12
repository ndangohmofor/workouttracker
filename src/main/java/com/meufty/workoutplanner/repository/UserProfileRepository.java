package com.meufty.workoutplanner.repository;

import com.meufty.workoutplanner.model.UserProfile;
import com.meufty.workoutplanner.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findUserProfileByUserId(Long userId);
    @Query("""
            SELECT id, userId, firstName, lastName, preferredName, goal, role, profilePhoto FROM UserProfile U WHERE U.role = :role
            """)
    Optional<List<UserProfile>> findUserProfileByUserRole(UserRole role);
}
