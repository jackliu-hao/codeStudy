package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.IndexColumn;
import org.h2.table.Table;

public class CreateIndex extends SchemaCommand {
   private String tableName;
   private String indexName;
   private IndexColumn[] indexColumns;
   private int uniqueColumnCount;
   private boolean primaryKey;
   private boolean hash;
   private boolean spatial;
   private boolean ifTableExists;
   private boolean ifNotExists;
   private String comment;

   public CreateIndex(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setIfTableExists(boolean var1) {
      this.ifTableExists = var1;
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public void setTableName(String var1) {
      this.tableName = var1;
   }

   public void setIndexName(String var1) {
      this.indexName = var1;
   }

   public void setIndexColumns(IndexColumn[] var1) {
      this.indexColumns = var1;
   }

   public long update() {
      Database var1 = this.session.getDatabase();
      boolean var2 = var1.isPersistent();
      Table var3 = this.getSchema().findTableOrView(this.session, this.tableName);
      if (var3 == null) {
         if (this.ifTableExists) {
            return 0L;
         } else {
            throw DbException.get(42102, (String)this.tableName);
         }
      } else if (this.indexName != null && this.getSchema().findIndex(this.session, this.indexName) != null) {
         if (this.ifNotExists) {
            return 0L;
         } else {
            throw DbException.get(42111, (String)this.indexName);
         }
      } else {
         this.session.getUser().checkTableRight(var3, 32);
         var3.lock(this.session, 2);
         if (!var3.isPersistIndexes()) {
            var2 = false;
         }

         int var4 = this.getObjectId();
         if (this.indexName == null) {
            if (this.primaryKey) {
               this.indexName = var3.getSchema().getUniqueIndexName(this.session, var3, "PRIMARY_KEY_");
            } else {
               this.indexName = var3.getSchema().getUniqueIndexName(this.session, var3, "INDEX_");
            }
         }

         IndexType var5;
         if (this.primaryKey) {
            if (var3.findPrimaryKey() != null) {
               throw DbException.get(90017);
            }

            var5 = IndexType.createPrimaryKey(var2, this.hash);
         } else if (this.uniqueColumnCount > 0) {
            var5 = IndexType.createUnique(var2, this.hash);
         } else {
            var5 = IndexType.createNonUnique(var2, this.hash, this.spatial);
         }

         IndexColumn.mapColumns(this.indexColumns, var3);
         var3.addIndex(this.session, this.indexName, var4, this.indexColumns, this.uniqueColumnCount, var5, this.create, this.comment);
         return 0L;
      }
   }

   public void setPrimaryKey(boolean var1) {
      this.primaryKey = var1;
   }

   public void setUniqueColumnCount(int var1) {
      this.uniqueColumnCount = var1;
   }

   public void setHash(boolean var1) {
      this.hash = var1;
   }

   public void setSpatial(boolean var1) {
      this.spatial = var1;
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   public int getType() {
      return 25;
   }
}
