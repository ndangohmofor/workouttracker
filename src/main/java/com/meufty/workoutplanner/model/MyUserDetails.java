package com.meufty.workoutplanner.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class MyUserDetails implements UserDetails {

    private final MyUser myUser;

    public MyUserDetails(MyUser user){
        super();
        this.myUser = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(myUser.getUserRole().name()));
    }

    @Override
    public String getPassword() {
        return myUser.getPassword();
    }

    @Override
    public String getUsername() {
        return myUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return myUser.isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !myUser.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return myUser.isActive();
    }

    @Override
    public boolean isEnabled() {
        return myUser.isEnabled();
    }
}
