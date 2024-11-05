package com.thirdeye.code.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye.code.entity.Number;
import com.thirdeye.code.entity.Break;
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
        if (breakRepository.existsById(breakId)) {
            breakEntity.setBreakid(breakId); // Set the ID to ensure the entity is updated
            breakEntity.setUpdateddate(LocalDateTime.now());
            return breakRepository.save(breakEntity);
        }
        throw new RuntimeException("Break not found");
    }

    // Get the break amount of the latest updated break
    public Integer getLatestBreakAmount() {
        List<Break> breaks = breakRepository.findTopByOrderByUpdateddateDesc();
        if (!breaks.isEmpty()) {
            return breaks.get(0).getBreakamount();
        }
        return 0;
    }
}
