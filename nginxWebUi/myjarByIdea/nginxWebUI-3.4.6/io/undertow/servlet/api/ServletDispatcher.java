package io.undertow.servlet.api;

import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletChain;
import io.undertow.servlet.handlers.ServletPathMatch;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletDispatcher {
   void dispatchToPath(HttpServerExchange var1, ServletPathMatch var2, DispatcherType var3) throws Exception;

   void dispatchToServlet(HttpServerExchange var1, ServletChain var2, DispatcherType var3) throws Exception;

   void dispatchMockRequest(HttpServletRequest var1, HttpServletResponse var2) throws ServletException;
}
