package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.TableLink;

public class CreateLinkedTable extends SchemaCommand {
   private String tableName;
   private String driver;
   private String url;
   private String user;
   private String password;
   private String originalSchema;
   private String originalTable;
   private boolean ifNotExists;
   private String comment;
   private boolean emitUpdates;
   private boolean force;
   private boolean temporary;
   private boolean globalTemporary;
   private boolean readOnly;
   private int fetchSize;
   private boolean autocommit = true;

   public CreateLinkedTable(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setTableName(String var1) {
      this.tableName = var1;
   }

   public void setDriver(String var1) {
      this.driver = var1;
   }

   public void setOriginalTable(String var1) {
      this.originalTable = var1;
   }

   public void setPassword(String var1) {
      this.password = var1;
   }

   public void setUrl(String var1) {
      this.url = var1;
   }

   public void setUser(String var1) {
      this.user = var1;
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public void setFetchSize(int var1) {
      this.fetchSize = var1;
   }

   public void setAutoCommit(boolean var1) {
      this.autocommit = var1;
   }

   public long update() {
      this.session.getUser().checkAdmin();
      Database var1 = this.session.getDatabase();
      if (this.getSchema().resolveTableOrView(this.session, this.tableName) != null) {
         if (this.ifNotExists) {
            return 0L;
         } else {
            throw DbException.get(42101, (String)this.tableName);
         }
      } else {
         int var2 = this.getObjectId();
         TableLink var3 = this.getSchema().createTableLink(var2, this.tableName, this.driver, this.url, this.user, this.password, this.originalSchema, this.originalTable, this.emitUpdates, this.force);
         var3.setTemporary(this.temporary);
         var3.setGlobalTemporary(this.globalTemporary);
         var3.setComment(this.comment);
         var3.setReadOnly(this.readOnly);
         if (this.fetchSize > 0) {
            var3.setFetchSize(this.fetchSize);
         }

         var3.setAutoCommit(this.autocommit);
         if (this.temporary && !this.globalTemporary) {
            this.session.addLocalTempTable(var3);
         } else {
            var1.addSchemaObject(this.session, var3);
         }

         return 0L;
      }
   }

   public void setEmitUpdates(boolean var1) {
      this.emitUpdates = var1;
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   public void setForce(boolean var1) {
      this.force = var1;
   }

   public void setTemporary(boolean var1) {
      this.temporary = var1;
   }

   public void setGlobalTemporary(boolean var1) {
      this.globalTemporary = var1;
   }

   public void setReadOnly(boolean var1) {
      this.readOnly = var1;
   }

   public void setOriginalSchema(String var1) {
      this.originalSchema = var1;
   }

   public int getType() {
      return 26;
   }
}
