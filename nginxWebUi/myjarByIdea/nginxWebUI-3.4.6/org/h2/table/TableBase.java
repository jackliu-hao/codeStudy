package org.h2.table;

import java.util.Collections;
import java.util.List;
import org.h2.command.ddl.CreateTableData;
import org.h2.engine.Database;
import org.h2.index.IndexType;
import org.h2.util.StringUtils;

public abstract class TableBase extends Table {
   private final String tableEngine;
   private final List<String> tableEngineParams;
   private final boolean globalTemporary;

   public static int getMainIndexColumn(IndexType var0, IndexColumn[] var1) {
      if (var0.isPrimaryKey() && var1.length == 1) {
         IndexColumn var2 = var1[0];
         if ((var2.sortType & 1) != 0) {
            return -1;
         } else {
            switch (var2.column.getType().getValueType()) {
               case 9:
               case 10:
               case 11:
               case 12:
                  return var2.column.getColumnId();
               default:
                  return -1;
            }
         }
      } else {
         return -1;
      }
   }

   public TableBase(CreateTableData var1) {
      super(var1.schema, var1.id, var1.tableName, var1.persistIndexes, var1.persistData);
      this.tableEngine = var1.tableEngine;
      this.globalTemporary = var1.globalTemporary;
      if (var1.tableEngineParams != null) {
         this.tableEngineParams = var1.tableEngineParams;
      } else {
         this.tableEngineParams = Collections.emptyList();
      }

      this.setTemporary(var1.temporary);
      this.setColumns((Column[])var1.columns.toArray(new Column[0]));
   }

   public String getDropSQL() {
      StringBuilder var1 = new StringBuilder("DROP TABLE IF EXISTS ");
      this.getSQL(var1, 0).append(" CASCADE");
      return var1.toString();
   }

   public String getCreateSQLForMeta() {
      return this.getCreateSQL(true);
   }

   public String getCreateSQL() {
      return this.getCreateSQL(false);
   }

   private String getCreateSQL(boolean var1) {
      Database var2 = this.getDatabase();
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var3 = new StringBuilder("CREATE ");
         if (this.isTemporary()) {
            if (this.isGlobalTemporary()) {
               var3.append("GLOBAL ");
            } else {
               var3.append("LOCAL ");
            }

            var3.append("TEMPORARY ");
         } else if (this.isPersistIndexes()) {
            var3.append("CACHED ");
         } else {
            var3.append("MEMORY ");
         }

         var3.append("TABLE ");
         if (this.isHidden) {
            var3.append("IF NOT EXISTS ");
         }

         this.getSQL(var3, 0);
         if (this.comment != null) {
            var3.append(" COMMENT ");
            StringUtils.quoteStringSQL(var3, this.comment);
         }

         var3.append("(\n    ");
         int var4 = 0;

         int var5;
         for(var5 = this.columns.length; var4 < var5; ++var4) {
            if (var4 > 0) {
               var3.append(",\n    ");
            }

            var3.append(this.columns[var4].getCreateSQL(var1));
         }

         var3.append("\n)");
         if (this.tableEngine != null) {
            String var6 = var2.getSettings().defaultTableEngine;
            if (var6 == null || !this.tableEngine.endsWith(var6)) {
               var3.append("\nENGINE ");
               StringUtils.quoteIdentifier(var3, this.tableEngine);
            }
         }

         if (!this.tableEngineParams.isEmpty()) {
            var3.append("\nWITH ");
            var4 = 0;

            for(var5 = this.tableEngineParams.size(); var4 < var5; ++var4) {
               if (var4 > 0) {
                  var3.append(", ");
               }

               StringUtils.quoteIdentifier(var3, (String)this.tableEngineParams.get(var4));
            }
         }

         if (!this.isPersistIndexes() && !this.isPersistData()) {
            var3.append("\nNOT PERSISTENT");
         }

         if (this.isHidden) {
            var3.append("\nHIDDEN");
         }

         return var3.toString();
      }
   }

   public boolean isGlobalTemporary() {
      return this.globalTemporary;
   }
}
