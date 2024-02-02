package io.undertow.servlet.api;

import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletChain;
import io.undertow.servlet.handlers.ServletPathMatch;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletDispatcher {
  void dispatchToPath(HttpServerExchange paramHttpServerExchange, ServletPathMatch paramServletPathMatch, DispatcherType paramDispatcherType) throws Exception;
  
  void dispatchToServlet(HttpServerExchange paramHttpServerExchange, ServletChain paramServletChain, DispatcherType paramDispatcherType) throws Exception;
  
  void dispatchMockRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws ServletException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\ServletDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */