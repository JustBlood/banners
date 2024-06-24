package ru.just.banners.controller.advice;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorMessage> handleInternalServerException(Exception e) {
        return new ResponseEntity<>(new ErrorMessage(e.getLocalizedMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = FeatureTagNotUniqueException.class)
    public ResponseEntity<ErrorMessage> handleFeatureTagNotUniqueException(FeatureTagNotUniqueException e) {
        String tags = String.join(",", e.getTagIds());
        String errorMessage = messageSource.getMessage(
                "error.featureTagPairExists",
                new Object[]{tags, e.getFeatureId()},
                Locale.of("ru")
        );
        return new ResponseEntity<>(new ErrorMessage(errorMessage), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = IncorrectArgumentsException.class)
    public ResponseEntity<ErrorMessage> handleIncorrectArgumentsException(IncorrectArgumentsException e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage()).append(".");
        for (var entry : e.getArgumentToException().entrySet()) {
            sb.append(" ").append(entry.getKey()).append(": ").append(entry.getValue()).append(";");
        }
        return new ResponseEntity<>(new ErrorMessage(sb.toString()), HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errorMessage = "Тело запроса указано неверно";
        if (ex.getCause() instanceof MismatchedInputException inputException) {
            String add = ". Поле '%s' должно иметь тип %s";
            JsonMappingException.Reference reference = inputException.getPath().getFirst();
            errorMessage += String.format(add, reference.getFieldName(),
                    inputException.getTargetType().getSimpleName());
        }
        return new ResponseEntity<>(new ErrorMessage(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream().map(fieldError -> fieldError.getField() + ": "
                        + fieldError.getDefaultMessage())
                .collect(Collectors.joining(","));
        return new ResponseEntity<>(new ErrorMessage(errorMessage), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errorMessage = messageSource.getMessage("error.requiredArgNotSpecified",
                new Object[]{ex.getParameterName(), ex.getParameterType()},
                LocaleContextHolder.getLocale());
        return new ResponseEntity<>(new ErrorMessage(errorMessage), HttpStatus.BAD_REQUEST);
    }
}
