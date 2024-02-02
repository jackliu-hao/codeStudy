package org.h2.command.dml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.h2.command.Command;
import org.h2.command.query.Query;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.ValueExpression;
import org.h2.expression.condition.Comparison;
import org.h2.expression.condition.ConditionAndOr;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.mvstore.db.MVPrimaryIndex;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.result.Row;
import org.h2.table.Column;
import org.h2.table.DataChangeDeltaTable;
import org.h2.table.Table;
import org.h2.value.Value;

public final class Insert extends CommandWithValues implements ResultTarget {
   private Table table;
   private Column[] columns;
   private Query query;
   private long rowNumber;
   private boolean insertFromSelect;
   private Boolean overridingSystem;
   private HashMap<Column, Expression> duplicateKeyAssignmentMap;
   private Value[] onDuplicateKeyRow;
   private boolean ignore;
   private ResultTarget deltaChangeCollector;
   private DataChangeDeltaTable.ResultOption deltaChangeCollectionMode;

   public Insert(SessionLocal var1) {
      super(var1);
   }

   public void setCommand(Command var1) {
      super.setCommand(var1);
      if (this.query != null) {
         this.query.setCommand(var1);
      }

   }

   public Table getTable() {
      return this.table;
   }

   public void setTable(Table var1) {
      this.table = var1;
   }

   public void setColumns(Column[] var1) {
      this.columns = var1;
   }

   public void setIgnore(boolean var1) {
      this.ignore = var1;
   }

   public void setQuery(Query var1) {
      this.query = var1;
   }

   public void setOverridingSystem(Boolean var1) {
      this.overridingSystem = var1;
   }

   public void addAssignmentForDuplicate(Column var1, Expression var2) {
      if (this.duplicateKeyAssignmentMap == null) {
         this.duplicateKeyAssignmentMap = new HashMap();
      }

      if (this.duplicateKeyAssignmentMap.putIfAbsent(var1, var2) != null) {
         throw DbException.get(42121, (String)var1.getName());
      }
   }

   public long update(ResultTarget var1, DataChangeDeltaTable.ResultOption var2) {
      this.deltaChangeCollector = var1;
      this.deltaChangeCollectionMode = var2;

      long var3;
      try {
         var3 = this.insertRows();
      } finally {
         this.deltaChangeCollector = null;
         this.deltaChangeCollectionMode = null;
      }

      return var3;
   }

