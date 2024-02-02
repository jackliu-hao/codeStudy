package org.noear.solon.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.annotation.Note;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Date {
  @Note("日期表达式, 默认为：ISO格式")
  String value() default "";
  
  String message() default "";
  
  Class<?>[] groups() default {};
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\Date.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */