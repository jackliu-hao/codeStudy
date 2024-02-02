package io.undertow.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jboss.logging.Logger;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface ExceptionLog {
   Logger.Level value() default Logger.Level.ERROR;

   Logger.Level stackTraceLevel() default Logger.Level.FATAL;

   String category();
}
