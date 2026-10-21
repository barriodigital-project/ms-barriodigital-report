package cl.brarriodigital.report.dto.response;

import java.util.UUID;

public record ProcedureTypeReportResponse(UUID procedureTypeId, long total) {
}
