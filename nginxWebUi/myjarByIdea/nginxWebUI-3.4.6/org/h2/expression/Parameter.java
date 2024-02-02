package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.expression.condition.Comparison;
import org.h2.message.DbException;
import org.h2.table.Column;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public final class Parameter extends Operation0 implements ParameterInterface {
   private Value value;
   private Column column;
   private final int index;

   public Parameter(int var1) {
      this.index = var1;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return var1.append('?').append(this.index + 1);
   }

   public void setValue(Value var1, boolean var2) {
      this.value = var1;
   }

   public void setValue(Value var1) {
      this.value = var1;
   }

   public Value getParamValue() {
      return (Value)(this.value == null ? ValueNull.INSTANCE : this.value);
   }

   public Value getValue(SessionLocal var1) {
      return this.getParamValue();
   }

   public TypeInfo getType() {
      if (this.value != null) {
         return this.value.getType();
      } else {
         return this.column != null ? this.column.getType() : TypeInfo.TYPE_UNKNOWN;
      }
   }

   public void checkSet() {
      if (this.value == null) {
         throw DbException.get(90012, "#" + (this.index + 1));
      }
   }

   public Expression optimize(SessionLocal var1) {
      if (var1.getDatabase().getMode().treatEmptyStringsAsNull && this.value instanceof ValueVarchar && this.value.getString().isEmpty()) {
         this.value = ValueNull.INSTANCE;
      }

      return this;
   }

   public boolean isValueSet() {
      return this.value != null;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 0:
            return this.value != null;
         default:
            return true;
      }
   }

   public int getCost() {
      return 0;
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      return new Comparison(0, this, ValueExpression.FALSE, false);
   }

   public void setColumn(Column var1) {
      this.column = var1;
   }

   public int getIndex() {
      return this.index;
   }
}
