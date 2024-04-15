package ru.just.banners.controller.advice;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(MethodArgumentNotValidException e) {
        ObjectError error = e.getAllErrors().getFirst();
        String errorMessage;
        try {
            String fieldName = ((FieldError) error).getField();
            errorMessage = String.format("Поле '%s' %s", fieldName, error.getDefaultMessage());
        } catch (ClassCastException castException) {
            errorMessage = e.getLocalizedMessage();
        }
        return new ResponseEntity<>(new ErrorMessage(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessage> handleMissingRequestParameterException(MissingServletRequestParameterException e) {
        String errorMessage = String.format("Обязательный параметр запроса '%s' (тип %s) не указан",
                e.getParameterName(), e.getParameterType());
        return new ResponseEntity<>(new ErrorMessage(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleMissingRequestParameterException(HttpMessageNotReadableException e) {
        String errorMessage = "Тело запроса указано неверно";
        if (e.getCause() instanceof MismatchedInputException inputException) {
            String add = ". Поле '%s' должен иметь тип %s";
            JsonMappingException.Reference reference = inputException.getPath().getFirst();
            errorMessage += String.format(add, reference.getFieldName(),
                    inputException.getTargetType().getSimpleName());
        }
        return new ResponseEntity<>(new ErrorMessage(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorMessage> handleInternalServerException(Exception e) {
        return new ResponseEntity<>(new ErrorMessage(e.getLocalizedMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
