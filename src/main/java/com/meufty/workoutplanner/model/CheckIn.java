package com.meufty.workoutplanner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "gym_checkin")
public class CheckIn {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private long userId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    public boolean isCheckedIn(){
        return getCheckInTime() != null && getCheckOutTime() == null;
    }
}
