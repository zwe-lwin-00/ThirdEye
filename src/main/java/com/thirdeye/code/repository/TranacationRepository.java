package com.thirdeye.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thirdeye.code.dto.transcationdto;
import com.thirdeye.code.entity.Tranacation;

@Repository
public interface TranacationRepository extends JpaRepository<Tranacation, Long> {


    // @Query("SELECT t FROM Tranacation t WHERE t.buynumber = :luckyNumber")
    // List<Tranacation> findtransbynumber(@Param("luckyNumber") int luckyNumber);


    @Query("SELECT t FROM Tranacation t WHERE t.buynumber = :luckyNumber")
    List<Tranacation> findtransbynumber(@Param("luckyNumber") int luckyNumber);

    

    @Query("SELECT t.buynumber, t.customer.customerid, SUM(t.amount) " +
       "FROM Tranacation t WHERE t.buynumber = :luckyNumber " +
       "GROUP BY t.buynumber, t.customer.customerid")
    List<Object[]> findtransactionsgroupbycus(@Param("luckyNumber") int luckyNumber);

    

    @Query("SELECT t.buynumber, t.customer.customerid, SUM(t.amount) " +
       "FROM Tranacation t WHERE t.buynumber = :luckyNumber " +
       "GROUP BY t.buynumber, t.customer.customerid")
    List<transcationdto> findtransactionsgroupbycus5(@Param("luckyNumber") int luckyNumber);








    
}
