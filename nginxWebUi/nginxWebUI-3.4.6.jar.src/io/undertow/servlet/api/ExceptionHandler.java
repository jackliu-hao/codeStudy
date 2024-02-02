package io.undertow.servlet.api;

import io.undertow.server.HttpServerExchange;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface ExceptionHandler {
  boolean handleThrowable(HttpServerExchange paramHttpServerExchange, ServletRequest paramServletRequest, ServletResponse paramServletResponse, Throwable paramThrowable);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\ExceptionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */