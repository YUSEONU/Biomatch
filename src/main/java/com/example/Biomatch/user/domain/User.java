package com.example.Biomatch.user.domain;

import com.example.Biomatch.bmi.domain.Bmi;
import com.example.Biomatch.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.*;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String nickname;

    private String email;

    private String password;

    @Enumerated(STRING)
    private RoleType role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Bmi bmi;

    public User(String nickname, String email ,String password, RoleType role) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}