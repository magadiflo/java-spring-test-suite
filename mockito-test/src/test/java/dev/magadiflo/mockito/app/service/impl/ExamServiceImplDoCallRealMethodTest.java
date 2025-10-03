package dev.magadiflo.mockito.app.service.impl;

import dev.magadiflo.mockito.app.fixtures.ExamFixtures;
import dev.magadiflo.mockito.app.model.Exam;
import dev.magadiflo.mockito.app.repository.ExamRepository;
import dev.magadiflo.mockito.app.repository.impl.QuestionRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplDoCallRealMethodTest {

    @Mock
    private ExamRepository examRepository;              // Interfaz
    @Mock
    private QuestionRepositoryImpl questionRepository;  // Implementación concreta
    @InjectMocks
    private ExamServiceImpl examService;                // Implementación concreta

    @Test
    void shouldInvokeRealMethodToFetchQuestionsAndReturnExpectedExam() {
        // given
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());
        Mockito.doCallRealMethod().when(this.questionRepository).findQuestionByExamId(Mockito.anyLong());

        // when
        Exam exam = this.examService.findExamByNameWithQuestions("Aritmética");

        // then
        assertThat(exam)
                .extracting(Exam::getId, Exam::getName)
                .containsExactly(1L, "Aritmética");
        assertThat(exam.getQuestions())
                .hasSize(5)
                .contains("Pregunta 1 (real)", "Pregunta 4 (real)", "Pregunta 5 (real)");
    }
}
