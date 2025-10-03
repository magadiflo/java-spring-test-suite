package dev.magadiflo.mockito.app.repository;

import java.util.List;

public interface QuestionRepository {
    List<String> findQuestionByExamId(Long examId);

    void saveQuestions(List<String> questions);
}
