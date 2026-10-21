package cl.brarriodigital.report.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "request_reports",
    indexes = {
        @Index(name = "idx_request_reports_status", columnList = "status"),
        @Index(name = "idx_request_reports_procedure_type", columnList = "procedure_type_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class RequestReport {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "request_id", nullable = false, unique = true)
    private UUID requestId;

    @Column(name = "procedure_type_id", nullable = false)
    private UUID procedureTypeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RequestStatus status;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
