package dev.magadiflo.mockito.app.util;

import org.mockito.ArgumentMatcher;

public class ValidExamIdMatcher implements ArgumentMatcher<Long> {
    private Long examId;

    @Override
    public boolean matches(Long examId) {
        this.examId = examId;
        return this.examId != null && this.examId > 0;
    }

    @Override
    public String toString() {
        return String.format("El id del examen enviado fue %d, se esperaba que fuera un entero positivo", this.examId);
    }
}
