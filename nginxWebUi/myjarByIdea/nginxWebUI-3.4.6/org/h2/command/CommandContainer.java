package org.h2.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.h2.command.ddl.DefineCommand;
import org.h2.command.dml.DataChangeStatement;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.DbSettings;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.Parameter;
import org.h2.expression.ParameterInterface;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.result.ResultWithGeneratedKeys;
import org.h2.table.Column;
import org.h2.table.DataChangeDeltaTable;
import org.h2.table.Table;
import org.h2.table.TableView;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.Value;

public class CommandContainer extends Command {
   private Prepared prepared;
   private boolean readOnlyKnown;
   private boolean readOnly;

   static void clearCTE(SessionLocal var0, Prepared var1) {
      List var2 = var1.getCteCleanups();
      if (var2 != null) {
         clearCTE(var0, var2);
      }

   }

   static void clearCTE(SessionLocal var0, List<TableView> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         TableView var3 = (TableView)var2.next();
         if (var3.getName() != null) {
            var0.removeLocalTempTable(var3);
         }
      }

   }

   public CommandContainer(SessionLocal var1, String var2, Prepared var3) {
      super(var1, var2);
      var3.setCommand(this);
      this.prepared = var3;
   }

   public ArrayList<? extends ParameterInterface> getParameters() {
      return this.prepared.getParameters();
   }

   public boolean isTransactional() {
      return this.prepared.isTransactional();
   }

   public boolean isQuery() {
      return this.prepared.isQuery();
   }

   private void recompileIfRequired() {
      if (this.prepared.needRecompile()) {
         this.prepared.setModificationMetaId(0L);
         String var1 = this.prepared.getSQL();
         ArrayList var2 = this.prepared.getSQLTokens();
         ArrayList var3 = this.prepared.getParameters();
         Parser var4 = new Parser(this.session);
         this.prepared = var4.parse(var1, var2);
         long var5 = this.prepared.getModificationMetaId();
         this.prepared.setModificationMetaId(0L);
         ArrayList var7 = this.prepared.getParameters();
         int var8 = 0;

         for(int var9 = Math.min(var7.size(), var3.size()); var8 < var9; ++var8) {
            Parameter var10 = (Parameter)var3.get(var8);
            if (var10.isValueSet()) {
               Value var11 = var10.getValue(this.session);
               Parameter var12 = (Parameter)var7.get(var8);
               var12.setValue(var11);
            }
         }

         this.prepared.prepare();
         this.prepared.setModificationMetaId(var5);
      }

   }

   public ResultWithGeneratedKeys update(Object var1) {
      this.recompileIfRequired();
      this.setProgress(5);
      this.start();
      this.prepared.checkParameters();
      Object var2;
      if (var1 != null && !Boolean.FALSE.equals(var1)) {
         if (this.prepared instanceof DataChangeStatement && this.prepared.getType() != 58) {
            var2 = this.executeUpdateWithGeneratedKeys((DataChangeStatement)this.prepared, var1);
         } else {
            var2 = new ResultWithGeneratedKeys.WithKeys(this.prepared.update(), new LocalResult());
         }
      } else {
         var2 = ResultWithGeneratedKeys.of(this.prepared.update());
      }

      this.prepared.trace(this.startTimeNanos, ((ResultWithGeneratedKeys)var2).getUpdateCount());
      this.setProgress(6);
      return (ResultWithGeneratedKeys)var2;
   }

   private ResultWithGeneratedKeys executeUpdateWithGeneratedKeys(DataChangeStatement var1, Object var2) {
      Database var3 = this.session.getDatabase();
      Table var4 = var1.getTable();
      ArrayList var5;
      int var9;
      Column var11;
      int var26;
      if (Boolean.TRUE.equals(var2)) {
         var5 = Utils.newSmallArrayList();
         Column[] var18 = var4.getColumns();
         Index var21 = var4.findPrimaryKey();
         Column[] var23 = var18;
         var9 = var18.length;

         for(var26 = 0; var26 < var9; ++var26) {
            var11 = var23[var26];
            Expression var29;
            if (var11.isIdentity() || (var29 = var11.getEffectiveDefaultExpression()) != null && !var29.isConstant() || var21 != null && var21.getColumnIndex(var11) >= 0) {
               var5.add(new ExpressionColumn(var3, var11));
            }
         }
      } else {
         int var8;
         if (var2 instanceof int[]) {
            int[] var17 = (int[])((int[])var2);
            Column[] var20 = var4.getColumns();
            var8 = var20.length;
            var5 = new ArrayList(var17.length);
            int[] var25 = var17;
            var26 = var17.length;

            for(int var30 = 0; var30 < var26; ++var30) {
               int var28 = var25[var30];
               if (var28 < 1 || var28 > var8) {
                  throw DbException.get(42122, (String)("Index: " + var28));
               }

               var5.add(new ExpressionColumn(var3, var20[var28 - 1]));
            }
         } else {
            if (!(var2 instanceof String[])) {
               throw DbException.getInternalError();
            }

            String[] var6 = (String[])((String[])var2);
            var5 = new ArrayList(var6.length);
            String[] var7 = var6;
            var8 = var6.length;

            for(var9 = 0; var9 < var8; ++var9) {
               String var10 = var7[var9];
               var11 = var4.findColumn(var10);
               if (var11 == null) {
                  DbSettings var12 = var3.getSettings();
                  if (var12.databaseToUpper) {
                     var11 = var4.findColumn(StringUtils.toUpperEnglish(var10));
                  } else if (var12.databaseToLower) {
                     var11 = var4.findColumn(StringUtils.toLowerEnglish(var10));
                  }

                  if (var11 == null) {
                     Column[] var13 = var4.getColumns();
                     int var14 = var13.length;
                     int var15 = 0;

                     while(true) {
                        if (var15 >= var14) {
                           throw DbException.get(42122, (String)var10);
                        }

                        Column var16 = var13[var15];
                        if (var16.getName().equalsIgnoreCase(var10)) {
                           var11 = var16;
                           break;
                        }

                        ++var15;
                     }
                  }
               }

               var5.add(new ExpressionColumn(var3, var11));
            }
         }
      }

      int var19 = var5.size();
      if (var19 == 0) {
         return new ResultWithGeneratedKeys.WithKeys(var1.update(), new LocalResult());
      } else {
         int[] var22 = new int[var19];
         ExpressionColumn[] var24 = (ExpressionColumn[])var5.toArray(new ExpressionColumn[0]);

         for(var9 = 0; var9 < var19; ++var9) {
            var22[var9] = var24[var9].getColumn().getColumnId();
         }

         LocalResult var27 = new LocalResult(this.session, var24, var19, var19);
         return new ResultWithGeneratedKeys.WithKeys(var1.update(new GeneratedKeysCollector(var22, var27), DataChangeDeltaTable.ResultOption.FINAL), var27);
      }
   }

   public ResultInterface query(long var1) {
      this.recompileIfRequired();
      this.setProgress(5);
      this.start();
      this.prepared.checkParameters();
      ResultInterface var3 = this.prepared.query(var1);
      this.prepared.trace(this.startTimeNanos, var3.isLazy() ? 0L : var3.getRowCount());
      this.setProgress(6);
      return var3;
   }

   public void stop() {
      super.stop();
      clearCTE(this.session, this.prepared);
   }

   public boolean canReuse() {
      return super.canReuse() && this.prepared.getCteCleanups() == null;
   }

   public boolean isReadOnly() {
      if (!this.readOnlyKnown) {
         this.readOnly = this.prepared.isReadOnly();
         this.readOnlyKnown = true;
      }

      return this.readOnly;
   }

   public ResultInterface queryMeta() {
      return this.prepared.queryMeta();
   }

   public boolean isCacheable() {
      return this.prepared.isCacheable();
   }

   public int getCommandType() {
      return this.prepared.getType();
   }

   void clearCTE() {
      clearCTE(this.session, this.prepared);
   }

   public Set<DbObject> getDependencies() {
      HashSet var1 = new HashSet();
      this.prepared.collectDependencies(var1);
      return var1;
   }

   protected boolean isCurrentCommandADefineCommand() {
      return this.prepared instanceof DefineCommand;
   }

   private static final class GeneratedKeysCollector implements ResultTarget {
      private final int[] indexes;
      private final LocalResult result;

      GeneratedKeysCollector(int[] var1, LocalResult var2) {
         this.indexes = var1;
         this.result = var2;
      }

      public void limitsWereApplied() {
      }

      public long getRowCount() {
         return 0L;
      }

      public void addRow(Value... var1) {
         int var2 = this.indexes.length;
         Value[] var3 = new Value[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = var1[this.indexes[var4]];
         }

         this.result.addRow(var3);
      }
   }
}
