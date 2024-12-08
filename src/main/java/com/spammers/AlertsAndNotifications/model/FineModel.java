package com.spammers.AlertsAndNotifications.model;

import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.model.enums.FineType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="Fines")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class FineModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String fineId;

    @ManyToOne
    @JoinColumn(name="loanId", nullable=false)
    private LoanModel loan;

    @Column(name="description", nullable=false, length=300)
    private String description;

    @Column(name="amount", nullable = false)
    private float amount;

    @Column(name="expiredDate", nullable = false)
    private LocalDate expiredDate;

    @Column(name="studentName", nullable = false)
    private String studentName;

    @Column(name="guardianEmail", nullable = false)
    private String guardianEmail;

    @Column(name="fineStatus", nullable = false)
    private FineStatus fineStatus;
  
    @Column(name="fineType", nullable = false)
    private FineType fineType;

    @OneToMany(mappedBy = "fine", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<FineNotification> fineNotifications;

}
