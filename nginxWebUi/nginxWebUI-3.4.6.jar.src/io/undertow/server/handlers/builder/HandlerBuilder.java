package io.undertow.server.handlers.builder;

import io.undertow.server.HandlerWrapper;
import java.util.Map;
import java.util.Set;

public interface HandlerBuilder {
  String name();
  
  Map<String, Class<?>> parameters();
  
  Set<String> requiredParameters();
  
  String defaultParameter();
  
  HandlerWrapper build(Map<String, Object> paramMap);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\builder\HandlerBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */