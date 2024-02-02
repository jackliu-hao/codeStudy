package org.h2.command.ddl;

import java.util.Iterator;
import org.h2.constraint.ConstraintReferential;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;

public class AlterTableRenameColumn extends AlterTable {
   private boolean ifExists;
   private String oldName;
   private String newName;

   public AlterTableRenameColumn(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public void setOldColumnName(String var1) {
      this.oldName = var1;
   }

   public void setNewColumnName(String var1) {
      this.newName = var1;
   }

   public long update(Table var1) {
      Column var2 = var1.getColumn(this.oldName, this.ifExists);
      if (var2 == null) {
         return 0L;
      } else {
         var1.checkSupportAlter();
         var1.renameColumn(var2, this.newName);
         var1.setModified();
         Database var3 = this.session.getDatabase();
         var3.updateMeta(this.session, var1);
         Iterator var4 = var1.getChildren().iterator();

         DbObject var5;
         while(var4.hasNext()) {
            var5 = (DbObject)var4.next();
            if (var5 instanceof ConstraintReferential) {
               ConstraintReferential var6 = (ConstraintReferential)var5;
               var6.updateOnTableColumnRename();
            }
         }

         var4 = var1.getChildren().iterator();

         while(var4.hasNext()) {
            var5 = (DbObject)var4.next();
            if (var5.getCreateSQL() != null) {
               var3.updateMeta(this.session, var5);
            }
         }

         return 0L;
      }
   }

   public int getType() {
      return 16;
   }
}
