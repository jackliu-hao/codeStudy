package io.undertow.server.handlers.builder;

import io.undertow.predicate.Predicate;
import io.undertow.server.HandlerWrapper;

public class PredicatedHandler {
   private final Predicate predicate;
   private final HandlerWrapper handler;
   private final HandlerWrapper elseHandler;

   public PredicatedHandler(Predicate predicate, HandlerWrapper handler) {
      this(predicate, handler, (HandlerWrapper)null);
   }

   public PredicatedHandler(Predicate predicate, HandlerWrapper handler, HandlerWrapper elseHandler) {
      this.predicate = predicate;
      this.handler = handler;
      this.elseHandler = elseHandler;
   }

   public Predicate getPredicate() {
      return this.predicate;
   }

   public HandlerWrapper getHandler() {
      return this.handler;
   }

   public HandlerWrapper getElseHandler() {
      return this.elseHandler;
   }
}
