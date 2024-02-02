package io.undertow.attribute;

public interface ExchangeAttributeBuilder {
  String name();
  
  ExchangeAttribute build(String paramString);
  
  int priority();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\ExchangeAttributeBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */