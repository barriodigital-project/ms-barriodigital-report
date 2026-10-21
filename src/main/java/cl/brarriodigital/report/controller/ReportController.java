package cl.brarriodigital.report.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.brarriodigital.report.dto.response.KpiResponse;
import cl.brarriodigital.report.dto.response.ProcedureTypeReportResponse;
import cl.brarriodigital.report.dto.response.RequestsByStatusResponse;
import cl.brarriodigital.report.service.ReportService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/kpis")
    public KpiResponse getKpis() {
        return reportService.getKpis();
    }

    @GetMapping("/requests-by-status")
    public List<RequestsByStatusResponse> getRequestsByStatus() {
        return reportService.getRequestsByStatus();
    }

    @GetMapping("/top-procedure-types")
    public List<ProcedureTypeReportResponse> getTopProcedureTypes() {
        return reportService.getTopProcedureTypes();
    }
}
