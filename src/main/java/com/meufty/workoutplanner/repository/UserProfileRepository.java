package com.meufty.workoutplanner.repository;

import com.meufty.workoutplanner.model.UserProfile;
import com.meufty.workoutplanner.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findUserProfileByUserId(Long userId);
    Optional<UserProfile> findUserProfileByUsername(String username);
    @Query("""
            SELECT id, userId, firstName, lastName, preferredName, goal, role, profilePhoto FROM UserProfile U WHERE U.role = :role
            """)
    Optional<List<UserProfile>> findUserProfileByUserRole(UserRole role);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
UPDATE UserProfile U SET firstName = :firstName, goal = :goal, lastName = :lastName, preferredName = :preferredName, username = :username, profilePhoto = :profilePhoto, role = :role WHERE userId = :userId  
""")
    int updateUserProfileByUserId(
            Long userId,
            String firstName,
            String goal,
            String lastName,
            String preferredName,
            String username,
            byte[] profilePhoto,
            UserRole role
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    UserProfile save(UserProfile userProfile);
}
