package com.beust.jcommander;

import com.beust.jcommander.validators.NoValidator;
import com.beust.jcommander.validators.NoValueValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DynamicParameter {
   String[] names() default {};

   boolean required() default false;

   String description() default "";

   String descriptionKey() default "";

   boolean hidden() default false;

   Class<? extends IParameterValidator> validateWith() default NoValidator.class;

   String assignment() default "=";

   Class<? extends IValueValidator> validateValueWith() default NoValueValidator.class;
}
