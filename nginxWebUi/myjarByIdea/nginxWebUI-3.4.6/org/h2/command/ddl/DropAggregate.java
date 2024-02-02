package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.UserAggregate;

public class DropAggregate extends SchemaOwnerCommand {
   private String name;
   private boolean ifExists;

   public DropAggregate(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   long update(Schema var1) {
      Database var2 = this.session.getDatabase();
      UserAggregate var3 = var1.findAggregate(this.name);
      if (var3 == null) {
         if (!this.ifExists) {
            throw DbException.get(90132, this.name);
         }
      } else {
         var2.removeSchemaObject(this.session, var3);
      }

      return 0L;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public int getType() {
      return 36;
   }
}
