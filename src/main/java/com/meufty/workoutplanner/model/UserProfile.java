package com.meufty.workoutplanner.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_profile", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class UserProfile {
    @Id
    @SequenceGenerator(name = "profile_sequence", sequenceName = "profile_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_sequence")
    private Long id;
    @JoinColumn(name = "id", nullable = false, table = "app_user", unique = true)
    private Long userId;
    private String firstName;
    private String lastName;
    private String preferredName;
    private String username;
    private String goal;
    private UserRole role;
    private byte[] profilePhoto;
}
