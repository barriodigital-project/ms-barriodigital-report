package cl.brarriodigital.report.repository;

import java.util.UUID;

public interface ProcedureTypeCountProjection {
    UUID getProcedureTypeId();

    Long getTotal();
}
