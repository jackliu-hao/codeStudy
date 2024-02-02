package org.noear.solon.aspect.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.annotation.Alias;
import org.noear.solon.annotation.Note;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Repository {
   @Alias("name")
   String value() default "";

   @Alias("value")
   String name() default "";

   @Note("同时注册类型，仅当名称非空时有效")
   boolean typed() default false;
}
