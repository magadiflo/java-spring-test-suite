package dev.magadiflo.mockito.app.service.impl;

import dev.magadiflo.mockito.app.fixtures.ExamFixtures;
import dev.magadiflo.mockito.app.model.Exam;
import dev.magadiflo.mockito.app.repository.ExamRepository;
import dev.magadiflo.mockito.app.repository.QuestionRepository;
import dev.magadiflo.mockito.app.repository.impl.ExamRepositoryImpl;
import dev.magadiflo.mockito.app.repository.impl.QuestionRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class ExamServiceImplSpyTest {
    @Test
    void shouldReturnRealExamWithQuestionsUsingSpiedRepositories() {
        ExamRepository examRepository = Mockito.spy(ExamRepositoryImpl.class);
        QuestionRepository questionRepository = Mockito.spy(QuestionRepositoryImpl.class);
        ExamServiceImpl examService = new ExamServiceImpl(examRepository, questionRepository);

        Exam exam = examService.findExamByNameWithQuestions("R_Aritmética");

        assertThat(exam)
                .extracting(Exam::getId, Exam::getName)
                .containsExactly(1L, "R_Aritmética");
        assertThat(exam.getQuestions())
                .isNotEmpty()
                .hasSize(5)
                .contains("Pregunta 3 (real)", "Pregunta 5 (real)");
    }

    @Test
    void name() {
        ExamRepository examRepository = Mockito.spy(ExamRepositoryImpl.class);
        QuestionRepository questionRepository = Mockito.spy(QuestionRepositoryImpl.class);
        ExamServiceImpl examService = new ExamServiceImpl(examRepository, questionRepository);

        Mockito.doReturn(ExamFixtures.getAllExams()).when(examRepository).findAll();
        Mockito.doReturn(ExamFixtures.getQuestions()).when(questionRepository).findQuestionByExamId(Mockito.anyLong());

        Exam exam = examService.findExamByNameWithQuestions("Aritmética");

        assertThat(exam)
                .extracting(Exam::getId, Exam::getName)
                .containsExactly(1L, "Aritmética");
        assertThat(exam.getQuestions())
                .isNotEmpty()
                .hasSize(10)
                .contains("Pregunta 3", "Pregunta 5");
    }
}
