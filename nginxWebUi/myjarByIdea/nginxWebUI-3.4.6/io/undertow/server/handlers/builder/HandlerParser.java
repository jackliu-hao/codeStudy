package io.undertow.server.handlers.builder;

import io.undertow.server.HandlerWrapper;

public class HandlerParser {
   public static HandlerWrapper parse(String string, ClassLoader classLoader) {
      return PredicatedHandlersParser.parseHandler(string, classLoader);
   }
}
