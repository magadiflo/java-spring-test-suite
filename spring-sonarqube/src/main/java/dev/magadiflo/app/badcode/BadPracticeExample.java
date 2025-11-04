package dev.magadiflo.app.badcode;

public class BadPracticeExample {

    // ⚠️ Variable pública (mala práctica)
    public int counter = 0;

    // ⚠️ Método sin usar y con código duplicado
    public void increment() {
        counter++;
        System.out.println("Incrementing counter to " + counter);
    }

    // ⚠️ Código duplicado, mala práctica de copy-paste
    public void decrement() {
        counter--;
        System.out.println("Incrementing counter to " + counter);
    }

    // ⚠️ Método que siempre devuelve el mismo resultado (redundante)
    public boolean isActive() {
        return true;
    }

    // ⚠️ Método vacío (smell)
    public void doNothing() {
    }

    // ⚠️ Método con complejidad cognitiva innecesaria
    public void complexMethod(int value) {
        if (value > 0) {
            if (value < 10) {
                if (value != 5) {
                    System.out.println("Value accepted");
                }
            }
        }
    }
}
