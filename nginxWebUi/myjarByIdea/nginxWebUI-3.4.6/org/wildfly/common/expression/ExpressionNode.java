package org.wildfly.common.expression;

import java.util.HashSet;
import org.wildfly.common.function.ExceptionBiConsumer;

class ExpressionNode extends Node {
   private final boolean generalExpression;
   private final Node key;
   private final Node defaultValue;

   ExpressionNode(boolean generalExpression, Node key, Node defaultValue) {
      this.generalExpression = generalExpression;
      this.key = key;
      this.defaultValue = defaultValue;
   }

   <E extends Exception> void emit(ResolveContext<E> context, ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> resolveFunction) throws E {
      ExpressionNode oldCurrent = context.setCurrent(this);

      try {
         resolveFunction.accept(context, context.getStringBuilder());
      } finally {
         context.setCurrent(oldCurrent);
      }

   }

   void catalog(HashSet<String> strings) {
      if (this.key instanceof LiteralNode) {
         strings.add(this.key.toString());
      } else {
         this.key.catalog(strings);
      }

      this.defaultValue.catalog(strings);
   }

   boolean isGeneralExpression() {
      return this.generalExpression;
   }

   Node getKey() {
      return this.key;
   }

   Node getDefaultValue() {
      return this.defaultValue;
   }

   public String toString() {
      return String.format("Expr<%s:%s>", this.key, this.defaultValue);
   }
}
