package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;

public class DropSequence extends SchemaOwnerCommand {
   private String sequenceName;
   private boolean ifExists;

   public DropSequence(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public void setSequenceName(String var1) {
      this.sequenceName = var1;
   }

   long update(Schema var1) {
      Sequence var2 = var1.findSequence(this.sequenceName);
      if (var2 == null) {
         if (!this.ifExists) {
            throw DbException.get(90036, this.sequenceName);
         }
      } else {
         if (var2.getBelongsToTable()) {
            throw DbException.get(90082, this.sequenceName);
         }

         this.session.getDatabase().removeSchemaObject(this.session, var2);
      }

      return 0L;
   }

   public int getType() {
      return 43;
   }
}
