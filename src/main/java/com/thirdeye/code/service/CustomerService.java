package com.thirdeye.code.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye.code.entity.Customer;
import com.thirdeye.code.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired

    private CustomerRepository customerrepository;

    public List<Customer> findAll() {
        return customerrepository.findAll();
    }

    public Customer findById(Long id) {
        Optional<Customer> customerOptional = customerrepository.findById(id);
        return customerOptional.orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public void deleteCustomer(Long id) {
        customerrepository.deleteById(id);
    }

    public Customer createCustomer(Customer customerEntity) {
        return customerrepository.save(customerEntity);
    }

    // Update an existing Customer
    public Customer updateCustomer(Long customerId, Customer customerEntity) {
        if (customerrepository.existsById(customerId)) {
            customerEntity.setCustomerid(customerId);

            return customerrepository.save(customerEntity);
        }
        throw new RuntimeException("Customer not found");
    }

}
