package org.noear.solon.data.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.data.tran.TranIsolation;
import org.noear.solon.data.tran.TranPolicy;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tran {
   TranPolicy policy() default TranPolicy.required;

   TranIsolation isolation() default TranIsolation.unspecified;

   boolean readOnly() default false;
}
