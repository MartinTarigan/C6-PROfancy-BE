package propensi.tens.bms.features.shift_management.overtime.controllers;

import propensi.tens.bms.features.shift_management.overtime.dto.request.OvertimeLogRequest;
import propensi.tens.bms.features.shift_management.overtime.dto.request.OvertimeLogStatusRequest;
import propensi.tens.bms.features.shift_management.overtime.dto.response.OvertimeLogResponse;
import propensi.tens.bms.features.shift_management.overtime.services.OvertimeLogService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/overtime-logs")
@RequiredArgsConstructor
public class OvertimeLogController {

    private final OvertimeLogService overtimeLogService;

    @GetMapping
    public ResponseEntity<List<OvertimeLogResponse>> getAllOvertimeLogs(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort) {

        List<OvertimeLogResponse> logs = overtimeLogService.getAllOvertimeLogs(status, sort);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/barista/{baristaId}")
    public ResponseEntity<List<OvertimeLogResponse>> getOvertimeLogsByBarista(@PathVariable Integer baristaId) {
        return ResponseEntity.ok(overtimeLogService.getOvertimeLogsByBarista(baristaId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OvertimeLogResponse>> getOvertimeLogsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(overtimeLogService.getOvertimeLogsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OvertimeLogResponse> getOvertimeLogById(@PathVariable Integer id) {
        return ResponseEntity.ok(overtimeLogService.getOvertimeLogById(id));
    }

    @PostMapping
    public ResponseEntity<OvertimeLogResponse> createOvertimeLog(@Valid @RequestBody OvertimeLogRequest request) {
        OvertimeLogResponse createdLog = overtimeLogService.createOvertimeLog(request);
        return new ResponseEntity<>(createdLog, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<OvertimeLogResponse> getOvertimeLogDetail(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(overtimeLogService.getOvertimeLogDetail(id));
    }
    

    @PutMapping("/{id}/status")
    public ResponseEntity<OvertimeLogResponse> updateOvertimeLogStatus(
            @PathVariable Integer id,
            @Valid @RequestBody OvertimeLogStatusRequest request) {
        
        OvertimeLogResponse updatedLog = overtimeLogService.updateOvertimeLogStatus(id, request);
        return ResponseEntity.ok(updatedLog);
    }
}