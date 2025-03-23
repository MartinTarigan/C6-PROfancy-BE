package propensi.tens.bms.features.trainee_management.models;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;
import propensi.tens.bms.features.account_management.models.EndUser;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "peer_review_assignment")
public class PeerReviewAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer peerReviewAssignmentId;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private EndUser reviewer;

    @ManyToOne
    @JoinColumn(name = "reviewed_user_id", nullable = false)
    private EndUser reviewee;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date_fill", nullable = false)
    private Date endDateFill;
}
