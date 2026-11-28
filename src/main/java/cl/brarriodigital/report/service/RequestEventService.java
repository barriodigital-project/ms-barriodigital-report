package cl.brarriodigital.report.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.brarriodigital.report.dto.event.RequestBusinessEvent;
import cl.brarriodigital.report.model.ProcessedRequestEvent;
import cl.brarriodigital.report.model.RequestReport;
import cl.brarriodigital.report.repository.ProcessedRequestEventRepository;
import cl.brarriodigital.report.repository.RequestReportRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestEventService {

    private final ProcessedRequestEventRepository processedEventRepository;
    private final RequestReportRepository requestReportRepository;

    @Transactional
    public void process(RequestBusinessEvent event) {
        if (processedEventRepository.existsById(event.eventId())) {
            return;
        }

        var existingReport = requestReportRepository.findByRequestIdForUpdate(event.requestId());
        var report = existingReport.orElseGet(() -> newRequestReport(event));
        if (existingReport.isPresent()
            && (report.getUpdatedAt() == null || event.occurredAt().isAfter(report.getUpdatedAt()))) {
            report.setProcedureTypeId(event.procedureTypeId());
            report.setStatus(event.status());
            report.setUpdatedAt(event.occurredAt());
        }
        requestReportRepository.save(report);

        processedEventRepository.save(new ProcessedRequestEvent(event.eventId(), Instant.now()));
    }

    private RequestReport newRequestReport(RequestBusinessEvent event) {
        var report = new RequestReport();
        report.setRequestId(event.requestId());
        report.setProcedureTypeId(event.procedureTypeId());
        report.setStatus(event.status());
        report.setUpdatedAt(event.occurredAt());
        return report;
    }
}
