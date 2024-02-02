package org.h2.engine;

import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

public final class Right extends DbObject {
   public static final int SELECT = 1;
   public static final int DELETE = 2;
   public static final int INSERT = 4;
   public static final int UPDATE = 8;
   public static final int ALTER_ANY_SCHEMA = 16;
   public static final int SCHEMA_OWNER = 32;
   public static final int ALL = 15;
   private RightOwner grantee;
   private Role grantedRole;
   private int grantedRight;
   private DbObject grantedObject;

   public Right(Database var1, int var2, RightOwner var3, Role var4) {
      super(var1, var2, "RIGHT_" + var2, 13);
      this.grantee = var3;
      this.grantedRole = var4;
   }

   public Right(Database var1, int var2, RightOwner var3, int var4, DbObject var5) {
      super(var1, var2, Integer.toString(var2), 13);
      this.grantee = var3;
      this.grantedRight = var4;
      this.grantedObject = var5;
   }

   private static boolean appendRight(StringBuilder var0, int var1, int var2, String var3, boolean var4) {
      if ((var1 & var2) != 0) {
         if (var4) {
            var0.append(", ");
         }

         var0.append(var3);
         return true;
      } else {
         return var4;
      }
   }

   public String getRights() {
      StringBuilder var1 = new StringBuilder();
      if (this.grantedRight == 15) {
         var1.append("ALL");
      } else {
         boolean var2 = false;
         var2 = appendRight(var1, this.grantedRight, 1, "SELECT", var2);
         var2 = appendRight(var1, this.grantedRight, 2, "DELETE", var2);
         var2 = appendRight(var1, this.grantedRight, 4, "INSERT", var2);
         var2 = appendRight(var1, this.grantedRight, 8, "UPDATE", var2);
         appendRight(var1, this.grantedRight, 16, "ALTER ANY SCHEMA", var2);
      }

      return var1.toString();
   }

   public Role getGrantedRole() {
      return this.grantedRole;
   }

   public DbObject getGrantedObject() {
      return this.grantedObject;
   }

   public DbObject getGrantee() {
      return this.grantee;
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      return this.getCreateSQLForCopy(var1);
   }

   private String getCreateSQLForCopy(DbObject var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("GRANT ");
      if (this.grantedRole != null) {
         this.grantedRole.getSQL(var2, 0);
      } else {
         var2.append(this.getRights());
         if (var1 != null) {
            if (var1 instanceof Schema) {
               var2.append(" ON SCHEMA ");
               var1.getSQL(var2, 0);
            } else if (var1 instanceof Table) {
               var2.append(" ON ");
               var1.getSQL(var2, 0);
            }
         }
      }

      var2.append(" TO ");
      this.grantee.getSQL(var2, 0);
      return var2.toString();
   }

   public String getCreateSQL() {
      return this.getCreateSQLForCopy(this.grantedObject);
   }

   public int getType() {
      return 8;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      if (this.grantedRole != null) {
         this.grantee.revokeRole(this.grantedRole);
      } else {
         this.grantee.revokeRight(this.grantedObject);
      }

      this.database.removeMeta(var1, this.getId());
      this.grantedRole = null;
      this.grantedObject = null;
      this.grantee = null;
      this.invalidate();
   }

   public void checkRename() {
      throw DbException.getInternalError();
   }

   public void setRightMask(int var1) {
      this.grantedRight = var1;
   }

   public int getRightMask() {
      return this.grantedRight;
   }
}
