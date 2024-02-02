package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.schema.Schema;

public class AlterIndexRename extends DefineCommand {
   private boolean ifExists;
   private Schema oldSchema;
   private String oldIndexName;
   private String newIndexName;

   public AlterIndexRename(SessionLocal var1) {
      super(var1);
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public void setOldSchema(Schema var1) {
      this.oldSchema = var1;
   }

   public void setOldName(String var1) {
      this.oldIndexName = var1;
   }

   public void setNewName(String var1) {
      this.newIndexName = var1;
   }

   public long update() {
      Database var1 = this.session.getDatabase();
      Index var2 = this.oldSchema.findIndex(this.session, this.oldIndexName);
      if (var2 == null) {
         if (!this.ifExists) {
            throw DbException.get(42112, (String)this.newIndexName);
         } else {
            return 0L;
         }
      } else if (this.oldSchema.findIndex(this.session, this.newIndexName) == null && !this.newIndexName.equals(this.oldIndexName)) {
         this.session.getUser().checkTableRight(var2.getTable(), 32);
         var1.renameSchemaObject(this.session, var2, this.newIndexName);
         return 0L;
      } else {
         throw DbException.get(42111, (String)this.newIndexName);
      }
   }

   public int getType() {
      return 1;
   }
}
