package org.noear.solon.socketd.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.handle.MethodType;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClientEndpoint {
   String uri();

   MethodType method() default MethodType.ALL;

   @Note("握手包头")
   String handshakeHeader() default "";

   @Note("自动重链")
   boolean autoReconnect() default true;

   @Note("心跳频率（单位：秒）")
   int heartbeatRate() default 30;
}
