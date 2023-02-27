package com.meufty.workoutplanner.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailMessage {

    private String to;
    private String name;
    private String token;
    private String body;
}
