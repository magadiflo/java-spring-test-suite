package dev.magadiflo.app.exception;

import dev.magadiflo.app.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========== EXCEPCIONES DE NEGOCIO (ESPERADAS) - NIVEL WARN ==========
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex, HttpServletRequest request) {
        return this.businessException("Cuenta no encontrada: {} | Excepción: {} | Path: {}", HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(BankNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBankNotFound(BankNotFoundException ex, HttpServletRequest request) {
        return this.businessException("Banco no encontrado: {} | Excepción: {} | Path: {}", HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return this.businessException("Entidad no encontrada: {} | Excepción: {} | Path: {}", HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex, HttpServletRequest request) {
        return this.businessException("Operación rechazada por saldo insuficiente: {} | Excepción: {} | Path: {}", HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransaction(InvalidTransactionException ex, HttpServletRequest request) {
        return this.businessException("Transacción inválida: {} | Excepción: {} | Path: {}", HttpStatus.BAD_REQUEST, ex, request);
    }

    // ========== VALIDACIONES DE BEAN VALIDATION (@Valid) ==========
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, List<String>> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                DefaultMessageSourceResolvable::getDefaultMessage,
                                Collectors.toList()
                        )
                ));

        // Se registra a nivel INFO porque las validaciones fallidas son errores esperados del cliente.
        // En DEV (nivel DEBUG) y QA (nivel INFO) se mostrarán estos logs para depuración.
        // En PROD (nivel WARN) estos logs NO aparecerán, reduciendo ruido en logs de producción.
        log.info("Errores de validación en petición {} | Campos con errores: {} | Path: {}",
                request.getMethod(), validationErrors.keySet(), request.getRequestURI());

        ErrorResponse errorResponse = ErrorResponse.create(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Los datos enviados no cumplen con las validaciones requeridas",
                request.getRequestURI(),
                validationErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // ========== EXCEPCIONES INESPERADAS (TÉCNICAS) - NIVEL ERROR ==========
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado del sistema: {} | Path: {} | Exception: {}",
                ex.getMessage(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex); // El stack trace completo se loguea aquí

        ErrorResponse errorResponse = ErrorResponse.create(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocurrió un error interno del servidor. Por favor, contacte al administrador.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Método auxiliar para manejar excepciones de negocio esperadas.
     * <p>
     * Registra el error a nivel WARN y construye una respuesta HTTP estandarizada.
     * </p>
     *
     * @param logMessage mensaje para el log con placeholders {}
     * @param status     código HTTP de respuesta
     * @param ex         excepción lanzada
     * @param request    contexto de la petición HTTP
     * @return respuesta con el error formateado
     */
    private ResponseEntity<ErrorResponse> businessException(String logMessage, HttpStatus status, Exception ex, HttpServletRequest request) {
        log.warn(logMessage, ex.getMessage(), ex.getClass().getSimpleName(), request.getRequestURI());
        ErrorResponse errorResponse = ErrorResponse.create(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}
