package org.h2.constraint;

import java.util.HashSet;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.StringUtils;
import org.h2.value.Value;

public class ConstraintCheck extends Constraint {
   private TableFilter filter;
   private Expression expr;

   public ConstraintCheck(Schema var1, int var2, String var3, Table var4) {
      super(var1, var2, var3, var4);
   }

   public Constraint.Type getConstraintType() {
      return Constraint.Type.CHECK;
   }

   public void setTableFilter(TableFilter var1) {
      this.filter = var1;
   }

   public void setExpression(Expression var1) {
      this.expr = var1;
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      StringBuilder var3 = new StringBuilder("ALTER TABLE ");
      var1.getSQL(var3, 0).append(" ADD CONSTRAINT ");
      if (var1.isHidden()) {
         var3.append("IF NOT EXISTS ");
      }

      var3.append(var2);
      if (this.comment != null) {
         var3.append(" COMMENT ");
         StringUtils.quoteStringSQL(var3, this.comment);
      }

      var3.append(" CHECK");
      this.expr.getEnclosedSQL(var3, 0).append(" NOCHECK");
      return var3.toString();
   }

   private String getShortDescription() {
      StringBuilder var1 = (new StringBuilder()).append(this.getName()).append(": ");
      this.expr.getTraceSQL();
      return var1.toString();
   }

   public String getCreateSQLWithoutIndexes() {
      return this.getCreateSQL();
   }

   public String getCreateSQL() {
      return this.getCreateSQLForCopy(this.table, this.getSQL(0));
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      this.table.removeConstraint(this);
      this.database.removeMeta(var1, this.getId());
      this.filter = null;
      this.expr = null;
      this.table = null;
      this.invalidate();
   }

   public void checkRow(SessionLocal var1, Table var2, Row var3, Row var4) {
      if (var4 != null) {
         boolean var5;
         try {
            Value var6;
            synchronized(this) {
               this.filter.set(var4);
               var6 = this.expr.getValue(var1);
            }

            var5 = var6.isFalse();
         } catch (DbException var10) {
            throw DbException.get(23514, var10, this.getShortDescription());
         }

         if (var5) {
            throw DbException.get(23513, (String)this.getShortDescription());
         }
      }
   }

   public boolean usesIndex(Index var1) {
      return false;
   }

   public void setIndexOwner(Index var1) {
      throw DbException.getInternalError(this.toString());
   }

   public HashSet<Column> getReferencedColumns(Table var1) {
      HashSet var2 = new HashSet();
      this.expr.isEverything(ExpressionVisitor.getColumnsVisitor(var2, var1));
      return var2;
   }

   public Expression getExpression() {
      return this.expr;
   }

   public boolean isBefore() {
      return true;
   }

   public void checkExistingData(SessionLocal var1) {
      if (!var1.getDatabase().isStarting()) {
         StringBuilder var2 = (new StringBuilder()).append("SELECT NULL FROM ");
         this.filter.getTable().getSQL(var2, 0).append(" WHERE NOT ");
         this.expr.getSQL(var2, 0, 0);
         String var3 = var2.toString();
         ResultInterface var4 = var1.prepare(var3).query(1L);
         if (var4.next()) {
            throw DbException.get(23513, (String)this.getName());
         }
      }
   }

   public void rebuild() {
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.expr.isEverything(var1);
   }
}
