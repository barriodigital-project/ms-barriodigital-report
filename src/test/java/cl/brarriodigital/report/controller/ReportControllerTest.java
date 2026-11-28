package cl.brarriodigital.report.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import cl.brarriodigital.report.model.RequestReport;
import cl.brarriodigital.report.model.RequestStatus;
import cl.brarriodigital.report.repository.ProcessedRequestEventRepository;
import cl.brarriodigital.report.repository.RequestReportRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    private static final UUID FIRST_PROCEDURE_TYPE_ID =
        UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SECOND_PROCEDURE_TYPE_ID =
        UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestReportRepository repository;

    @Autowired
    private ProcessedRequestEventRepository processedEventRepository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        processedEventRepository.deleteAll();
        saveRequest(FIRST_PROCEDURE_TYPE_ID, RequestStatus.INGRESADO);
        saveRequest(FIRST_PROCEDURE_TYPE_ID, RequestStatus.RESUELTO);
        saveRequest(SECOND_PROCEDURE_TYPE_ID, RequestStatus.RECHAZADO);
        saveRequest(SECOND_PROCEDURE_TYPE_ID, RequestStatus.ADMITIDO);
    }

    @Test
    void returnsKpisForCurrentRequestStatuses() throws Exception {
        mockMvc.perform(get("/api/v1/reports/kpis"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalRequests").value(4))
            .andExpect(jsonPath("$.ingresados").value(1))
            .andExpect(jsonPath("$.admitidos").value(1))
            .andExpect(jsonPath("$.enGestion").value(0))
            .andExpect(jsonPath("$.enTerreno").value(0))
            .andExpect(jsonPath("$.resueltos").value(1))
            .andExpect(jsonPath("$.rechazados").value(1));
    }

    @Test
    void returnsAllStatusesIncludingZeroCounts() throws Exception {
        mockMvc.perform(get("/api/v1/reports/requests-by-status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(6))
            .andExpect(jsonPath("$[0].status").value("INGRESADO"))
            .andExpect(jsonPath("$[0].total").value(1))
            .andExpect(jsonPath("$[2].status").value("EN_GESTION"))
            .andExpect(jsonPath("$[2].total").value(0));
    }

    @Test
    void returnsProcedureTypesRankedByCountThenIdentifier() throws Exception {
        mockMvc.perform(get("/api/v1/reports/top-procedure-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].procedureTypeId").value(FIRST_PROCEDURE_TYPE_ID.toString()))
            .andExpect(jsonPath("$[0].total").value(2))
            .andExpect(jsonPath("$[1].procedureTypeId").value(SECOND_PROCEDURE_TYPE_ID.toString()))
            .andExpect(jsonPath("$[1].total").value(2));
    }

    @Test
    void returnsEmptyReportsWhenNoRequestsHaveBeenLoaded() throws Exception {
        repository.deleteAll();

        mockMvc.perform(get("/api/v1/reports/kpis"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalRequests").value(0));
        mockMvc.perform(get("/api/v1/reports/requests-by-status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[5].total").value(0));
        mockMvc.perform(get("/api/v1/reports/top-procedure-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void processesRequestEventsIdempotentlyAndIgnoresOlderEvents() throws Exception {
        repository.deleteAll();
        processedEventRepository.deleteAll();
        UUID requestId = UUID.fromString("00000000-0000-0000-0000-000000000010");
        UUID procedureTypeId = UUID.fromString("00000000-0000-0000-0000-000000000011");
        String createdEvent = """
            {
              "schemaVersion": 1,
              "eventId": "00000000-0000-0000-0000-000000000012",
              "eventType": "REQUEST_CREATED",
              "occurredAt": "2026-03-01T10:00:00Z",
              "requestId": "00000000-0000-0000-0000-000000000010",
              "procedureTypeId": "00000000-0000-0000-0000-000000000011",
              "status": "INGRESADO"
            }
            """;
        String statusChangedEvent = """
            {
              "schemaVersion": 1,
              "eventId": "00000000-0000-0000-0000-000000000013",
              "eventType": "REQUEST_STATUS_CHANGED",
              "occurredAt": "2026-03-01T10:01:00Z",
              "requestId": "00000000-0000-0000-0000-000000000010",
              "procedureTypeId": "00000000-0000-0000-0000-000000000011",
              "status": "ADMITIDO"
            }
            """;

        mockMvc.perform(post("/api/v1/internal/request-events")
                .contentType("application/json")
                .content(statusChangedEvent))
            .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/v1/internal/request-events")
                .contentType("application/json")
                .content(createdEvent))
            .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/v1/internal/request-events")
                .contentType("application/json")
                .content(statusChangedEvent))
            .andExpect(status().isNoContent());

        var report = repository.findByRequestId(requestId).orElseThrow();
        assertThat(report.getProcedureTypeId()).isEqualTo(procedureTypeId);
        assertThat(report.getStatus()).isEqualTo(RequestStatus.ADMITIDO);
        assertThat(repository.count()).isEqualTo(1);
        assertThat(processedEventRepository.count()).isEqualTo(2);
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
