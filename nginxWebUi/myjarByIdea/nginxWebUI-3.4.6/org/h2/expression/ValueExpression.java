package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.index.IndexCondition;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public class ValueExpression extends Operation0 {
   public static final ValueExpression NULL;
   public static final ValueExpression DEFAULT;
   public static final ValueExpression TRUE;
   public static final ValueExpression FALSE;
   final Value value;

   ValueExpression(Value var1) {
      this.value = var1;
   }

   public static ValueExpression get(Value var0) {
      if (var0 == ValueNull.INSTANCE) {
         return NULL;
      } else {
         return var0.getValueType() == 8 ? getBoolean(var0.getBoolean()) : new ValueExpression(var0);
      }
   }

   public static ValueExpression getBoolean(Value var0) {
      return (ValueExpression)(var0 == ValueNull.INSTANCE ? TypedValueExpression.UNKNOWN : getBoolean(var0.getBoolean()));
   }

   public static ValueExpression getBoolean(boolean var0) {
      return var0 ? TRUE : FALSE;
   }

   public Value getValue(SessionLocal var1) {
      return this.value;
   }

   public TypeInfo getType() {
      return this.value.getType();
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (this.value.getValueType() == 8 && !this.value.getBoolean()) {
         var2.addIndexCondition(IndexCondition.get(9, (ExpressionColumn)null, this));
      }

   }

   public Expression getNotIfPossible(SessionLocal var1) {
      return (Expression)(this.value == ValueNull.INSTANCE ? TypedValueExpression.UNKNOWN : getBoolean(!this.value.getBoolean()));
   }

   public boolean isConstant() {
      return true;
   }

   public boolean isNullConstant() {
      return this == NULL;
   }

   public boolean isValueSet() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      if (this == DEFAULT) {
         var1.append("DEFAULT");
      } else {
         this.value.getSQL(var1, var2);
      }

      return var1;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return true;
   }

   public int getCost() {
      return 0;
   }

   static {
      NULL = new ValueExpression(ValueNull.INSTANCE);
      DEFAULT = new ValueExpression(ValueNull.INSTANCE);
      TRUE = new ValueExpression(ValueBoolean.TRUE);
      FALSE = new ValueExpression(ValueBoolean.FALSE);
   }
}
