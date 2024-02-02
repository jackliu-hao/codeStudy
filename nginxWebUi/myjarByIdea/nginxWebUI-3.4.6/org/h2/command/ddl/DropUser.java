package org.h2.command.ddl;

import java.util.Iterator;
import org.h2.engine.Database;
import org.h2.engine.RightOwner;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.message.DbException;

public class DropUser extends DefineCommand {
   private boolean ifExists;
   private String userName;

   public DropUser(SessionLocal var1) {
      super(var1);
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public void setUserName(String var1) {
      this.userName = var1;
   }

   public long update() {
      this.session.getUser().checkAdmin();
      Database var1 = this.session.getDatabase();
      User var2 = var1.findUser(this.userName);
      if (var2 == null) {
         if (!this.ifExists) {
            throw DbException.get(90032, this.userName);
         }
      } else {
         if (var2 == this.session.getUser()) {
            int var3 = 0;
            Iterator var4 = var1.getAllUsersAndRoles().iterator();

            while(var4.hasNext()) {
               RightOwner var5 = (RightOwner)var4.next();
               if (var5 instanceof User && ((User)var5).isAdmin()) {
                  ++var3;
               }
            }

            if (var3 == 1) {
               throw DbException.get(90019);
            }
         }

         var2.checkOwnsNoSchemas();
         var1.removeDatabaseObject(this.session, var2);
      }

      return 0L;
   }

   public boolean isTransactional() {
      return false;
   }

   public int getType() {
      return 46;
   }
}
