package com.meufty.workoutplanner.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "app_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class MyUser {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;
    private String username;
    private String email;
    private String password;
    private boolean active;
    private boolean enabled;
    private boolean locked = false;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    @OneToMany(mappedBy = "app_user")
    private List<Token> tokens;

    public MyUser(String username,
                  String email,
                  String password,
                  UserRole userRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.active = true;
        this.enabled = true;
        this.userRole = userRole;
    }
}
