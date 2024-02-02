package io.undertow.servlet.predicate;

import io.undertow.predicate.Predicate;
import io.undertow.predicate.PredicateBuilder;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.DispatcherType;

public class DispatcherTypePredicate implements Predicate {
   public static final DispatcherTypePredicate FORWARD;
   public static final DispatcherTypePredicate INCLUDE;
   public static final DispatcherTypePredicate REQUEST;
   public static final DispatcherTypePredicate ASYNC;
   public static final DispatcherTypePredicate ERROR;
   private final DispatcherType dispatcherType;

   public DispatcherTypePredicate(DispatcherType dispatcherType) {
      this.dispatcherType = dispatcherType;
   }

   public boolean resolve(HttpServerExchange value) {
      return ((ServletRequestContext)value.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getDispatcherType() == this.dispatcherType;
   }

   public String toString() {
      return "dispatcher( " + this.dispatcherType.toString() + " )";
   }

   static {
      FORWARD = new DispatcherTypePredicate(DispatcherType.FORWARD);
      INCLUDE = new DispatcherTypePredicate(DispatcherType.INCLUDE);
      REQUEST = new DispatcherTypePredicate(DispatcherType.REQUEST);
      ASYNC = new DispatcherTypePredicate(DispatcherType.ASYNC);
      ERROR = new DispatcherTypePredicate(DispatcherType.ERROR);
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "dispatcher";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("value", String.class);
         return params;
      }

      public Set<String> requiredParameters() {
         Set<String> params = new HashSet();
         params.add("value");
         return params;
      }

      public String defaultParameter() {
         return "value";
      }

      public Predicate build(Map<String, Object> config) {
         String value = (String)config.get("value");
         return new DispatcherTypePredicate(DispatcherType.valueOf(value));
      }
   }
}
