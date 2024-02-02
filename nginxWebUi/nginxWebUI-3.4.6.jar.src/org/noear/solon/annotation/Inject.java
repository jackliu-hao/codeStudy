package org.noear.solon.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inject {
  String value() default "";
  
  boolean required() default true;
  
  @Note("单例才有自动刷新的必要")
  boolean autoRefreshed() default false;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\annotation\Inject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */