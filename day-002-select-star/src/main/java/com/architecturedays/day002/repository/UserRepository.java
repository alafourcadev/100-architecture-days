package com.architecturedays.day002.repository;

import com.architecturedays.day002.dto.UserSummary;
import com.architecturedays.day002.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // Interface Projection: Spring genera SELECT id, email, name automáticamente
    List<UserSummary> findAllProjectedBy();
}
