package com.thirdeye.code.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.thirdeye.code.entity.Customer;
import com.thirdeye.code.service.CustomerService;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerservice;

    @GetMapping
    public String listCustomers(Model model) {
        List<Customer> customers = customerservice.findAll();
        model.addAttribute("customers", customers);
        return "customer/customer-list";
    }

    @GetMapping("/new")
    public String buycustomerform(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/customer-form";
    }

    @GetMapping("/{id}")
    public String editCustomerForm(@PathVariable Long id, Model model) {
        Customer customerEntity = customerservice.findById(id);
        model.addAttribute("customer", customerEntity);
        return "customer/customer-form";
    }

    @PostMapping("/{id}")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute Customer customerEntity) {
        customerservice.updateCustomer(id, customerEntity);
        return "redirect:/customer";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomerk(@PathVariable Long id) {
        customerservice.deleteCustomer(id);
        return "redirect:/customer";
    }

    @PostMapping
    public String createCustomer(@ModelAttribute Customer customerEntity) {
        customerservice.createCustomer(customerEntity);
        return "redirect:/customer";
    }

}
