package cl.brarriodigital.report.service;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.brarriodigital.report.dto.response.KpiResponse;
import cl.brarriodigital.report.dto.response.ProcedureTypeReportResponse;
import cl.brarriodigital.report.dto.response.RequestsByStatusResponse;
import cl.brarriodigital.report.model.RequestStatus;
import cl.brarriodigital.report.repository.RequestReportRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final RequestReportRepository requestReportRepository;

    public KpiResponse getKpis() {
        Map<RequestStatus, Long> counts = getCountsByStatus();

        return new KpiResponse(
            counts.values().stream().mapToLong(Long::longValue).sum(),
            countFor(counts, RequestStatus.INGRESADO),
            countFor(counts, RequestStatus.ADMITIDO),
            countFor(counts, RequestStatus.EN_GESTION),
            countFor(counts, RequestStatus.EN_TERRENO),
            countFor(counts, RequestStatus.RESUELTO),
            countFor(counts, RequestStatus.RECHAZADO)
        );
    }

    public List<RequestsByStatusResponse> getRequestsByStatus() {
        Map<RequestStatus, Long> counts = getCountsByStatus();

        return Arrays.stream(RequestStatus.values())
            .map(status -> new RequestsByStatusResponse(status, countFor(counts, status)))
            .toList();
    }

    public List<ProcedureTypeReportResponse> getTopProcedureTypes() {
        return requestReportRepository.countRequestsByProcedureType().stream()
            .map(count -> new ProcedureTypeReportResponse(
                count.getProcedureTypeId(),
                count.getTotal()
            ))
            .toList();
    }

    private Map<RequestStatus, Long> getCountsByStatus() {
        Map<RequestStatus, Long> counts = new EnumMap<>(RequestStatus.class);
        requestReportRepository.countRequestsByStatus()
            .forEach(count -> counts.put(count.getStatus(), count.getTotal()));
        return counts;
    }

    private long countFor(Map<RequestStatus, Long> counts, RequestStatus status) {
        return counts.getOrDefault(status, 0L);
    }
}
