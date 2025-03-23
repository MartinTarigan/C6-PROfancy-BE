package propensi.tens.bms.features.shift_management.shift.services;

import propensi.tens.bms.features.shift_management.shift.controllers.ShiftController;
import propensi.tens.bms.features.shift_management.shift.dto.request.ShiftScheduleRequestDto;
import propensi.tens.bms.features.shift_management.shift.dto.response.ShiftScheduleResponseDto;
import propensi.tens.bms.features.shift_management.shift.models.ShiftSchedule;
import propensi.tens.bms.features.shift_management.shift.repositories.ShiftScheduleRepository;

import propensi.tens.bms.features.account_management.dto.response.BaristaDropdownResponseDTO;
import propensi.tens.bms.features.account_management.models.Barista;
import propensi.tens.bms.features.account_management.models.HeadBar;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.models.ProbationBarista;
import propensi.tens.bms.features.account_management.repositories.EndUserDb;

import propensi.tens.bms.features.account_management.services.OutletService;
import propensi.tens.bms.features.account_management.services.EndUserService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ShiftServiceImpl implements ShiftService {

    @Autowired
    private ShiftScheduleRepository shiftScheduleRepository;

    private final Logger logger = LoggerFactory.getLogger(ShiftController.class);


    @Autowired
    private EndUserDb endUserRepository;

    @Autowired
    private OutletService outletService;

    @Autowired
    private EndUserService endUserService;

    @Override
    public List<ShiftScheduleResponseDto> getShiftsByUserRole(String role, Long outletId) {
        List<ShiftSchedule> shiftSchedules;

        if (role.equalsIgnoreCase("HeadBar") || role.equalsIgnoreCase("HeadBarista")) {
            shiftSchedules = shiftScheduleRepository.findByOutletId(outletId);
        } else if (role.equalsIgnoreCase("Barista") || role.equalsIgnoreCase("ProbationBarista")) {
            shiftSchedules = shiftScheduleRepository.findByOutletId(outletId);
        } else {
            // Admin / C-Level biasanya fetch pakai range, jadi kosongin di sini
            shiftSchedules = new ArrayList<>();
        }

        return shiftSchedules.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShiftScheduleResponseDto createShift(ShiftScheduleRequestDto dto) {
        // Membuat objek ShiftSchedule baru
        ShiftSchedule shift = new ShiftSchedule();
        System.out.println("ShiftServiceImpl.createShift: dto = " + dto);
        // Set shiftType
        shift.setShiftType(dto.getShiftType());

        // Pastikan dateShift sudah berupa LocalDate
        shift.setDateShift(dto.getDateShift());  // `dateShift` di DTO sudah LocalDate, jadi langsung set ke LocalDate

        // Mendapatkan headBarId dari DTO
        UUID headBarId = dto.getHeadBarId();
        if (headBarId == null) {
            throw new IllegalArgumentException("HeadBarId is required"); // HeadBarId wajib ada
        }
        shift.setHeadBarId(headBarId);  // Set headBarId ke ShiftSchedule

        // Set outlet ID
        shift.setOutletId(dto.getOutletId());

        // Logika untuk assign barista jika ada listBaristaIds di request DTO
        if (dto.getBaristaIds() != null && !dto.getBaristaIds().isEmpty()) {
            // Mendapatkan barista berdasarkan ID yang dikirim dari frontend
            List<EndUser> baristas = endUserRepository.findAllById(dto.getBaristaIds());
            shift.setListBarista(baristas);  // Set barista ke shift
        }

        // Simpan shift ke database
        ShiftSchedule savedShift = shiftScheduleRepository.save(shift);

        // Kembalikan response DTO yang sudah diubah
        return convertToResponseDto(savedShift);
    }


    @Override
    public List<ShiftScheduleResponseDto> getShiftsByOutletAndDateRange(Long outletId, LocalDate startDate, LocalDate endDate) {
        try {
            logger.info("Fetching shifts for outlet " + outletId + " from " + startDate + " to " + endDate);
            
            List<ShiftSchedule> shiftEntities = shiftScheduleRepository.findByOutletIdAndDateShiftBetween(outletId, startDate, endDate);
            
            if (shiftEntities.isEmpty()) {
                logger.warn("No shifts found for outlet " + outletId + " in the given date range!");
            }

            return shiftEntities.stream().map(this::convertToResponseDto).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching shifts - " + e.getMessage());
            e.printStackTrace(); // Menampilkan stack trace error untuk debugging
            throw new RuntimeException("Failed to fetch shifts. Check logs for details.");
        }
    }

    private ShiftScheduleResponseDto convertToResponseDto(ShiftSchedule shift) {
        ShiftScheduleResponseDto dto = new ShiftScheduleResponseDto();

        dto.setShiftScheduleId(shift.getShiftScheduleId());
        dto.setShiftType(shift.getShiftType());
        dto.setDateShift(shift.getDateShift());

        dto.setOutletId(shift.getOutletId());
        dto.setOutletName(getOutletName(shift.getOutletId()));

        dto.setHeadBarId(shift.getHeadBarId());
        dto.setHeadBarName(getHeadBarName(shift.getHeadBarId()));

        List<BaristaDropdownResponseDTO> baristasDto = shift.getListBarista().stream().map(barista -> {
            BaristaDropdownResponseDTO bDto = new BaristaDropdownResponseDTO();
            bDto.setId(barista.getId().toString());
            bDto.setFullName(barista.getFullName());
            bDto.setRole(getRoleFromUser(barista));
            return bDto;
        }).collect(Collectors.toList());

        dto.setBaristas(baristasDto);

        return dto;
    }

    private String getOutletName(Long outletId) {
        return outletService.findById(outletId)
                .map(outlet -> outlet.getName()) // Make sure field is 'name'
                .orElse("Outlet Tidak Ditemukan");
    }

    private String getHeadBarName(UUID headBarId) {
        return endUserService.findById(headBarId)
                .map(user -> user.getFullName())
                .orElse("HeadBar Tidak Ditemukan");
    }

    private String getRoleFromUser(EndUser user) {
        if (user instanceof HeadBar) return "HeadBar";
        if (user instanceof Barista) return "Barista";
        if (user instanceof ProbationBarista) return "ProbationBarista";
        return "Unknown";
    }
}
