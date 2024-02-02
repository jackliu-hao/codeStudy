package org.h2.expression.condition;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.function.CastSpecification;
import org.h2.value.TypeInfo;

abstract class Condition extends Expression {
   static Expression castToBoolean(SessionLocal var0, Expression var1) {
      return (Expression)(var1.getType().getValueType() == 8 ? var1 : new CastSpecification(var1, TypeInfo.TYPE_BOOLEAN));
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_BOOLEAN;
   }
}
