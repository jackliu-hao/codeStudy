package org.noear.solon.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
  @Alias("name")
  String value() default "";
  
  @Alias("value")
  String name() default "";
  
  @Note("标签，用于快速查找")
  String tag() default "";
  
  @Note("特性，用于辅助配置")
  String[] attrs() default {};
  
  @Note("同时注册类型，仅当名称非空时有效")
  boolean typed() default false;
  
  @Note("顺序位，仅某些类型有效")
  int index() default 0;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\annotation\Bean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */