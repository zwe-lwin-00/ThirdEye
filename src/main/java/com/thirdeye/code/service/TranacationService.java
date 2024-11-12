package com.thirdeye.code.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye.code.dto.TransactionSummary;
import com.thirdeye.code.entity.Customer;
import com.thirdeye.code.entity.Transaction;
import com.thirdeye.code.repository.TransactionRepository;

@Service
public class TransactionService {

    @Autowired

    private TransactionRepository transactionrepository;

    public List<Transaction> findAll() {
        return transactionrepository.findAll();
    }

    public Transaction createTransaction(Transaction transactionEntity) {
        return transactionrepository.save(transactionEntity);
    }

    public List<Transaction> findalltransactions(int luckyNumber) {
        return transactionrepository.findalltransactions(luckyNumber);
    }

    public List<TransactionSummary> findtransgroupbycustomer(int luckyNumber) {
        List<Object[]> results = transactionrepository.findtransgroupbycustomer(luckyNumber);
        List<TransactionSummary> summaries = new ArrayList<>();

        for (Object[] result : results) {
            TransactionSummary summary = new TransactionSummary();
            summary.setBuynumber((Integer) result[0]);
            summary.setCustomer((Customer) result[1]);
            summary.setTotalAmount((Long) result[2]);
            summaries.add(summary);
        }

        return summaries;
    }

}
