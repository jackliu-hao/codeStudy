package org.h2.command.dml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.command.Prepared;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultTarget;
import org.h2.result.Row;
import org.h2.table.Column;
import org.h2.table.DataChangeDeltaTable;
import org.h2.table.PlanItem;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.HasSQL;
import org.h2.util.Utils;
import org.h2.value.Value;

public final class MergeUsing extends DataChangeStatement {
   TableFilter targetTableFilter;
   TableFilter sourceTableFilter;
   Expression onCondition;
   private ArrayList<When> when = Utils.newSmallArrayList();
   private final HashSet<Long> targetRowidsRemembered = new HashSet();

   public MergeUsing(SessionLocal var1, TableFilter var2) {
      super(var1);
      this.targetTableFilter = var2;
   }

   public long update(ResultTarget var1, DataChangeDeltaTable.ResultOption var2) {
      long var3 = 0L;
      this.targetRowidsRemembered.clear();
      this.checkRights();
      this.setCurrentRowNumber(0L);
      this.sourceTableFilter.startQuery(this.session);
      this.sourceTableFilter.reset();
      Table var5 = this.targetTableFilter.getTable();
      var5.fire(this.session, this.evaluateTriggerMasks(), true);
      var5.lock(this.session, 1);
      this.setCurrentRowNumber(0L);
      long var6 = 0L;
      Row var8 = null;
      Row var9 = null;
      boolean var10 = var5.getRowIdColumn() != null;

      while(true) {
         while(this.sourceTableFilter.next()) {
            Row var11 = this.sourceTableFilter.get();
            if (var9 != null) {
               if (var11 != var9) {
                  Row var12 = this.targetTableFilter.get();
                  this.sourceTableFilter.set(var9);
                  this.targetTableFilter.set(var5.getNullRow());
                  var3 += (long)this.merge(true, var1, var2);
                  this.sourceTableFilter.set(var11);
                  this.targetTableFilter.set(var12);
                  ++var6;
               }

               var9 = null;
            }

            this.setCurrentRowNumber(var6 + 1L);
            boolean var16 = this.targetTableFilter.isNullRow();
            if (!var16) {
               Row var13 = this.targetTableFilter.get();
               if (var5.isRowLockable()) {
                  Row var14 = var5.lockRow(this.session, var13);
                  if (var14 == null) {
                     if (var8 != var11) {
                        var9 = var11;
                     }
                     continue;
                  }

                  if (!var13.hasSharedData(var14)) {
                     var13 = var14;
                     this.targetTableFilter.set(var14);
                     if (!this.onCondition.getBooleanValue(this.session)) {
                        if (var8 != var11) {
                           var9 = var11;
                        }
                        continue;
                     }
                  }
               }

               if (var10) {
                  long var17 = var13.getKey();
                  if (!this.targetRowidsRemembered.add(var17)) {
                     throw DbException.get(23505, (String)("Merge using ON column expression, duplicate _ROWID_ target record already processed:_ROWID_=" + var17 + ":in:" + this.targetTableFilter.getTable()));
                  }
               }
            }

            var3 += (long)this.merge(var16, var1, var2);
            ++var6;
            var8 = var11;
         }

         if (var9 != null) {
            this.sourceTableFilter.set(var9);
            this.targetTableFilter.set(var5.getNullRow());
            var3 += (long)this.merge(true, var1, var2);
         }

         this.targetRowidsRemembered.clear();
         var5.fire(this.session, this.evaluateTriggerMasks(), false);
         return var3;
      }
   }

   private int merge(boolean var1, ResultTarget var2, DataChangeDeltaTable.ResultOption var3) {
      Iterator var4 = this.when.iterator();

      When var5;
      Expression var6;
      do {
         do {
            if (!var4.hasNext()) {
               return 0;
            }

            var5 = (When)var4.next();
         } while(var5.getClass() == WhenNotMatched.class != var1);

         var6 = var5.andCondition;
      } while(var6 != null && !var6.getBooleanValue(this.session));

      var5.merge(this.session, var2, var3);
      return 1;
   }

