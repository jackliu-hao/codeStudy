package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;

public class DropRole extends DefineCommand {
   private String roleName;
   private boolean ifExists;

   public DropRole(SessionLocal var1) {
      super(var1);
   }

   public void setRoleName(String var1) {
      this.roleName = var1;
   }

   public long update() {
      this.session.getUser().checkAdmin();
      Database var1 = this.session.getDatabase();
      Role var2 = var1.findRole(this.roleName);
      if (var2 == null) {
         if (!this.ifExists) {
            throw DbException.get(90070, this.roleName);
         }
      } else {
         if (var2 == var1.getPublicRole()) {
            throw DbException.get(90091, this.roleName);
         }

         var2.checkOwnsNoSchemas();
         var1.removeDatabaseObject(this.session, var2);
      }

      return 0L;
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public int getType() {
      return 41;
   }
}
