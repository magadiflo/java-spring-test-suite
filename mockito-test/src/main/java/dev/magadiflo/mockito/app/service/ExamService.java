package dev.magadiflo.mockito.app.service;

import dev.magadiflo.mockito.app.model.Exam;

public interface ExamService {
    Exam findExamByName(String name);
}
