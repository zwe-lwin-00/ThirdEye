package com.thirdeye.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thirdeye.code.dto.TranacationSummary;
import com.thirdeye.code.entity.Tranacation;

@Repository
public interface TranacationRepository extends JpaRepository<Tranacation, Long> {

   // @Query("SELECT t FROM Tranacation t WHERE t.buynumber = :luckyNumber")
   // List<Tranacation> findtransbynumber(@Param("luckyNumber") int luckyNumber);

   @Query("SELECT t FROM Tranacation t WHERE t.buynumber = :luckyNumber")
   List<Tranacation> findalltranscations(@Param("luckyNumber") int luckyNumber);

   @Query("SELECT t.buynumber AS buynumber, t.customer AS customer, SUM(t.amount) AS totalAmount " +
         "FROM Tranacation t WHERE t.buynumber = :luckyNumber " +
         "GROUP BY t.buynumber, t.customer")
   List<Object[]> findtransgroupbycustomer(@Param("luckyNumber") int luckyNumber);

}
