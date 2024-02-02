package io.undertow.server.handlers.proxy;

import io.undertow.server.HttpServerExchange;

public interface ExclusivityChecker {
   boolean isExclusivityRequired(HttpServerExchange var1);
}
