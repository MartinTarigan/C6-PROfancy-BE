package propensi.tens.bms.features.trainee_management.services;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import propensi.tens.bms.features.trainee_management.dto.request.TrainingMaterialRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.response.TrainingMaterialResponseDTO;
import propensi.tens.bms.features.trainee_management.enums.MaterialType;
import propensi.tens.bms.features.trainee_management.models.TrainingMaterial;
import propensi.tens.bms.features.trainee_management.repositories.TrainingMaterialDB;

@Service
@Transactional
public class TrainingMaterialServiceImpl implements TrainingMaterialService {

    @Autowired
    private TrainingMaterialDB trainingMaterialDb;

    @Override
    public TrainingMaterialResponseDTO createTrainingMaterial(TrainingMaterialRequestDTO request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Judul materi wajib diisi.");
        }
        if (request.getLink() == null || request.getLink().trim().isEmpty()) {
            throw new RuntimeException("Link materi wajib diisi.");
        }
        if (request.getAssignedRoles() == null || request.getAssignedRoles().isEmpty()) {
            throw new RuntimeException("Setidaknya assign materi ke satu role.");
        }

        MaterialType materialType;
        try {
            materialType = MaterialType.valueOf(request.getType().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Tipe materi tidak valid. Gunakan VIDEO atau DOCUMENT.");
        }

        TrainingMaterial trainingMaterial = new TrainingMaterial();
        trainingMaterial.setTitle(request.getTitle());
        trainingMaterial.setType(materialType);
        trainingMaterial.setLink(request.getLink());
        trainingMaterial.setDescription(request.getDescription());
        trainingMaterial.setCreatedAt(new Date());

        trainingMaterial.setAssignedRoles(new HashSet<>(request.getAssignedRoles()));

        TrainingMaterial saved = trainingMaterialDb.save(trainingMaterial);
        return toResponse(saved);
    }

    @Override
    public List<TrainingMaterialResponseDTO> getAllTrainingMaterials() {
        List<TrainingMaterial> list = trainingMaterialDb.findAll();
        List<TrainingMaterialResponseDTO> responseList = new ArrayList<>();
        for(TrainingMaterial tm : list){
            responseList.add(toResponse(tm));
        }
        return responseList;
    }

    @Override
    public TrainingMaterialResponseDTO getTrainingMaterialDetail(Long id) {
        Optional<TrainingMaterial> opt = trainingMaterialDb.findById(id);
        if(opt.isPresent()){
            return toResponse(opt.get());
        } else {
            throw new RuntimeException("Materi pelatihan tidak ditemukan.");
        }
    }

    private TrainingMaterialResponseDTO toResponse(TrainingMaterial entity) {
        TrainingMaterialResponseDTO response = new TrainingMaterialResponseDTO();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setType(entity.getType().name());
        response.setLink(entity.getLink());
        response.setDescription(entity.getDescription());
        response.setCreatedAt(entity.getCreatedAt());
        response.setAssignedRoles(new ArrayList<>(entity.getAssignedRoles()));

        return response;
    }

}

