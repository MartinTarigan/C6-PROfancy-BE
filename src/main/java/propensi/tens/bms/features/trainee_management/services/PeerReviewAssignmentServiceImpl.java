package propensi.tens.bms.features.trainee_management.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.repositories.EndUserDb;
import propensi.tens.bms.features.trainee_management.dto.request.PeerReviewAssignmentRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.response.PeerReviewAssignmentResponseDTO;
import propensi.tens.bms.features.trainee_management.models.PeerReviewAssignment;
import propensi.tens.bms.features.trainee_management.repositories.PeerReviewAssignmentRepository;

@Service
public class PeerReviewAssignmentServiceImpl implements PeerReviewAssignmentService {

    @Autowired
    private PeerReviewAssignmentRepository peerReviewAssignmentRepository;

    @Autowired
    private EndUserDb endUserDb;

    @Override
    public PeerReviewAssignmentResponseDTO createPeerReviewAssignment(PeerReviewAssignmentRequestDTO request) throws Exception {
        EndUser reviewer = endUserDb.findByUsername(request.getReviewerUsername());
        EndUser reviewee = endUserDb.findByUsername(request.getRevieweeUsername());
        if (reviewer.getId().equals(reviewee.getId())) {
            throw new Exception("Reviewer and reviewee cannot be the same user");
        }
        PeerReviewAssignment assignment = new PeerReviewAssignment();
        assignment.setReviewer(reviewer);
        assignment.setReviewee(reviewee);
        assignment.setEndDateFill(request.getEndDateFill());
        PeerReviewAssignment saved = peerReviewAssignmentRepository.save(assignment);
        PeerReviewAssignmentResponseDTO response = new PeerReviewAssignmentResponseDTO();
        response.setPeerReviewAssignmentId(saved.getPeerReviewAssignmentId());
        response.setReviewerUsername(saved.getReviewer().getUsername());
        response.setRevieweeUsername(saved.getReviewee().getUsername());
        response.setEndDateFill(saved.getEndDateFill());
        return response;
    }

    @Override
    public List<PeerReviewAssignmentResponseDTO> getAllPeerReviewAssignments() {
        List<PeerReviewAssignment> list = peerReviewAssignmentRepository.findAll();
        List<PeerReviewAssignmentResponseDTO> dtoList = new ArrayList<>();
        for (PeerReviewAssignment assignment : list) {
            PeerReviewAssignmentResponseDTO dto = new PeerReviewAssignmentResponseDTO();
            dto.setPeerReviewAssignmentId(assignment.getPeerReviewAssignmentId());
            dto.setReviewerUsername(assignment.getReviewer().getUsername());
            dto.setRevieweeUsername(assignment.getReviewee().getUsername());
            dto.setEndDateFill(assignment.getEndDateFill());
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public PeerReviewAssignmentResponseDTO getPeerReviewAssignmentById(Integer id) throws Exception {
        PeerReviewAssignment assignment = peerReviewAssignmentRepository.findById(id)
                .orElseThrow(() -> new Exception("Peer review assignment not found with id: " + id));
        PeerReviewAssignmentResponseDTO dto = new PeerReviewAssignmentResponseDTO();
        dto.setPeerReviewAssignmentId(assignment.getPeerReviewAssignmentId());
        dto.setReviewerUsername(assignment.getReviewer().getUsername());
        dto.setRevieweeUsername(assignment.getReviewee().getUsername());
        dto.setEndDateFill(assignment.getEndDateFill());
        return dto;
    }

    // Method untuk menampilkan seluruh reviewer (selain Admin)
    @Override
    public List<String> getAllReviewerUsernamesExceptAdmin() {
        List<EndUser> users = peerReviewAssignmentRepository.findAllExceptAdmin();
        return users.stream().map(EndUser::getUsername).collect(Collectors.toList());
    }

    // Method untuk menampilkan seluruh probation barista (reviewee)
    @Override
    public List<String> getAllProbationBaristaUsernames() {
        List<EndUser> probationUsers = peerReviewAssignmentRepository.findAllProbationBarista();
        return probationUsers.stream().map(EndUser::getUsername).collect(Collectors.toList());
    }
}
