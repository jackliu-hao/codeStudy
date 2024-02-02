package com.beust.jcommander;

import com.beust.jcommander.converters.IParameterSplitter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Parameter {
  String[] names() default {};
  
  String description() default "";
  
  boolean required() default false;
  
  String descriptionKey() default "";
  
  int arity() default -1;
  
  boolean password() default false;
  
  Class<? extends IStringConverter<?>> converter() default com.beust.jcommander.converters.NoConverter.class;
  
  Class<? extends IStringConverter<?>> listConverter() default com.beust.jcommander.converters.NoConverter.class;
  
  boolean hidden() default false;
  
  Class<? extends IParameterValidator> validateWith() default com.beust.jcommander.validators.NoValidator.class;
  
  Class<? extends IValueValidator> validateValueWith() default com.beust.jcommander.validators.NoValueValidator.class;
  
  boolean variableArity() default false;
  
  Class<? extends IParameterSplitter> splitter() default com.beust.jcommander.converters.CommaParameterSplitter.class;
  
  boolean echoInput() default false;
  
  boolean help() default false;
  
  boolean forceNonOverwritable() default false;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\Parameter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */