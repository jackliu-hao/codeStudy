package org.h2.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.security.SHA256;
import org.h2.table.DualTable;
import org.h2.table.MetaTable;
import org.h2.table.RangeTable;
import org.h2.table.Table;
import org.h2.table.TableType;
import org.h2.table.TableView;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public final class User extends RightOwner {
   private final boolean systemUser;
   private byte[] salt;
   private byte[] passwordHash;
   private boolean admin;

   public User(Database var1, int var2, String var3, boolean var4) {
      super(var1, var2, var3, 13);
      this.systemUser = var4;
   }

   public void setAdmin(boolean var1) {
      this.admin = var1;
   }

   public boolean isAdmin() {
      return this.admin;
   }

   public void setSaltAndHash(byte[] var1, byte[] var2) {
      this.salt = var1;
      this.passwordHash = var2;
   }

   public void setUserPasswordHash(byte[] var1) {
      if (var1 != null) {
         if (var1.length == 0) {
            this.salt = this.passwordHash = var1;
         } else {
            this.salt = new byte[8];
            MathUtils.randomBytes(this.salt);
            this.passwordHash = SHA256.getHashWithSalt(var1, this.salt);
         }
      }

   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      throw DbException.getInternalError(this.toString());
   }

   public String getCreateSQL() {
      return this.getCreateSQL(true);
   }

   public String getCreateSQL(boolean var1) {
      StringBuilder var2 = new StringBuilder("CREATE USER IF NOT EXISTS ");
      this.getSQL(var2, 0);
      if (this.comment != null) {
         var2.append(" COMMENT ");
         StringUtils.quoteStringSQL(var2, this.comment);
      }

      if (var1) {
         var2.append(" SALT '");
         StringUtils.convertBytesToHex(var2, this.salt).append("' HASH '");
         StringUtils.convertBytesToHex(var2, this.passwordHash).append('\'');
      } else {
         var2.append(" PASSWORD ''");
      }

      if (this.admin) {
         var2.append(" ADMIN");
      }

      return var2.toString();
   }

   boolean validateUserPasswordHash(byte[] var1) {
      if (var1.length == 0 && this.passwordHash.length == 0) {
         return true;
      } else {
         if (var1.length == 0) {
            var1 = SHA256.getKeyPasswordHash(this.getName(), new char[0]);
         }

         byte[] var2 = SHA256.getHashWithSalt(var1, this.salt);
         return Utils.compareSecure(var2, this.passwordHash);
      }
   }

   public void checkAdmin() {
      if (!this.admin) {
         throw DbException.get(90040);
      }
   }

   public void checkSchemaAdmin() {
      if (!this.hasSchemaRight((Schema)null)) {
         throw DbException.get(90040);
      }
   }

   public void checkSchemaOwner(Schema var1) {
      if (!this.hasSchemaRight(var1)) {
         throw DbException.get(90096, var1.getTraceSQL());
      }
   }

   private boolean hasSchemaRight(Schema var1) {
      if (this.admin) {
         return true;
      } else {
         Role var2 = this.database.getPublicRole();
         return var2.isSchemaRightGrantedRecursive(var1) ? true : this.isSchemaRightGrantedRecursive(var1);
      }
   }

   public void checkTableRight(Table var1, int var2) {
      if (!this.hasTableRight(var1, var2)) {
         throw DbException.get(90096, var1.getTraceSQL());
      }
   }

   public boolean hasTableRight(Table var1, int var2) {
      if (var2 != 1 && !this.systemUser) {
         var1.checkWritingAllowed();
      }

      if (this.admin) {
         return true;
      } else {
         Role var3 = this.database.getPublicRole();
         if (var3.isTableRightGrantedRecursive(var1, var2)) {
            return true;
         } else if (!(var1 instanceof MetaTable) && !(var1 instanceof DualTable) && !(var1 instanceof RangeTable)) {
            TableType var4 = var1.getTableType();
            if (TableType.VIEW == var4) {
               TableView var5 = (TableView)var1;
               if (var5.getOwner() == this) {
                  return true;
               }
            } else if (var4 == null) {
               return true;
            }

            return var1.isTemporary() && !var1.isGlobalTemporary() ? true : this.isTableRightGrantedRecursive(var1, var2);
         } else {
            return true;
         }
      }
   }

   public int getType() {
      return 2;
   }

   public ArrayList<DbObject> getChildren() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.database.getAllRights().iterator();

      while(var2.hasNext()) {
         Right var3 = (Right)var2.next();
         if (var3.getGrantee() == this) {
            var1.add(var3);
         }
      }

      var2 = this.database.getAllSchemas().iterator();

      while(var2.hasNext()) {
         Schema var4 = (Schema)var2.next();
         if (var4.getOwner() == this) {
            var1.add(var4);
         }
      }

      return var1;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      Iterator var2 = this.database.getAllRights().iterator();

      while(var2.hasNext()) {
         Right var3 = (Right)var2.next();
         if (var3.getGrantee() == this) {
            this.database.removeDatabaseObject(var1, var3);
         }
      }

      this.database.removeMeta(var1, this.getId());
      this.salt = null;
      Arrays.fill(this.passwordHash, (byte)0);
      this.passwordHash = null;
      this.invalidate();
   }
}
