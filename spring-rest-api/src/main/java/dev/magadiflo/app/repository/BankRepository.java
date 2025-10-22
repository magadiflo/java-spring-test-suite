package dev.magadiflo.app.repository;

import dev.magadiflo.app.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {
    /**
     * Busca un banco por su nombre.
     *
     * @param name nombre del banco
     * @return un {@link Optional} que contiene el banco si existe
     */
    Optional<Bank> findByName(String name);

    /**
     * Verifica si existe un banco con el nombre indicado.
     *
     * @param name nombre del banco
     * @return {@code true} si el banco existe, {@code false} en caso contrario
     */
    boolean existsByName(String name);
}
