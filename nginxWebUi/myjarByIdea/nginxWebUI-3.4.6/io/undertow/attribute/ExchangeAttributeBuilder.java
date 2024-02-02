package io.undertow.attribute;

public interface ExchangeAttributeBuilder {
   String name();

   ExchangeAttribute build(String var1);

   int priority();
}
