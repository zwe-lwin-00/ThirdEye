package com.thirdeye.code.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye.code.entity.Tranacation;
import com.thirdeye.code.repository.TranacationRepository;

@Service
public class TranacationService {

    @Autowired

    private TranacationRepository tranacationrepository;

    public List<Tranacation> findAll() {
        return tranacationrepository.findAll();
    }

}