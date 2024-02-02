package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.TableSynonym;

public class DropSynonym extends SchemaOwnerCommand {
   private String synonymName;
   private boolean ifExists;

   public DropSynonym(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setSynonymName(String var1) {
      this.synonymName = var1;
   }

   long update(Schema var1) {
      TableSynonym var2 = var1.getSynonym(this.synonymName);
      if (var2 == null) {
         if (!this.ifExists) {
            throw DbException.get(42102, (String)this.synonymName);
         }
      } else {
         this.session.getDatabase().removeSchemaObject(this.session, var2);
      }

      return 0L;
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public int getType() {
      return 89;
   }
}
