package io.undertow.predicate;

import java.util.Map;
import java.util.Set;

public interface PredicateBuilder {
  String name();
  
  Map<String, Class<?>> parameters();
  
  Set<String> requiredParameters();
  
  String defaultParameter();
  
  Predicate build(Map<String, Object> paramMap);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\PredicateBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */