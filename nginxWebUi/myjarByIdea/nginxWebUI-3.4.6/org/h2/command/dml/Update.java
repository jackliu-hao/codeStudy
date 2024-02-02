package org.h2.command.dml;

import java.util.HashSet;
import org.h2.command.Prepared;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultTarget;
import org.h2.result.Row;
import org.h2.table.DataChangeDeltaTable;
import org.h2.table.PlanItem;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public final class Update extends FilteredDataChangeStatement {
   private SetClauseList setClauseList;
   private Insert onDuplicateKeyInsert;
   private TableFilter fromTableFilter;

   public Update(SessionLocal var1) {
      super(var1);
   }

   public void setSetClauseList(SetClauseList var1) {
      this.setClauseList = var1;
   }

   public void setFromTableFilter(TableFilter var1) {
      this.fromTableFilter = var1;
   }

   public long update(ResultTarget var1, DataChangeDeltaTable.ResultOption var2) {
      this.targetTableFilter.startQuery(this.session);
      this.targetTableFilter.reset();
      Table var3 = this.targetTableFilter.getTable();
      LocalResult var4 = LocalResult.forTable(this.session, var3);
      Throwable var5 = null;

      try {
         this.session.getUser().checkTableRight(var3, 8);
         var3.fire(this.session, 2, true);
         var3.lock(this.session, 1);
         this.setCurrentRowNumber(0L);
         long var6 = 0L;
         long var8 = -1L;
         if (this.fetchExpr != null) {
            Value var10 = this.fetchExpr.getValue(this.session);
            if (var10 == ValueNull.INSTANCE || (var8 = var10.getLong()) < 0L) {
               throw DbException.getInvalidValueException("FETCH", var10);
            }
         }

         while(this.nextRow(var8, var6)) {
            Row var22 = this.targetTableFilter.get();
            if (var3.isRowLockable()) {
               Row var11 = var3.lockRow(this.session, var22);
               if (var11 == null) {
                  continue;
               }

               if (!var22.hasSharedData(var11)) {
                  var22 = var11;
                  this.targetTableFilter.set(var11);
                  if (this.condition != null && !this.condition.getBooleanValue(this.session)) {
                     continue;
                  }
               }
            }

            if (this.setClauseList.prepareUpdate(var3, this.session, var1, var2, var4, var22, this.onDuplicateKeyInsert != null)) {
               ++var6;
            }
         }

         doUpdate(this, this.session, var3, var4);
         var3.fire(this.session, 2, false);
         long var23 = var6;
         return var23;
      } catch (Throwable var20) {
         var5 = var20;
         throw var20;
      } finally {
         if (var4 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var19) {
                  var5.addSuppressed(var19);
               }
            } else {
               var4.close();
            }
         }

      }
   }

   static void doUpdate(Prepared var0, SessionLocal var1, Table var2, LocalResult var3) {
      var3.done();
      var2.updateRows(var0, var1, var3);
      if (var2.fireRow()) {
         var3.reset();

         while(var3.next()) {
            Row var4 = var3.currentRowForTable();
            var3.next();
            Row var5 = var3.currentRowForTable();
            var2.fireAfterRow(var1, var4, var5, false);
         }
      }

   }

   public String getPlanSQL(int var1) {
      StringBuilder var2 = new StringBuilder("UPDATE ");
      this.targetTableFilter.getPlanSQL(var2, false, var1);
      if (this.fromTableFilter != null) {
         var2.append("\nFROM ");
         this.fromTableFilter.getPlanSQL(var2, false, var1);
      }

      this.setClauseList.getSQL(var2, var1);
      this.appendFilterCondition(var2, var1);
      return var2.toString();
   }

   public void prepare() {
      if (this.fromTableFilter != null) {
         this.targetTableFilter.addJoin(this.fromTableFilter, false, (Expression)null);
      }

      if (this.condition != null) {
         this.condition.mapColumns(this.targetTableFilter, 0, 0);
         if (this.fromTableFilter != null) {
            this.condition.mapColumns(this.fromTableFilter, 0, 0);
         }

         this.condition = this.condition.optimizeCondition(this.session);
         if (this.condition != null) {
            this.condition.createIndexConditions(this.session, this.targetTableFilter);
         }
      }

      this.setClauseList.mapAndOptimize(this.session, this.targetTableFilter, this.fromTableFilter);
      TableFilter[] var1 = null;
      if (this.fromTableFilter == null) {
         var1 = new TableFilter[]{this.targetTableFilter};
      } else {
         var1 = new TableFilter[]{this.targetTableFilter, this.fromTableFilter};
      }

      PlanItem var2 = this.targetTableFilter.getBestPlanItem(this.session, var1, 0, new AllColumnsForPlan(var1));
      this.targetTableFilter.setPlanItem(var2);
      this.targetTableFilter.prepare();
   }

   public int getType() {
      return 68;
   }

   public String getStatementName() {
      return "UPDATE";
   }

   public void collectDependencies(HashSet<DbObject> var1) {
      ExpressionVisitor var2 = ExpressionVisitor.getDependenciesVisitor(var1);
      if (this.condition != null) {
         this.condition.isEverything(var2);
      }

      this.setClauseList.isEverything(var2);
   }

   public Insert getOnDuplicateKeyInsert() {
      return this.onDuplicateKeyInsert;
   }

   void setOnDuplicateKeyInsert(Insert var1) {
      this.onDuplicateKeyInsert = var1;
   }
}
