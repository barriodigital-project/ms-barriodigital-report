package cl.brarriodigital.report.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import cl.brarriodigital.report.model.RequestReport;
import cl.brarriodigital.report.model.RequestStatus;

@DataJpaTest
class RequestReportRepositoryTest {

    @Autowired
    private RequestReportRepository repository;

    private UUID commonProcedureTypeId;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        commonProcedureTypeId = UUID.randomUUID();
        saveRequest(commonProcedureTypeId, RequestStatus.INGRESADO);
        saveRequest(commonProcedureTypeId, RequestStatus.RESUELTO);
        saveRequest(UUID.randomUUID(), RequestStatus.RECHAZADO);
    }

    @Test
    void countsCurrentRequestsByStatus() {
        var counts = repository.countRequestsByStatus();

        assertThat(counts).hasSize(3);
        assertThat(counts)
            .extracting(StatusCountProjection::getStatus)
            .containsExactlyInAnyOrder(
                RequestStatus.INGRESADO,
                RequestStatus.RESUELTO,
                RequestStatus.RECHAZADO
            );
        assertThat(counts)
            .filteredOn(count -> count.getStatus() == RequestStatus.INGRESADO)
            .singleElement()
            .extracting(StatusCountProjection::getTotal)
            .isEqualTo(1L);
    }

    @Test
    void ranksProcedureTypesByRequestCount() {
        var ranking = repository.countRequestsByProcedureType();

        assertThat(ranking).hasSize(2);
        assertThat(ranking.getFirst().getProcedureTypeId()).isEqualTo(commonProcedureTypeId);
        assertThat(ranking.getFirst().getTotal()).isEqualTo(2L);
    }

    private void saveRequest(UUID procedureTypeId, RequestStatus status) {
        var request = new RequestReport();
        request.setRequestId(UUID.randomUUID());
        request.setProcedureTypeId(procedureTypeId);
        request.setStatus(status);
        request.setUpdatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        repository.save(request);
    }
}
