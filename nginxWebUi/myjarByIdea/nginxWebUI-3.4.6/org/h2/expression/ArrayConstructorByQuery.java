package org.h2.expression;

import java.util.ArrayList;
import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;

public final class ArrayConstructorByQuery extends Expression {
   private final Query query;
   private TypeInfo componentType;
   private TypeInfo type;

   public ArrayConstructorByQuery(Query var1) {
      this.query = var1;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return StringUtils.indent(var1.append("ARRAY ("), this.query.getPlanSQL(var2), 4, false).append(')');
   }

   public Value getValue(SessionLocal var1) {
      this.query.setSession(var1);
      ArrayList var2 = new ArrayList();
      ResultInterface var3 = this.query.query(0L);
      Throwable var4 = null;

      try {
         while(var3.next()) {
            var2.add(var3.currentRow()[0]);
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

      return ValueArray.get(this.componentType, (Value[])var2.toArray(new Value[0]), var1);
   }

   public TypeInfo getType() {
      return this.type;
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.query.mapColumns(var1, var2 + 1);
   }

   public Expression optimize(SessionLocal var1) {
      this.query.prepare();
      if (this.query.getColumnCount() != 1) {
         throw DbException.get(90052);
      } else {
         this.componentType = ((Expression)this.query.getExpressions().get(0)).getType();
         this.type = TypeInfo.getTypeInfo(40, -1L, -1, this.componentType);
         return this;
      }
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.query.setEvaluatable(var1, var2);
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.query.updateAggregate(var1, var2);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.query.isEverything(var1);
   }

   public int getCost() {
      return this.query.getCostAsExpression();
   }
}
