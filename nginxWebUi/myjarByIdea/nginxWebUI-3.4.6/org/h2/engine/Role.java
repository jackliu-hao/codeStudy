package org.h2.engine;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

public final class Role extends RightOwner {
   private final boolean system;

   public Role(Database var1, int var2, String var3, boolean var4) {
      super(var1, var2, var3, 13);
      this.system = var4;
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      throw DbException.getInternalError(this.toString());
   }

   public String getCreateSQL(boolean var1) {
      if (this.system) {
         return null;
      } else {
         StringBuilder var2 = new StringBuilder("CREATE ROLE ");
         if (var1) {
            var2.append("IF NOT EXISTS ");
         }

         return this.getSQL(var2, 0).toString();
      }
   }

   public String getCreateSQL() {
      return this.getCreateSQL(false);
   }

   public int getType() {
      return 7;
   }

   public ArrayList<DbObject> getChildren() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.database.getAllSchemas().iterator();

      while(var2.hasNext()) {
         Schema var3 = (Schema)var2.next();
         if (var3.getOwner() == this) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      Iterator var2 = this.database.getAllUsersAndRoles().iterator();

      while(var2.hasNext()) {
         RightOwner var3 = (RightOwner)var2.next();
         Right var4 = var3.getRightForRole(this);
         if (var4 != null) {
            this.database.removeDatabaseObject(var1, var4);
         }
      }

      var2 = this.database.getAllRights().iterator();

      while(var2.hasNext()) {
         Right var5 = (Right)var2.next();
         if (var5.getGrantee() == this) {
            this.database.removeDatabaseObject(var1, var5);
         }
      }

      this.database.removeMeta(var1, this.getId());
      this.invalidate();
   }
}
