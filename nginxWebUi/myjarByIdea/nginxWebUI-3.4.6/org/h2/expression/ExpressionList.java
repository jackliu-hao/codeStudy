package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueRow;

public final class ExpressionList extends Expression {
   private final Expression[] list;
   private final boolean isArray;
   private TypeInfo type;

   public ExpressionList(Expression[] var1, boolean var2) {
      this.list = var1;
      this.isArray = var2;
   }

   public Value getValue(SessionLocal var1) {
      Value[] var2 = new Value[this.list.length];

      for(int var3 = 0; var3 < this.list.length; ++var3) {
         var2[var3] = this.list[var3].getValue(var1);
      }

      return (Value)(this.isArray ? ValueArray.get((TypeInfo)this.type.getExtTypeInfo(), var2, var1) : ValueRow.get(this.type, var2));
   }

   public TypeInfo getType() {
      return this.type;
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      Expression[] var4 = this.list;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Expression var7 = var4[var6];
         var7.mapColumns(var1, var2, var3);
      }

   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = true;
      int var3 = this.list.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Expression var5 = this.list[var4].optimize(var1);
         if (!var5.isConstant()) {
            var2 = false;
         }

         this.list[var4] = var5;
      }

      this.initializeType();
      if (var2) {
         return ValueExpression.get(this.getValue(var1));
      } else {
         return this;
      }
   }

   void initializeType() {
      this.type = this.isArray ? TypeInfo.getTypeInfo(40, (long)this.list.length, 0, TypeInfo.getHigherType(this.list)) : TypeInfo.getTypeInfo(41, 0L, 0, new ExtTypeInfoRow(this.list));
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      Expression[] var3 = this.list;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Expression var6 = var3[var5];
         var6.setEvaluatable(var1, var2);
      }

   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.isArray ? writeExpressions(var1.append("ARRAY ["), this.list, var2).append(']') : writeExpressions(var1.append("ROW ("), this.list, var2).append(')');
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      Expression[] var3 = this.list;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Expression var6 = var3[var5];
         var6.updateAggregate(var1, var2);
      }

   }

   public boolean isEverything(ExpressionVisitor var1) {
      Expression[] var2 = this.list;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Expression var5 = var2[var4];
         if (!var5.isEverything(var1)) {
            return false;
         }
      }

      return true;
   }

   public int getCost() {
      int var1 = 1;
      Expression[] var2 = this.list;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Expression var5 = var2[var4];
         var1 += var5.getCost();
      }

      return var1;
   }

   public boolean isConstant() {
      Expression[] var1 = this.list;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Expression var4 = var1[var3];
         if (!var4.isConstant()) {
            return false;
         }
      }

      return true;
   }

   public int getSubexpressionCount() {
      return this.list.length;
   }

   public Expression getSubexpression(int var1) {
      return this.list[var1];
   }

   public boolean isArray() {
      return this.isArray;
   }
}
