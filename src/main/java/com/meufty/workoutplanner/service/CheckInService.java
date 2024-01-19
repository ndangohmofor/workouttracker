package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.model.CheckIn;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.repository.CheckInRepository;
import com.meufty.workoutplanner.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
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
        checkIn.setUserId(user.getId());
        checkIn.setCheckInTime(LocalDateTime.now());
        return checkInRepository.save(checkIn);
    }

    public CheckIn addCheckout(HttpServletRequest request){
        MyUser user = jwtUtil.getUserFromHttpRequest(request);
        CheckIn checkIn = checkInRepository.getCurrentCheckInById(user.getId());
        checkIn.setCheckOutTime(LocalDateTime.now());
        return checkInRepository.save(checkIn);
    }

    public boolean getCheckinStatus(MyUser user){
        CheckIn checkedin = checkInRepository.getCurrentCheckInById(user.getId());
        if (null == checkedin){
            return false;
        }
        return  checkedin.isCheckedIn();
    }

    public LocalDateTime getLastWorkoutDate(HttpServletRequest request){
        MyUser user = jwtUtil.getUserFromHttpRequest(request);
        return checkInRepository.getLastWorkoutDateById(user.getId()).getCheckOutTime();
    }

    public Duration getWorkoutStats(HttpServletRequest request){
        MyUser user = jwtUtil.getUserFromHttpRequest(request);
        List<CheckIn> checkInList = checkInRepository.getCheckInByUserId(user.getId());
        Duration avgWorkoutDuration = Duration.ofSeconds(0);
        for (CheckIn checkIn: checkInList){
            if (checkIn.getCheckOutTime() != null){
                avgWorkoutDuration = Duration.between(checkIn.getCheckOutTime(), checkIn.getCheckInTime());
            }
        }
        return avgWorkoutDuration;
    }

    public Duration getSessionWorkoutStats(HttpServletRequest request){
        MyUser user = jwtUtil.getUserFromHttpRequest(request);
        CheckIn lastCheckIn = checkInRepository.getLastWorkoutDateById(user.getId());
        Duration avgWorkoutSessionDuration = Duration.ofSeconds(0);
        if (lastCheckIn.getCheckOutTime() != null){
            avgWorkoutSessionDuration = Duration.between(lastCheckIn.getCheckOutTime(), lastCheckIn.getCheckInTime());
        }
        return avgWorkoutSessionDuration;
    }
}
