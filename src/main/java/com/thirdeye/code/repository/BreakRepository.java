package com.thirdeye.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thirdeye.code.entity.Break;

@Repository
public interface BreakRepository extends JpaRepository<Break, Long> {
    @Query("SELECT b FROM Break b ORDER BY b.updateddate DESC")
    List<Break> findTopByOrderByUpdateddateDesc();

}
