package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.RightOwner;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.security.SHA256;
import org.h2.util.StringUtils;
import org.h2.value.DataType;
import org.h2.value.Value;

public class CreateUser extends DefineCommand {
   private String userName;
   private boolean admin;
   private Expression password;
   private Expression salt;
   private Expression hash;
   private boolean ifNotExists;
   private String comment;

   public CreateUser(SessionLocal var1) {
      super(var1);
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public void setUserName(String var1) {
      this.userName = var1;
   }

   public void setPassword(Expression var1) {
      this.password = var1;
   }

   static void setSaltAndHash(User var0, SessionLocal var1, Expression var2, Expression var3) {
      var0.setSaltAndHash(getByteArray(var1, var2), getByteArray(var1, var3));
   }

   private static byte[] getByteArray(SessionLocal var0, Expression var1) {
      Value var2 = var1.optimize(var0).getValue(var0);
      if (DataType.isBinaryStringType(var2.getValueType())) {
         byte[] var4 = var2.getBytes();
         return var4 == null ? new byte[0] : var4;
      } else {
         String var3 = var2.getString();
         return var3 == null ? new byte[0] : StringUtils.convertHexToBytes(var3);
      }
   }

   static void setPassword(User var0, SessionLocal var1, Expression var2) {
      String var3 = var2.optimize(var1).getValue(var1).getString();
      char[] var4 = var3 == null ? new char[0] : var3.toCharArray();
      String var6 = var0.getName();
      byte[] var5;
      if (var6.isEmpty() && var4.length == 0) {
         var5 = new byte[0];
      } else {
         var5 = SHA256.getKeyPasswordHash(var6, var4);
      }

      var0.setUserPasswordHash(var5);
   }

   public long update() {
      this.session.getUser().checkAdmin();
      Database var1 = this.session.getDatabase();
      RightOwner var2 = var1.findUserOrRole(this.userName);
      if (var2 != null) {
         if (var2 instanceof User) {
            if (this.ifNotExists) {
               return 0L;
            } else {
               throw DbException.get(90033, this.userName);
            }
         } else {
            throw DbException.get(90069, this.userName);
         }
      } else {
         int var3 = this.getObjectId();
         User var4 = new User(var1, var3, this.userName, false);
         var4.setAdmin(this.admin);
         var4.setComment(this.comment);
         if (this.hash != null && this.salt != null) {
            setSaltAndHash(var4, this.session, this.salt, this.hash);
         } else {
            if (this.password == null) {
               throw DbException.getInternalError();
            }

            setPassword(var4, this.session, this.password);
         }

         var1.addDatabaseObject(this.session, var4);
         return 0L;
      }
   }

   public void setSalt(Expression var1) {
      this.salt = var1;
   }

   public void setHash(Expression var1) {
      this.hash = var1;
   }

   public void setAdmin(boolean var1) {
      this.admin = var1;
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   public int getType() {
      return 32;
   }
}
