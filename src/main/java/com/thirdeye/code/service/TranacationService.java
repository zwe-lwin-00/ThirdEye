package com.thirdeye.code.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye.code.dto.TranacationSummary;
import com.thirdeye.code.entity.Customer;
import com.thirdeye.code.entity.Tranacation;
import com.thirdeye.code.repository.TranacationRepository;

@Service
public class TranacationService {

    @Autowired

    private TranacationRepository tranacationrepository;

    public List<Tranacation> findAll() {
        return tranacationrepository.findAll();
    }

    public Tranacation createTranacation(Tranacation tranacationEntity) {
        return tranacationrepository.save(tranacationEntity);
    }

    public List<Tranacation> findalltranscations(int luckyNumber) {
        return tranacationrepository.findalltranscations(luckyNumber);
    }

    public List<TranacationSummary> findtransgroupbycustomer(int luckyNumber) {
        List<Object[]> results = tranacationrepository.findtransgroupbycustomer(luckyNumber);
        List<TranacationSummary> summaries = new ArrayList<>();

        for (Object[] result : results) {
            TranacationSummary summary = new TranacationSummary();
            summary.setBuynumber((Integer) result[0]);
            summary.setCustomer((Customer) result[1]);
            summary.setTotalAmount((Long) result[2]);
            summaries.add(summary);
        }

        return summaries;
    }

}
