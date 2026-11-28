package cl.brarriodigital.report.dto.event;

import java.time.Instant;
import java.util.UUID;

import cl.brarriodigital.report.model.RequestStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public record RequestBusinessEvent(
    @Min(1) @Max(1) int schemaVersion,
    @NotNull UUID eventId,
    @NotNull RequestEventType eventType,
    @NotNull Instant occurredAt,
    @NotNull UUID requestId,
    @NotNull UUID procedureTypeId,
    @NotNull RequestStatus status
) {
}
