package com.jun.smartlineup.attendee.repository;

import com.jun.smartlineup.attendee.domain.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AttendeeRepository extends JpaRepository<Attendee, Long> {

    @Query("SELECT a FROM Attendee a WHERE a.name = :name AND a.phone = :phone")
    Optional<Attendee> findByNameAndPhone(String name, String phone);
}
