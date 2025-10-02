package dev.magadiflo.mockito.app.service.impl;

import dev.magadiflo.mockito.app.fixtures.ExamFixtures;
import dev.magadiflo.mockito.app.model.Exam;
import dev.magadiflo.mockito.app.repository.ExamRepository;
import dev.magadiflo.mockito.app.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class) // Habilita las anotaciones de mockito: @Mock, @InjectMocks
class ExamServiceImplExtensionTest {

    @Mock
    private ExamRepository examRepository;          // Interfaz
    @Mock
    private QuestionRepository questionRepository;  // Interfaz
    @InjectMocks
    private ExamServiceImpl examService;            // Implementación concreta

    @Test
    void shouldReturnOptionalExamWithCorrectIdAndNameWhenRepositoryIsMocked() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());

        Exam exam = this.examService.findExamByName("Aritmética");

        assertThat(exam)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName)
                .containsExactly(1L, "Aritmética");
    }

    @Test
    void shouldThrowNoSuchElementExceptionWithCorrectMessageWhenExamIsNotFound() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getEmptyExams());

        assertThatThrownBy(() -> this.examService.findExamByName("Aritmética"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No existe el examen Aritmética");

    }

    @Test
    void shouldReturnExamWithQuestionsWhenSearchingByName() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());
        Mockito.when(this.questionRepository.findQuestionByExamId(Mockito.anyLong())).thenReturn(ExamFixtures.getQuestions());

        Exam exam = this.examService.findExamByNameWithQuestions("Geometría");

        assertThat(exam.getQuestions())
                .hasSize(10)
                .contains("Pregunta 10");

        Mockito.verify(this.examRepository).findAll();
        Mockito.verify(this.questionRepository).findQuestionByExamId(Mockito.anyLong());
    }

    @Test
    void shouldFailToFindExamByNameAndThrowExceptionWhenRepositoryIsEmpty() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getEmptyExams());

        assertThatThrownBy(() -> this.examService.findExamByNameWithQuestions("Aritmética"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No existe el examen Aritmética");

    }

    @Test
    void shouldThrowExceptionAndSkipQuestionLookupWhenExamIsNotFound() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());

        assertThatThrownBy(() -> this.examService.findExamByNameWithQuestions("Lenguaje"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No existe el examen Lenguaje");

        Mockito.verify(this.examRepository).findAll();
        Mockito.verify(this.questionRepository, Mockito.never()).findQuestionByExamId(Mockito.anyLong());
    }

    @Test
    void shouldSaveExamWithoutQuestionsAndSkipQuestionPersistence() {
        Mockito.when(this.examRepository.saveExam(Mockito.any(Exam.class))).thenReturn(ExamFixtures.getValidExam());

        Exam exam = this.examService.saveExam(ExamFixtures.getValidExam());

        assertThat(exam)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName, Exam::getQuestions)
                .containsExactly(9L, "Docker", ExamFixtures.getEmptyExams());
        Mockito.verify(this.examRepository).saveExam(Mockito.any(Exam.class));
        Mockito.verify(this.questionRepository, Mockito.never()).saveQuestions(Mockito.anyList());
    }

    @Test
    void shouldSaveExamWithQuestionsAndPersistBothExamAndQuestions() {
        Exam exam = ExamFixtures.getValidExam();
        exam.setQuestions(ExamFixtures.getQuestions());
        Mockito.when(this.examRepository.saveExam(Mockito.any(Exam.class))).thenReturn(exam);
        Mockito.doNothing().when(this.questionRepository).saveQuestions(Mockito.anyList());

        Exam examSaved = this.examService.saveExam(exam);

        assertThat(examSaved)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName, Exam::getQuestions)
                .containsExactly(9L, "Docker", ExamFixtures.getQuestions());
        Mockito.verify(this.examRepository).saveExam(Mockito.any(Exam.class));
        Mockito.verify(this.questionRepository).saveQuestions(Mockito.anyList());
    }
}
