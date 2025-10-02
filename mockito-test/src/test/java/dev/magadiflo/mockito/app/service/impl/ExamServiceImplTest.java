package dev.magadiflo.mockito.app.service.impl;

import dev.magadiflo.mockito.app.model.Exam;
import dev.magadiflo.mockito.app.repository.ExamRepository;
import dev.magadiflo.mockito.app.service.ExamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamServiceImplTest {

    private ExamRepository examRepository;
    private ExamService examService;

    @BeforeEach
    void setUp() {
        this.examRepository = Mockito.mock(ExamRepository.class);
        this.examService = new ExamServiceImpl(this.examRepository);
    }

    @Test
    void shouldReturnOptionalExamWithCorrectIdAndNameWhenRepositoryIsMocked() {
        List<Exam> exams = List.of(
                new Exam(1L, "Aritmética"),
                new Exam(2L, "Geometría"),
                new Exam(3L, "Álgebra"),
                new Exam(4L, "Trigonometría"),
                new Exam(5L, "Programación"),
                new Exam(6L, "Bases de Datos"),
                new Exam(7L, "Estructura de datos"),
                new Exam(8L, "Java 17")
        );

        Mockito.when(this.examRepository.findAll()).thenReturn(exams);

        Exam exam = this.examService.findExamByName("Aritmética");

        assertThat(exam)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName)
                .containsExactly(1L, "Aritmética");
    }

    @Test
    void shouldThrowNoSuchElementExceptionWithCorrectMessageWhenExamIsNotFound() {
        Mockito.when(this.examRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> this.examService.findExamByName("Aritmética"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No existe el examen Aritmética");

    }
}
