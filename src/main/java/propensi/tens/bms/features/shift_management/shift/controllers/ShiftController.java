package propensi.tens.bms.features.shift_management.shift.controllers;

import propensi.tens.bms.features.shift_management.shift.dto.request.ShiftScheduleRequestDto;
import propensi.tens.bms.features.shift_management.shift.dto.response.ShiftScheduleResponseDto;
import propensi.tens.bms.features.shift_management.shift.services.ShiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("api/shift")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    private final Logger logger = LoggerFactory.getLogger(ShiftController.class);


    @GetMapping("/{outletId}")
    public ResponseEntity<?> getShiftsByOutletAndDateRange(
            @PathVariable Long outletId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        logger.info("===== START getShiftsByOutletAndDateRange =====");
        logger.info("Received request: outletId=" + outletId + ", startDate=" + startDate + ", endDate=" + endDate);

        try {
            if (startDate == null || endDate == null) {
                logger.error("Missing required query params: startDate or endDate is null");
                return ResponseEntity.badRequest().body("Missing required query parameters: startDate or endDate.");
            }

            LocalDate start;
            LocalDate end;

            try {
                start = LocalDate.parse(startDate);
                logger.info("Parsed startDate: " + start);
            } catch (Exception e) {
                logger.error("Failed to parse startDate: " + startDate, e);
                return ResponseEntity.badRequest().body("Invalid startDate format. Expected format: YYYY-MM-DD");
            }

            try {
                end = LocalDate.parse(endDate);
                logger.info("Parsed endDate: " + end);
            } catch (Exception e) {
                logger.error("Failed to parse endDate: " + endDate, e);
                return ResponseEntity.badRequest().body("Invalid endDate format. Expected format: YYYY-MM-DD");
            }

            // Validasi range date
            if (start.isAfter(end)) {
                logger.error("Start date is after end date! startDate=" + start + ", endDate=" + end);
                return ResponseEntity.badRequest().body("Start date cannot be after end date.");
            }

            // Log sebelum ke service
            logger.info("Calling service getShiftsByOutletAndDateRange with outletId=" + outletId + ", start=" + start + ", end=" + end);

            List<ShiftScheduleResponseDto> shifts = shiftService.getShiftsByOutletAndDateRange(outletId, start, end);

            logger.info("Service returned " + shifts.size() + " shift(s)");

            logger.info("===== END getShiftsByOutletAndDateRange SUCCESS =====");
            return ResponseEntity.ok(shifts);

        } catch (Exception e) {
            logger.error("===== ERROR getShiftsByOutletAndDateRange =====", e);
            e.printStackTrace(); // Tampilkan stack trace lengkap
            return ResponseEntity.internalServerError().body("Internal server error. Check logs for details.");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ShiftScheduleResponseDto> createShift(@RequestBody ShiftScheduleRequestDto requestDto) {
        ShiftScheduleResponseDto responseDto = shiftService.createShift(requestDto);
        return ResponseEntity.ok(responseDto);
    }



}
