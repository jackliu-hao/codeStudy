package org.noear.solon.socketd.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.annotation.Note;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Handshake {
  @Note("握手包头")
  String handshakeHeader() default "";
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\annotation\Handshake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */