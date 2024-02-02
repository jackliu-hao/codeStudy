package io.undertow.servlet.sse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ServerSentEvent {
  String value();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\sse\ServerSentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */