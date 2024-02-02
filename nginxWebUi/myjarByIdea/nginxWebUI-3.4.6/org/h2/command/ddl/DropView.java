package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.constraint.ConstraintActionType;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;
import org.h2.table.TableType;
import org.h2.table.TableView;

public class DropView extends SchemaCommand {
   private String viewName;
   private boolean ifExists;
   private ConstraintActionType dropAction;

   public DropView(SessionLocal var1, Schema var2) {
      super(var1, var2);
      this.dropAction = var1.getDatabase().getSettings().dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public void setDropAction(ConstraintActionType var1) {
      this.dropAction = var1;
   }

   public void setViewName(String var1) {
      this.viewName = var1;
   }

   public long update() {
      Table var1 = this.getSchema().findTableOrView(this.session, this.viewName);
      if (var1 == null) {
         if (!this.ifExists) {
            throw DbException.get(90037, this.viewName);
         }
      } else {
         if (TableType.VIEW != var1.getTableType()) {
            throw DbException.get(90037, this.viewName);
         }

         this.session.getUser().checkSchemaOwner(var1.getSchema());
         if (this.dropAction == ConstraintActionType.RESTRICT) {
            Iterator var2 = var1.getChildren().iterator();

            while(var2.hasNext()) {
               DbObject var3 = (DbObject)var2.next();
               if (var3 instanceof TableView) {
                  throw DbException.get(90107, this.viewName, var3.getName());
               }
            }
         }

         TableView var7 = (TableView)var1;
         ArrayList var8 = new ArrayList(var7.getTables());
         var1.lock(this.session, 2);
         this.session.getDatabase().removeSchemaObject(this.session, var1);
         Iterator var4 = var8.iterator();

         while(var4.hasNext()) {
            Table var5 = (Table)var4.next();
            if (TableType.VIEW == var5.getTableType()) {
               TableView var6 = (TableView)var5;
               if (var6.isTableExpression() && var6.getName() != null) {
                  this.session.getDatabase().removeSchemaObject(this.session, var6);
               }
            }
         }

         this.session.getDatabase().unlockMeta(this.session);
      }

      return 0L;
   }

   public int getType() {
      return 48;
   }
}
