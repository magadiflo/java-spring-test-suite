package dev.magadiflo.mockito.app.service.impl;

import dev.magadiflo.mockito.app.fixtures.ExamFixtures;
import dev.magadiflo.mockito.app.model.Exam;
import dev.magadiflo.mockito.app.repository.ExamRepository;
import dev.magadiflo.mockito.app.repository.QuestionRepository;
import dev.magadiflo.mockito.app.repository.impl.ExamRepositoryImpl;
import dev.magadiflo.mockito.app.repository.impl.QuestionRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplSpyAnnotationTest {

    @Spy
    private ExamRepositoryImpl examRepository;          // Implementación concreta
    @Spy
    private QuestionRepositoryImpl questionRepository;  // Implementación concreta
    @InjectMocks
    private ExamServiceImpl examService;                // Implementación concreta

    @Test
    void shouldReturnRealExamWithQuestionsUsingSpiedRepositories() {
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
    void shouldReturnExamWithAllQuestionsUsingSpiedRepositoriesAndStubbedData() {
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
