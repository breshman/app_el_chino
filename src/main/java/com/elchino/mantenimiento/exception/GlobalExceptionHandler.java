package com.elchino.mantenimiento.exception;

import com.elchino.mantenimiento.dto.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

/**
 * Manejador global de excepciones ({@code @ControllerAdvice}) para interceptar
 * tanto errores en peticiones REST/AJAX ({@code /api/**}) como en formularios MVC Thymeleaf.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(ResourceNotFoundException ex,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error(ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorMensaje", ex.getMessage());
        return "redirect:/dashboard";
    }

    @ExceptionHandler({StockInsuficienteException.class, HorarioConflictoException.class})
    public Object handleConflictExceptions(BusinessException ex,
                                           HttpServletRequest request,
                                           RedirectAttributes redirectAttributes) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDTO.error(ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorMensaje", ex.getMessage());
        return "redirect:" + getRefererOrDashboard(request);
    }

    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(BusinessException ex,
                                          HttpServletRequest request,
                                          RedirectAttributes redirectAttributes) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error(ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorMensaje", ex.getMessage());
        return "redirect:" + getRefererOrDashboard(request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                               HttpServletRequest request,
                                               RedirectAttributes redirectAttributes) {
        String mensaje = "No se pudo completar la operación: ya existe un registro con los mismos datos únicos (placa, código, SKU o correo) o tiene registros asociados.";
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDTO.error(mensaje));
        }
        redirectAttributes.addFlashAttribute("errorMensaje", mensaje);
        return "redirect:" + getRefererOrDashboard(request);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Object handleValidationExceptions(BindException ex,
                                             HttpServletRequest request,
                                             RedirectAttributes redirectAttributes) {
        String detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        String mensaje = "Error de validación: " + (detalles.isBlank() ? "revise los datos ingresados." : detalles);

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error(mensaje));
        }
        redirectAttributes.addFlashAttribute("errorMensaje", mensaje);
        return "redirect:" + getRefererOrDashboard(request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException ex,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        String mensaje = "Acceso denegado: Su rol no tiene permisos para ejecutar esta operación.";
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDTO.error(mensaje));
        }
        redirectAttributes.addFlashAttribute("errorMensaje", mensaje);
        return "redirect:/dashboard";
    }

    @ExceptionHandler(Exception.class)
    public Object handleUnexpectedException(Exception ex,
                                            HttpServletRequest request,
                                            RedirectAttributes redirectAttributes) {
        String mensaje = "Ocurrió un error interno inesperado al procesar la solicitud.";
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error(mensaje));
        }
        redirectAttributes.addFlashAttribute("errorMensaje", mensaje);
        return "redirect:/dashboard";
    }

    private String getRefererOrDashboard(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return (referer != null && !referer.isBlank()) ? referer : "/dashboard";
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return (uri != null && uri.startsWith("/api/"))
                || (accept != null && accept.contains("application/json"));
    }
}