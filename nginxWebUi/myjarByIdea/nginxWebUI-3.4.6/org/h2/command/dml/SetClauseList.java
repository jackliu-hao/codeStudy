package org.h2.command.dml;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionList;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultTarget;
import org.h2.result.Row;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.DataChangeDeltaTable;
import org.h2.table.Table;
import org.h2.util.HasSQL;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public final class SetClauseList implements HasSQL {
   private final Table table;
   private final UpdateAction[] actions;
   private boolean onUpdate;

   public SetClauseList(Table var1) {
      this.table = var1;
      this.actions = new UpdateAction[var1.getColumns().length];
   }

   public void addSingle(Column var1, Expression var2) {
      int var3 = var1.getColumnId();
      if (this.actions[var3] != null) {
         throw DbException.get(42121, (String)var1.getName());
      } else {
         if (var2 != ValueExpression.DEFAULT) {
            this.actions[var3] = new SetSimple(var2);
            if (var2 instanceof Parameter) {
               ((Parameter)var2).setColumn(var1);
            }
         } else {
            this.actions[var3] = SetClauseList.UpdateAction.SET_DEFAULT;
         }

      }
   }

   public void addMultiple(ArrayList<Column> var1, Expression var2) {
      int var3 = var1.size();
      if (var2 instanceof ExpressionList) {
         ExpressionList var4 = (ExpressionList)var2;
         if (!var4.isArray()) {
            if (var3 != var4.getSubexpressionCount()) {
               throw DbException.get(21002);
            }

            for(int var12 = 0; var12 < var3; ++var12) {
               this.addSingle((Column)var1.get(var12), var4.getSubexpression(var12));
            }

            return;
         }
      }

      if (var3 == 1) {
         this.addSingle((Column)var1.get(0), var2);
      } else {
         int[] var11 = new int[var3];
         RowExpression var5 = new RowExpression(var2, var11);
         int var6 = this.table.getColumns().length - 1;
         int var7 = 0;

         int var8;
         for(var8 = 0; var8 < var3; ++var8) {
            int var9 = ((Column)var1.get(var8)).getColumnId();
            if (var9 < var6) {
               var6 = var9;
            }

            if (var9 > var7) {
               var7 = var9;
            }
         }

         for(var8 = 0; var8 < var3; ++var8) {
            Column var13 = (Column)var1.get(var8);
            int var10 = var13.getColumnId();
            var11[var8] = var10;
            if (this.actions[var10] != null) {
               throw DbException.get(42121, (String)var13.getName());
            }

            this.actions[var10] = new SetMultiple(var5, var8, var10 == var6, var10 == var7);
         }
      }

   }

   boolean prepareUpdate(Table var1, SessionLocal var2, ResultTarget var3, DataChangeDeltaTable.ResultOption var4, LocalResult var5, Row var6, boolean var7) {
      Column[] var8 = var1.getColumns();
      int var9 = var8.length;
      Row var10 = var1.getTemplateRow();

      for(int var11 = 0; var11 < var9; ++var11) {
         UpdateAction var12 = this.actions[var11];
         Column var13 = var8[var11];
         Value var14;
         if (var12 != null && var12 != SetClauseList.UpdateAction.ON_UPDATE) {
            if (var12 == SetClauseList.UpdateAction.SET_DEFAULT) {
               var14 = !var13.isIdentity() ? null : var6.getValue(var11);
            } else {
               var14 = var12.update(var2);
               if (var14 == ValueNull.INSTANCE && var13.isDefaultOnNull()) {
                  var14 = !var13.isIdentity() ? null : var6.getValue(var11);
               } else if (var13.isGeneratedAlways()) {
                  throw DbException.get(90154, var13.getSQLWithTable(new StringBuilder(), 3).toString());
               }
            }
         } else {
            var14 = var13.isGenerated() ? null : var6.getValue(var11);
         }

         var10.setValue(var11, var14);
      }

      var10.setKey(var6.getKey());
      var1.convertUpdateRow(var2, var10, false);
      boolean var15 = true;
      if (this.onUpdate) {
         if (!var6.hasSameValues(var10)) {
            for(int var16 = 0; var16 < var9; ++var16) {
               if (this.actions[var16] == SetClauseList.UpdateAction.ON_UPDATE) {
                  var10.setValue(var16, var8[var16].getEffectiveOnUpdateExpression().getValue(var2));
               } else if (var8[var16].isGenerated()) {
                  var10.setValue(var16, (Value)null);
               }
            }

            var1.convertUpdateRow(var2, var10, false);
         } else if (var7) {
            var15 = false;
         }
      } else if (var7 && var6.hasSameValues(var10)) {
         var15 = false;
      }

      if (var4 == DataChangeDeltaTable.ResultOption.OLD) {
         var3.addRow(var6.getValueList());
      } else if (var4 == DataChangeDeltaTable.ResultOption.NEW) {
         var3.addRow((Value[])var10.getValueList().clone());
      }

      if (!var1.fireRow() || !var1.fireBeforeRow(var2, var6, var10)) {
         var5.addRowForTable(var6);
         var5.addRowForTable(var10);
      }

      if (var4 == DataChangeDeltaTable.ResultOption.FINAL) {
         var3.addRow(var10.getValueList());
      }

      return var15;
   }

   boolean isEverything(ExpressionVisitor var1) {
      UpdateAction[] var2 = this.actions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         UpdateAction var5 = var2[var4];
         if (var5 != null && !var5.isEverything(var1)) {
            return false;
         }
      }

      return true;
   }

   void mapAndOptimize(SessionLocal var1, ColumnResolver var2, ColumnResolver var3) {
      Column[] var4 = this.table.getColumns();
      boolean var5 = false;

      for(int var6 = 0; var6 < this.actions.length; ++var6) {
         UpdateAction var7 = this.actions[var6];
         if (var7 != null) {
            var7.mapAndOptimize(var1, var2, var3);
         } else {
            Column var8 = var4[var6];
            if (var8.getEffectiveOnUpdateExpression() != null) {
               this.actions[var6] = SetClauseList.UpdateAction.ON_UPDATE;
               var5 = true;
            }
         }
      }

      this.onUpdate = var5;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      Column[] var3 = this.table.getColumns();
      var1.append("\nSET\n    ");
      boolean var4 = false;

      for(int var5 = 0; var5 < this.actions.length; ++var5) {
         UpdateAction var6 = this.actions[var5];
         if (var6 != null && var6 != SetClauseList.UpdateAction.ON_UPDATE) {
            if (var6.getClass() != SetMultiple.class) {
               if (var4) {
                  var1.append(",\n    ");
               }

               var4 = true;
               Column var12 = var3[var5];
               if (var6 != SetClauseList.UpdateAction.SET_DEFAULT) {
                  var6.getSQL(var1, var2, var12);
               } else {
                  var12.getSQL(var1, var2).append(" = DEFAULT");
               }
            } else {
               SetMultiple var7 = (SetMultiple)var6;
               if (var7.first) {
                  if (var4) {
                     var1.append(",\n    ");
                  }

                  var4 = true;
                  RowExpression var8 = var7.row;
                  var1.append('(');
                  int[] var9 = var8.columns;
                  int var10 = 0;

                  for(int var11 = var9.length; var10 < var11; ++var10) {
                     if (var10 > 0) {
                        var1.append(", ");
                     }

                     var3[var9[var10]].getSQL(var1, var2);
                  }

                  var8.expression.getUnenclosedSQL(var1.append(") = "), var2);
               }
            }
         }
      }

      return var1;
   }

   private static final class SetMultiple extends UpdateAction {
      final RowExpression row;
      private final int position;
      boolean first;
      private boolean last;

      SetMultiple(RowExpression var1, int var2, boolean var3, boolean var4) {
         this.row = var1;
         this.position = var2;
         this.first = var3;
         this.last = var4;
      }

      Value update(SessionLocal var1) {
         Value[] var2;
         if (this.first) {
            Value var3 = this.row.expression.getValue(var1);
            if (var3 == ValueNull.INSTANCE) {
               throw DbException.get(22018, (String)"NULL to assigned row value");
            }

            this.row.values = var2 = var3.convertToAnyRow().getList();
            if (var2.length != this.row.columns.length) {
               throw DbException.get(21002);
            }
         } else {
            var2 = this.row.values;
            if (this.last) {
               this.row.values = null;
            }
         }

         return var2[this.position];
      }

      boolean isEverything(ExpressionVisitor var1) {
         return !this.first || this.row.isEverything(var1);
      }

      void mapAndOptimize(SessionLocal var1, ColumnResolver var2, ColumnResolver var3) {
         if (this.first) {
            this.row.mapAndOptimize(var1, var2, var3);
         }

      }
   }

   private static final class RowExpression {
      Expression expression;
      final int[] columns;
      Value[] values;

      RowExpression(Expression var1, int[] var2) {
         this.expression = var1;
         this.columns = var2;
      }

      boolean isEverything(ExpressionVisitor var1) {
         return this.expression.isEverything(var1);
      }

      void mapAndOptimize(SessionLocal var1, ColumnResolver var2, ColumnResolver var3) {
         this.expression.mapColumns(var2, 0, 0);
         if (var3 != null) {
            this.expression.mapColumns(var3, 0, 0);
         }

         this.expression = this.expression.optimize(var1);
      }
   }

   private static final class SetSimple extends UpdateAction {
      private Expression expression;

      SetSimple(Expression var1) {
         this.expression = var1;
      }

      Value update(SessionLocal var1) {
         return this.expression.getValue(var1);
      }

      boolean isEverything(ExpressionVisitor var1) {
         return this.expression.isEverything(var1);
      }

      void mapAndOptimize(SessionLocal var1, ColumnResolver var2, ColumnResolver var3) {
         this.expression.mapColumns(var2, 0, 0);
         if (var3 != null) {
            this.expression.mapColumns(var3, 0, 0);
         }

         this.expression = this.expression.optimize(var1);
      }

      void getSQL(StringBuilder var1, int var2, Column var3) {
         this.expression.getUnenclosedSQL(var3.getSQL(var1, var2).append(" = "), var2);
      }
   }

   private static class UpdateAction {
      static UpdateAction ON_UPDATE = new UpdateAction();
      static UpdateAction SET_DEFAULT = new UpdateAction();

      UpdateAction() {
      }

      Value update(SessionLocal var1) {
         throw DbException.getInternalError();
      }

      boolean isEverything(ExpressionVisitor var1) {
         return true;
      }

      void mapAndOptimize(SessionLocal var1, ColumnResolver var2, ColumnResolver var3) {
      }

      void getSQL(StringBuilder var1, int var2, Column var3) {
         throw DbException.getInternalError();
      }
   }
}
