package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public interface ExchangeAttribute {
  String readAttribute(HttpServerExchange paramHttpServerExchange);
  
  void writeAttribute(HttpServerExchange paramHttpServerExchange, String paramString) throws ReadOnlyAttributeException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\ExchangeAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */