package io.undertow.server.handlers.builder;

import io.undertow.server.HandlerWrapper;
import java.util.Map;
import java.util.Set;

public interface HandlerBuilder {
   String name();

   Map<String, Class<?>> parameters();

   Set<String> requiredParameters();

   String defaultParameter();

   HandlerWrapper build(Map<String, Object> var1);
}
