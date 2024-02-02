package io.undertow.servlet.api;

import io.undertow.server.HttpServerExchange;

public interface ConfidentialPortManager {
   int getConfidentialPort(HttpServerExchange var1);
}
