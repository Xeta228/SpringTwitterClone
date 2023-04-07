package com.baron.webapp.controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

public class ControllerUtils {


     static Map<String, String> getErrors(BindingResult bindingResult) {
        Map <String, String> errorsMap = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(fe -> fe.getField() + "Error", FieldError::getDefaultMessage));
        return errorsMap;
    }
}
