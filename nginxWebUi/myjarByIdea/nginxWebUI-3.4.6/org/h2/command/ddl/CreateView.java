package org.h2.command.ddl;

import java.util.ArrayList;
import org.h2.command.query.Query;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableType;
import org.h2.table.TableView;
import org.h2.value.TypeInfo;

public class CreateView extends SchemaOwnerCommand {
   private Query select;
   private String viewName;
   private boolean ifNotExists;
   private String selectSQL;
   private String[] columnNames;
   private String comment;
   private boolean orReplace;
   private boolean force;
   private boolean isTableExpression;

   public CreateView(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setViewName(String var1) {
      this.viewName = var1;
   }

   public void setSelect(Query var1) {
      this.select = var1;
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public void setSelectSQL(String var1) {
      this.selectSQL = var1;
   }

   public void setColumnNames(String[] var1) {
      this.columnNames = var1;
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   public void setOrReplace(boolean var1) {
      this.orReplace = var1;
   }

   public void setForce(boolean var1) {
      this.force = var1;
   }

   public void setTableExpression(boolean var1) {
      this.isTableExpression = var1;
   }

   long update(Schema var1) {
      Database var2 = this.session.getDatabase();
      TableView var3 = null;
      Table var4 = var1.findTableOrView(this.session, this.viewName);
      if (var4 != null) {
         if (this.ifNotExists) {
            return 0L;
         }

         if (!this.orReplace || TableType.VIEW != var4.getTableType()) {
            throw DbException.get(90038, this.viewName);
         }

         var3 = (TableView)var4;
      }

      int var5 = this.getObjectId();
      String var6;
      if (this.select == null) {
         var6 = this.selectSQL;
      } else {
         ArrayList var7 = this.select.getParameters();
         if (var7 != null && !var7.isEmpty()) {
            throw DbException.getUnsupportedException("parameters in views");
         }

         var6 = this.select.getPlanSQL(0);
      }

      Column[] var10 = null;
      Column[] var8 = null;
      if (this.columnNames != null) {
         var10 = new Column[this.columnNames.length];
         var8 = new Column[this.columnNames.length];

         for(int var9 = 0; var9 < this.columnNames.length; ++var9) {
            var10[var9] = new Column(this.columnNames[var9], TypeInfo.TYPE_UNKNOWN);
            var8[var9] = new Column(this.columnNames[var9], TypeInfo.TYPE_VARCHAR);
         }
      }

      if (var3 == null) {
         if (this.isTableExpression) {
            var3 = TableView.createTableViewMaybeRecursive(var1, var5, this.viewName, var6, (ArrayList)null, var8, this.session, false, this.isTableExpression, false, var2);
         } else {
            var3 = new TableView(var1, var5, this.viewName, var6, (ArrayList)null, var10, this.session, false, false, this.isTableExpression, false);
         }
      } else {
         var3.replace(var6, var10, this.session, false, this.force, false);
         var3.setModified();
      }

      if (this.comment != null) {
         var3.setComment(this.comment);
      }

      if (var4 == null) {
         var2.addSchemaObject(this.session, var3);
         var2.unlockMeta(this.session);
      } else {
         var2.updateMeta(this.session, var3);
      }

      return 0L;
   }

   public int getType() {
      return 34;
   }
}
