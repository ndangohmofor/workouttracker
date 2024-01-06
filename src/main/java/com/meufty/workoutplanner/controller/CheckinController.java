package com.meufty.workoutplanner.controller;

import com.meufty.workoutplanner.model.CheckIn;
import com.meufty.workoutplanner.service.CheckInService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@AllArgsConstructor
@CrossOrigin
@RestController
@RequestMapping(path = "/api/v1/checkin")
public class CheckinController {
    private CheckInService checkInService;

    @GetMapping
    public List<CheckIn> getUserCheckIns(HttpServletRequest request){
        return checkInService.getCheckins(request);
    }
}
