package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.model.CheckIn;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.repository.CheckInRepository;
import com.meufty.workoutplanner.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class CheckInService {
    private CheckInRepository checkInRepository;
    private JwtUtil jwtUtil;

    public List<CheckIn> getCheckins(HttpServletRequest request){
        MyUser user = jwtUtil.getUserFromHttpRequest(request);
        return checkInRepository.getCheckInById(user.getId());
    }

    public CheckIn addCheckin(HttpServletRequest request){
        MyUser user = jwtUtil.getUserFromHttpRequest(request);
        CheckIn checkIn = new CheckIn();
        checkIn.setId(user.getId());
        checkIn.setCheckInTime(LocalDateTime.now());
        return checkInRepository.save(checkIn);
    }

    public CheckIn addCheckout(HttpServletRequest request){
        MyUser user = jwtUtil.getUserFromHttpRequest(request);
        CheckIn checkIn = checkInRepository.getCurrentCheckInById(user.getId());
        checkIn.setCheckOutTime(LocalDateTime.now());
        return checkInRepository.save(checkIn);
    }
}
