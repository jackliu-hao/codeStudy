package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.Right;
import org.h2.engine.RightOwner;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;
import org.h2.util.Utils;

public class GrantRevoke extends DefineCommand {
   private ArrayList<String> roleNames;
   private int operationType;
   private int rightMask;
   private final ArrayList<Table> tables = Utils.newSmallArrayList();
   private Schema schema;
   private RightOwner grantee;

   public GrantRevoke(SessionLocal var1) {
      super(var1);
   }

   public void setOperationType(int var1) {
      this.operationType = var1;
   }

   public void addRight(int var1) {
      this.rightMask |= var1;
   }

   public void addRoleName(String var1) {
      if (this.roleNames == null) {
         this.roleNames = Utils.newSmallArrayList();
      }

      this.roleNames.add(var1);
   }

   public void setGranteeName(String var1) {
      Database var2 = this.session.getDatabase();
      this.grantee = var2.findUserOrRole(var1);
      if (this.grantee == null) {
         throw DbException.get(90071, var1);
      }
   }

   public long update() {
      Database var1 = this.session.getDatabase();
      User var2 = this.session.getUser();
      Iterator var3;
      if (this.roleNames != null) {
         var2.checkAdmin();
         var3 = this.roleNames.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            Role var5 = var1.findRole(var4);
            if (var5 == null) {
               throw DbException.get(90070, var4);
            }

            if (this.operationType == 49) {
               this.grantRole(var5);
            } else {
               if (this.operationType != 50) {
                  throw DbException.getInternalError("type=" + this.operationType);
               }

               this.revokeRole(var5);
            }
         }
      } else {
         if ((this.rightMask & 16) != 0) {
            var2.checkAdmin();
         } else {
            if (this.schema != null) {
               var2.checkSchemaOwner(this.schema);
            }

            var3 = this.tables.iterator();

            while(var3.hasNext()) {
               Table var6 = (Table)var3.next();
               var2.checkSchemaOwner(var6.getSchema());
            }
         }

         if (this.operationType == 49) {
            this.grantRight();
         } else {
            if (this.operationType != 50) {
               throw DbException.getInternalError("type=" + this.operationType);
            }

            this.revokeRight();
         }
      }

      return 0L;
   }

   private void grantRight() {
      if (this.schema != null) {
         this.grantRight(this.schema);
      }

      Iterator var1 = this.tables.iterator();

      while(var1.hasNext()) {
         Table var2 = (Table)var1.next();
         this.grantRight(var2);
      }

   }

   private void grantRight(DbObject var1) {
      Database var2 = this.session.getDatabase();
      Right var3 = this.grantee.getRightForObject(var1);
      if (var3 == null) {
         int var4 = this.getPersistedObjectId();
         if (var4 == 0) {
            var4 = this.session.getDatabase().allocateObjectId();
         }

         var3 = new Right(var2, var4, this.grantee, this.rightMask, var1);
         this.grantee.grantRight(var1, var3);
         var2.addDatabaseObject(this.session, var3);
      } else {
         var3.setRightMask(var3.getRightMask() | this.rightMask);
         var2.updateMeta(this.session, var3);
      }

   }

   private void grantRole(Role var1) {
      if (var1 == this.grantee || !this.grantee.isRoleGranted(var1)) {
         if (this.grantee instanceof Role) {
            Role var2 = (Role)this.grantee;
            if (var1.isRoleGranted(var2)) {
               throw DbException.get(90074, var1.getTraceSQL());
            }
         }

         Database var5 = this.session.getDatabase();
         int var3 = this.getObjectId();
         Right var4 = new Right(var5, var3, this.grantee, var1);
         var5.addDatabaseObject(this.session, var4);
         this.grantee.grantRole(var1, var4);
      }
   }

   private void revokeRight() {
      if (this.schema != null) {
         this.revokeRight(this.schema);
      }

      Iterator var1 = this.tables.iterator();

      while(var1.hasNext()) {
         Table var2 = (Table)var1.next();
         this.revokeRight(var2);
      }

   }

   private void revokeRight(DbObject var1) {
      Right var2 = this.grantee.getRightForObject(var1);
      if (var2 != null) {
         int var3 = var2.getRightMask();
         int var4 = var3 & ~this.rightMask;
         Database var5 = this.session.getDatabase();
         if (var4 == 0) {
            var5.removeDatabaseObject(this.session, var2);
         } else {
            var2.setRightMask(var4);
            var5.updateMeta(this.session, var2);
         }

      }
   }

   private void revokeRole(Role var1) {
      Right var2 = this.grantee.getRightForRole(var1);
      if (var2 != null) {
         Database var3 = this.session.getDatabase();
         var3.removeDatabaseObject(this.session, var2);
      }
   }

   public boolean isTransactional() {
      return false;
   }

   public void addTable(Table var1) {
      this.tables.add(var1);
   }

   public void setSchema(Schema var1) {
      this.schema = var1;
   }

   public int getType() {
      return this.operationType;
   }
}
