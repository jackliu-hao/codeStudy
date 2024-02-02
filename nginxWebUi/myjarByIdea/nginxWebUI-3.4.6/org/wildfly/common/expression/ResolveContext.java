package org.wildfly.common.expression;

import org.wildfly.common.function.ExceptionBiConsumer;

public final class ResolveContext<E extends Exception> {
   private final ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> function;
   private StringBuilder builder;
   private ExpressionNode current;

   ResolveContext(ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> function, StringBuilder builder) {
      this.function = function;
      this.builder = builder;
   }

   public String getKey() throws E {
      if (this.current == null) {
         throw new IllegalStateException();
      } else {
         Node key = this.current.getKey();
         if (key instanceof LiteralNode) {
            return key.toString();
         } else if (key == Node.NULL) {
            return "";
         } else {
            StringBuilder b = new StringBuilder();
            this.emitToBuilder(b, key);
            return b.toString();
         }
      }
   }

   public void expandDefault(StringBuilder target) throws E {
      if (this.current == null) {
         throw new IllegalStateException();
      } else {
         this.emitToBuilder(target, this.current.getDefaultValue());
      }
   }

   private void emitToBuilder(StringBuilder target, Node node) throws E {
      if (node != Node.NULL) {
         if (node instanceof LiteralNode) {
            target.append(node.toString());
         } else {
            StringBuilder old = this.builder;

            try {
               this.builder = target;
               node.emit(this, this.function);
            } finally {
               this.builder = old;
            }

         }
      }
   }

   public void expandDefault() throws E {
      this.expandDefault(this.builder);
   }

   public String getExpandedDefault() throws E {
      if (this.current == null) {
         throw new IllegalStateException();
      } else {
         Node defaultValue = this.current.getDefaultValue();
         if (defaultValue instanceof LiteralNode) {
            return defaultValue.toString();
         } else if (defaultValue == Node.NULL) {
            return "";
         } else {
            StringBuilder b = new StringBuilder();
            this.emitToBuilder(b, defaultValue);
            return b.toString();
         }
      }
   }

   public boolean hasDefault() {
      return this.current.getDefaultValue() != Node.NULL;
   }

   StringBuilder getStringBuilder() {
      return this.builder;
   }

   ExpressionNode setCurrent(ExpressionNode current) {
      ExpressionNode var2;
      try {
         var2 = this.current;
      } finally {
         this.current = current;
      }

      return var2;
   }
}
