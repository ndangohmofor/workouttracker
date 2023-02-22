package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.email.EmailSender;
import com.meufty.workoutplanner.email.EmailService;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.repository.ConfirmationTokenRepository;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.token.ConfirmationToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MSG = "user with userName %s not found";
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService mailSender;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
    }

    public String signUpUser(MyUser myUser) {
        boolean userExists = userRepository.findByUserName(myUser.getUsername()).isPresent();

        if (userExists){
            myUser.setId(userRepository.findByUserName(myUser.getUsername()).get().getId());
        }

        boolean tokenConfirmed = confirmationTokenRepository.findByMyUserIdAndConfirmedAtIsNull(myUser.getId()).isPresent();

        if (userExists && tokenConfirmed) {
            log.error("Username: " + myUser.getUsername() + " already taken");
            throw new IllegalStateException("Username: " + myUser.getUsername() + " already taken. Please select another username");
        }

        if (userExists) {
            MyUser existingUser = userRepository.findByUserName(myUser.getUsername()).get();
            log.info("Username: " + myUser.getUsername() + " already registered. Please confirm your email address");
            mailSender.send(myUser.getEmail(), myUser.getUsername(), String.valueOf(confirmationTokenRepository.findTokenByUserId(existingUser.getId()).get()));
            throw new IllegalStateException("Username: " + myUser.getUsername() + " registered but not confirmed. Please check and confirm your email address");
        }
        //Everything below happens if the user does not exist => User is created, token is generated and email is sent!
        String encodedPassword = bCryptPasswordEncoder.encode(myUser.getPassword());

        myUser.setPassword(encodedPassword);
        userRepository.save(myUser);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), myUser);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        log.info("Token " + confirmationToken.getToken() + " created and stored in the DB");
        return token;
    }

    public int enableUser(String username) {
        log.info(username + " enabled");
        return userRepository.enableUser(username);
    }
}
