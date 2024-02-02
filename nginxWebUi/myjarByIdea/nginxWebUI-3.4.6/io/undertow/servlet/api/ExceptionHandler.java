package io.undertow.servlet.api;

import io.undertow.server.HttpServerExchange;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface ExceptionHandler {
   boolean handleThrowable(HttpServerExchange var1, ServletRequest var2, ServletResponse var3, Throwable var4);
}
