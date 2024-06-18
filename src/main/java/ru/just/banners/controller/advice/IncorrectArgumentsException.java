package ru.just.banners.controller.advice;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class IncorrectArgumentsException extends Exception {
    private final Map<String, String> argumentToException;

    public IncorrectArgumentsException(String aggregateException,
                                       Map<String, String> argumentToException) {
        super(aggregateException);
        this.argumentToException = argumentToException == null ? new HashMap<>() : argumentToException;
    }

    public IncorrectArgumentsException(String aggregateException) {
        super(aggregateException);
        argumentToException = new HashMap<>();
    }
}
