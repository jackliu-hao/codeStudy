package com.beust.jcommander;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Parameters {
  public static final String DEFAULT_OPTION_PREFIXES = "-";
  
  String resourceBundle() default "";
  
  String separators() default " ";
  
  String optionPrefixes() default "-";
  
  String commandDescription() default "";
  
  String commandDescriptionKey() default "";
  
  String[] commandNames() default {};
  
  boolean hidden() default false;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\Parameters.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */