package dev.magadiflo.mockito.app.repository;

import dev.magadiflo.mockito.app.model.Exam;

import java.util.List;

public interface ExamRepository {
    List<Exam> findAll();
}
