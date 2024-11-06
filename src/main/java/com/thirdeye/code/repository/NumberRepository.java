package com.thirdeye.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thirdeye.code.entity.Number;

@Repository
public interface NumberRepository extends JpaRepository<Number, Long> {
    Number findByNumber(int number);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN TRUE ELSE FALSE END FROM Number n WHERE n.number = :number AND n.buyamount = 0")
    boolean existsByNumberAndBuyAmountIsZero(int number);


    @Query("SELECT n FROM Number n WHERE n.buyamount > 0")
    List<Number> findoverbuyamounts();

}
