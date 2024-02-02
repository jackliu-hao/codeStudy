package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.UserAggregate;

public class CreateAggregate extends SchemaCommand {
   private String name;
   private String javaClassMethod;
   private boolean ifNotExists;
   private boolean force;

   public CreateAggregate(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public long update() {
      this.session.getUser().checkAdmin();
      Database var1 = this.session.getDatabase();
      Schema var2 = this.getSchema();
      if (var2.findFunctionOrAggregate(this.name) != null) {
         if (!this.ifNotExists) {
            throw DbException.get(90076, this.name);
         }
      } else {
         int var3 = this.getObjectId();
         UserAggregate var4 = new UserAggregate(var2, var3, this.name, this.javaClassMethod, this.force);
         var1.addSchemaObject(this.session, var4);
      }

      return 0L;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setJavaClassMethod(String var1) {
      this.javaClassMethod = var1;
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public void setForce(boolean var1) {
      this.force = var1;
   }

   public int getType() {
      return 22;
   }
}
