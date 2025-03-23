package propensi.tens.bms.features.shift_management.overtime.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import propensi.tens.bms.features.account_management.models.Outlet;
import propensi.tens.bms.features.account_management.repositories.OutletDb;
import propensi.tens.bms.features.shift_management.overtime.dto.request.OvertimeLogRequest;
import propensi.tens.bms.features.shift_management.overtime.dto.request.OvertimeLogStatusRequest;
import propensi.tens.bms.features.shift_management.overtime.dto.response.OvertimeLogResponse;
import propensi.tens.bms.features.shift_management.overtime.models.OvertimeLog;
import propensi.tens.bms.features.shift_management.overtime.repositories.OvertimeLogDb;


import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OvertimeLogService {

    private final OvertimeLogDb overtimeLogDb;
    private final OutletDb outletDb;

    // Get All Overtime Logs
    public List<OvertimeLogResponse> getAllOvertimeLogs(String status, String sort) {
        List<OvertimeLog> logs;

        if (status != null) {
            OvertimeLog.OvertimeStatus statusEnum;
            try {
                statusEnum = OvertimeLog.OvertimeStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter: {}", status);
                throw new IllegalArgumentException("Status tidak valid!");
            }

            logs = "desc".equalsIgnoreCase(sort)
                    ? overtimeLogDb.findByStatusOrderByDateOvertimeDesc(statusEnum)
                    : overtimeLogDb.findByStatusOrderByDateOvertimeAsc(statusEnum);

        } else {
            logs = overtimeLogDb.findAll();

            logs.sort("asc".equalsIgnoreCase(sort)
                    ? Comparator.comparing(OvertimeLog::getDateOvertime)
                    : Comparator.comparing(OvertimeLog::getDateOvertime).reversed());
        }

        return logs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get Overtime Logs by Barista ID
    public List<OvertimeLogResponse> getOvertimeLogsByBarista(Integer baristaId) {
        return overtimeLogDb.findByBaristaIdOrderByDateOvertimeDesc(baristaId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get Overtime Logs by User ID
    public List<OvertimeLogResponse> getOvertimeLogsByUser(UUID userId) {
        return overtimeLogDb.findByUserIdOrderByDateOvertimeDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Get Overtime Log by ID
    public OvertimeLogResponse getOvertimeLogById(Integer id) {
        OvertimeLog overtimeLog = overtimeLogDb.findById(id)
                .orElseThrow(() -> {
                    log.warn("Overtime log not found with id: {}", id);
                    return new RuntimeException("Overtime log tidak ditemukan dengan id: " + id);
                });

        return mapToResponse(overtimeLog);
    }

    // Create Overtime Log
    public OvertimeLogResponse createOvertimeLog(OvertimeLogRequest request) {
        log.info("Membuat overtime log untuk baristaId: {}, userId: {}", request.getBaristaId(), request.getUserId());

        OvertimeLog overtimeLog = new OvertimeLog();
        overtimeLog.setBaristaId(request.getBaristaId());
        overtimeLog.setUserId(request.getUserId());
        overtimeLog.setOutletId(request.getOutletId());
        overtimeLog.setDateOvertime(request.getDateOvertime());
        overtimeLog.setStartHour(request.getStartHour());
        overtimeLog.setDuration(request.getDuration());
        overtimeLog.setReason(request.getReason());
        overtimeLog.setStatus(OvertimeLog.OvertimeStatus.PENDING);

        OvertimeLog savedLog = overtimeLogDb.save(overtimeLog);

        log.info("Overtime log berhasil disimpan dengan ID: {}", savedLog.getOvertimeLogId());

        return mapToResponse(savedLog);
    }

    // Get Detail Overtime Log
    public OvertimeLogResponse getOvertimeLogDetail(Integer id) {
        OvertimeLog overtimeLog = overtimeLogDb.findById(id)
                .orElseThrow(() -> {
                    log.warn("Detail overtime log tidak ditemukan dengan id: {}", id);
                    return new RuntimeException("Detail overtime log tidak ditemukan dengan id: " + id);
                });

        return mapToResponse(overtimeLog);
    }
    
    // Update Overtime Log Status
    public OvertimeLogResponse updateOvertimeLogStatus(Integer id, OvertimeLogStatusRequest request) {
        log.info("Memperbarui status lembur dengan ID: {}", id);
        
        OvertimeLog overtimeLog = overtimeLogDb.findById(id)
                .orElseThrow(() -> {
                    log.warn("Overtime log tidak ditemukan dengan id: {}", id);
                    return new RuntimeException("Overtime log tidak ditemukan dengan id: " + id);
                });
        
        try {
            // Validate the status
            OvertimeLog.OvertimeStatus newStatus;
            try {
                newStatus = OvertimeLog.OvertimeStatus.valueOf(request.getStatus());
            } catch (IllegalArgumentException e) {
                log.warn("Status tidak valid: {}", request.getStatus());
                throw new IllegalArgumentException("Status tidak valid: " + request.getStatus());
            }
            
            // Make sure it's only ONGOING or CANCELLED
            if (newStatus != OvertimeLog.OvertimeStatus.ONGOING && newStatus != OvertimeLog.OvertimeStatus.CANCELLED) {
                log.warn("Status hanya bisa diubah menjadi ONGOING atau CANCELLED");
                throw new IllegalArgumentException("Status hanya bisa diubah menjadi ONGOING atau CANCELLED");
            }
            
            // Make sure the current status is PENDING
            if (overtimeLog.getStatus() != OvertimeLog.OvertimeStatus.PENDING) {
                log.warn("Hanya log lembur dengan status PENDING yang dapat diubah");
                throw new IllegalArgumentException("Hanya log lembur dengan status PENDING yang dapat diubah");
            }
            
            // Update the status
            overtimeLog.setStatus(newStatus);
            
            // Save the updated overtime log
            OvertimeLog updatedLog = overtimeLogDb.save(overtimeLog);
            log.info("Status lembur berhasil diperbarui ke: {}", newStatus);
            
            return mapToResponse(updatedLog);
        } catch (Exception e) {
            log.error("Gagal memperbarui status lembur: {}", e.getMessage());
            throw e;
        }
    }

    // Mapper dari Entity ke Response
    private OvertimeLogResponse mapToResponse(OvertimeLog overtimeLog) {
        return OvertimeLogResponse.builder()
                .id(overtimeLog.getOvertimeLogId())
                .baristaId(overtimeLog.getBaristaId())
                .userId(overtimeLog.getUserId())
                .outletId(overtimeLog.getOutletId())
                .dateOvertime(overtimeLog.getDateOvertime())
                .startHour(overtimeLog.getStartHour())
                .duration(overtimeLog.getDuration())
                .reason(overtimeLog.getReason())
                .status(overtimeLog.getStatus())
                .statusDisplay(overtimeLog.getStatus().getDisplayValue())
                .verifier(overtimeLog.getVerifier())
                .outletName(getOutletName(overtimeLog.getOutletId()))
                .createdAt(overtimeLog.getCreatedAt())
                .updatedAt(overtimeLog.getUpdatedAt())
                .build();
    }

    // Placeholder Get Outlet Name (Nanti bisa dihubungkan ke outletDb)
    private String getOutletName(Integer outletId) {
        log.info("Mengambil nama outlet untuk outletId: {}", outletId);

        try {
            return outletDb.findById(Long.valueOf(outletId))
                    .map(Outlet::getName)
                    .orElse("Outlet Tidak Ditemukan");
        } catch (Exception e) {
            log.error("Error mengambil outletName untuk outletId {}: {}", outletId, e.getMessage());
            return "Outlet Error";
        }
    }
}