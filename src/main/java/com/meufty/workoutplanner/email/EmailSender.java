package com.meufty.workoutplanner.email;

public interface EmailSender {
    void send(String to, String name, String token);
}
