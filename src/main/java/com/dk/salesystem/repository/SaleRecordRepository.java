package com.dk.salesystem.repository;

import com.dk.salesystem.entity.SaleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRecordRepository extends JpaRepository<SaleRecord, Long> {
    List<SaleRecord> findBySaleDate(LocalDate date);

    @Query("SELECT sr FROM SaleRecord sr WHERE sr.saleDate BETWEEN :startDate AND :endDate ORDER BY sr.saleDate")
    List<SaleRecord> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
