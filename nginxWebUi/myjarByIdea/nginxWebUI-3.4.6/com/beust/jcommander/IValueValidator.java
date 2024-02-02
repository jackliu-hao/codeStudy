package com.beust.jcommander;

public interface IValueValidator<T> {
   void validate(String var1, T var2) throws ParameterException;
}
