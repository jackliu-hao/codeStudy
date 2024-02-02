package org.h2.command.ddl;

import java.util.ArrayList;
import org.h2.engine.Database;
import org.h2.engine.RightOwner;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;

public class CreateSchema extends DefineCommand {
   private String schemaName;
   private String authorization;
   private boolean ifNotExists;
   private ArrayList<String> tableEngineParams;

   public CreateSchema(SessionLocal var1) {
      super(var1);
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public long update() {
      this.session.getUser().checkSchemaAdmin();
      Database var1 = this.session.getDatabase();
      RightOwner var2 = var1.findUserOrRole(this.authorization);
      if (var2 == null) {
         throw DbException.get(90071, this.authorization);
      } else if (var1.findSchema(this.schemaName) != null) {
         if (this.ifNotExists) {
            return 0L;
         } else {
            throw DbException.get(90078, this.schemaName);
         }
      } else {
         int var3 = this.getObjectId();
         Schema var4 = new Schema(var1, var3, this.schemaName, var2, false);
         var4.setTableEngineParams(this.tableEngineParams);
         var1.addDatabaseObject(this.session, var4);
         return 0L;
      }
   }

   public void setSchemaName(String var1) {
      this.schemaName = var1;
   }

   public void setAuthorization(String var1) {
      this.authorization = var1;
   }

   public void setTableEngineParams(ArrayList<String> var1) {
      this.tableEngineParams = var1;
   }

   public int getType() {
      return 28;
   }
}