   private int evaluateTriggerMasks() {
      int var1 = 0;

      When var3;
      for(Iterator var2 = this.when.iterator(); var2.hasNext(); var1 |= var3.evaluateTriggerMasks()) {
         var3 = (When)var2.next();
      }

      return var1;
   }

   private void checkRights() {
      Iterator var1 = this.when.iterator();

      while(var1.hasNext()) {
         When var2 = (When)var1.next();
         var2.checkRights();
      }

      this.session.getUser().checkTableRight(this.targetTableFilter.getTable(), 1);
      this.session.getUser().checkTableRight(this.sourceTableFilter.getTable(), 1);
   }

   public String getPlanSQL(int var1) {
      StringBuilder var2 = new StringBuilder("MERGE INTO ");
      this.targetTableFilter.getPlanSQL(var2, false, var1);
      var2.append('\n').append("USING ");
      this.sourceTableFilter.getPlanSQL(var2, false, var1);
      Iterator var3 = this.when.iterator();

      while(var3.hasNext()) {
         When var4 = (When)var3.next();
         var4.getSQL(var2.append('\n'), var1);
      }

      return var2.toString();
   }

   public void prepare() {
      this.onCondition.addFilterConditions(this.sourceTableFilter);
      this.onCondition.addFilterConditions(this.targetTableFilter);
      this.onCondition.mapColumns(this.sourceTableFilter, 0, 0);
      this.onCondition.mapColumns(this.targetTableFilter, 0, 0);
      this.onCondition = this.onCondition.optimize(this.session);
      this.onCondition.createIndexConditions(this.session, this.targetTableFilter);
      TableFilter[] var1 = new TableFilter[]{this.sourceTableFilter, this.targetTableFilter};
      this.sourceTableFilter.addJoin(this.targetTableFilter, true, this.onCondition);
      PlanItem var2 = this.sourceTableFilter.getBestPlanItem(this.session, var1, 0, new AllColumnsForPlan(var1));
      this.sourceTableFilter.setPlanItem(var2);
      this.sourceTableFilter.prepare();
      boolean var3 = false;
      boolean var4 = false;
      Iterator var5 = this.when.iterator();

      while(var5.hasNext()) {
         When var6 = (When)var5.next();
         if (!var6.prepare(this.session)) {
            var5.remove();
         } else if (var6.getClass() == WhenNotMatched.class) {
            if (var3) {
               var5.remove();
            } else if (var6.andCondition == null) {
               var3 = true;
            }
         } else if (var4) {
            var5.remove();
         } else if (var6.andCondition == null) {
            var4 = true;
         }
      }

   }

   public void setSourceTableFilter(TableFilter var1) {
      this.sourceTableFilter = var1;
   }

   public TableFilter getSourceTableFilter() {
      return this.sourceTableFilter;
   }

   public void setOnCondition(Expression var1) {
      this.onCondition = var1;
   }

   public Expression getOnCondition() {
      return this.onCondition;
   }

   public ArrayList<When> getWhen() {
      return this.when;
   }

   public void addWhen(When var1) {
      this.when.add(var1);
   }

   public Table getTable() {
      return this.targetTableFilter.getTable();
   }

   public void setTargetTableFilter(TableFilter var1) {
      this.targetTableFilter = var1;
   }

   public TableFilter getTargetTableFilter() {
      return this.targetTableFilter;
   }

   public int getType() {
      return 62;
   }

   public String getStatementName() {
      return "MERGE";
   }

   public void collectDependencies(HashSet<DbObject> var1) {
      var1.add(this.targetTableFilter.getTable());
      var1.add(this.sourceTableFilter.getTable());
      ExpressionVisitor var2 = ExpressionVisitor.getDependenciesVisitor(var1);
      Iterator var3 = this.when.iterator();

      while(var3.hasNext()) {
         When var4 = (When)var3.next();
         var4.collectDependencies(var2);
      }

      this.onCondition.isEverything(var2);
   }

