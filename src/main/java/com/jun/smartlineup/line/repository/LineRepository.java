package com.jun.smartlineup.line.repository;

import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    List<Line> getLinesByUserAndDeleteAtIsNullOrderByCreatedAt(User user);
    Optional<Line> getLineByIdAndUserAndDeleteAtIsNull(Long id, User user);
    Optional<Line> findByUuidAndDeleteAtIsNull(String uuid);

    void deleteAllByUser(User user);
}
