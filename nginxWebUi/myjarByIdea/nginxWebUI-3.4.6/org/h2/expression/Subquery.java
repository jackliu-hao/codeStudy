package org.h2.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

public final class Subquery extends Expression {
   private final Query query;
   private Expression expression;
   private Value nullValue;
   private HashSet<ColumnResolver> outerResolvers = new HashSet();

   public Subquery(Query var1) {
      this.query = var1;
   }

   public Value getValue(SessionLocal var1) {
      this.query.setSession(var1);
      ResultInterface var2 = this.query.query(2L);
      Throwable var3 = null;

      Value var5;
      try {
         if (var2.next()) {
            Value var4 = this.readRow(var2);
            if (var2.hasNext()) {
               throw DbException.get(90053);
            }

            var5 = var4;
            return var5;
         }

         var5 = this.nullValue;
      } catch (Throwable var15) {
         var3 = var15;
         throw var15;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var14) {
                  var3.addSuppressed(var14);
               }
            } else {
               var2.close();
            }
         }

      }

      return var5;
   }

   public ArrayList<Value> getAllRows(SessionLocal var1) {
      ArrayList var2 = new ArrayList();
      this.query.setSession(var1);
      ResultInterface var3 = this.query.query(2147483647L);
      Throwable var4 = null;

      try {
         while(var3.next()) {
            var2.add(this.readRow(var3));
         }
      } catch (Throwable var13) {
         var4 = var13;
         throw var13;
      } finally {
         if (var3 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var12) {
                  var4.addSuppressed(var12);
               }
            } else {
               var3.close();
            }
         }

      }

      return var2;
   }

   private Value readRow(ResultInterface var1) {
      Value[] var2 = var1.currentRow();
      int var3 = var1.getVisibleColumnCount();
      return (Value)(var3 == 1 ? var2[0] : ValueRow.get(this.getType(), var3 == var2.length ? var2 : (Value[])Arrays.copyOf(var2, var3)));
   }

   public TypeInfo getType() {
      return this.expression.getType();
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.outerResolvers.add(var1);
      this.query.mapColumns(var1, var2 + 1);
   }

   public Expression optimize(SessionLocal var1) {
      this.query.prepare();
      if (this.query.isConstantQuery()) {
         this.setType();
         return ValueExpression.get(this.getValue(var1));
      } else {
         if (this.outerResolvers != null && var1.getDatabase().getSettings().optimizeSimpleSingleRowSubqueries) {
            Expression var2 = this.query.getIfSingleRow();
            if (var2 != null && var2.isEverything(ExpressionVisitor.getDecrementQueryLevelVisitor(this.outerResolvers, 0))) {
               var2.isEverything(ExpressionVisitor.getDecrementQueryLevelVisitor(this.outerResolvers, 1));
               return var2.optimize(var1);
            }
         }

         this.outerResolvers = null;
         this.setType();
         return this;
      }
   }

   private void setType() {
      ArrayList var1 = this.query.getExpressions();
      int var2 = this.query.getColumnCount();
      if (var2 == 1) {
         this.expression = (Expression)var1.get(0);
         this.nullValue = ValueNull.INSTANCE;
      } else {
         Expression[] var3 = new Expression[var2];
         Value[] var4 = new Value[var2];

         for(int var5 = 0; var5 < var2; ++var5) {
            var3[var5] = (Expression)var1.get(var5);
            var4[var5] = ValueNull.INSTANCE;
         }

         ExpressionList var6 = new ExpressionList(var3, false);
         var6.initializeType();
         this.expression = var6;
         this.nullValue = ValueRow.get(new ExtTypeInfoRow(var3), var4);
      }

   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.query.setEvaluatable(var1, var2);
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return var1.append('(').append(this.query.getPlanSQL(var2)).append(')');
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.query.updateAggregate(var1, var2);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.query.isEverything(var1);
   }

   public Query getQuery() {
      return this.query;
   }

   public int getCost() {
      return this.query.getCostAsExpression();
   }

   public boolean isConstant() {
      return this.query.isConstantQuery();
   }
}
