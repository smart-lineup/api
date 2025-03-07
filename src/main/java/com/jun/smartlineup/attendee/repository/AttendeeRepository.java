package com.jun.smartlineup.attendee.repository;

import com.jun.smartlineup.attendee.domain.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendeeRepository extends JpaRepository<Attendee, Long> {

}
