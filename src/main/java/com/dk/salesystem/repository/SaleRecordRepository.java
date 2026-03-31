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

    List<SaleRecord> findBySaleDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT SUM(sr.totalPrice) FROM SaleRecord sr WHERE sr.saleDate = :date")
    java.math.BigDecimal sumBySaleDate(@Param("date") LocalDate date);

    @Query("SELECT SUM(sr.totalPrice) FROM SaleRecord sr WHERE sr.saleDate BETWEEN :start AND :end")
    java.math.BigDecimal sumBySaleDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
