package org.noear.solon.extend.cors.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.annotation.Before;
import org.noear.solon.annotation.Note;
import org.noear.solon.annotation.Options;
import org.noear.solon.extend.cors.CrossOriginInterceptor;

/** @deprecated */
@Deprecated
@Options
@Inherited
@Before({CrossOriginInterceptor.class})
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossOrigin {
   @Note("支持配置模式： ${xxx}")
   String origins() default "*";

   int maxAge() default 3600;

   boolean credentials() default true;
}
