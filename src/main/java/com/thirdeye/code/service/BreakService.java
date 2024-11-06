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
                throw new RuntimeException("I CLOSED THIS FUNCTION FOR A WHILE SO JUST EDIT THE BREAK AMOUNT AND DO NOT CREATE NEW ONE...");
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

        // Retrieve current break amount and incoming break amount
        int currentBreakAmount = getCurrentBreakAmount();
        int incomingBreakAmount = breakEntity.getBreakamount();

        // Calculate the difference in amounts
        int difference = incomingBreakAmount - currentBreakAmount;

        // Only adjust if there is a difference
        if (difference != 0) {
            adjustBuyAndOwnAmounts(difference);
        }

        // Save the updated break entity
        return breakRepository.save(breakEntity);
    }

    // Helper method to get the current break amount from the most recent break
    private int getCurrentBreakAmount() {
        List<Break> breaks = breakRepository.findTopByOrderByUpdateddateDesc();
        if (breaks.isEmpty()) {
            throw new RuntimeException("No breaks found to compare the amounts");
        }
        return breaks.get(0).getBreakamount();
    }

    // Helper method to adjust the buy and own amounts
    private void adjustBuyAndOwnAmounts(int difference) {
        List<Number> numbers = numberRepository.findoverbuyamounts();

        if (!numbers.isEmpty()) {
            for (Number number : numbers) {
                // Adjust the amounts based on the difference

                int newBuyAmount = number.getBuyamount() - difference;
                if (newBuyAmount < 0) {
                    newBuyAmount = 0;  // Ensure buyamount doesn't go negative
                    number.setBuyamount(newBuyAmount);
                } else {
                    number.setBuyamount(number.getBuyamount() - difference);
                }
                number.setOwnamount(number.getOwnamount() + difference);
                numberRepository.save(number); // Consider batch saving if performance is a concern
            }
        }
    }

    // public Break updateBreak(Long breakId, Break breakEntity) {
    //     if (breakRepository.existsById(breakId)) {
    //         breakEntity.setBreakid(breakId); // Set the ID to ensure the entity is updated
    //         breakEntity.setUpdateddate(LocalDateTime.now());
    //         //adjust the buy and own amounts
    //         List<Break> breaks = breakRepository.findTopByOrderByUpdateddateDesc();
    //         List<Number> numbers = numberRepository.findoverbuyamounts();
    //         int currentbreakamount = breaks.get(0).getBreakamount();
    //         int incomingbreakamount = breakEntity.getBreakamount();
    //         // Check if incoming break amount is greater than current break amount
    //         if (incomingbreakamount > currentbreakamount) {
    //             int differentbreakamount = incomingbreakamount - currentbreakamount; // The excess amount to adjust
    //             // If there are numbers available to adjust
    //             if (!numbers.isEmpty()) {
    //                 for (Number number : numbers) {
    //                     number.setBuyamount(number.getBuyamount() - differentbreakamount);
    //                     number.setOwnamount(number.getOwnamount() + differentbreakamount);
    //                     numberRepository.save(number);
    //                 }
    //             }
    //         } else {
    //             int differentbreakamount = currentbreakamount - incomingbreakamount; // The excess amount to adjust
    //             // If there are numbers available to adjust
    //             if (!numbers.isEmpty()) {
    //                 for (Number number : numbers) {
    //                     number.setBuyamount(number.getBuyamount() + differentbreakamount);
    //                     number.setOwnamount(number.getOwnamount() - differentbreakamount);
    //                     numberRepository.save(number);
    //                 }
    //             }
    //         }
    //         return breakRepository.save(breakEntity);
    //     }
    //     throw new RuntimeException("Break not found");
    // }
    // Get the break amount of the latest updated break
    public Integer getLatestBreakAmount() {
        List<Break> breaks = breakRepository.findTopByOrderByUpdateddateDesc();
        if (!breaks.isEmpty()) {
            return breaks.get(0).getBreakamount();
        }
        return 0;
    }
}
