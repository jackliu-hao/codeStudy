package com.beust.jcommander;

public interface IParameterValidator {
   void validate(String var1, String var2) throws ParameterException;
}
