package cl.brarriodigital.report.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cl.brarriodigital.report.model.RequestReport;

public interface RequestReportRepository extends JpaRepository<RequestReport, UUID> {

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
