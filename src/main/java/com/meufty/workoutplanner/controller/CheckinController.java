package com.meufty.workoutplanner.controller;

import com.meufty.workoutplanner.model.CheckIn;
import com.meufty.workoutplanner.service.CheckInService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@AllArgsConstructor
@CrossOrigin
@RestController
@RequestMapping(path = "/api/v1/checkins")
public class CheckinController {
    private CheckInService checkInService;

    @GetMapping(path = "/usercheckins")
    public List<CheckIn> getUserCheckIns(HttpServletRequest request){
        return checkInService.getCheckins(request);
    }

    @PostMapping(path = "usercheckin")
    public CheckIn addUserCheckIn(HttpServletRequest request){
        return checkInService.addCheckin(request);
    }

    @PostMapping(path = "usercheckout")
    public CheckIn addUserCheckout(HttpServletRequest request){
        return checkInService.addCheckout(request);
    }
}
