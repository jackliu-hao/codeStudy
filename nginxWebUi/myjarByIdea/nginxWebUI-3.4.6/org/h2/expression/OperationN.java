package org.h2.expression;

import java.util.Arrays;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;

public abstract class OperationN extends Expression implements ExpressionWithVariableParameters {
   protected Expression[] args;
   protected int argsCount;
   protected TypeInfo type;

   protected OperationN(Expression[] var1) {
      this.args = var1;
   }

   public void addParameter(Expression var1) {
      int var2 = this.args.length;
      if (this.argsCount >= var2) {
         this.args = (Expression[])Arrays.copyOf(this.args, var2 * 2);
      }

      this.args[this.argsCount++] = var1;
   }

   public void doneWithParameters() throws DbException {
      if (this.args.length != this.argsCount) {
         this.args = (Expression[])Arrays.copyOf(this.args, this.argsCount);
      }

   }

   public TypeInfo getType() {
      return this.type;
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      Expression[] var4 = this.args;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Expression var7 = var4[var6];
         var7.mapColumns(var1, var2, var3);
      }

   }

   protected boolean optimizeArguments(SessionLocal var1, boolean var2) {
      int var3 = 0;

      for(int var4 = this.args.length; var3 < var4; ++var3) {
         Expression var5 = this.args[var3].optimize(var1);
         this.args[var3] = var5;
         if (var2 && !var5.isConstant()) {
            var2 = false;
         }
      }

      return var2;
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      Expression[] var3 = this.args;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Expression var6 = var3[var5];
         var6.setEvaluatable(var1, var2);
      }

   }

   public void updateAggregate(SessionLocal var1, int var2) {
      Expression[] var3 = this.args;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Expression var6 = var3[var5];
         var6.updateAggregate(var1, var2);
      }

   }

   public boolean isEverything(ExpressionVisitor var1) {
      Expression[] var2 = this.args;
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
      int var1 = this.args.length + 1;
      Expression[] var2 = this.args;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Expression var5 = var2[var4];
         var1 += var5.getCost();
      }

      return var1;
   }

   public int getSubexpressionCount() {
      return this.args.length;
   }

   public Expression getSubexpression(int var1) {
      return this.args[var1];
   }
}
