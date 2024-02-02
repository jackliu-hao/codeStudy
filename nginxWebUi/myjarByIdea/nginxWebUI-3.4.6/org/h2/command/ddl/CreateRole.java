package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.RightOwner;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;

public class CreateRole extends DefineCommand {
   private String roleName;
   private boolean ifNotExists;

   public CreateRole(SessionLocal var1) {
      super(var1);
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public void setRoleName(String var1) {
      this.roleName = var1;
   }

   public long update() {
      this.session.getUser().checkAdmin();
      Database var1 = this.session.getDatabase();
      RightOwner var2 = var1.findUserOrRole(this.roleName);
      if (var2 != null) {
         if (var2 instanceof Role) {
            if (this.ifNotExists) {
               return 0L;
            } else {
               throw DbException.get(90069, this.roleName);
            }
         } else {
            throw DbException.get(90033, this.roleName);
         }
      } else {
         int var3 = this.getObjectId();
         Role var4 = new Role(var1, var3, this.roleName, false);
         var1.addDatabaseObject(this.session, var4);
         return 0L;
      }
   }

   public int getType() {
      return 27;
   }
}
