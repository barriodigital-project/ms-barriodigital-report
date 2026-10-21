package cl.brarriodigital.report.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cl.brarriodigital.report.dto.response.ApiErrorResponse;

@RestControllerAdvice
public class ReportExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReportExceptionHandler.class);

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDataAccessException(DataAccessException exception) {
        logger.error("Unable to read report data", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno"));
    }
}
