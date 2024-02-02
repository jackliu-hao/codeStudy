package org.noear.solon.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.core.handle.Handler;

@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface After {
  Class<? extends Handler>[] value();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\annotation\After.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */