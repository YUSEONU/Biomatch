package com.example.Biomatch.user.controller;

import com.example.Biomatch.user.requestDTO.LoginDTO;
import com.example.Biomatch.user.requestDTO.RegisterDTO;
import com.example.Biomatch.user.responseDTO.ErrorResponse;
import com.example.Biomatch.user.responseDTO.TokenResponse;
import com.example.Biomatch.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Tag(name="회원", description = "회원가입, 로그인, 로그아웃")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "회원가입 API")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청이 잘못되었습니다. 다음과 같은 이유로 요청이 실패할 수 있습니다:\n" +
                            "- 이메일 중복\n" +
                            "- 닉네임은 필수 입력 값입니다.\n" +
                            "- 이메일은 필수 입력 값입니다.\n" +
                            "- 이메일 형식이 올바르지 않습니다.\n" +
                            "- 비밀번호는 필수 입력 값입니다.\n" +
                            "- 비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<TokenResponse> register(@RequestBody @Valid RegisterDTO registerDTO) {
        userService.register(registerDTO);

        String token = userService.login(new LoginDTO(registerDTO.getEmail(), registerDTO.getPassword()));
        insertToken(token);

        if (token == null || token.trim().isEmpty()) {
            System.out.println("실패");
        } else {
            System.out.println(token);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new TokenResponse(token));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 API")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
        @ApiResponse(responseCode = "401", description = "- 이메일을 찾을 수 없습니다.\n"
                + "- 이메일의 비밀번호가 틀립니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TokenResponse> login(@RequestBody LoginDTO dto) {
        System.out.println("dto = " + dto);
        String status = userService.login(dto);

        insertToken(status);
        return ResponseEntity.status(HttpStatus.OK).body(new TokenResponse(status));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃 API")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "로그아웃 실패", content = @Content(mediaType = "application/json"))
    public ResponseEntity<String> logout() {
        try {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

            if (response != null) {
                // 쿠키 삭제를 위해 동일한 이름의 쿠키를 만료 시간 0으로 설정
                Cookie cookie = new Cookie("accessToken", null);
                cookie.setPath("/"); // 동일한 경로 설정
                cookie.setMaxAge(0); // 만료 시간 0으로 설정
                cookie.setHttpOnly(true);
                cookie.setSecure(true); // 실제 배포 시 true로 설정

                response.addCookie(cookie);
            }

            return ResponseEntity.status(HttpStatus.OK).body("로그아웃 성공");
        } catch (Exception e) {
            System.err.println("로그아웃 중 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그아웃 실패");
        }
    }


    public void insertToken(String token) {
        try {
            // 쿠키 값 유효성 검사
            if (!isValidCookieValue(token)) {
                System.err.println("유효하지 않은 쿠키 값: " + token);
                return; // 값이 유효하지 않으면 쿠키 설정 중단
            }

            // 쿠키 값 UTF-8로 인코딩
            String cookieValue = URLEncoder.encode(token, "UTF-8");
            Cookie cookie = new Cookie("accessToken", cookieValue);

            cookie.setPath("/");
            cookie.setSecure(true); // 실제 배포 시 true로 설정
            cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
            cookie.setHttpOnly(true);

            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            // 인코딩 실패 시 로깅 또는 예외 처리
            System.err.println("쿠키 인코딩 오류: " + e.getMessage());
        }
    }

    // 쿠키 값에 유효한 문자가 포함되어 있는지 확인하는 메서드
    private boolean isValidCookieValue(String value) {
        // 예시: 공백이 아닌 ASCII 32 이상인지 확인
        for (char c : value.toCharArray()) {
            if (c < 32 || c > 126) { // 허용할 ASCII 범위 설정
                return false;
            }
        }
        return true;
    }
}