   private long insertRows() {
      this.session.getUser().checkTableRight(this.table, 4);
      this.setCurrentRowNumber(0L);
      this.table.fire(this.session, 1, true);
      this.rowNumber = 0L;
      int var1 = this.valuesExpressionList.size();
      if (var1 > 0) {
         int var2 = this.columns.length;

         for(int var3 = 0; var3 < var1; ++var3) {
            Row var4 = this.table.getTemplateRow();
            Expression[] var5 = (Expression[])this.valuesExpressionList.get(var3);
            this.setCurrentRowNumber((long)(var3 + 1));

            for(int var6 = 0; var6 < var2; ++var6) {
               Column var7 = this.columns[var6];
               int var8 = var7.getColumnId();
               Expression var9 = var5[var6];
               if (var9 != ValueExpression.DEFAULT) {
                  try {
                     var4.setValue(var8, var9.getValue(this.session));
                  } catch (DbException var11) {
                     throw this.setRow(var11, (long)var3, getSimpleSQL(var5));
                  }
               }
            }

            ++this.rowNumber;
            this.table.convertInsertRow(this.session, var4, this.overridingSystem);
            if (this.deltaChangeCollectionMode == DataChangeDeltaTable.ResultOption.NEW) {
               this.deltaChangeCollector.addRow((Value[])var4.getValueList().clone());
            }

            if (!this.table.fireBeforeRow(this.session, (Row)null, var4)) {
               this.table.lock(this.session, 1);

               try {
                  this.table.addRow(this.session, var4);
               } catch (DbException var13) {
                  if (this.handleOnDuplicate(var13, (Value[])null)) {
                     ++this.rowNumber;
                     continue;
                  }

                  --this.rowNumber;
                  continue;
               }

               DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, this.deltaChangeCollector, this.deltaChangeCollectionMode, var4);
               this.table.fireAfterRow(this.session, (Row)null, var4, false);
            } else {
               DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, this.deltaChangeCollector, this.deltaChangeCollectionMode, var4);
            }
         }
      } else {
         this.table.lock(this.session, 1);
         if (this.insertFromSelect) {
            this.query.query(0L, this);
         } else {
            ResultInterface var14 = this.query.query(0L);

            while(var14.next()) {
               Value[] var15 = var14.currentRow();

               try {
                  this.addRow(var15);
               } catch (DbException var12) {
                  if (this.handleOnDuplicate(var12, var15)) {
                     ++this.rowNumber;
                  } else {
                     --this.rowNumber;
                  }
               }
            }

            var14.close();
         }
      }

      this.table.fire(this.session, 1, false);
      return this.rowNumber;
   }

   public void addRow(Value... var1) {
      Row var2 = this.table.getTemplateRow();
      this.setCurrentRowNumber(++this.rowNumber);
      int var3 = 0;

      for(int var4 = this.columns.length; var3 < var4; ++var3) {
         var2.setValue(this.columns[var3].getColumnId(), var1[var3]);
      }

      this.table.convertInsertRow(this.session, var2, this.overridingSystem);
      if (this.deltaChangeCollectionMode == DataChangeDeltaTable.ResultOption.NEW) {
         this.deltaChangeCollector.addRow((Value[])var2.getValueList().clone());
      }

      if (!this.table.fireBeforeRow(this.session, (Row)null, var2)) {
         this.table.addRow(this.session, var2);
         DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, this.deltaChangeCollector, this.deltaChangeCollectionMode, var2);
         this.table.fireAfterRow(this.session, (Row)null, var2, false);
      } else {
         DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, this.deltaChangeCollector, this.deltaChangeCollectionMode, var2);
      }

   }

   public long getRowCount() {
      return this.rowNumber;
   }

   public void limitsWereApplied() {
   }

   public String getPlanSQL(int var1) {
      StringBuilder var2 = new StringBuilder("INSERT INTO ");
      this.table.getSQL(var2, var1).append('(');
      Column.writeColumns(var2, this.columns, var1);
      var2.append(")\n");
      if (this.insertFromSelect) {
         var2.append("DIRECT ");
      }

      if (!this.valuesExpressionList.isEmpty()) {
         var2.append("VALUES ");
         int var3 = 0;
         if (this.valuesExpressionList.size() > 1) {
            var2.append('\n');
         }

         Expression[] var5;
         for(Iterator var4 = this.valuesExpressionList.iterator(); var4.hasNext(); Expression.writeExpressions(var2.append('('), var5, var1).append(')')) {
            var5 = (Expression[])var4.next();
            if (var3++ > 0) {
               var2.append(",\n");
            }
         }
      } else {
         var2.append(this.query.getPlanSQL(var1));
      }

      return var2.toString();
   }

   public void prepare() {
      if (this.columns == null) {
         if (!this.valuesExpressionList.isEmpty() && ((Expression[])this.valuesExpressionList.get(0)).length == 0) {
            this.columns = new Column[0];
         } else {
            this.columns = this.table.getColumns();
         }
      }

      if (!this.valuesExpressionList.isEmpty()) {
         Iterator var1 = this.valuesExpressionList.iterator();

         while(var1.hasNext()) {
            Expression[] var2 = (Expression[])var1.next();
            if (var2.length != this.columns.length) {
               throw DbException.get(21002);
            }

            int var3 = 0;

            for(int var4 = var2.length; var3 < var4; ++var3) {
               Expression var5 = var2[var3];
               if (var5 != null) {
                  var5 = var5.optimize(this.session);
                  if (var5 instanceof Parameter) {
                     Parameter var6 = (Parameter)var5;
                     var6.setColumn(this.columns[var3]);
                  }

                  var2[var3] = var5;
               }
            }
         }
      } else {
         this.query.prepare();
         if (this.query.getColumnCount() != this.columns.length) {
            throw DbException.get(21002);
         }
      }

   }

   public int getType() {
      return 61;
   }

   public String getStatementName() {
      return "INSERT";
   }

   public void setInsertFromSelect(boolean var1) {
      this.insertFromSelect = var1;
   }

   public boolean isCacheable() {
      return this.duplicateKeyAssignmentMap == null;
   }

   private boolean handleOnDuplicate(DbException var1, Value[] var2) {
      if (var1.getErrorCode() != 23505) {
         throw var1;
      } else if (this.duplicateKeyAssignmentMap == null) {
         if (this.ignore) {
            return false;
         } else {
            throw var1;
         }
      } else {
         int var3 = this.columns.length;
         Expression[] var4 = var2 == null ? (Expression[])this.valuesExpressionList.get((int)this.getCurrentRowNumber() - 1) : new Expression[var3];
         this.onDuplicateKeyRow = new Value[this.table.getColumns().length];

         for(int var5 = 0; var5 < var3; ++var5) {
            Value var6;
            if (var2 != null) {
               var6 = var2[var5];
               var4[var5] = ValueExpression.get(var6);
            } else {
               var6 = var4[var5].getValue(this.session);
            }

            this.onDuplicateKeyRow[this.columns[var5].getColumnId()] = var6;
         }

         StringBuilder var13 = new StringBuilder("UPDATE ");
         this.table.getSQL(var13, 0).append(" SET ");
         boolean var14 = false;
         Iterator var7 = this.duplicateKeyAssignmentMap.entrySet().iterator();

         while(var7.hasNext()) {
            Map.Entry var8 = (Map.Entry)var7.next();
            if (var14) {
               var13.append(", ");
            }

            var14 = true;
            ((Column)var8.getKey()).getSQL(var13, 0).append('=');
            ((Expression)var8.getValue()).getUnenclosedSQL(var13, 0);
         }

         var13.append(" WHERE ");
         Index var15 = (Index)var1.getSource();
         if (var15 == null) {
            throw DbException.getUnsupportedException("Unable to apply ON DUPLICATE KEY UPDATE, no index found!");
         } else {
            this.prepareUpdateCondition(var15, var4).getUnenclosedSQL(var13, 0);
            String var16 = var13.toString();
            Update var9 = (Update)this.session.prepare(var16);
            var9.setOnDuplicateKeyInsert(this);
            Iterator var10 = var9.getParameters().iterator();

            while(var10.hasNext()) {
               Parameter var11 = (Parameter)var10.next();
               Parameter var12 = (Parameter)this.parameters.get(var11.getIndex());
               var11.setValue(var12.getValue(this.session));
            }

            boolean var17 = var9.update() > 0L;
            this.onDuplicateKeyRow = null;
            return var17;
         }
      }
   }

   private Expression prepareUpdateCondition(Index var1, Expression[] var2) {
      Column[] var3;
      if (var1 instanceof MVPrimaryIndex) {
         MVPrimaryIndex var4 = (MVPrimaryIndex)var1;
         var3 = new Column[]{var4.getIndexColumns()[var4.getMainIndexColumn()].column};
      } else {
         var3 = var1.getColumns();
      }

      Object var11 = null;
      Column[] var5 = var3;
      int var6 = var3.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Column var8 = var5[var7];
         ExpressionColumn var9 = new ExpressionColumn(this.session.getDatabase(), this.table.getSchema().getName(), this.table.getName(), var8.getName());

         for(int var10 = 0; var10 < this.columns.length; ++var10) {
            if (var9.getColumnName(this.session, var10).equals(this.columns[var10].getName())) {
               if (var11 == null) {
                  var11 = new Comparison(0, var9, var2[var10], false);
               } else {
                  var11 = new ConditionAndOr(0, (Expression)var11, new Comparison(0, var9, var2[var10], false));
               }
               break;
            }
         }
      }

      return (Expression)var11;
   }

   public Value getOnDuplicateKeyValue(int var1) {
      return this.onDuplicateKeyRow[var1];
   }

   public void collectDependencies(HashSet<DbObject> var1) {
      ExpressionVisitor var2 = ExpressionVisitor.getDependenciesVisitor(var1);
      if (!this.valuesExpressionList.isEmpty()) {
         Iterator var3 = this.valuesExpressionList.iterator();

         while(var3.hasNext()) {
            Expression[] var4 = (Expression[])var3.next();
            Expression[] var5 = var4;
            int var6 = var4.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Expression var8 = var5[var7];
               var8.isEverything(var2);
            }
         }
      } else {
         this.query.isEverything(var2);
      }

   }
}
