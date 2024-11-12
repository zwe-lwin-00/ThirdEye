package com.thirdeye.code.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye.code.entity.Break;
import com.thirdeye.code.entity.Number;
import com.thirdeye.code.repository.BreakRepository;
import com.thirdeye.code.repository.NumberRepository;

@Service
public class BreakService {

    @Autowired
    private BreakRepository breakRepository;

    @Autowired
    private NumberRepository numberRepository;

    public List<Break> findAll() {
        return breakRepository.findAll();
    }

    public Break findById(Long id) {
        Optional<Break> breakOptional = breakRepository.findById(id);
        return breakOptional.orElseThrow(() -> new RuntimeException("Break not found"));
    }

    public void deleteBreak(Long id) {
        breakRepository.deleteById(id);
    }

    // Insert a new Break
    public Break createBreak(Break breakEntity) {
        int existingbreakamount = getLatestBreakAmount();
        int comingbreakamount = breakEntity.getBreakamount();
        
        
        if (existingbreakamount > 0) {
            if (existingbreakamount!=0.991) {
                throw new RuntimeException("I CLOSED THIS FUNCTION FOR A WHILE...");
            }
            adjustAmounts(existingbreakamount, comingbreakamount);
        }

        breakEntity.setUpdateddate(LocalDateTime.now());
        return breakRepository.save(breakEntity);
    }

    public void adjustAmounts(int existingBreakAmount, int comingBreakAmount) {
        List<Number> numbers = numberRepository.findAll();

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
            numberRepository.save(numberEntity);
        }
    }

    // Update an existing Break
    public Break updateBreak(Long breakId, Break breakEntity) {
        if (!breakRepository.existsById(breakId)) {
            throw new RuntimeException("Break not found");
        }

        breakEntity.setBreakid(breakId);
        breakEntity.setUpdateddate(LocalDateTime.now());
        int currentBreakAmount = getCurrentBreakAmount();
        int incomingBreakAmount = breakEntity.getBreakamount();
        int difference = incomingBreakAmount - currentBreakAmount;
        if (difference != 0) {
            adjustBuyAndOwnAmounts(difference);
        }
        return breakRepository.save(breakEntity);
    }
    private int getCurrentBreakAmount() {
        List<Break> breaks = breakRepository.findTopByOrderByUpdateddateDesc();
        if (breaks.isEmpty()) {
            throw new RuntimeException("No breaks found to compare the amounts");
        }
        return breaks.get(0).getBreakamount();
    }
    private void adjustBuyAndOwnAmounts(int difference) {
        List<Number> numbers = numberRepository.findoverbuyamounts();

        if (!numbers.isEmpty()) {
            for (Number number : numbers) {

                int newBuyAmount = number.getBuyamount() - difference;
                if (newBuyAmount < 0) {
                    newBuyAmount = 0;
                    number.setBuyamount(newBuyAmount);
                } else {
                    number.setBuyamount(number.getBuyamount() - difference);
                }
                number.setOwnamount(number.getOwnamount() + difference);
                numberRepository.save(number); 
            }
        }
    }

    public Integer getLatestBreakAmount() {
        List<Break> breaks = breakRepository.findTopByOrderByUpdateddateDesc();
        if (!breaks.isEmpty()) {
            return breaks.get(0).getBreakamount();
        }
        return 0;
    }
}