   public final class WhenNotMatched extends When {
      private Column[] columns;
      private final Boolean overridingSystem;
      private final Expression[] values;

      public WhenNotMatched(Column[] var2, Boolean var3, Expression[] var4) {
         super();
         this.columns = var2;
         this.overridingSystem = var3;
         this.values = var4;
      }

      void merge(SessionLocal var1, ResultTarget var2, DataChangeDeltaTable.ResultOption var3) {
         Table var4 = MergeUsing.this.targetTableFilter.getTable();
         Row var5 = var4.getTemplateRow();
         Expression[] var6 = this.values;
         int var7 = 0;

         for(int var8 = this.columns.length; var7 < var8; ++var7) {
            Column var9 = this.columns[var7];
            int var10 = var9.getColumnId();
            Expression var11 = var6[var7];
            if (var11 != ValueExpression.DEFAULT) {
               try {
                  var5.setValue(var10, var11.getValue(var1));
               } catch (DbException var13) {
                  var13.addSQL("INSERT -- " + Prepared.getSimpleSQL(var6));
                  throw var13;
               }
            }
         }

         var4.convertInsertRow(var1, var5, this.overridingSystem);
         if (var3 == DataChangeDeltaTable.ResultOption.NEW) {
            var2.addRow((Value[])var5.getValueList().clone());
         }

         if (!var4.fireBeforeRow(var1, (Row)null, var5)) {
            var4.addRow(var1, var5);
            DataChangeDeltaTable.collectInsertedFinalRow(var1, var4, var2, var3, var5);
            var4.fireAfterRow(var1, (Row)null, var5, false);
         } else {
            DataChangeDeltaTable.collectInsertedFinalRow(var1, var4, var2, var3, var5);
         }

      }

      boolean prepare(SessionLocal var1) {
         boolean var2 = super.prepare(var1);
         TableFilter var3 = MergeUsing.this.targetTableFilter;
         TableFilter var4 = MergeUsing.this.sourceTableFilter;
         if (this.columns == null) {
            this.columns = var3.getTable().getColumns();
         }

         if (this.values.length != this.columns.length) {
            throw DbException.get(21002);
         } else {
            int var5 = 0;

            for(int var6 = this.values.length; var5 < var6; ++var5) {
               Expression var7 = this.values[var5];
               var7.mapColumns(var3, 0, 0);
               var7.mapColumns(var4, 0, 0);
               var7 = var7.optimize(var1);
               if (var7 instanceof Parameter) {
                  ((Parameter)var7).setColumn(this.columns[var5]);
               }

               this.values[var5] = var7;
            }

            return var2;
         }
      }

      int evaluateTriggerMasks() {
         return 1;
      }

      void checkRights() {
         MergeUsing.this.getSession().getUser().checkTableRight(MergeUsing.this.targetTableFilter.getTable(), 4);
      }

      void collectDependencies(ExpressionVisitor var1) {
         super.collectDependencies(var1);
         Expression[] var2 = this.values;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Expression var5 = var2[var4];
            var5.isEverything(var1);
         }

      }

