package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintActionType;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;
import org.h2.table.TableView;
import org.h2.util.Utils;

public class DropTable extends DefineCommand {
   private boolean ifExists;
   private ConstraintActionType dropAction;
   private final ArrayList<SchemaAndTable> tables = Utils.newSmallArrayList();

   public DropTable(SessionLocal var1) {
      super(var1);
      this.dropAction = var1.getDatabase().getSettings().dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public void addTable(Schema var1, String var2) {
      this.tables.add(new SchemaAndTable(var1, var2));
   }

   private boolean prepareDrop() {
      HashSet var1 = new HashSet();
      Iterator var2 = this.tables.iterator();

      while(var2.hasNext()) {
         SchemaAndTable var3 = (SchemaAndTable)var2.next();
         String var4 = var3.tableName;
         Table var5 = var3.schema.findTableOrView(this.session, var4);
         if (var5 == null) {
            if (!this.ifExists) {
               throw DbException.get(42102, (String)var4);
            }
         } else {
            this.session.getUser().checkTableRight(var5, 32);
            if (!var5.canDrop()) {
               throw DbException.get(90118, var4);
            }

            var1.add(var5);
         }
      }

      if (var1.isEmpty()) {
         return false;
      } else {
         Table var9;
         for(var2 = var1.iterator(); var2.hasNext(); var9.lock(this.session, 2)) {
            var9 = (Table)var2.next();
            ArrayList var10 = new ArrayList();
            if (this.dropAction == ConstraintActionType.RESTRICT) {
               CopyOnWriteArrayList var11 = var9.getDependentViews();
               if (var11 != null && !var11.isEmpty()) {
                  Iterator var6 = var11.iterator();

                  while(var6.hasNext()) {
                     TableView var7 = (TableView)var6.next();
                     if (!var1.contains(var7)) {
                        var10.add(var7.getName());
                     }
                  }
               }

               ArrayList var12 = var9.getConstraints();
               if (var12 != null && !var12.isEmpty()) {
                  Iterator var13 = var12.iterator();

                  while(var13.hasNext()) {
                     Constraint var8 = (Constraint)var13.next();
                     if (!var1.contains(var8.getTable())) {
                        var10.add(var8.getName());
                     }
                  }
               }

               if (!var10.isEmpty()) {
                  throw DbException.get(90107, var9.getName(), String.join(", ", var10));
               }
            }
         }

         return true;
      }
   }

   private void executeDrop() {
      Iterator var1 = this.tables.iterator();

      while(var1.hasNext()) {
         SchemaAndTable var2 = (SchemaAndTable)var1.next();
         Table var3 = var2.schema.findTableOrView(this.session, var2.tableName);
         if (var3 != null) {
            var3.setModified();
            Database var4 = this.session.getDatabase();
            var4.lockMeta(this.session);
            var4.removeSchemaObject(this.session, var3);
         }
      }

   }

   public long update() {
      if (this.prepareDrop()) {
         this.executeDrop();
      }

      return 0L;
   }

   public void setDropAction(ConstraintActionType var1) {
      this.dropAction = var1;
   }

   public int getType() {
      return 44;
   }

   private static final class SchemaAndTable {
      final Schema schema;
      final String tableName;

      SchemaAndTable(Schema var1, String var2) {
         this.schema = var1;
         this.tableName = var2;
      }
   }
}
