package javax.servlet.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.DispatcherType;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebFilter {
   String description() default "";

   String displayName() default "";

   WebInitParam[] initParams() default {};

   String filterName() default "";

   String smallIcon() default "";

   String largeIcon() default "";

   String[] servletNames() default {};

   String[] value() default {};

   String[] urlPatterns() default {};

   DispatcherType[] dispatcherTypes() default {DispatcherType.REQUEST};

   boolean asyncSupported() default false;
}
