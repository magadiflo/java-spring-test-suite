package dev.magadiflo.app.unit.exception;

import dev.magadiflo.app.dto.ErrorResponse;
import dev.magadiflo.app.exception.BankNotFoundException;
import dev.magadiflo.app.exception.DatabaseOperationException;
import dev.magadiflo.app.exception.EntityNotFoundException;
import dev.magadiflo.app.exception.GlobalExceptionHandler;
import dev.magadiflo.app.exception.InvalidTransactionException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private BindingResult bindingResult;
    @InjectMocks
    private GlobalExceptionHandler handlerUnderTest;

    @Test
    void shouldHandleValidationErrors() {
        // given
        FieldError fieldError = new FieldError("accountCreateRequest", "holder", "no puede estar vacío");
        Mockito.when(this.bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, this.bindingResult);

        // when
        ResponseEntity<ErrorResponse> response = this.handlerUnderTest.handleValidationErrors(ex, this.request);

        // then
        ErrorResponse error = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error).isNotNull();
        assertThat(error.error()).isEqualTo("Validation Failed");
        assertThat(error.validationErrors()).containsKey("holder");
    }

    @Test
    void shouldHandleGenericException() {
        // given
        Exception ex = new RuntimeException("Error inesperado");

        // when
        ResponseEntity<ErrorResponse> response = this.handlerUnderTest.handleGenericException(ex, this.request);

        // then
        ErrorResponse error = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(error).isNotNull();
        assertThat(error.message()).isEqualTo("Ocurrió un error interno del servidor. Por favor, contacte al administrador");
    }

    @Test
    void shouldHandleDatabaseOperationException() {
        // given
        DatabaseOperationException ex = new DatabaseOperationException("Error al guardar en DB");

        // when
        ResponseEntity<ErrorResponse> response = this.handlerUnderTest.handleDatabaseOperation(ex, this.request);

        // then
        ErrorResponse error = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(error).isNotNull();
        assertThat(error.message()).isEqualTo("Error al procesar la operación en la base de datos");
    }

    @Test
    void shouldHandleBankNotFoundException() {
        // given
        BankNotFoundException ex = new BankNotFoundException(99L);

        // when
        ResponseEntity<ErrorResponse> response = this.handlerUnderTest.handleBankNotFound(ex, this.request);

        // then
        ErrorResponse error = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(error).isNotNull();
        assertThat(error.message()).isEqualTo(ex.getMessage());
    }

    @Test
    void shouldHandleEntityNotFoundException() {
        // given
        EntityNotFoundException ex = new EntityNotFoundException("Entidad no encontrada");

        // when
        ResponseEntity<ErrorResponse> response = this.handlerUnderTest.handleEntityNotFound(ex, this.request);

        // then
        ErrorResponse error = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(error).isNotNull();
        assertThat(error.message()).isEqualTo("Entidad no encontrada");
    }

    @Test
    void shouldHandleInvalidTransactionException() {
        // given
        InvalidTransactionException ex = new InvalidTransactionException("Monto inválido");

        // when
        ResponseEntity<ErrorResponse> response = this.handlerUnderTest.handleInvalidTransaction(ex, this.request);

        // then
        ErrorResponse error = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error).isNotNull();
        assertThat(error.message()).isEqualTo("Monto inválido");
    }
}
