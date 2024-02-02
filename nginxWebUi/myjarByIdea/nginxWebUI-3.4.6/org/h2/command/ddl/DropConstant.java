package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Constant;
import org.h2.schema.Schema;

public class DropConstant extends SchemaOwnerCommand {
   private String constantName;
   private boolean ifExists;

   public DropConstant(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public void setConstantName(String var1) {
      this.constantName = var1;
   }

   long update(Schema var1) {
      Database var2 = this.session.getDatabase();
      Constant var3 = var1.findConstant(this.constantName);
      if (var3 == null) {
         if (!this.ifExists) {
            throw DbException.get(90115, this.constantName);
         }
      } else {
         var2.removeSchemaObject(this.session, var3);
      }

      return 0L;
   }

   public int getType() {
      return 37;
   }
}
