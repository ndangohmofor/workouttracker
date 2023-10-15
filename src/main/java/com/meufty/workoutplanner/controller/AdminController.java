package com.meufty.workoutplanner.controller;

import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.UserRole;
import com.meufty.workoutplanner.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("gymusers")
    public List<MyUser> getGymUsers(UserRole role) throws Exception{
        return userRepository.findMyUserByRole(role).orElseThrow();
    }

    @GetMapping("allusers")
    public List<MyUser> getAllUsers() throws Exception {
        return userRepository.findAll();
    }
}
