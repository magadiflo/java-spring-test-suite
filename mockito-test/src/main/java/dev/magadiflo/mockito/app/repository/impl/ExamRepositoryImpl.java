package dev.magadiflo.mockito.app.repository.impl;

import dev.magadiflo.mockito.app.model.Exam;
import dev.magadiflo.mockito.app.repository.ExamRepository;

import java.util.List;

public class ExamRepositoryImpl implements ExamRepository {
    @Override
    public List<Exam> findAll() {
        return List.of();
    }

    @Override
    public Exam saveExam(Exam exam) {
        return null;
    }
}
