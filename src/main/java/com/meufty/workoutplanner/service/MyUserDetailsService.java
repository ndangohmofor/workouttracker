package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.token.ConfirmationToken;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with userName %s not found";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
    }

    public String signUpUser(MyUser myUser) {
        boolean userExists = userRepository.findByUserName(myUser.getUsername())
                .isPresent();
        if (userExists) {
            throw new IllegalStateException("Username: " + myUser.getUsername() + " already taken");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(myUser.getPassword());

        myUser.setPassword(encodedPassword);
        userRepository.save(myUser);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                myUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        //TODO: SEND EMAIL

        return token;
    }

    public int enableUser(String username) {
        return userRepository.enableUser(username);
    }
}
