package com.example.Biomatch.user.service;


import com.example.Biomatch.security.custom.CustomUserInfoDto;
import com.example.Biomatch.security.jwt.JwtUtil;
import com.example.Biomatch.user.domain.RoleType;
import com.example.Biomatch.user.domain.User;
import com.example.Biomatch.user.repository.UserRepository;
import com.example.Biomatch.user.requestDTO.LoginDTO;
import com.example.Biomatch.user.requestDTO.RegisterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class UserService {


    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void register(RegisterDTO dto) {

        // 이메일 중복 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(dto.getPassword());

        User user = new User(dto.getNickname(), dto.getEmail(), encodedPassword, RoleType.USER);
        userRepository.save(user);
    }

    public String login(LoginDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) {
            throw new IllegalStateException( dto.getEmail() + "을 찾을 수 없습니다.");
        }

        if (!bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalStateException(dto.getEmail() + "의 비밀번호가 틀립니다." );
        }

        CustomUserInfoDto customUserinfoDTO = new CustomUserInfoDto(user.getId(), user.getEmail(), user.getNickname(), user.getPassword(), user.getRole());
        return jwtUtil.createAccessToken(customUserinfoDTO);

    }
}
