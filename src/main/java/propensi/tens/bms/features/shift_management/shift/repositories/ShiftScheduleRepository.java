package propensi.tens.bms.features.shift_management.shift.repositories;


import propensi.tens.bms.features.shift_management.shift.models.ShiftSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ShiftScheduleRepository extends JpaRepository<ShiftSchedule, Long> {

    List<ShiftSchedule> findByOutletId(Long outletId);

    List<ShiftSchedule> findByDateShift(LocalDate date);

    List<ShiftSchedule> findByOutletIdAndDateShiftBetween(Long outletId, LocalDate startDate, LocalDate endDate);

    Optional<ShiftSchedule> findByDateShiftAndShiftTypeAndOutletIdAndHeadBarId(LocalDate dateShift, Integer shiftType, Long outletId, UUID headBarId);

    @Query("SELECT s FROM ShiftSchedule s WHERE s.outletId = :outletId")
    List<ShiftSchedule> getShiftsByUserRole(@Param("role") String role, @Param("outletId") Long outletId);
}
