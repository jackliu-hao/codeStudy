package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public interface ExchangeAttribute {
   String readAttribute(HttpServerExchange var1);

   void writeAttribute(HttpServerExchange var1, String var2) throws ReadOnlyAttributeException;
}
