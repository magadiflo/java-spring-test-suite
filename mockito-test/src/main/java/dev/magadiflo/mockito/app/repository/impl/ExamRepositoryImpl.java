package dev.magadiflo.mockito.app.repository.impl;

import dev.magadiflo.mockito.app.model.Exam;
import dev.magadiflo.mockito.app.repository.ExamRepository;

import java.util.List;

public class ExamRepositoryImpl implements ExamRepository {
    @Override
    public List<Exam> findAll() {
        return List.of(
                new Exam(1L, "R_Aritmética"),
                new Exam(2L, "R_Geometría"),
                new Exam(3L, "R_Álgebra"),
                new Exam(4L, "R_Trigonometría"),
                new Exam(5L, "R_Programación"),
                new Exam(6L, "R_Bases de Datos"),
                new Exam(7L, "R_Estructura de datos"),
                new Exam(8L, "R_Java 17")
        );
    }

    @Override
    public Exam saveExam(Exam exam) {
        return null;
    }
}
