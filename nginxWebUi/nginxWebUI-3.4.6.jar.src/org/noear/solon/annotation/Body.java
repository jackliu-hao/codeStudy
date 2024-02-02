package org.noear.solon.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Body {
  boolean required() default false;
  
  String defaultValue() default "\n\t\t\n\t\t\n\n\t\t\t\t\n";
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\annotation\Body.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */