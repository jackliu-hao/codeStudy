package org.h2.command.dml;

import java.util.HashSet;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
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

public final class Delete extends FilteredDataChangeStatement {
   public Delete(SessionLocal var1) {
      super(var1);
   }

   public long update(ResultTarget var1, DataChangeDeltaTable.ResultOption var2) {
      this.targetTableFilter.startQuery(this.session);
      this.targetTableFilter.reset();
      Table var3 = this.targetTableFilter.getTable();
      this.session.getUser().checkTableRight(var3, 2);
      var3.fire(this.session, 4, true);
      var3.lock(this.session, 1);
      long var4 = -1L;
      if (this.fetchExpr != null) {
         Value var6 = this.fetchExpr.getValue(this.session);
         if (var6 == ValueNull.INSTANCE || (var4 = var6.getLong()) < 0L) {
            throw DbException.getInvalidValueException("FETCH", var6);
         }
      }

      LocalResult var24 = LocalResult.forTable(this.session, var3);
      Throwable var7 = null;

      try {
         this.setCurrentRowNumber(0L);
         long var8 = 0L;

         while(true) {
            Row var10;
            while(true) {
               if (!this.nextRow(var4, var8)) {
                  var24.done();
                  long var25 = 0L;

                  while(var24.next()) {
                     if ((++var25 & 127L) == 0L) {
                        this.checkCanceled();
                     }

                     Row var12 = var24.currentRowForTable();
                     var3.removeRow(this.session, var12);
                  }

                  if (var3.fireRow()) {
                     var24.reset();

                     while(var24.next()) {
                        var3.fireAfterRow(this.session, var24.currentRowForTable(), (Row)null, false);
                     }
                  }

                  var3.fire(this.session, 4, false);
                  long var26 = var8;
                  return var26;
               }

               var10 = this.targetTableFilter.get();
               if (!var3.isRowLockable()) {
                  break;
               }

               Row var11 = var3.lockRow(this.session, var10);
               if (var11 != null) {
                  if (var10.hasSharedData(var11)) {
                     break;
                  }

                  var10 = var11;
                  this.targetTableFilter.set(var11);
                  if (this.condition == null || this.condition.getBooleanValue(this.session)) {
                     break;
                  }
               }
            }

            if (var2 == DataChangeDeltaTable.ResultOption.OLD) {
               var1.addRow(var10.getValueList());
            }

            if (!var3.fireRow() || !var3.fireBeforeRow(this.session, var10, (Row)null)) {
               var24.addRowForTable(var10);
            }

            ++var8;
         }
      } catch (Throwable var22) {
         var7 = var22;
         throw var22;
      } finally {
         if (var24 != null) {
            if (var7 != null) {
               try {
                  var24.close();
               } catch (Throwable var21) {
                  var7.addSuppressed(var21);
               }
            } else {
               var24.close();
            }
         }

      }
   }

   public String getPlanSQL(int var1) {
      StringBuilder var2 = new StringBuilder("DELETE FROM ");
      this.targetTableFilter.getPlanSQL(var2, false, var1);
      this.appendFilterCondition(var2, var1);
      return var2.toString();
   }

   public void prepare() {
      if (this.condition != null) {
         this.condition.mapColumns(this.targetTableFilter, 0, 0);
         this.condition = this.condition.optimizeCondition(this.session);
         if (this.condition != null) {
            this.condition.createIndexConditions(this.session, this.targetTableFilter);
         }
      }

      TableFilter[] var1 = new TableFilter[]{this.targetTableFilter};
      PlanItem var2 = this.targetTableFilter.getBestPlanItem(this.session, var1, 0, new AllColumnsForPlan(var1));
      this.targetTableFilter.setPlanItem(var2);
      this.targetTableFilter.prepare();
   }

   public int getType() {
      return 58;
   }

   public String getStatementName() {
      return "DELETE";
   }

   public void collectDependencies(HashSet<DbObject> var1) {
      ExpressionVisitor var2 = ExpressionVisitor.getDependenciesVisitor(var1);
      if (this.condition != null) {
         this.condition.isEverything(var2);
      }

   }
}
