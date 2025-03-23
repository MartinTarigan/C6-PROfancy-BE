package propensi.tens.bms.features.account_management.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import propensi.tens.bms.features.account_management.services.BaristaService;
import propensi.tens.bms.features.account_management.dto.response.BaristaDropdownResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/baristas")
public class BaristaController {

    @Autowired
    private BaristaService baristaService;

    @GetMapping
    public ResponseEntity<List<BaristaDropdownResponseDTO>> getBaristasByOutlet(@RequestParam Long outletId) {
        List<BaristaDropdownResponseDTO> baristas = baristaService.getBaristasForDropdown(outletId);
        return ResponseEntity.ok(baristas);
    }
}
