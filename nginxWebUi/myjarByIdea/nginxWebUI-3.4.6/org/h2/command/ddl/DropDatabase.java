package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.h2.engine.Database;
import org.h2.engine.Right;
import org.h2.engine.RightOwner;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.schema.Sequence;
import org.h2.table.Table;
import org.h2.table.TableType;
import org.h2.value.ValueNull;

public class DropDatabase extends DefineCommand {
   private boolean dropAllObjects;
   private boolean deleteFiles;

   public DropDatabase(SessionLocal var1) {
      super(var1);
   }

   public long update() {
      if (this.dropAllObjects) {
         this.dropAllObjects();
      }

      if (this.deleteFiles) {
         this.session.getDatabase().setDeleteFilesOnDisconnect(true);
      }

      return 0L;
   }

   private void dropAllObjects() {
      User var1 = this.session.getUser();
      var1.checkAdmin();
      Database var2 = this.session.getDatabase();
      var2.lockMeta(this.session);

      boolean var3;
      ArrayList var5;
      Iterator var6;
      do {
         ArrayList var4 = var2.getAllTablesAndViews();
         var5 = new ArrayList(var4.size());
         var6 = var4.iterator();

         Table var7;
         while(var6.hasNext()) {
            var7 = (Table)var6.next();
            if (var7.getName() != null && TableType.VIEW == var7.getTableType()) {
               var5.add(var7);
            }
         }

         var6 = var4.iterator();

         while(var6.hasNext()) {
            var7 = (Table)var6.next();
            if (var7.getName() != null && TableType.TABLE_LINK == var7.getTableType()) {
               var5.add(var7);
            }
         }

         var6 = var4.iterator();

         while(var6.hasNext()) {
            var7 = (Table)var6.next();
            if (var7.getName() != null && TableType.TABLE == var7.getTableType() && !var7.isHidden()) {
               var5.add(var7);
            }
         }

         var6 = var4.iterator();

         while(var6.hasNext()) {
            var7 = (Table)var6.next();
            if (var7.getName() != null && TableType.EXTERNAL_TABLE_ENGINE == var7.getTableType() && !var7.isHidden()) {
               var5.add(var7);
            }
         }

         var3 = false;
         var6 = var5.iterator();

         while(var6.hasNext()) {
            var7 = (Table)var6.next();
            if (var7.getName() != null) {
               if (var2.getDependentTable(var7, var7) == null) {
                  var2.removeSchemaObject(this.session, var7);
               } else {
                  var3 = true;
               }
            }
         }
      } while(var3);

      Collection var11 = var2.getAllSchemasNoMeta();
      Iterator var12 = var11.iterator();

      while(var12.hasNext()) {
         Schema var13 = (Schema)var12.next();
         if (var13.canDrop()) {
            var2.removeDatabaseObject(this.session, var13);
         }
      }

      var5 = new ArrayList();
      var6 = var11.iterator();

      while(var6.hasNext()) {
         Schema var15 = (Schema)var6.next();
         Iterator var8 = var15.getAllSequences().iterator();

         while(var8.hasNext()) {
            Sequence var9 = (Sequence)var8.next();
            if (!var9.getBelongsToTable()) {
               var5.add(var9);
            }
         }
      }

      addAll(var11, 5, var5);
      addAll(var11, 4, var5);
      addAll(var11, 11, var5);
      addAll(var11, 9, var5);
      addAll(var11, 12, var5);
      var6 = var5.iterator();

      while(var6.hasNext()) {
         SchemaObject var18 = (SchemaObject)var6.next();
         if (var18.getSchema().isValid() && !var18.isHidden()) {
            var2.removeSchemaObject(this.session, var18);
         }
      }

      Role var16 = var2.getPublicRole();
      Iterator var20 = var2.getAllUsersAndRoles().iterator();

      while(var20.hasNext()) {
         RightOwner var14 = (RightOwner)var20.next();
         if (var14 != var1 && var14 != var16) {
            var2.removeDatabaseObject(this.session, var14);
         }
      }

      var20 = var2.getAllRights().iterator();

      while(var20.hasNext()) {
         Right var17 = (Right)var20.next();
         var2.removeDatabaseObject(this.session, var17);
      }

      SessionLocal[] var22 = var2.getSessions(false);
      int var19 = var22.length;

      for(int var21 = 0; var21 < var19; ++var21) {
         SessionLocal var10 = var22[var21];
         var10.setLastIdentity(ValueNull.INSTANCE);
      }

   }

   private static void addAll(Collection<Schema> var0, int var1, ArrayList<SchemaObject> var2) {
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         Schema var4 = (Schema)var3.next();
         var4.getAll(var1, var2);
      }

   }

   public void setDropAllObjects(boolean var1) {
      this.dropAllObjects = var1;
   }

   public void setDeleteFiles(boolean var1) {
      this.deleteFiles = var1;
   }

   public int getType() {
      return 38;
   }
}
