package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;

public class AlterSchemaRename extends DefineCommand {
   private Schema oldSchema;
   private String newSchemaName;

   public AlterSchemaRename(SessionLocal var1) {
      super(var1);
   }

   public void setOldSchema(Schema var1) {
      this.oldSchema = var1;
   }

   public void setNewName(String var1) {
      this.newSchemaName = var1;
   }

   public long update() {
      this.session.getUser().checkSchemaAdmin();
      Database var1 = this.session.getDatabase();
      if (!this.oldSchema.canDrop()) {
         throw DbException.get(90090, this.oldSchema.getName());
      } else if (var1.findSchema(this.newSchemaName) == null && !this.newSchemaName.equals(this.oldSchema.getName())) {
         var1.renameDatabaseObject(this.session, this.oldSchema, this.newSchemaName);
         ArrayList var2 = new ArrayList();
         Iterator var3 = var1.getAllSchemas().iterator();

         while(var3.hasNext()) {
            Schema var4 = (Schema)var3.next();
            var4.getAll(var2);
            Iterator var5 = var2.iterator();

            while(var5.hasNext()) {
               SchemaObject var6 = (SchemaObject)var5.next();
               var1.updateMeta(this.session, var6);
            }

            var2.clear();
         }

         return 0L;
      } else {
         throw DbException.get(90078, this.newSchemaName);
      }
   }

   public int getType() {
      return 2;
   }
}
