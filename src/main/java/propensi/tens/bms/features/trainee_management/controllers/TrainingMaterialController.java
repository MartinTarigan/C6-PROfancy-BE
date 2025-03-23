package propensi.tens.bms.features.trainee_management.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import propensi.tens.bms.features.trainee_management.dto.request.TrainingMaterialRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.response.BaseResponseDTO;
import propensi.tens.bms.features.trainee_management.dto.response.TrainingMaterialResponseDTO;
import propensi.tens.bms.features.trainee_management.services.TrainingMaterialService;

@RestController
@RequestMapping("/api/training-materials")
public class TrainingMaterialController {

    private final TrainingMaterialService trainingMaterialService;

    public TrainingMaterialController(TrainingMaterialService trainingMaterialService) {
        this.trainingMaterialService = trainingMaterialService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTrainingMaterial(@RequestBody TrainingMaterialRequestDTO request) {
        BaseResponseDTO<TrainingMaterialResponseDTO> response = new BaseResponseDTO<>();
        try {
            TrainingMaterialResponseDTO result = trainingMaterialService.createTrainingMaterial(request);
            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Materi pelatihan berhasil ditambahkan dan ditugaskan.");
            response.setTimestamp(new Date());
            response.setData(result);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Gagal menambahkan materi: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTrainingMaterials() {
        BaseResponseDTO<List<TrainingMaterialResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<TrainingMaterialResponseDTO> materials = trainingMaterialService.getAllTrainingMaterials();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar materi pelatihan berhasil diambil.");
            response.setTimestamp(new Date());
            response.setData(materials);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Gagal mengambil materi pelatihan: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTrainingMaterialDetail(@PathVariable("id") Long id) {
        BaseResponseDTO<TrainingMaterialResponseDTO> response = new BaseResponseDTO<>();
        try {
            TrainingMaterialResponseDTO material = trainingMaterialService.getTrainingMaterialDetail(id);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Detail materi pelatihan berhasil diambil.");
            response.setTimestamp(new Date());
            response.setData(material);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(Exception e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}

