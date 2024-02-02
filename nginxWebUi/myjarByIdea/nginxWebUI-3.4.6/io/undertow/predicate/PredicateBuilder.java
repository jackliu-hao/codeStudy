package io.undertow.predicate;

import java.util.Map;
import java.util.Set;

public interface PredicateBuilder {
   String name();

   Map<String, Class<?>> parameters();

   Set<String> requiredParameters();

   String defaultParameter();

   Predicate build(Map<String, Object> var1);
}
