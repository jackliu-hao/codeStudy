package org.h2.command.dml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.command.Command;
import org.h2.command.query.Query;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Parameter;
import org.h2.expression.ValueExpression;
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
import org.h2.value.ValueNull;

public final class Merge extends CommandWithValues {
   private boolean isReplace;
   private Table table;
   private Column[] columns;
   private Column[] keys;
   private Query query;
   private Update update;

   public Merge(SessionLocal var1, boolean var2) {
      super(var1);
      this.isReplace = var2;
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

   public void setKeys(Column[] var1) {
      this.keys = var1;
   }

   public void setQuery(Query var1) {
      this.query = var1;
   }

   public long update(ResultTarget var1, DataChangeDeltaTable.ResultOption var2) {
      long var3 = 0L;
      this.session.getUser().checkTableRight(this.table, 4);
      this.session.getUser().checkTableRight(this.table, 8);
      this.setCurrentRowNumber(0L);
      if (!this.valuesExpressionList.isEmpty()) {
         int var5 = 0;

         for(int var6 = this.valuesExpressionList.size(); var5 < var6; ++var5) {
            this.setCurrentRowNumber((long)(var5 + 1));
            Expression[] var7 = (Expression[])this.valuesExpressionList.get(var5);
            Row var8 = this.table.getTemplateRow();
            int var9 = 0;

            for(int var10 = this.columns.length; var9 < var10; ++var9) {
               Column var11 = this.columns[var9];
               int var12 = var11.getColumnId();
               Expression var13 = var7[var9];
               if (var13 != ValueExpression.DEFAULT) {
                  try {
                     var8.setValue(var12, var13.getValue(this.session));
                  } catch (DbException var15) {
                     throw this.setRow(var15, var3, getSimpleSQL(var7));
                  }
               }
            }

            var3 += (long)this.merge(var8, var7, var1, var2);
         }
      } else {
         this.query.setNeverLazy(true);
         ResultInterface var16 = this.query.query(0L);
         this.table.fire(this.session, 3, true);
         this.table.lock(this.session, 1);

         while(var16.next()) {
            Value[] var17 = var16.currentRow();
            Row var18 = this.table.getTemplateRow();
            this.setCurrentRowNumber(var3);

            for(int var19 = 0; var19 < this.columns.length; ++var19) {
               var18.setValue(this.columns[var19].getColumnId(), var17[var19]);
            }

            var3 += (long)this.merge(var18, (Expression[])null, var1, var2);
         }

         var16.close();
         this.table.fire(this.session, 3, false);
      }

      return var3;
   }

   private int merge(Row var1, Expression[] var2, ResultTarget var3, DataChangeDeltaTable.ResultOption var4) {
      long var5;
      Column[] var16;
      int var19;
      if (this.update == null) {
         var5 = 0L;
      } else {
         ArrayList var7 = this.update.getParameters();
         int var8 = 0;
         int var9 = 0;
         int var10 = this.columns.length;

         while(true) {
            if (var9 >= var10) {
               var16 = this.keys;
               var10 = var16.length;

               for(var19 = 0; var19 < var10; ++var19) {
                  Column var20 = var16[var19];
                  Value var21 = var1.getValue(var20.getColumnId());
                  if (var21 == null) {
                     throw DbException.get(90081, var20.getTraceSQL());
                  }

                  ((Parameter)var7.get(var8++)).setValue(var21);
               }

               var5 = this.update.update(var3, var4);
               break;
            }

            Column var11 = this.columns[var9];
            if (var11.isGeneratedAlways()) {
               if (var2 == null || var2[var9] != ValueExpression.DEFAULT) {
                  throw DbException.get(90154, var11.getSQLWithTable(new StringBuilder(), 3).toString());
               }
            } else {
               Object var12 = var1.getValue(var11.getColumnId());
               if (var12 == null) {
                  Expression var13 = var11.getEffectiveDefaultExpression();
                  var12 = var13 != null ? var13.getValue(this.session) : ValueNull.INSTANCE;
               }

               ((Parameter)var7.get(var8++)).setValue((Value)var12);
            }

            ++var9;
         }
      }

      if (var5 == 0L) {
         try {
            this.table.convertInsertRow(this.session, var1, (Boolean)null);
            if (var4 == DataChangeDeltaTable.ResultOption.NEW) {
               var3.addRow((Value[])var1.getValueList().clone());
            }

            if (!this.table.fireBeforeRow(this.session, (Row)null, var1)) {
               this.table.lock(this.session, 1);
               this.table.addRow(this.session, var1);
               DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, var3, var4, var1);
               this.table.fireAfterRow(this.session, (Row)null, var1, false);
            } else {
               DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, var3, var4, var1);
            }

            return 1;
         } catch (DbException var14) {
            if (var14.getErrorCode() == 23505) {
               Index var15 = (Index)var14.getSource();
               if (var15 != null) {
                  if (var15 instanceof MVPrimaryIndex) {
                     MVPrimaryIndex var17 = (MVPrimaryIndex)var15;
                     var16 = new Column[]{var17.getIndexColumns()[var17.getMainIndexColumn()].column};
                  } else {
                     var16 = var15.getColumns();
                  }

                  boolean var18;
                  if (var16.length <= this.keys.length) {
                     var18 = true;

                     for(var19 = 0; var19 < var16.length; ++var19) {
                        if (var16[var19] != this.keys[var19]) {
                           var18 = false;
                           break;
                        }
                     }
                  } else {
                     var18 = false;
                  }

                  if (var18) {
                     throw DbException.get(90131, this.table.getName());
                  }
               }
            }

            throw var14;
         }
      } else if (var5 == 1L) {
         return this.isReplace ? 2 : 1;
      } else {
         throw DbException.get(23505, (String)this.table.getTraceSQL());
      }
   }

   public String getPlanSQL(int var1) {
      StringBuilder var2 = new StringBuilder(this.isReplace ? "REPLACE INTO " : "MERGE INTO ");
      this.table.getSQL(var2, var1).append('(');
      Column.writeColumns(var2, this.columns, var1);
      var2.append(')');
      if (!this.isReplace && this.keys != null) {
         var2.append(" KEY(");
         Column.writeColumns(var2, this.keys, var1);
         var2.append(')');
      }

      var2.append('\n');
      if (!this.valuesExpressionList.isEmpty()) {
         var2.append("VALUES ");
         int var3 = 0;

         Expression[] var5;
         for(Iterator var4 = this.valuesExpressionList.iterator(); var4.hasNext(); Expression.writeExpressions(var2.append('('), var5, var1).append(')')) {
            var5 = (Expression[])var4.next();
            if (var3++ > 0) {
               var2.append(", ");
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

      int var3;
      if (!this.valuesExpressionList.isEmpty()) {
         Iterator var1 = this.valuesExpressionList.iterator();

         while(var1.hasNext()) {
            Expression[] var2 = (Expression[])var1.next();
            if (var2.length != this.columns.length) {
               throw DbException.get(21002);
            }

            for(var3 = 0; var3 < var2.length; ++var3) {
               Expression var4 = var2[var3];
               if (var4 != null) {
                  var2[var3] = var4.optimize(this.session);
               }
            }
         }
      } else {
         this.query.prepare();
         if (this.query.getColumnCount() != this.columns.length) {
            throw DbException.get(21002);
         }
      }

      if (this.keys == null) {
         Index var10 = this.table.getPrimaryKey();
         if (var10 == null) {
            throw DbException.get(90057, "PRIMARY KEY");
         }

         this.keys = var10.getColumns();
      }

      if (this.isReplace) {
         Column[] var11 = this.keys;
         int var13 = var11.length;

         for(var3 = 0; var3 < var13; ++var3) {
            Column var15 = var11[var3];
            boolean var5 = false;
            Column[] var6 = this.columns;
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Column var9 = var6[var8];
               if (var9.getColumnId() == var15.getColumnId()) {
                  var5 = true;
                  break;
               }
            }

            if (!var5) {
               return;
            }
         }
      }

      StringBuilder var12 = this.table.getSQL(new StringBuilder("UPDATE "), 0).append(" SET ");
      boolean var14 = false;
      var3 = 0;

      for(int var16 = this.columns.length; var3 < var16; ++var3) {
         Column var17 = this.columns[var3];
         if (!var17.isGeneratedAlways()) {
            if (var14) {
               var12.append(", ");
            }

            var14 = true;
            var17.getSQL(var12, 0).append("=?");
         }
      }

      if (!var14) {
         throw DbException.getSyntaxError(this.sqlStatement, this.sqlStatement.length(), "Valid MERGE INTO statement with at least one updatable column");
      } else {
         Column.writeColumns(var12.append(" WHERE "), this.keys, " AND ", "=?", 0);
         this.update = (Update)this.session.prepare(var12.toString());
      }
   }

   public int getType() {
      return this.isReplace ? 63 : 62;
   }

   public String getStatementName() {
      return this.isReplace ? "REPLACE" : "MERGE";
   }

   public void collectDependencies(HashSet<DbObject> var1) {
      if (this.query != null) {
         this.query.collectDependencies(var1);
      }

   }
}
