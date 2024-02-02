package org.h2.constraint;

import java.util.HashSet;
import org.h2.command.Parser;
import org.h2.command.ddl.AlterDomain;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.command.query.Select;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.IndexHints;
import org.h2.table.PlanItem;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.StringUtils;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public class ConstraintDomain extends Constraint {
   private Domain domain;
   private Expression expr;
   private DomainColumnResolver resolver;

   public ConstraintDomain(Schema var1, int var2, String var3, Domain var4) {
      super(var1, var2, var3, (Table)null);
      this.domain = var4;
      this.resolver = new DomainColumnResolver(var4.getDataType());
   }

   public Constraint.Type getConstraintType() {
      return Constraint.Type.DOMAIN;
   }

   public Domain getDomain() {
      return this.domain;
   }

   public void setExpression(SessionLocal var1, Expression var2) {
      var2.mapColumns(this.resolver, 0, 0);
      var2 = var2.optimize(var1);
      synchronized(this) {
         this.resolver.setValue(ValueNull.INSTANCE);
         var2.getValue(var1);
      }

      this.expr = var2;
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      throw DbException.getInternalError(this.toString());
   }

   public String getCreateSQLWithoutIndexes() {
      return this.getCreateSQL();
   }

   public String getCreateSQL() {
      StringBuilder var1 = new StringBuilder("ALTER DOMAIN ");
      this.domain.getSQL(var1, 0).append(" ADD CONSTRAINT ");
      this.getSQL(var1, 0);
      if (this.comment != null) {
         var1.append(" COMMENT ");
         StringUtils.quoteStringSQL(var1, this.comment);
      }

      var1.append(" CHECK");
      this.expr.getEnclosedSQL(var1, 0).append(" NOCHECK");
      return var1.toString();
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      this.domain.removeConstraint(this);
      this.database.removeMeta(var1, this.getId());
      this.domain = null;
      this.expr = null;
      this.invalidate();
   }

   public void checkRow(SessionLocal var1, Table var2, Row var3, Row var4) {
      throw DbException.getInternalError(this.toString());
   }

   public void check(SessionLocal var1, Value var2) {
      Value var3;
      synchronized(this) {
         this.resolver.setValue(var2);
         var3 = this.expr.getValue(var1);
      }

      if (var3.isFalse()) {
         throw DbException.get(23513, (String)this.expr.getTraceSQL());
      }
   }

   public Expression getCheckConstraint(SessionLocal var1, String var2) {
      String var3;
      if (var2 != null) {
         synchronized(this) {
            try {
               this.resolver.setColumnName(var2);
               var3 = this.expr.getSQL(0);
            } finally {
               this.resolver.resetColumnName();
            }
         }

         return (new Parser(var1)).parseExpression(var3);
      } else {
         synchronized(this) {
            var3 = this.expr.getSQL(0);
         }

         return (new Parser(var1)).parseDomainConstraintExpression(var3);
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
         new CheckExistingData(var1, this.domain);
      }
   }

   public void rebuild() {
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.expr.isEverything(var1);
   }

   private class CheckExistingData {
      private final SessionLocal session;

      CheckExistingData(SessionLocal var2, Domain var3) {
         this.session = var2;
         this.checkDomain((Domain)null, var3);
      }

      private boolean checkColumn(Domain var1, Column var2) {
         Table var3 = var2.getTable();
         TableFilter var4 = new TableFilter(this.session, var3, (String)null, true, (Select)null, 0, (IndexHints)null);
         TableFilter[] var5 = new TableFilter[]{var4};
         PlanItem var6 = var4.getBestPlanItem(this.session, var5, 0, new AllColumnsForPlan(var5));
         var4.setPlanItem(var6);
         var4.prepare();
         var4.startQuery(this.session);
         var4.reset();

         while(var4.next()) {
            ConstraintDomain.this.check(this.session, var4.getValue(var2));
         }

         return false;
      }

      private boolean checkDomain(Domain var1, Domain var2) {
         AlterDomain.forAllDependencies(this.session, var2, this::checkColumn, this::checkDomain, false);
         return false;
      }
   }
}
