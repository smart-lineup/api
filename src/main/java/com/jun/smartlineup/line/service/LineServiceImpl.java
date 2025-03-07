package com.jun.smartlineup.line.service;

import com.jun.smartlineup.line.domain.Line;
import com.jun.smartlineup.line.repository.LineRepository;
import com.jun.smartlineup.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {
    private final LineRepository lineRepository;

    public void save(Line line) {
        lineRepository.save(line);
    }

    public List<Line> getLineList(User user) {
        return lineRepository.getLinesByUser(user);
    }


}
