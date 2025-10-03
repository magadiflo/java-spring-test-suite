package dev.magadiflo.mockito.app.fixtures;

import dev.magadiflo.mockito.app.model.Exam;

import java.util.List;

public class ExamFixtures {
    public static List<Exam> getAllExams() {
        return List.of(
                new Exam(1L, "Aritmética"),
                new Exam(2L, "Geometría"),
                new Exam(3L, "Álgebra"),
                new Exam(4L, "Trigonometría"),
                new Exam(5L, "Programación"),
                new Exam(6L, "Bases de Datos"),
                new Exam(7L, "Estructura de datos"),
                new Exam(8L, "Java 17")
        );
    }

    public static List<Exam> getEmptyExams() {
        return List.of();
    }

    public static List<Exam> getExamsWithNegativeIds() {
        return List.of(
                new Exam(-1L, "Aritmética"),
                new Exam(-2L, "Geometría"),
                new Exam(-3L, "Álgebra")
        );
    }

    public static List<Exam> getExamsWithNullIds() {
        return List.of(
                new Exam(null, "Aritmética"),
                new Exam(null, "Geometría"),
                new Exam(null, "Álgebra")
        );
    }

    public static Exam getValidExam() {
        return new Exam(9L, "Docker");
    }

    public static Exam getNewExam() {
        return new Exam(null, "Kubernetes");
    }

    public static Exam getDefaultExam() {
        return new Exam(1L, "Aritmética");
    }

    public static List<String> getQuestions() {
        return List.of(
                "Pregunta 1", "Pregunta 2", "Pregunta 3",
                "Pregunta 4", "Pregunta 5", "Pregunta 6",
                "Pregunta 7", "Pregunta 8", "Pregunta 9",
                "Pregunta 10"
        );
    }

    public static List<String> getFewQuestions() {
        return List.of(
                "Pregunta 1", "Pregunta 2", "Pregunta 3",
                "Pregunta 4", "Pregunta 5"
        );
    }

    public static List<String> getEmptyQuestions() {
        return List.of();
    }

}
