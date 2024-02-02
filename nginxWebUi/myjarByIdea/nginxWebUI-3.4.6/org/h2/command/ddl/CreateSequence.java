package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;

public class CreateSequence extends SchemaOwnerCommand {
   private String sequenceName;
   private boolean ifNotExists;
   private SequenceOptions options;
   private boolean belongsToTable;

   public CreateSequence(SessionLocal var1, Schema var2) {
      super(var1, var2);
      this.transactional = true;
   }

   public void setSequenceName(String var1) {
      this.sequenceName = var1;
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public void setOptions(SequenceOptions var1) {
      this.options = var1;
   }

   long update(Schema var1) {
      Database var2 = this.session.getDatabase();
      if (var1.findSequence(this.sequenceName) != null) {
         if (this.ifNotExists) {
            return 0L;
         } else {
            throw DbException.get(90035, this.sequenceName);
         }
      } else {
         int var3 = this.getObjectId();
         Sequence var4 = new Sequence(this.session, var1, var3, this.sequenceName, this.options, this.belongsToTable);
         var2.addSchemaObject(this.session, var4);
         return 0L;
      }
   }

   public void setBelongsToTable(boolean var1) {
      this.belongsToTable = var1;
   }

   public int getType() {
      return 29;
   }
}
