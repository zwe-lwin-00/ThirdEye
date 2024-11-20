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

    public Break createBreak(Break breakEntity) {
        int existingbreakamount = getLatestBreakAmount();
        int comingbreakamount = breakEntity.getBreakamount();

        if (existingbreakamount > 0) {
            if (existingbreakamount != 0.991) {
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
            amountadjustinglogic(currentBreakAmount, incomingBreakAmount);
        }
        return breakRepository.save(breakEntity);
    }

    public void amountadjustinglogic(int currentBreakAmount, int incomingBreakAmount) {
        List<Number> numbers = numberRepository.findAll();
        if (!numbers.isEmpty()) {
            for (Number number : numbers) {

                int ownAmount = number.getOwnamount();
                int buyAmount = number.getBuyamount();

                int totalamount = ownAmount + buyAmount;

                if (totalamount < incomingBreakAmount) {
                    number.setBuyamount(0);
                    number.setOwnamount(ownAmount + buyAmount);
                } else if (totalamount == incomingBreakAmount) {
                    number.setBuyamount(0);
                    number.setOwnamount(incomingBreakAmount);
                } else if (totalamount > incomingBreakAmount) {
                    number.setBuyamount(totalamount - incomingBreakAmount);
                    number.setOwnamount(incomingBreakAmount);
                }
                numberRepository.save(number);
            }
        }
    }

    private int getCurrentBreakAmount() {
        List<Break> breaks = breakRepository.findTopByOrderByUpdateddateDesc();
        if (breaks.isEmpty()) {
            throw new RuntimeException("No breaks found to compare the amounts");
        }
        return breaks.get(0).getBreakamount();
    }

    public Integer getLatestBreakAmount() {
        List<Break> breaks = breakRepository.findTopByOrderByUpdateddateDesc();
        if (!breaks.isEmpty()) {
            return breaks.get(0).getBreakamount();
        }
        return 0;
    }
}
