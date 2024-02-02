package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.util.ParserUtil;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class Variable extends Operation0 {
   private final String name;
   private Value lastValue;

   public Variable(SessionLocal var1, String var2) {
      this.name = var2;
      this.lastValue = var1.getVariable(var2);
   }

   public int getCost() {
      return 0;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return ParserUtil.quoteIdentifier(var1.append('@'), this.name, var2);
   }

   public TypeInfo getType() {
      return this.lastValue.getType();
   }

   public Value getValue(SessionLocal var1) {
      this.lastValue = var1.getVariable(this.name);
      return this.lastValue;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 2:
            return false;
         default:
            return true;
      }
   }

   public String getName() {
      return this.name;
   }
}
