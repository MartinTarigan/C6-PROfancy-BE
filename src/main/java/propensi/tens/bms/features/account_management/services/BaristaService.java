package propensi.tens.bms.features.account_management.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.tens.bms.features.account_management.models.Barista;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.models.HeadBar;
import propensi.tens.bms.features.account_management.models.ProbationBarista;
import propensi.tens.bms.features.account_management.repositories.BaristaDb;
import propensi.tens.bms.features.account_management.repositories.HeadBarDb;
import propensi.tens.bms.features.account_management.repositories.ProbationBaristaDb;
import propensi.tens.bms.features.account_management.dto.response.*;

import java.util.List;
import java.util.ArrayList;
@Service
public class BaristaService {

    @Autowired
    private BaristaDb baristaDb;

    @Autowired
    private HeadBarDb headBarDb;

    @Autowired
    private ProbationBaristaDb probationBaristaDb;

    public List<BaristaDropdownResponseDTO> getBaristasForDropdown(Long outletId) {
        List<BaristaDropdownResponseDTO> result = new ArrayList<>();

        List<Barista> baristas = baristaDb.findByOutlet_OutletId(outletId);
        List<ProbationBarista> probations = probationBaristaDb.findByOutlet_OutletId(outletId);
        List<HeadBar> headBars = headBarDb.findByOutlet_OutletId(outletId);

        baristas.forEach(b -> result.add(mapToDropdownDto(b, "Barista")));
        probations.forEach(p -> result.add(mapToDropdownDto(p, "Probation Barista")));
        headBars.forEach(h -> result.add(mapToDropdownDto(h, "Head Bar")));

        return result;
    }

    private BaristaDropdownResponseDTO mapToDropdownDto(EndUser user, String role) {
        return new BaristaDropdownResponseDTO(
            user.getId().toString(),
            user.getFullName(),
            role
        );
    }
}