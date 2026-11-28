package cl.brarriodigital.report.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.brarriodigital.report.model.RequestReport;
import jakarta.persistence.LockModeType;

public interface RequestReportRepository extends JpaRepository<RequestReport, UUID> {

    Optional<RequestReport> findByRequestId(UUID requestId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select report from RequestReport report where report.requestId = :requestId")
    Optional<RequestReport> findByRequestIdForUpdate(@Param("requestId") UUID requestId);

    @Query("""
        select report.status as status, count(report.id) as total
        from RequestReport report
        group by report.status
        """)
    List<StatusCountProjection> countRequestsByStatus();

    @Query("""
        select report.procedureTypeId as procedureTypeId, count(report.id) as total
        from RequestReport report
        group by report.procedureTypeId
        order by count(report.id) desc, report.procedureTypeId asc
        """)
    List<ProcedureTypeCountProjection> countRequestsByProcedureType();
}
