package io.undertow.predicate;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathMatcher;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PathMatchPredicate implements Predicate {
   private final PathMatcher<Boolean> pathMatcher;
   private static final boolean traceEnabled;

   PathMatchPredicate(String... paths) {
      PathMatcher<Boolean> matcher = new PathMatcher();
      String[] var3 = paths;
      int var4 = paths.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String path = var3[var5];
         if (!path.startsWith("/")) {
            matcher.addExactPath("/" + path, Boolean.TRUE);
         } else {
            matcher.addExactPath(path, Boolean.TRUE);
         }
      }

      this.pathMatcher = matcher;
   }

   public String toString() {
      Set<String> matches = this.pathMatcher.getExactPathMatchesSet();
      return matches.size() == 1 ? "path( '" + matches.toArray()[0] + "' )" : "path( { '" + (String)matches.stream().collect(Collectors.joining("', '")) + "' } )";
   }

   public boolean resolve(HttpServerExchange value) {
      String relativePath = value.getRelativePath();
      PathMatcher.PathMatch<Boolean> result = this.pathMatcher.match(relativePath);
      boolean matches = Boolean.TRUE.equals(result.getValue());
      if (traceEnabled) {
         UndertowLogger.PREDICATE_LOGGER.tracef("Path(s) [%s] %s input [%s] for %s.", new Object[]{this.pathMatcher.getExactPathMatchesSet().stream().collect(Collectors.joining(", ")), matches ? "MATCH" : "DO NOT MATCH", relativePath, value});
      }

      return matches;
   }

   static {
      traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "path";
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
         return new PathMatchPredicate(path);
      }
   }
}
