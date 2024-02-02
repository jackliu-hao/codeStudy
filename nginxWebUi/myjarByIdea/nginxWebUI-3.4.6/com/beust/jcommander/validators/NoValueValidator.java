package com.beust.jcommander.validators;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

public class NoValueValidator<T> implements IValueValidator<T> {
   public void validate(String parameterName, T parameterValue) throws ParameterException {
   }
}
