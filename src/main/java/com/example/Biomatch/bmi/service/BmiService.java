package com.example.Biomatch.bmi.service;

import com.example.Biomatch.bmi.domain.Bmi;
import com.example.Biomatch.bmi.requestDTO.BmiDTO;
import com.example.Biomatch.bmi.repository.BmiRepository;
import com.example.Biomatch.bmi.responseDTO.BmiResultDTO;
import com.example.Biomatch.security.jwt.JwtUtil;
import com.example.Biomatch.user.domain.User;
import com.example.Biomatch.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Transactional
public class BmiService {

    private final BmiRepository bmiRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public BmiResultDTO calculateAndSaveBMI(BmiDTO dto, HttpServletRequest request) {
        // 요청에서 JWT 추출
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            throw new IllegalStateException("유효한 JWT 토큰이 필요합니다.");
        }

        // JWT에서 사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        // BMI 계산
        double weight = dto.getWeight();
        double height = dto.getHeight();
        double heightInMeters = height / 100.0; // 키를 m로 변환
        double bmiValue = weight / (heightInMeters * heightInMeters);

        // BMI 카테고리 결정
        String category;
        if (bmiValue < 18.5) {
            category = "저체중";
        } else if (bmiValue < 25.0) {
            category = "정상";
        } else if (bmiValue < 30.0) {
            category = "과체중";
        } else {
            category = "비만";
        }

        // BMI 데이터 생성 및 저장
        Bmi bmi = bmiRepository.findByUser(user).orElse(new Bmi());
        bmi.setWeight(weight);
        bmi.setHeight(height);
        bmi.setBmi(Math.round(bmiValue * 100.0) / 100.0); // 소수점 두 자리로 반올림
        bmi.setCategory(category);
        bmi.setUser(user);

        bmiRepository.save(bmi);

        // 결과 DTO 생성
        return new BmiResultDTO(Math.round(bmiValue * 100.0) / 100.0, category);
    }

    /**
     * 요청에서 JWT 추출
     * @param request HttpServletRequest
     * @return JWT String
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }
        return null;
    }
}
