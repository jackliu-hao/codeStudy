package org.noear.solon.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.core.handle.MethodType;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServerEndpoint {
   @Alias("path")
   String value() default "";

   @Alias("value")
   String path() default "";

   MethodType method() default MethodType.ALL;
}
