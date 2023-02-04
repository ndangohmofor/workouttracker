package com.meufty.workoutplanner.controller;

import org.springframework.web.bind.annotation.*;

/**
 * AccountController
 */

@RestController
@RequestMapping(path = "/workout")
public class AccountController {

    @GetMapping(path = "/home")
    public String home(){
        return "<h1>Welcome to the app</h1>";
    }

    @GetMapping(path = "/user")
    public String getUser(){
        return "<h2>Welcome user</h2>";
    }
}
