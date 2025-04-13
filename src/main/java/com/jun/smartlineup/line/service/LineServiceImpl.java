package com.jun.smartlineup.line.service;

import com.jun.smartlineup.common.exception.ImpossibleRequestException;
import com.jun.smartlineup.common.exception.NoExistUserException;
import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.line.dto.LineChangeNameRequestDto;
import com.jun.smartlineup.line.dto.LineResponseDto;
import com.jun.smartlineup.line.repository.LineRepository;
import com.jun.smartlineup.queue.domain.Queue;
import com.jun.smartlineup.queue.repository.QueueRepository;
import com.jun.smartlineup.user.domain.Role;
import com.jun.smartlineup.user.domain.User;
import com.jun.smartlineup.user.dto.CustomUserDetails;
import com.jun.smartlineup.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {
    private final LineRepository lineRepository;
    private final UserRepository userRepository;

    @Transactional
    public void add(CustomUserDetails userDetails, String name) {
        Optional<User> OptionalUser = userRepository.findByEmail(userDetails.getUsername());
        User user = OptionalUser.orElseThrow(NoExistUserException::new);

        Line line = Line.builder().name(name)
                .user(user)
                .uuid(UUID.randomUUID().toString())
                .build();
        lineRepository.save(line);
    }

    public List<LineResponseDto> getLineList(CustomUserDetails userDetails) {
        Optional<User> OptionalUser = userRepository.findByEmail(userDetails.getUsername());
        User user = OptionalUser.orElseThrow(NoExistUserException::new);

        List<Line> linesByUser = lineRepository.getLinesByUserAndDeleteAtIsNull(user);
        return linesByUser.stream().map(LineResponseDto::fromEntity).toList();
    }

    @Transactional
    public void remove(CustomUserDetails userDetails, Long id) {
        Optional<User> optionalUser = userRepository.findByEmail(userDetails.getUsername());
        User user = optionalUser.orElseThrow(NoExistUserException::new);

        Optional<Line> optionalLine = lineRepository.getLineByIdAndUserAndDeleteAtIsNull(id, user);
        Line line = optionalLine.orElseThrow(() ->
                new RuntimeException("Remove error::No correct::" + user.getEmail() + "::" + id));

        line.delete();
    }

    @Transactional
    public void changeName(CustomUserDetails userDetails, LineChangeNameRequestDto dto) {
        Optional<User> OptionalUser = userRepository.findByEmail(userDetails.getUsername());
        User user = OptionalUser.orElseThrow(NoExistUserException::new);

        Optional<Line> optionalLine = lineRepository.getLineByIdAndUserAndDeleteAtIsNull(dto.getId(), user);
        Line line = optionalLine.orElseThrow(() ->
                new RuntimeException("Remove error::No correct::" + user.getEmail() + "::" + dto.getId()));

        line.changeName(dto.getName());
    }

    @Transactional
    public void changeIsQueuePositionVisibleToAttendee(CustomUserDetails userDetails, Long lineId) {
        Optional<User> OptionalUser = userRepository.findByEmail(userDetails.getUsername());
        User user = OptionalUser.orElseThrow(NoExistUserException::new);

        Optional<Line> optionalLine = lineRepository.getLineByIdAndUserAndDeleteAtIsNull(lineId, user);
        Line line = optionalLine.orElseThrow(() -> new ImpossibleRequestException("changeIsQueuePositionVisibleToAttendee", user));

        line.changeIsQueuePositionVisibleToAttendee();
    }
}
