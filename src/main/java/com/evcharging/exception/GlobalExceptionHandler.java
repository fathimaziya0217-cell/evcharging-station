package com.evcharging.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return path.startsWith("/api/") || (accept != null && accept.contains("application/json"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException ex, HttpServletRequest request, Model model) {
        logger.warn("Resource not found: {} on URI {}", ex.getMessage(), request.getRequestURI());
        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.NOT_FOUND.value());
            body.put("error", "Not Found");
            body.put("message", ex.getMessage());
            body.put("path", request.getRequestURI());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        model.addAttribute("errorCode", 404);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(SlotUnavailableException.class)
    public Object handleSlotUnavailable(SlotUnavailableException ex, HttpServletRequest request, Model model) {
        logger.warn("Slot unavailable conflict: {}", ex.getMessage());
        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.CONFLICT.value());
            body.put("error", "Conflict");
            body.put("message", ex.getMessage());
            body.put("path", request.getRequestURI());
            return new ResponseEntity<>(body, HttpStatus.CONFLICT);
        }
        model.addAttribute("errorCode", 409);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public Object handleDuplicateEmail(DuplicateEmailException ex, HttpServletRequest request, Model model) {
        logger.warn("Duplicate email registration attempt: {}", ex.getMessage());
        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "Bad Request");
            body.put("message", ex.getMessage());
            body.put("path", request.getRequestURI());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        model.addAttribute("errorCode", 400);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public Object handleUnauthorized(UnauthorizedAccessException ex, HttpServletRequest request, Model model) {
        logger.warn("Unauthorized resource access: {}", ex.getMessage());
        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.FORBIDDEN.value());
            body.put("error", "Forbidden");
            body.put("message", ex.getMessage());
            body.put("path", request.getRequestURI());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        }
        model.addAttribute("errorCode", 403);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception ex, HttpServletRequest request, Model model) {
        logger.error("Unhandled application exception on URI {}: ", request.getRequestURI(), ex);
        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            body.put("error", "Internal Server Error");
            body.put("message", "An unexpected error occurred. Please try again later.");
            body.put("path", request.getRequestURI());
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        model.addAttribute("errorCode", 500);
        model.addAttribute("errorMessage", "An internal error occurred. Please try again later.");
        return "error";
    }
}
