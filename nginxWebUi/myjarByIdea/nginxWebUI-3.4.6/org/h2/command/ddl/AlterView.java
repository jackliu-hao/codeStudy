package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.table.TableView;

public class AlterView extends DefineCommand {
   private boolean ifExists;
   private TableView view;

   public AlterView(SessionLocal var1) {
      super(var1);
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public void setView(TableView var1) {
      this.view = var1;
   }

   public long update() {
      if (this.view == null && this.ifExists) {
         return 0L;
      } else {
         this.session.getUser().checkSchemaOwner(this.view.getSchema());
         DbException var1 = this.view.recompile(this.session, false, true);
         if (var1 != null) {
            throw var1;
         } else {
            return 0L;
         }
      }
   }

   public int getType() {
      return 20;
   }
}
