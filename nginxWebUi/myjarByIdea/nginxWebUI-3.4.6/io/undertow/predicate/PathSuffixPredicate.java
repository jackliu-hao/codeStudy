package io.undertow.predicate;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class PathSuffixPredicate implements Predicate {
   private final String suffix;
   private static final boolean traceEnabled;

   PathSuffixPredicate(String suffix) {
      this.suffix = suffix;
   }

   public boolean resolve(HttpServerExchange value) {
      boolean matches = value.getRelativePath().endsWith(this.suffix);
      if (traceEnabled) {
         UndertowLogger.PREDICATE_LOGGER.tracef("Path suffix [%s] %s input [%s] for %s.", new Object[]{this.suffix, matches ? "MATCHES" : "DOES NOT MATCH", value.getRelativePath(), value});
      }

      return matches;
   }

   public String toString() {
      return "path-suffix( '" + this.suffix + "' )";
   }

   static {
      traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "path-suffix";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.singletonMap("path", String[].class);
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("path");
      }

      public String defaultParameter() {
         return "path";
      }

      public Predicate build(Map<String, Object> config) {
         String[] path = (String[])((String[])config.get("path"));
         return Predicates.suffixes(path);
      }
   }
}
