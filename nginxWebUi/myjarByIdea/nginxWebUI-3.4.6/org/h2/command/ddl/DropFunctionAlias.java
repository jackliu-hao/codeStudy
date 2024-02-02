package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.FunctionAlias;
import org.h2.schema.Schema;

public class DropFunctionAlias extends SchemaOwnerCommand {
   private String aliasName;
   private boolean ifExists;

   public DropFunctionAlias(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   long update(Schema var1) {
      Database var2 = this.session.getDatabase();
      FunctionAlias var3 = var1.findFunction(this.aliasName);
      if (var3 == null) {
         if (!this.ifExists) {
            throw DbException.get(90077, this.aliasName);
         }
      } else {
         var2.removeSchemaObject(this.session, var3);
      }

      return 0L;
   }

   public void setAliasName(String var1) {
      this.aliasName = var1;
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public int getType() {
      return 39;
   }
}
