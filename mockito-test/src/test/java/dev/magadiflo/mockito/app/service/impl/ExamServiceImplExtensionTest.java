package dev.magadiflo.mockito.app.service.impl;

import dev.magadiflo.mockito.app.fixtures.ExamFixtures;
import dev.magadiflo.mockito.app.model.Exam;
import dev.magadiflo.mockito.app.repository.ExamRepository;
import dev.magadiflo.mockito.app.repository.QuestionRepository;
import dev.magadiflo.mockito.app.util.ValidExamIdMatcher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

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

    @Captor
    private ArgumentCaptor<Long> examIdCaptor;

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

    @Test
    void shouldAssignIdAndPersistExamWithQuestionsCorrectly() {
        Exam exam = ExamFixtures.getNewExam();
        exam.setQuestions(ExamFixtures.getQuestions());

        Mockito.when(this.examRepository.saveExam(Mockito.any(Exam.class))).then(new Answer<Exam>() {
            Long sequence = 8L;

            @Override
            public Exam answer(InvocationOnMock invocation) throws Throwable {
                Exam examToSave = invocation.getArgument(0);
                examToSave.setId(sequence++);
                return examToSave;
            }
        });
        Mockito.doNothing().when(this.questionRepository).saveQuestions(Mockito.anyList());

        Exam savedExam = this.examService.saveExam(exam);

        assertThat(savedExam)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName, Exam::getQuestions)
                .containsExactly(8L, "Kubernetes", ExamFixtures.getQuestions());
        Mockito.verify(this.examRepository).saveExam(Mockito.any(Exam.class));
        Mockito.verify(this.questionRepository).saveQuestions(Mockito.anyList());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenExamIdIsNullAndQuestionsAreRequested() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getExamsWithNullIds());
        Mockito.when(this.questionRepository.findQuestionByExamId(Mockito.isNull())).thenThrow(IllegalArgumentException.class);

        assertThatThrownBy(() -> this.examService.findExamByNameWithQuestions("Aritmética"))
                .isInstanceOf(IllegalArgumentException.class);

        Mockito.verify(this.examRepository).findAll();
        Mockito.verify(this.questionRepository).findQuestionByExamId(Mockito.isNull());
    }

    @Test
    void shouldVerifyCorrectExamIdIsUsedWhenFetchingQuestions() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());
        Mockito.when(this.questionRepository.findQuestionByExamId(Mockito.anyLong())).thenReturn(ExamFixtures.getQuestions());

        this.examService.findExamByNameWithQuestions("Aritmética");

        Mockito.verify(this.examRepository).findAll();
        Mockito.verify(this.questionRepository).findQuestionByExamId(Mockito.argThat(arg -> arg != null && arg.equals(1L)));
        Mockito.verify(this.questionRepository).findQuestionByExamId(Mockito.eq(1L));
    }

    @Test
    @Disabled
    void shouldVerifyCorrectExamIdIsUsedWhenFetchingQuestions_ArgumentMatcher() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getExamsWithNegativeIds());
        Mockito.when(this.questionRepository.findQuestionByExamId(Mockito.anyLong())).thenReturn(ExamFixtures.getQuestions());

        this.examService.findExamByNameWithQuestions("Aritmética");

        Mockito.verify(this.examRepository).findAll();
        Mockito.verify(this.questionRepository).findQuestionByExamId(Mockito.argThat(new ValidExamIdMatcher()));
    }

    @Test
    void shouldCaptureExamIdUsedToFetchQuestionsWithArgumentCaptor() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        this.examService.findExamByNameWithQuestions("Aritmética");


        Mockito.verify(this.questionRepository).findQuestionByExamId(captor.capture());
        assertThat(captor.getValue()).isEqualTo(1L);
    }

    @Test
    void shouldCaptureCorrectExamIdWhenFetchingQuestionsByName() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());

        this.examService.findExamByNameWithQuestions("Aritmética");

        Mockito.verify(this.questionRepository).findQuestionByExamId(this.examIdCaptor.capture());
        assertThat(this.examIdCaptor.getValue()).isEqualTo(1L);
    }
}
