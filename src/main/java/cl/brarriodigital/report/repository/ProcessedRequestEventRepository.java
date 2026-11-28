package cl.brarriodigital.report.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.brarriodigital.report.model.ProcessedRequestEvent;

public interface ProcessedRequestEventRepository extends JpaRepository<ProcessedRequestEvent, UUID> {
}
