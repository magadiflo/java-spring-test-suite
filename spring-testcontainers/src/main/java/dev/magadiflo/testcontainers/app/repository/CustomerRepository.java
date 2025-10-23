package dev.magadiflo.testcontainers.app.repository;

import dev.magadiflo.testcontainers.app.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
}
