package cl.brarriodigital.report.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.brarriodigital.report.dto.event.RequestBusinessEvent;
import cl.brarriodigital.report.service.RequestEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/internal")
@RequiredArgsConstructor
public class RequestEventController {

    private final RequestEventService requestEventService;

    @PostMapping("/request-events")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void receive(@Valid @RequestBody RequestBusinessEvent event) {
        requestEventService.process(event);
    }
}
