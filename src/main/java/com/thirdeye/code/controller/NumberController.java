package com.thirdeye.code.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thirdeye.code.dto.PermutationData;
import com.thirdeye.code.dto.TransactionSummary;
import com.thirdeye.code.entity.Break;
import com.thirdeye.code.entity.Customer;
import com.thirdeye.code.entity.Number;
import com.thirdeye.code.entity.Transaction;
import com.thirdeye.code.service.BreakService;
import com.thirdeye.code.service.CustomerService;
import com.thirdeye.code.service.NumberService;
import com.thirdeye.code.service.TransactionService;

@Controller
@RequestMapping("/number")
public class NumberController {

    @Autowired
    private NumberService numberservice;

    @Autowired
    private BreakService breakService;

    @Autowired
    private CustomerService customerservice;

    @Autowired
    private TransactionService transactionservice;

    @Autowired
    private BreakService breakservice;

    @GetMapping("/checkavailable")
    public String checkAvailable(Model model) {
        // for disable break add
        List<Break> breaks = breakService.findAll();
        if (breaks != null && !breaks.isEmpty()) {
            model.addAttribute("breakstatus", true);
        } else {
            model.addAttribute("breakstatus", false);
        }

        model.addAttribute("Number", new Number());
        return "number/checkavailable-form";
    }

    @GetMapping("/restartdb")
    public String restartdb() {
        numberservice.restartdb();
        return "redirect:/";
    }

    @GetMapping("/checklucky")
    public String checkLuckyform(Model model) {
        // for disable break add
        List<Break> breaks = breakService.findAll();
        if (breaks != null && !breaks.isEmpty()) {
            model.addAttribute("breakstatus", true);
        } else {
            model.addAttribute("breakstatus", false);
        }

        model.addAttribute("Number", new Number());
        return "number/checklucky-form";
    }

    @PostMapping("/checklucky")
    public String checkLuckyPost(@ModelAttribute("Number") Number number, Model model) {
        int luckyNumber = number.getNumber();
        // To show all the transactions not group by customer names
        List<Transaction> transactions = transactionservice.findalltransactions(luckyNumber);
        // To show the transactions group by customer names
        List<TransactionSummary> transactionSummaries = transactionservice.findtransgroupbycustomer(luckyNumber);
        // Calculate the total of all amounts
        long totalAmount = transactionSummaries.stream()
                .mapToLong(TransactionSummary::getTotalAmount)
                .sum();
        // show own and buy amount
        Number numbers = numberservice.findByNumber(luckyNumber);
        model.addAttribute("numbers", numbers);

        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("transactionSummaries", transactionSummaries);
        model.addAttribute("transactions", transactions);
        return "number/checklucky-list";
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
        // for disable break add
        List<Break> breaks = breakService.findAll();
        if (breaks != null && !breaks.isEmpty()) {
            model.addAttribute("breakstatus", true);
        } else {
            model.addAttribute("breakstatus", false);
        }

        List<Number> numbers = numberservice.findAll();
        model.addAttribute("numbers", numbers);
        return "number/number-list";
    }

