package com.example.Biomatch.user.repository;

import com.example.Biomatch.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email); // 아이디 중복성 검사

}
