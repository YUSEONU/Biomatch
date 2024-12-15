package com.example.Biomatch.bmi.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BmiResultDTO {
    private double bmi;
    private String category;
}