    @GetMapping("/new")
    public String buynumberform(Model model) {
        // for disable break add
        List<Break> breaks = breakService.findAll();
        if (breaks != null && !breaks.isEmpty()) {
            model.addAttribute("breakstatus", true);
        } else {
            model.addAttribute("breakstatus", false);
        }

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
        Transaction Transaction = new Transaction();
        int wantToBuyAmount = numberEntity.getOwnamount();
        Number existingNumber = numberservice.findByNumber(numberEntity.getNumber());
        Customer existingCustomer = customerservice.findById(customerid);

        if (existingNumber != null) {
            handleExistingNumber(numberEntity, existingNumber, wantToBuyAmount);
            Transaction.setNumber(existingNumber);
        } else {
            handleNewNumber(numberEntity, wantToBuyAmount);
            Number newNumber = numberservice.findByNumber(numberEntity.getNumber());
            Transaction.setNumber(newNumber);
        }
        Transaction.setCustomer(existingCustomer);
        Transaction.setTransactiondate(LocalDateTime.now());
        Transaction.setAmount(wantToBuyAmount);
        Transaction.setBuynumber(numberEntity.getNumber());
        transactionservice.createTransaction(Transaction);

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

    @GetMapping("/newnumberversion")
    public String newnumberversion(Model model) {
        List<Customer> customers = customerservice.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("number", new Number());
        return "number/newnumberform";
    }

    @PostMapping("/getallrelatednumbers")
    public String getallrelatednumbers(Number number, Model model) {
        int targetnumber = number.getNumber();
        List<String> permutations = generatePermutations(targetnumber);
        List<Integer> availableAmounts = new ArrayList<>();

        // For each permutation, find the available amount
        for (String perm : permutations) {
            // Convert the permutation back to an integer
            int permNumber = Integer.parseInt(perm);
            int availableAmount = findAvailableAmountForNumber(permNumber);
            availableAmounts.add(availableAmount);
        }
        model.addAttribute("availableAmounts", availableAmounts);
        model.addAttribute("permutations", permutations);
        model.addAttribute("number", number);
        List<Customer> customers = customerservice.findAll();
        model.addAttribute("customers", customers);
        return "number/newnumberform";
    }

    private int findAvailableAmountForNumber(int number) {
        Number numberinfo = numberservice.findByNumber(number);
        int currentbreakamount = breakservice.getLatestBreakAmount();
        int availableamount;
        if (numberinfo != null) {
            int currentownamount = numberinfo.getOwnamount();
            int currentbuyamount = numberinfo.getBuyamount();
            if (currentbuyamount > 0) {
                availableamount = 0;
            } else {
                availableamount = currentbreakamount - currentownamount;
            }

        } else {
            availableamount = currentbreakamount;

        }

        return availableamount;
    }

    // Function to generate all permutations of a number
    public List<String> generatePermutations(int number) {
        // Convert the number to a string to manipulate digits
        String numStr = String.valueOf(number);
        List<String> permutations = new ArrayList<>();

        // Call the helper function to perform backtracking and generate permutations
        backtrack(numStr.toCharArray(), 0, permutations);

        return permutations;
    }

    // Backtracking method to generate permutations
    private void backtrack(char[] chars, int index, List<String> permutations) {
        // If we've processed all the digits, we have a complete permutation
        if (index == chars.length) {
            permutations.add(new String(chars));
            return;
        }

        for (int i = index; i < chars.length; i++) {
            // Swap characters
            swap(chars, index, i);
            // Recursively generate permutations for the remaining digits
            backtrack(chars, index + 1, permutations);
            // Backtrack: undo the swap
            swap(chars, index, i);
        }
    }

    // Helper function to swap two characters
    private void swap(char[] chars, int i, int j) {
        char temp = chars[i];
        chars[i] = chars[j];
        chars[j] = temp;
    }

    @PostMapping("/savepermutations")
    @ResponseBody
    public String savePermutations(@RequestBody PermutationData permutationData) {
        long customerId = Long.parseLong(permutationData.getCustomerid());

        // Lists of values for each permutation
        List<String> number = permutationData.getNumber();
        List<String> remain = permutationData.getRemain(); // The 'remain' values
        List<String> amt = permutationData.getAmt(); // The 'amt' values
        List<String> my = permutationData.getMy(); // The 'my' values
        List<String> buy = permutationData.getBuy(); // The 'buy' values

        if (remain.size() != amt.size() || amt.size() != my.size() || my.size() != buy.size()) {
            return "{\"message\": \"Mismatch in data sizes for remain, amt, my, and buy\"}";
        }

        // Get the customer from the database
        Customer existingCustomer = customerservice.findById(customerId);
        if (existingCustomer == null) {
            return "{\"message\": \"Customer not found\"}";
        }

        // Create a list of transactions for each permutation
        for (int i = 0; i < remain.size(); i++) {
            // Get the current values for the permutation
            int currentNumber = Integer.parseInt(number.get(i));
            int currentRemain = Integer.parseInt(remain.get(i));
            int currentAmt;
            int currentMy;
            int currentBuy;

            if ("".equals(amt.get(i))) {
                currentAmt = 0;
            } else {
                currentAmt = Integer.parseInt(amt.get(i));
            }

            if ("".equals(my.get(i))) {
                currentMy = 0;
            } else {
                currentMy = Integer.parseInt(my.get(i));
            }

            if ("".equals(buy.get(i))) {
                currentBuy = 0;
            } else {
                currentBuy = Integer.parseInt(buy.get(i));
            }

            if (currentAmt == 0) {
                System.out.println(
                        "The number " + currentNumber + " Buyamount and Own Amount is 0. So it do not need to count");

            }

            // get the current numnerrelatedentity
            Number existingNumber = numberservice.findInfoByNumber(currentNumber);
            Number preparenumberentity = new Number();
            if (existingNumber != null) {
                int curbuy = existingNumber.getBuyamount();
                int curown = existingNumber.getOwnamount();

                preparenumberentity.setNumber(currentNumber);
                preparenumberentity.setBuyamount(currentBuy + curbuy);
                preparenumberentity.setOwnamount(currentMy + curown);

                if (currentAmt != 0) {
                    numberservice.updateNumber(existingNumber.getNumberid(), preparenumberentity);

                }

            } else {
                preparenumberentity.setNumber(currentNumber);
                preparenumberentity.setBuyamount(currentBuy);
                preparenumberentity.setOwnamount(currentMy);

                if (currentAmt != 0) {
                    numberservice.createNumber(preparenumberentity);

                }
            }

            if (currentAmt != 0) {
                // Create a new transaction for each permutation
                Transaction transaction = new Transaction();
                Number existingNumber2 = numberservice.findInfoByNumber(currentNumber);
                transaction.setAmount(currentAmt);
                transaction.setBuynumber(currentNumber);
                transaction.setTransactiondate(LocalDateTime.now());
                transaction.setCustomer(existingCustomer);
                transaction.setNumber(existingNumber2);
                transactionservice.createTransaction(transaction);

            }

        }
        return "{\"message\": \"Data submitted successfully\"}";
    }

}
