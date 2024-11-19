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
            // adjustBuyAndOwnAmounts(difference);
            changeincomingisbigger(difference);
        }
        return breakRepository.save(breakEntity);
    }

    // public void changeincomingisbigger(int difference) {
    // List<Number> numbers = numberRepository.findoverbuyamounts();

    // if (!numbers.isEmpty()) {
    // for (Number number : numbers) {
    // int loopown = number.getOwnamount();
    // int loopbuy = number.getBuyamount();
    // System.out.println("----------Before stage --------------------#");
    // System.out.println("Own amount " + number.getOwnamount());
    // System.out.println("Buy amount " + number.getBuyamount());
    // System.out.println("");
    // if (difference > 0) {
    // System.out.println("Difference is PLUS value" + difference);
    // if (difference > number.getBuyamount()) {
    // System.out.println("PLUS Difference is more than buy");
    // number.setOwnamount(loopown + loopbuy);
    // number.setBuyamount(0);
    // } else if (difference < number.getBuyamount()) {
    // System.out.println("PLUS Difference is less than buy");
    // int diff = loopbuy - difference;
    // number.setBuyamount(diff);
    // number.setOwnamount(loopown + difference);
    // } else {
    // System.out.println("PLUS Extra for a while");
    // }

    // } else if (difference < 0) {
    // System.out.println("Difference is MINUS value");
    // if (difference > number.getOwnamount()) {
    // System.out.println("MINUS Difference is less than own");
    // } else if (difference < number.getOwnamount()) {
    // System.out.println("MINUS Difference is more than own");
    // number.setOwnamount(0);
    // number.setBuyamount(loopbuy + loopown);

    // } else {
    // System.out.println("MINUS Extra for a while");
    // }

    // } else {
    // System.out.println("Extra for a while");
    // }

    // System.out.println("----------After stage --------------------#");
    // System.out.println("Own amount " + number.getOwnamount());
    // System.out.println("Buy amount " + number.getBuyamount());
    // System.out.println("----------After stage --------------------#");
    // System.out.println("");

    // }
    // }
    // }
    public void changeincomingisbigger(int difference) {
        List<Number> numbers = numberRepository.findoverbuyamounts();

        if (!numbers.isEmpty()) {
            for (Number number : numbers) {
                int ownAmount = number.getOwnamount();
                int buyAmount = number.getBuyamount();

                System.out.println("---------- Before Stage --------------------#");
                System.out.println("Own amount: " + ownAmount);
                System.out.println("Buy amount: " + buyAmount);
                System.out.println("");

                if (difference > 0) { // Positive difference
                    System.out.println("Difference is a PLUS value: " + difference);
                    if (difference >= buyAmount) {
                        System.out.println("PLUS Difference is more than or equal to buy amount");
                        number.setOwnamount(ownAmount + buyAmount);
                        number.setBuyamount(0);
                    } else {
                        System.out.println("PLUS Difference is less than buy amount");
                        number.setBuyamount(buyAmount - difference);
                        number.setOwnamount(ownAmount + difference);
                    }
                } else if (difference < 0) { // Negative difference
                    int absDiff = Math.abs(difference); // Absolute value of the difference
                    System.out.println("Difference is a MINUS value: " + difference);
                    if (absDiff >= ownAmount) {
                        System.out.println("MINUS Difference is more than or equal to own amount");
                        number.setOwnamount(0);
                        number.setBuyamount(buyAmount + ownAmount);
                    } else {
                        System.out.println("MINUS Difference is less than own amount");
                        number.setOwnamount(ownAmount - absDiff);
                        number.setBuyamount(buyAmount + absDiff);
                    }
                } else {
                    System.out.println("No adjustment needed (Difference is zero).");
                }

                System.out.println("---------- After Stage --------------------#");
                System.out.println("Own amount: " + number.getOwnamount());
                System.out.println("Buy amount: " + number.getBuyamount());
                System.out.println("");
                numberRepository.save(number);
            }
        } else {
            System.out.println("No numbers found to process.");
        }
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
