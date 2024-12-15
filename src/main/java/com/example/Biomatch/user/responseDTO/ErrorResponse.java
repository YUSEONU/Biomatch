package com.example.Biomatch.user.responseDTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String error; // 오류 요약 메시지
    private List<String> message; // 상세 메시지
}