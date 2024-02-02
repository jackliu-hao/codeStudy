package org.h2.command.dml;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.h2.command.Prepared;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.mvstore.db.Store;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.table.Column;
import org.h2.value.TypeInfo;
import org.h2.value.ValueVarchar;

public class Explain extends Prepared {
   private Prepared command;
   private LocalResult result;
   private boolean executeCommand;

   public Explain(SessionLocal var1) {
      super(var1);
   }

   public void setCommand(Prepared var1) {
      this.command = var1;
   }

   public Prepared getCommand() {
      return this.command;
   }

   public void prepare() {
      this.command.prepare();
   }

   public void setExecuteCommand(boolean var1) {
      this.executeCommand = var1;
   }

   public ResultInterface queryMeta() {
      return this.query(-1L);
   }

   protected void checkParameters() {
      if (this.executeCommand) {
         super.checkParameters();
      }

   }

   public ResultInterface query(long var1) {
      Database var3 = this.session.getDatabase();
      Expression[] var4 = new Expression[]{new ExpressionColumn(var3, new Column("PLAN", TypeInfo.TYPE_VARCHAR))};
      this.result = new LocalResult(this.session, var4, 1, 1);
      byte var5 = 8;
      if (var1 >= 0L) {
         String var6;
         if (!this.executeCommand) {
            var6 = this.command.getPlanSQL(var5);
         } else {
            Store var7 = null;
            if (var3.isPersistent()) {
               var7 = var3.getStore();
               var7.statisticsStart();
            }

            if (this.command.isQuery()) {
               this.command.query(var1);
            } else {
               this.command.update();
            }

            var6 = this.command.getPlanSQL(var5);
            Map var8 = null;
            if (var7 != null) {
               var8 = var7.statisticsEnd();
            }

            if (var8 != null) {
               int var9 = 0;

               Map.Entry var11;
               for(Iterator var10 = var8.entrySet().iterator(); var10.hasNext(); var9 += (Integer)var11.getValue()) {
                  var11 = (Map.Entry)var10.next();
               }

               if (var9 > 0) {
                  TreeMap var15 = new TreeMap(var8);
                  StringBuilder var16 = new StringBuilder();
                  if (var15.size() > 1) {
                     var16.append("total: ").append(var9).append('\n');
                  }

                  for(Iterator var17 = var15.entrySet().iterator(); var17.hasNext(); var16.append('\n')) {
                     Map.Entry var12 = (Map.Entry)var17.next();
                     int var13 = (Integer)var12.getValue();
                     int var14 = (int)(100L * (long)var13 / (long)var9);
                     var16.append((String)var12.getKey()).append(": ").append(var13);
                     if (var15.size() > 1) {
                        var16.append(" (").append(var14).append("%)");
                     }
                  }

                  var6 = var6 + "\n/*\n" + var16.toString() + "*/";
               }
            }
         }

         this.add(var6);
      }

      this.result.done();
      return this.result;
   }

   private void add(String var1) {
      this.result.addRow(ValueVarchar.get(var1));
   }

   public boolean isQuery() {
      return true;
   }

   public boolean isTransactional() {
      return true;
   }

   public boolean isReadOnly() {
      return this.command.isReadOnly();
   }

   public int getType() {
      return this.executeCommand ? 86 : 60;
   }

   public void collectDependencies(HashSet<DbObject> var1) {
      this.command.collectDependencies(var1);
   }
}
