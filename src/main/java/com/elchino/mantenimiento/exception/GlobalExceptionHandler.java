package com.elchino.mantenimiento.exception;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

/**
 * Manejador global de excepciones ({@code @ControllerAdvice}) para interceptar
 * tanto errores en peticiones REST/AJAX ({@code /api/**}) como en formularios MVC Thymeleaf.
 *
 * <p>Garantiza respuestas HTTP semánticas (400, 403, 404, 409, 500) en la API
 * y mensajes amigables para el usuario en la interfaz web (RNF02).</p>
 *
 * @author Arquitecto de Software (@ArchitectAgent)
 * @version 1.0.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de recurso no encontrado ({@code 404 Not Found}).
     *
     * @param ex      excepción capturada.
     * @param request petición HTTP actual.
     * @return respuesta JSON si es ruta de API o redirección con alerta en vistas MVC.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(ex.getMessage()));
        }
        request.setAttribute("errorMensaje", ex.getMessage());
        return "redirect:/dashboard?error=" + ex.getMessage();
    }

    /**
     * Maneja conflictos de negocio críticos como falta de stock ({@link StockInsuficienteException})
     * o cruce de horarios de técnicos ({@link HorarioConflictoException}), devolviendo {@code 409 Conflict}.
     *
     * @param ex                 excepción de conflicto de negocio.
     * @param request            petición HTTP actual.
     * @param redirectAttributes atributos flash para vistas Thymeleaf.
     * @return {@link ResponseEntity} con código 409 o redirección a la vista de origen.
     */
    @ExceptionHandler({StockInsuficienteException.class, HorarioConflictoException.class})
    public Object handleConflictExceptions(BusinessException ex,
                                           HttpServletRequest request,
                                           RedirectAttributes redirectAttributes) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDTO.error(ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorMensaje", ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    /**
     * Maneja cualquier otra violación de regla de negocio general ({@code 400 Bad Request}).
     *
     * @param ex                 excepción de negocio.
     * @param request            petición HTTP actual.
     * @param redirectAttributes atributos flash para la vista.
     * @return respuesta JSON 400 o redirección con mensaje de error.
     */
    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(BusinessException ex,
                                          HttpServletRequest request,
                                          RedirectAttributes redirectAttributes) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error(ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorMensaje", ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    /**
     * Maneja errores de validación de Bean Validation ({@code @Valid}).
     *
     * @param ex      excepción con la lista de campos inválidos.
     * @param request petición HTTP actual.
     * @return respuesta JSON 400 detallando los campos con errores.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                                           HttpServletRequest request) {
        String detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Error de validación: " + detalles));
    }

    /**
     * Maneja intentos de acceso no autorizado por rol ({@code 403 Forbidden} - QA-03 / RNF01).
     *
     * @param ex      excepción de acceso denegado de Spring Security.
     * @param request petición HTTP actual.
     * @return respuesta HTTP 403 Forbidden.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleAccessDenied(AccessDeniedException ex,
                                                                   HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponseDTO.error("Acceso denegado (403 Forbidden): Su rol no tiene permisos para ejecutar esta operación."));
    }

    /**
     * Determina si la petición HTTP corresponde a un endpoint REST o llamada AJAX.
     *
     * @param request petición HTTP a evaluar.
     * @return {@code true} si la URI inicia con {@code /api/} o acepta JSON.
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return (uri != null && uri.startsWith("/api/"))
                || (accept != null && accept.contains("application/json"));
    }
}
