package org.h2.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class Wildcard extends Expression {
   private final String schema;
   private final String table;
   private ArrayList<ExpressionColumn> exceptColumns;

   public Wildcard(String var1, String var2) {
      this.schema = var1;
      this.table = var2;
   }

   public ArrayList<ExpressionColumn> getExceptColumns() {
      return this.exceptColumns;
   }

   public void setExceptColumns(ArrayList<ExpressionColumn> var1) {
      this.exceptColumns = var1;
   }

   public HashMap<Column, ExpressionColumn> mapExceptColumns() {
      HashMap var1 = new HashMap();
      Iterator var2 = this.exceptColumns.iterator();

      ExpressionColumn var3;
      Column var4;
      do {
         if (!var2.hasNext()) {
            return var1;
         }

         var3 = (ExpressionColumn)var2.next();
         var4 = var3.getColumn();
         if (var4 == null) {
            throw var3.getColumnException(42122);
         }
      } while(var1.putIfAbsent(var4, var3) == null);

      throw var3.getColumnException(42121);
   }

   public Value getValue(SessionLocal var1) {
      throw DbException.getInternalError(this.toString());
   }

   public TypeInfo getType() {
      throw DbException.getInternalError(this.toString());
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      if (this.exceptColumns != null) {
         Iterator var4 = this.exceptColumns.iterator();

         while(var4.hasNext()) {
            ExpressionColumn var5 = (ExpressionColumn)var4.next();
            var5.mapColumns(var1, var2, var3);
         }
      }

   }

   public Expression optimize(SessionLocal var1) {
      throw DbException.get(42000, (String)this.table);
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      throw DbException.getInternalError(this.toString());
   }

   public String getTableAlias() {
      return this.table;
   }

   public String getSchemaName() {
      return this.schema;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      if (this.table != null) {
         StringUtils.quoteIdentifier(var1, this.table).append('.');
      }

      var1.append('*');
      if (this.exceptColumns != null) {
         writeExpressions(var1.append(" EXCEPT ("), this.exceptColumns, var2).append(')');
      }

      return var1;
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      throw DbException.getInternalError(this.toString());
   }

   public boolean isEverything(ExpressionVisitor var1) {
      if (var1.getType() == 8) {
         return true;
      } else {
         throw DbException.getInternalError(Integer.toString(var1.getType()));
      }
   }

   public int getCost() {
      throw DbException.getInternalError(this.toString());
   }
}
