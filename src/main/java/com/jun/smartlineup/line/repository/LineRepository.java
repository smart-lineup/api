package com.jun.smartlineup.line.repository;

import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    List<Line> getLinesByUser(User user);
    Optional<Line> getLineByIdAndUser(Long id, User user);
    Optional<Line> findByUuid(String uuid);
}
