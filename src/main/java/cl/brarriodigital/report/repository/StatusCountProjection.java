package cl.brarriodigital.report.repository;

import cl.brarriodigital.report.model.RequestStatus;

public interface StatusCountProjection {
    RequestStatus getStatus();

    Long getTotal();
}
