package org.h2.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;
import org.h2.util.StringUtils;

public abstract class RightOwner extends DbObject {
   private HashMap<Role, Right> grantedRoles;
   private HashMap<DbObject, Right> grantedRights;

   protected RightOwner(Database var1, int var2, String var3, int var4) {
      super(var1, var2, StringUtils.toUpperEnglish(var3), var4);
   }

   public void rename(String var1) {
      super.rename(StringUtils.toUpperEnglish(var1));
   }

   public boolean isRoleGranted(Role var1) {
      if (var1 == this) {
         return true;
      } else {
         if (this.grantedRoles != null) {
            Iterator var2 = this.grantedRoles.keySet().iterator();

            while(var2.hasNext()) {
               Role var3 = (Role)var2.next();
               if (var3 == var1) {
                  return true;
               }

               if (var3.isRoleGranted(var1)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   final boolean isTableRightGrantedRecursive(Table var1, int var2) {
      Schema var3 = var1.getSchema();
      if (var3.getOwner() == this) {
         return true;
      } else {
         if (this.grantedRights != null) {
            Right var4 = (Right)this.grantedRights.get((Object)null);
            if (var4 != null && (var4.getRightMask() & 16) == 16) {
               return true;
            }

            var4 = (Right)this.grantedRights.get(var3);
            if (var4 != null && (var4.getRightMask() & var2) == var2) {
               return true;
            }

            var4 = (Right)this.grantedRights.get(var1);
            if (var4 != null && (var4.getRightMask() & var2) == var2) {
               return true;
            }
         }

         if (this.grantedRoles != null) {
            Iterator var6 = this.grantedRoles.keySet().iterator();

            while(var6.hasNext()) {
               Role var5 = (Role)var6.next();
               if (var5.isTableRightGrantedRecursive(var1, var2)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   final boolean isSchemaRightGrantedRecursive(Schema var1) {
      if (var1 != null && var1.getOwner() == this) {
         return true;
      } else {
         if (this.grantedRights != null) {
            Right var2 = (Right)this.grantedRights.get((Object)null);
            if (var2 != null && (var2.getRightMask() & 16) == 16) {
               return true;
            }
         }

         if (this.grantedRoles != null) {
            Iterator var4 = this.grantedRoles.keySet().iterator();

            while(var4.hasNext()) {
               Role var3 = (Role)var4.next();
               if (var3.isSchemaRightGrantedRecursive(var1)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public void grantRight(DbObject var1, Right var2) {
      if (this.grantedRights == null) {
         this.grantedRights = new HashMap();
      }

      this.grantedRights.put(var1, var2);
   }

   void revokeRight(DbObject var1) {
      if (this.grantedRights != null) {
         this.grantedRights.remove(var1);
         if (this.grantedRights.size() == 0) {
            this.grantedRights = null;
         }

      }
   }

   public void grantRole(Role var1, Right var2) {
      if (this.grantedRoles == null) {
         this.grantedRoles = new HashMap();
      }

      this.grantedRoles.put(var1, var2);
   }

   void revokeRole(Role var1) {
      if (this.grantedRoles != null) {
         Right var2 = (Right)this.grantedRoles.get(var1);
         if (var2 != null) {
            this.grantedRoles.remove(var1);
            if (this.grantedRoles.size() == 0) {
               this.grantedRoles = null;
            }

         }
      }
   }

   public void revokeTemporaryRightsOnRoles() {
      if (this.grantedRoles != null) {
         ArrayList var1 = new ArrayList();
         Iterator var2 = this.grantedRoles.entrySet().iterator();

         while(true) {
            Map.Entry var3;
            do {
               if (!var2.hasNext()) {
                  var2 = var1.iterator();

                  while(var2.hasNext()) {
                     Role var4 = (Role)var2.next();
                     this.revokeRole(var4);
                  }

                  return;
               }

               var3 = (Map.Entry)var2.next();
            } while(!((Right)var3.getValue()).isTemporary() && ((Right)var3.getValue()).isValid());

            var1.add(var3.getKey());
         }
      }
   }

   public Right getRightForObject(DbObject var1) {
      return this.grantedRights == null ? null : (Right)this.grantedRights.get(var1);
   }

   public Right getRightForRole(Role var1) {
      return this.grantedRoles == null ? null : (Right)this.grantedRoles.get(var1);
   }

   public final void checkOwnsNoSchemas() {
      Iterator var1 = this.database.getAllSchemas().iterator();

      Schema var2;
      do {
         if (!var1.hasNext()) {
            return;
         }

         var2 = (Schema)var1.next();
      } while(this != var2.getOwner());

      throw DbException.get(90107, this.getName(), var2.getName());
   }
}
