package com.example.Biomatch.bmi.repository;

import com.example.Biomatch.bmi.domain.Bmi;
import com.example.Biomatch.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BmiRepository extends JpaRepository<Bmi, Long> {
    Optional<Bmi> findByUser(User user);
}
