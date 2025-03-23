package propensi.tens.bms.features.shift_management.overtime.repositories;

import propensi.tens.bms.features.shift_management.overtime.models.OvertimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OvertimeLogDb extends JpaRepository<OvertimeLog, Integer> {
    
    List<OvertimeLog> findByBaristaIdOrderByDateOvertimeDesc(Integer baristaId);
    
    List<OvertimeLog> findByUserIdOrderByDateOvertimeDesc(UUID userId);
    
    List<OvertimeLog> findByOutletIdOrderByDateOvertimeDesc(Integer outletId);

    // Filter berdasarkan status dan urutkan berdasarkan dateOvertime
    List<OvertimeLog> findByStatusOrderByDateOvertimeDesc(OvertimeLog.OvertimeStatus status);

    List<OvertimeLog> findByStatusOrderByDateOvertimeAsc(OvertimeLog.OvertimeStatus status);

    // Filter berdasarkan baristaId (nanti bisa dikaitkan dengan nama dari tabel lain)
    List<OvertimeLog> findByBaristaId(Integer baristaId);
}