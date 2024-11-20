package com.thirdeye.code.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye.code.entity.Number;
import com.thirdeye.code.repository.NumberRepository;

@Service
public class NumberService {

    @Autowired

    private NumberRepository numberrepository;

    public List<Number> findAll() {
        return numberrepository.findAll();
    }

    public Number findById(Long id) {
        Optional<Number> breakOptional = numberrepository.findById(id);
        return breakOptional.orElseThrow(() -> new RuntimeException("Number not found"));
    }

    public void deleteNumber(Long id) {
        numberrepository.deleteById(id);
    }

    public Number findByNumber(int number) {
        return numberrepository.findByNumber(number);
    }

    public Number createNumber(Number numberEntity) {
        return numberrepository.save(numberEntity);
    }

    public Number updateNumber(Long numberId, Number numberEntity) {
        if (numberrepository.existsById(numberId)) {
            numberEntity.setNumberid(numberId);

            return numberrepository.save(numberEntity);
        }
        throw new RuntimeException("Break not found");
    }

    public void adjustAmounts(int existingBreakAmount, int comingBreakAmount) {
        List<Number> numbers = numberrepository.findAll();

        for (Number numberEntity : numbers) {
            if (comingBreakAmount > existingBreakAmount) {
                int difference = comingBreakAmount - existingBreakAmount;
                numberEntity.setBuyamount(numberEntity.getBuyamount() + difference);
                numberEntity.setOwnamount(numberEntity.getOwnamount() + difference);
            } else if (comingBreakAmount < existingBreakAmount) {
                int reduction = existingBreakAmount - comingBreakAmount;
                numberEntity.setBuyamount(numberEntity.getBuyamount() - reduction);
                numberEntity.setOwnamount(numberEntity.getOwnamount() - reduction);
            }
            numberrepository.save(numberEntity);
        }
    }

    public void restartdb() {
        numberrepository.restartdb();
    }

    public Number findInfoByNumber(int number) {
        return numberrepository.findInfoByNumber(number);
    }

}
