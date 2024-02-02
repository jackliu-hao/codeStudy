package org.noear.solon.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.core.handle.MethodType;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {
   @Alias("path")
   String value() default "";

   @Alias("value")
   String path() default "";

   MethodType[] method() default {MethodType.ALL};

   String consumes() default "";

   String produces() default "";

   boolean multipart() default false;

   int index() default 0;

   boolean before() default false;

   boolean after() default false;
}
