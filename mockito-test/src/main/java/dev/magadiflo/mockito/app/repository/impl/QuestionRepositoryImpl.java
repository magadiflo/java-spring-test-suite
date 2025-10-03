package dev.magadiflo.mockito.app.repository.impl;

import dev.magadiflo.mockito.app.repository.QuestionRepository;

import java.util.List;

public class QuestionRepositoryImpl implements QuestionRepository {
    @Override
    public List<String> findQuestionByExamId(Long examId) {
        return List.of(
                "Pregunta 1 (real)",
                "Pregunta 2 (real)",
                "Pregunta 3 (real)",
                "Pregunta 4 (real)",
                "Pregunta 5 (real)"
        );
    }

    @Override
    public void saveQuestions(List<String> questions) {
        // No implementado porque no lo usamos en los test del doCallRealMethod
    }
}
