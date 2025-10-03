package dev.magadiflo.mockito.app.service.impl;

import dev.magadiflo.mockito.app.fixtures.ExamFixtures;
import dev.magadiflo.mockito.app.repository.ExamRepository;
import dev.magadiflo.mockito.app.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplInvocationsTest {

    @Mock
    private ExamRepository examRepository;          // Interfaz
    @Mock
    private QuestionRepository questionRepository;  // Interfaz
    @InjectMocks
    private ExamServiceImpl examService;            // Implementación concreta

    @Test
    void shouldVerifyQuestionRepositoryIsCalledInOrderForMultipleExamNames() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());

        this.examService.findExamByNameWithQuestions("Aritmética");
        this.examService.findExamByNameWithQuestions("Programación");

        InOrder inOrder = Mockito.inOrder(this.questionRepository);

        inOrder.verify(this.questionRepository).findQuestionByExamId(1L);
        inOrder.verify(this.questionRepository).findQuestionByExamId(5L);
    }

    @Test
    void shouldVerifyExamAndQuestionRepositoriesAreCalledInOrderForEachExamName() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());

        this.examService.findExamByNameWithQuestions("Aritmética");
        this.examService.findExamByNameWithQuestions("Programación");

        // Definimos los repositorios a los que verificaremos su orden de ejecución
        InOrder inOrder = Mockito.inOrder(this.examRepository, this.questionRepository);

        // Para Aritmética
        inOrder.verify(this.examRepository).findAll();
        inOrder.verify(this.questionRepository).findQuestionByExamId(1L);

        // Para Programación
        inOrder.verify(this.examRepository).findAll();
        inOrder.verify(this.questionRepository).findQuestionByExamId(5L);
    }
}
