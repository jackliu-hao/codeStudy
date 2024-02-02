package org.noear.snack.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ONodeAttr {
  String name() default "";
  
  String format() default "";
  
  boolean ignore() default false;
  
  boolean serialize() default true;
  
  boolean deserialize() default true;
  
  boolean incNull() default true;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\annotation\ONodeAttr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */