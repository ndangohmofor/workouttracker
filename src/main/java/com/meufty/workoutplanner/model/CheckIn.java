package com.meufty.workoutplanner.model;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class CheckIn {
    private long id;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private long userId;
}
