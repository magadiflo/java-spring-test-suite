package dev.magadiflo.testcontainers.app.service;

import dev.magadiflo.testcontainers.app.entity.Customer;
import dev.magadiflo.testcontainers.app.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> findAllCustomers() {
        return this.customerRepository.findAll();
    }

    @Override
    public Customer findCustomerById(Long id) {
        return this.customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente con id " + id + " no encontrado"));
    }

    @Override
    public Customer findCustomerByEmail(String email) {
        return this.customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Cliente con email " + email + " no encontrado"));
    }

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
        return this.customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, Customer customer) {
        return this.customerRepository.findById(id)
                .map(customerDB -> {
                    customerDB.setName(customer.getName());
                    customerDB.setEmail(customer.getEmail());
                    return customerDB;
                })
                .map(this.customerRepository::save)
                .orElseThrow(() -> new NoSuchElementException("Cliente con id " + id + " no encontrado"));
    }

    @Override
    @Transactional
    public void deleteCustomerById(Long id) {
        Customer customer = this.customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente con id " + id + " no encontrado"));
        this.customerRepository.deleteById(customer.getId());
    }
}
