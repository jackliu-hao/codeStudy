package org.noear.solon.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {
  HttpPart[] value() default {HttpPart.params};
  
  int seconds() default 1;
  
  String message() default "";
  
  Class<?>[] groups() default {};
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\NoRepeatSubmit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */