package cl.brarriodigital.report.dto.response;

import cl.brarriodigital.report.model.RequestStatus;

public record RequestsByStatusResponse(RequestStatus status, long total) {
}
