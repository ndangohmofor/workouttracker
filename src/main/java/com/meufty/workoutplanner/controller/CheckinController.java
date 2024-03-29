package com.meufty.workoutplanner.controller;

import com.meufty.workoutplanner.model.CheckIn;
import com.meufty.workoutplanner.service.CheckInService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@CrossOrigin
@RestController
@RequestMapping(path = "/api/v1/checkins")
public class CheckinController {
    private CheckInService checkInService;

    @GetMapping(path = "/usercheckins")
    @Secured({"ROLE_USER"})
    public List<CheckIn> getUserCheckIns(HttpServletRequest request){
        return checkInService.getCheckins(request);
    }

    @PostMapping(path = "usercheckin")
    @Secured({"ROLE_USER"})
    public CheckIn addUserCheckIn(HttpServletRequest request){
        return checkInService.addCheckin(request);
    }

    @PostMapping(path = "usercheckout")
    @Secured({"ROLE_USER"})
    public CheckIn addUserCheckout(HttpServletRequest request){
        return checkInService.addCheckout(request);
    }

    @GetMapping(path = "lastworkoutdate")
    @Secured({"ROLE_USER"})
    public LocalDateTime getLastWorkoutDate(HttpServletRequest request){
        return checkInService.getLastWorkoutDate(request);
    }

    @GetMapping(path = "totalWorkout")
    @Secured({"ROLE_USER"})
    public Long getAverageWorkout(HttpServletRequest request){
        return checkInService.getWorkoutStats(request).getSeconds();
    }

    @GetMapping(path = "totalSessionWorkout")
    @Secured({"ROLE_USER"})
    public Long getAverageSessionWorkout(HttpServletRequest request){
        return checkInService.getSessionWorkoutStats(request).getSeconds();
    }
}
