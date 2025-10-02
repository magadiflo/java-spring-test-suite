package dev.magadiflo.mockito.app.service.impl;

import dev.magadiflo.mockito.app.model.Exam;
import dev.magadiflo.mockito.app.repository.ExamRepository;
import dev.magadiflo.mockito.app.repository.QuestionRepository;
import dev.magadiflo.mockito.app.service.ExamService;

import java.util.List;
import java.util.NoSuchElementException;

public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;

    public ExamServiceImpl(ExamRepository examRepository, QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Exam findExamByName(String name) {
        return this.examRepository.findAll().stream()
                .filter(exam -> exam.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No existe el examen " + name));
    }

    @Override
    public Exam findExamByNameWithQuestions(String name) {
        Exam exam = this.findExamByName(name);
        List<String> questions = this.questionRepository.findQuestionByExamId(exam.getId());
        exam.setQuestions(questions);
        return exam;
    }
}
