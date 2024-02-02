package org.noear.solon.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.annotation.Note;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
   @Note("正则表达式")
   String value() default "";

   String message() default "";

   Class<?>[] groups() default {};
}
