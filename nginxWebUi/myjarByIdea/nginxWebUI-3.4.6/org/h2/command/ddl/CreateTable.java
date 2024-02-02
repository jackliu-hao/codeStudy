package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.command.Command;
import org.h2.command.dml.Insert;
import org.h2.command.query.Query;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.table.Column;
import org.h2.table.Table;

public class CreateTable extends CommandWithColumns {
   private final CreateTableData data = new CreateTableData();
   private boolean ifNotExists;
   private boolean onCommitDrop;
   private boolean onCommitTruncate;
   private Query asQuery;
   private String comment;
   private boolean withNoData;

   public CreateTable(SessionLocal var1, Schema var2) {
      super(var1, var2);
      this.data.persistIndexes = true;
      this.data.persistData = true;
   }

   public void setQuery(Query var1) {
      this.asQuery = var1;
   }

   public void setTemporary(boolean var1) {
      this.data.temporary = var1;
   }

   public void setTableName(String var1) {
      this.data.tableName = var1;
   }

   public void addColumn(Column var1) {
      this.data.columns.add(var1);
   }

   public ArrayList<Column> getColumns() {
      return this.data.columns;
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public long update() {
      Schema var1 = this.getSchema();
      boolean var2 = this.data.temporary && !this.data.globalTemporary;
      if (!var2) {
         this.session.getUser().checkSchemaOwner(var1);
      }

      Database var3 = this.session.getDatabase();
      if (!var3.isPersistent()) {
         this.data.persistIndexes = false;
      }

      if (!var2) {
         var3.lockMeta(this.session);
      }

      if (var1.resolveTableOrView(this.session, this.data.tableName) != null) {
         if (this.ifNotExists) {
            return 0L;
         } else {
            throw DbException.get(42101, (String)this.data.tableName);
         }
      } else {
         if (this.asQuery != null) {
            this.asQuery.prepare();
            if (this.data.columns.isEmpty()) {
               this.generateColumnsFromQuery();
            } else {
               if (this.data.columns.size() != this.asQuery.getColumnCount()) {
                  throw DbException.get(21002);
               }

               ArrayList var4 = this.data.columns;

               for(int var5 = 0; var5 < var4.size(); ++var5) {
                  Column var6 = (Column)var4.get(var5);
                  if (var6.getType().getValueType() == -1) {
                     var4.set(var5, new Column(var6.getName(), ((Expression)this.asQuery.getExpressions().get(var5)).getType()));
                  }
               }
            }
         }

         this.changePrimaryKeysToNotNull(this.data.columns);
         this.data.id = this.getObjectId();
         this.data.session = this.session;
         Table var20 = var1.createTable(this.data);
         ArrayList var21 = this.generateSequences(this.data.columns, this.data.temporary);
         var20.setComment(this.comment);
         if (var2) {
            if (this.onCommitDrop) {
               var20.setOnCommitDrop(true);
            }

            if (this.onCommitTruncate) {
               var20.setOnCommitTruncate(true);
            }

            this.session.addLocalTempTable(var20);
         } else {
            var3.lockMeta(this.session);
            var3.addSchemaObject(this.session, var20);
         }

         try {
            Iterator var22 = this.data.columns.iterator();

            while(var22.hasNext()) {
               Column var7 = (Column)var22.next();
               var7.prepareExpressions(this.session);
            }

            var22 = var21.iterator();

            while(var22.hasNext()) {
               Sequence var24 = (Sequence)var22.next();
               var20.addSequence(var24);
            }

            this.createConstraints();
            HashSet var23 = new HashSet();
            var20.addDependencies(var23);
            Iterator var25 = var23.iterator();

            while(var25.hasNext()) {
               DbObject var8 = (DbObject)var25.next();
               if (var8 != var20 && var8.getType() == 0 && var8 instanceof Table) {
                  Table var9 = (Table)var8;
                  if (var9.getId() > var20.getId()) {
                     throw DbException.get(50100, (String)("Table depends on another table with a higher ID: " + var9 + ", this is currently not supported, as it would prevent the database from being re-opened"));
                  }
               }
            }

            if (this.asQuery != null && !this.withNoData) {
               boolean var26 = false;
               int var10;
               Column var11;
               Sequence var12;
               Column[] var27;
               int var29;
               if (!var2) {
                  var3.unlockMeta(this.session);
                  var27 = var20.getColumns();
                  var29 = var27.length;

                  for(var10 = 0; var10 < var29; ++var10) {
                     var11 = var27[var10];
                     var12 = var11.getSequence();
                     if (var12 != null) {
                        var26 = true;
                        var12.setTemporary(true);
                     }
                  }
               }

               try {
                  this.session.startStatementWithinTransaction((Command)null);
                  Insert var28 = new Insert(this.session);
                  var28.setQuery(this.asQuery);
                  var28.setTable(var20);
                  var28.setInsertFromSelect(true);
                  var28.prepare();
                  var28.update();
               } finally {
                  this.session.endStatement();
               }

               if (var26) {
                  var3.lockMeta(this.session);
                  var27 = var20.getColumns();
                  var29 = var27.length;

                  for(var10 = 0; var10 < var29; ++var10) {
                     var11 = var27[var10];
                     var12 = var11.getSequence();
                     if (var12 != null) {
                        var12.setTemporary(false);
                        var12.flush(this.session);
                     }
                  }
               }
            }

            return 0L;
         } catch (DbException var19) {
            try {
               var3.checkPowerOff();
               var3.removeSchemaObject(this.session, var20);
               if (!this.transactional) {
                  this.session.commit(true);
               }
            } catch (Throwable var17) {
               var19.addSuppressed(var17);
            }

            throw var19;
         }
      }
   }

   private void generateColumnsFromQuery() {
      int var1 = this.asQuery.getColumnCount();
      ArrayList var2 = this.asQuery.getExpressions();

      for(int var3 = 0; var3 < var1; ++var3) {
         Expression var4 = (Expression)var2.get(var3);
         this.addColumn(new Column(var4.getColumnNameForView(this.session, var3), var4.getType()));
      }

   }

   public void setPersistIndexes(boolean var1) {
      this.data.persistIndexes = var1;
   }

   public void setGlobalTemporary(boolean var1) {
      this.data.globalTemporary = var1;
   }

   public void setOnCommitDrop() {
      this.onCommitDrop = true;
   }

   public void setOnCommitTruncate() {
      this.onCommitTruncate = true;
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   public void setPersistData(boolean var1) {
      this.data.persistData = var1;
      if (!var1) {
         this.data.persistIndexes = false;
      }

   }

   public void setWithNoData(boolean var1) {
      this.withNoData = var1;
   }

   public void setTableEngine(String var1) {
      this.data.tableEngine = var1;
   }

   public void setTableEngineParams(ArrayList<String> var1) {
      this.data.tableEngineParams = var1;
   }

   public void setHidden(boolean var1) {
      this.data.isHidden = var1;
   }

   public int getType() {
      return 30;
   }
}
