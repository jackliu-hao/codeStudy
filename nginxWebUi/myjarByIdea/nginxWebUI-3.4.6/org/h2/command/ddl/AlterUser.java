package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.expression.Expression;
import org.h2.message.DbException;

public class AlterUser extends DefineCommand {
   private int type;
   private User user;
   private String newName;
   private Expression password;
   private Expression salt;
   private Expression hash;
   private boolean admin;

   public AlterUser(SessionLocal var1) {
      super(var1);
   }

   public void setType(int var1) {
      this.type = var1;
   }

   public void setNewName(String var1) {
      this.newName = var1;
   }

   public void setUser(User var1) {
      this.user = var1;
   }

   public void setAdmin(boolean var1) {
      this.admin = var1;
   }

   public void setSalt(Expression var1) {
      this.salt = var1;
   }

   public void setHash(Expression var1) {
      this.hash = var1;
   }

   public void setPassword(Expression var1) {
      this.password = var1;
   }

   public long update() {
      Database var1 = this.session.getDatabase();
      switch (this.type) {
         case 17:
            this.session.getUser().checkAdmin();
            this.user.setAdmin(this.admin);
            break;
         case 18:
            this.session.getUser().checkAdmin();
            if (var1.findUser(this.newName) != null || this.newName.equals(this.user.getName())) {
               throw DbException.get(90033, this.newName);
            }

            var1.renameDatabaseObject(this.session, this.user, this.newName);
            break;
         case 19:
            if (this.user != this.session.getUser()) {
               this.session.getUser().checkAdmin();
            }

            if (this.hash != null && this.salt != null) {
               CreateUser.setSaltAndHash(this.user, this.session, this.salt, this.hash);
            } else {
               CreateUser.setPassword(this.user, this.session, this.password);
            }
            break;
         default:
            throw DbException.getInternalError("type=" + this.type);
      }

      var1.updateMeta(this.session, this.user);
      return 0L;
   }

   public int getType() {
      return this.type;
   }
}
