package cl.brarriodigital.report.dto.response;

public record KpiResponse(
    long totalRequests,
    long ingresados,
    long admitidos,
    long enGestion,
    long enTerreno,
    long resueltos,
    long rechazados
) {
}
