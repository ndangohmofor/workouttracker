package com.meufty.workoutplanner.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "gym_checkin", uniqueConstraints = {@UniqueConstraint(columnNames = {"userId"})})
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @JoinColumn(name = "user_id")
    private long userId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    public boolean isCheckedIn(){
        return getCheckInTime() != null && getCheckOutTime() == null;
    }
}
