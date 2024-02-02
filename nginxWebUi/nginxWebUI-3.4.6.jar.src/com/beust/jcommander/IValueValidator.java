package com.beust.jcommander;

public interface IValueValidator<T> {
  void validate(String paramString, T paramT) throws ParameterException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\IValueValidator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */