package org.noear.solon.extend.aspect.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.annotation.Alias;
import org.noear.solon.annotation.Note;

@Deprecated
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
  @Alias("name")
  String value() default "";
  
  @Alias("value")
  String name() default "";
  
  @Note("同时注册类型，仅当名称非空时有效")
  boolean typed() default false;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\extend\aspect\annotation\Service.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */