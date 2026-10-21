package cl.brarriodigital.report.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @BeforeEach
    void setUp() {
        repository.deleteAll();
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

    private void saveRequest(UUID procedureTypeId, RequestStatus status) {
        var request = new RequestReport();
        request.setRequestId(UUID.randomUUID());
        request.setProcedureTypeId(procedureTypeId);
        request.setStatus(status);
        request.setUpdatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        repository.save(request);
    }
}
