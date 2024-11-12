package com.thirdeye.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thirdeye.code.entity.Number;

import jakarta.transaction.Transactional;

@Repository
public interface NumberRepository extends JpaRepository<Number, Long> {

    Number findByNumber(int number);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN TRUE ELSE FALSE END FROM Number n WHERE n.number = :number AND n.buyamount = 0")
    boolean existsByNumberAndBuyAmountIsZero(int number);

    @Query("SELECT n FROM Number n WHERE n.buyamount > 0")
    List<Number> findoverbuyamounts();

    @Modifying
    @Transactional
    @Query(value = "delete from testingdemodb1.break", nativeQuery = true)
    void deletebreak();

    @Modifying
    @Transactional
    @Query(value = "delete from testingdemodb1.transaction", nativeQuery = true)
    void deletetransaction();

    @Modifying
    @Transactional
    @Query(value = "delete from testingdemodb1.number", nativeQuery = true)
    void deletenumber();

    @Modifying
    @Transactional
    @Query(value = "delete from testingdemodb1.customer", nativeQuery = true)
    void deletecustomer();

    default void restartdb() {
        deletebreak();
        deletetransaction();
        deletenumber();
        //deletecustomer();
    }

}
