package com.example.Biomatch.bmi.controller;

import com.example.Biomatch.bmi.domain.Bmi;
import com.example.Biomatch.bmi.requestDTO.BmiDTO;
import com.example.Biomatch.bmi.responseDTO.BmiResultDTO;
import com.example.Biomatch.bmi.service.BmiService;
import com.example.Biomatch.user.responseDTO.ErrorResponse;
import com.example.Biomatch.user.responseDTO.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="BMI", description = "BMI 계산")
@RestController
@RequestMapping("/api/bmi")
@RequiredArgsConstructor
public class BmiController {
    private final BmiService bmiService;

    @PostMapping("/calculate")
    @Operation(summary = "BMI", description = "키, 몸무게로 BMI 계산")
    @ApiResponses({
    @ApiResponse(responseCode = "200", description = "BMI 계산 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BmiResultDTO.class))),
            @ApiResponse(responseCode = "401", description = "유효한 JWT 토큰이 필요합니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BmiResultDTO> calculateBMI(@RequestBody BmiDTO dto, HttpServletRequest request) {
        // BMI 계산 및 저장
        BmiResultDTO resultDTO = bmiService.calculateAndSaveBMI(dto, request);

        // ResponseEntity로 결과 반환
        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK 상태 반환
                .body(resultDTO); // 결과 데이터를 포함
    }
}
