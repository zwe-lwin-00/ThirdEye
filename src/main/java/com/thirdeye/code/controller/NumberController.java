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
import org.springframework.web.bind.annotation.RequestParam;

import com.thirdeye.code.entity.Customer;
import com.thirdeye.code.entity.Number;
import com.thirdeye.code.entity.Tranacation;
import com.thirdeye.code.service.BreakService;
import com.thirdeye.code.service.CustomerService;
import com.thirdeye.code.service.NumberService;

@Controller
@RequestMapping("/number")
public class NumberController {

    @Autowired
    private NumberService numberservice;

    @Autowired
    private CustomerService customerservice;

    @Autowired
    private BreakService breakservice;

    @GetMapping("/checkavailable")
    public String checkAvailable(Model model) {
        model.addAttribute("Number", new Number());
        return "number/checkavailable-form";
    }

    @PostMapping("/checkavailable")
    public String checkAvailablePost(@ModelAttribute("Number") Number number, Model model) {
        Number existingNumber = numberservice.findByNumber(number.getNumber());
        int availableAmount;
        if (existingNumber != null) {
            availableAmount = breakservice.getLatestBreakAmount() - existingNumber.getOwnamount();

        } else {
            availableAmount = breakservice.getLatestBreakAmount();
        }
        model.addAttribute("availableAmount", availableAmount);

        return "number/checkavailable-form";
    }

    @GetMapping
    public String listNumbers(Model model) {
        List<Number> numbers = numberservice.findAll();
        model.addAttribute("numbers", numbers);
        return "number/number-list";
    }

    @GetMapping("/new")
    public String buynumberform(Model model) {
        model.addAttribute("number", new Number());
        List<Customer> customers = customerservice.findAll();
        model.addAttribute("customers", customers);
        return "number/number-form";
    }

    @GetMapping("/{id}")
    public String editNumberForm(@PathVariable Long id, Model model) {
        Number numberEntity = numberservice.findById(id);
        model.addAttribute("number", numberEntity);
        return "number/number-form";
    }

    @PostMapping("/{id}")
    public String updateNumber(@PathVariable Long id, @ModelAttribute Number numberEntity) {
        numberservice.updateNumber(id, numberEntity);
        return "redirect:/number";
    }

    @GetMapping("/delete/{id}")
    public String deleteNumberk(@PathVariable Long id) {
        numberservice.deleteNumber(id);
        return "redirect:/number";
    }

    @PostMapping
    public String buynewnumber(@ModelAttribute Number numberEntity, @RequestParam Long customerid) {
        int wantToBuyAmount = numberEntity.getOwnamount();
        Number existingNumber = numberservice.findByNumber(numberEntity.getNumber());

        // enter into transcation
        Tranacation Tranacation = new Tranacation();

        if (existingNumber != null) {
            handleExistingNumber(numberEntity, existingNumber, wantToBuyAmount);
        } else {
            handleNewNumber(numberEntity, wantToBuyAmount);
        }

        return "redirect:/number";
    }

    private void handleExistingNumber(Number numberEntity, Number existingNumber, int wantToBuyAmount) {
        int currentOwnAmount = existingNumber.getOwnamount();
        int currentBuyAmount = existingNumber.getBuyamount();
        int latestBreakAmount = breakservice.getLatestBreakAmount();
        int totalBecomeAmount = currentOwnAmount + wantToBuyAmount;

        if (latestBreakAmount >= totalBecomeAmount) {

            numberEntity.setOwnamount(totalBecomeAmount);
        } else {

            int exceedingAmount = totalBecomeAmount - latestBreakAmount;
            numberEntity.setOwnamount(latestBreakAmount);
            numberEntity.setBuyamount(exceedingAmount + currentBuyAmount);
        }

        numberservice.updateNumber(existingNumber.getNumberid(), numberEntity);
    }

    private void handleNewNumber(Number numberEntity, int wantToBuyAmount) {
        int latestBreakAmount = breakservice.getLatestBreakAmount();

        if (wantToBuyAmount > latestBreakAmount) {
            numberEntity.setOwnamount(latestBreakAmount);
            numberEntity.setBuyamount(wantToBuyAmount - latestBreakAmount);
        } else {
            numberEntity.setOwnamount(wantToBuyAmount);
        }

        numberservice.createNumber(numberEntity);
    }

}
