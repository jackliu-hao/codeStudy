package io.undertow.predicate;

import io.undertow.server.handlers.builder.PredicatedHandlersParser;

public class PredicateParser {
   public static final Predicate parse(String string, ClassLoader classLoader) {
      return PredicatedHandlersParser.parsePredicate(string, classLoader);
   }
}