      public StringBuilder getSQL(StringBuilder var1, int var2) {
         super.getSQL(var1, var2).append("INSERT (");
         Column.writeColumns(var1, this.columns, var2).append(")\nVALUES (");
         return Expression.writeExpressions(var1, this.values, var2).append(')');
      }
   }

   public final class WhenMatchedThenUpdate extends When {
      private SetClauseList setClauseList;

      public WhenMatchedThenUpdate() {
         super();
      }

      public void setSetClauseList(SetClauseList var1) {
         this.setClauseList = var1;
      }

      void merge(SessionLocal var1, ResultTarget var2, DataChangeDeltaTable.ResultOption var3) {
         TableFilter var4 = MergeUsing.this.targetTableFilter;
         Table var5 = var4.getTable();
         LocalResult var6 = LocalResult.forTable(var1, var5);
         Throwable var7 = null;

         try {
            this.setClauseList.prepareUpdate(var5, var1, var2, var3, var6, var4.get(), false);
            Update.doUpdate(MergeUsing.this, var1, var5, var6);
         } catch (Throwable var16) {
            var7 = var16;
            throw var16;
         } finally {
            if (var6 != null) {
               if (var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var15) {
                     var7.addSuppressed(var15);
                  }
               } else {
                  var6.close();
               }
            }

         }

      }

      boolean prepare(SessionLocal var1) {
         boolean var2 = super.prepare(var1);
         this.setClauseList.mapAndOptimize(var1, MergeUsing.this.targetTableFilter, MergeUsing.this.sourceTableFilter);
         return var2;
      }

      int evaluateTriggerMasks() {
         return 2;
      }

      void checkRights() {
         MergeUsing.this.getSession().getUser().checkTableRight(MergeUsing.this.targetTableFilter.getTable(), 8);
      }

      void collectDependencies(ExpressionVisitor var1) {
         super.collectDependencies(var1);
         this.setClauseList.isEverything(var1);
      }

      public StringBuilder getSQL(StringBuilder var1, int var2) {
         return this.setClauseList.getSQL(super.getSQL(var1, var2).append("UPDATE"), var2);
      }
   }

   public final class WhenMatchedThenDelete extends When {
      public WhenMatchedThenDelete() {
         super();
      }

      void merge(SessionLocal var1, ResultTarget var2, DataChangeDeltaTable.ResultOption var3) {
         TableFilter var4 = MergeUsing.this.targetTableFilter;
         Table var5 = var4.getTable();
         Row var6 = var4.get();
         if (var3 == DataChangeDeltaTable.ResultOption.OLD) {
            var2.addRow(var6.getValueList());
         }

         if (!var5.fireRow() || !var5.fireBeforeRow(var1, var6, (Row)null)) {
            var5.removeRow(var1, var6);
            var5.fireAfterRow(var1, var6, (Row)null, false);
         }

      }

      int evaluateTriggerMasks() {
         return 4;
      }

      void checkRights() {
         MergeUsing.this.getSession().getUser().checkTableRight(MergeUsing.this.targetTableFilter.getTable(), 2);
      }

      public StringBuilder getSQL(StringBuilder var1, int var2) {
         return super.getSQL(var1, var2).append("DELETE");
      }
   }

   public abstract class When implements HasSQL {
      Expression andCondition;

      When() {
      }

      public void setAndCondition(Expression var1) {
         this.andCondition = var1;
      }

      abstract void merge(SessionLocal var1, ResultTarget var2, DataChangeDeltaTable.ResultOption var3);

      boolean prepare(SessionLocal var1) {
         if (this.andCondition != null) {
            this.andCondition.mapColumns(MergeUsing.this.targetTableFilter, 0, 0);
            this.andCondition.mapColumns(MergeUsing.this.sourceTableFilter, 0, 0);
            this.andCondition = this.andCondition.optimize(var1);
            if (this.andCondition.isConstant()) {
               if (!this.andCondition.getBooleanValue(var1)) {
                  return false;
               }

               this.andCondition = null;
            }
         }

         return true;
      }

      abstract int evaluateTriggerMasks();

      abstract void checkRights();

      void collectDependencies(ExpressionVisitor var1) {
         if (this.andCondition != null) {
            this.andCondition.isEverything(var1);
         }

      }

      public StringBuilder getSQL(StringBuilder var1, int var2) {
         var1.append("WHEN ");
         if (this.getClass() == WhenNotMatched.class) {
            var1.append("NOT ");
         }

         var1.append("MATCHED");
         if (this.andCondition != null) {
            this.andCondition.getUnenclosedSQL(var1.append(" AND "), var2);
         }

         return var1.append(" THEN ");
      }
   }
}
