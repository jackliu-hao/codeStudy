package com.beust.jcommander.validators;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

public class NoValueValidator<T> implements IValueValidator<T> {
  public void validate(String parameterName, T parameterValue) throws ParameterException {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\validators\NoValueValidator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */