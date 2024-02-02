package io.undertow.server.handlers.builder;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.ResponseCodeHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResponseCodeHandlerBuilder implements HandlerBuilder {
   public String name() {
      return "response-code";
   }

   public Map<String, Class<?>> parameters() {
      Map<String, Class<?>> parameters = new HashMap();
      parameters.put("value", Integer.class);
      return parameters;
   }

   public Set<String> requiredParameters() {
      Set<String> req = new HashSet();
      req.add("value");
      return req;
   }

   public String defaultParameter() {
      return "value";
   }

   public HandlerWrapper build(Map<String, Object> config) {
      final Integer value = (Integer)config.get("value");
      return new HandlerWrapper() {
         public HttpHandler wrap(HttpHandler handler) {
            return new ResponseCodeHandler(value);
         }
      };
   }
}
