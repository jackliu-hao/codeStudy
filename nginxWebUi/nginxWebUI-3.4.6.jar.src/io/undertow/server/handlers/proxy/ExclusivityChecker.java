package io.undertow.server.handlers.proxy;

import io.undertow.server.HttpServerExchange;

public interface ExclusivityChecker {
  boolean isExclusivityRequired(HttpServerExchange paramHttpServerExchange);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\ExclusivityChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */