package org.h2.command.ddl;

import java.util.ArrayList;
import org.h2.constraint.ConstraintActionType;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;

public class DropSchema extends DefineCommand {
   private String schemaName;
   private boolean ifExists;
   private ConstraintActionType dropAction;

   public DropSchema(SessionLocal var1) {
      super(var1);
      this.dropAction = var1.getDatabase().getSettings().dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
   }

   public void setSchemaName(String var1) {
      this.schemaName = var1;
   }

   public long update() {
      Database var1 = this.session.getDatabase();
      Schema var2 = var1.findSchema(this.schemaName);
      if (var2 == null) {
         if (!this.ifExists) {
            throw DbException.get(90079, this.schemaName);
         }
      } else {
         this.session.getUser().checkSchemaOwner(var2);
         if (!var2.canDrop()) {
            throw DbException.get(90090, this.schemaName);
         }

         if (this.dropAction == ConstraintActionType.RESTRICT && !var2.isEmpty()) {
            ArrayList var3 = var2.getAll((ArrayList)null);
            int var4 = var3.size();
            if (var4 > 0) {
               StringBuilder var5 = new StringBuilder();

               for(int var6 = 0; var6 < var4; ++var6) {
                  if (var6 > 0) {
                     var5.append(", ");
                  }

                  var5.append(((SchemaObject)var3.get(var6)).getName());
               }

               throw DbException.get(90107, this.schemaName, var5.toString());
            }
         }

         var1.removeDatabaseObject(this.session, var2);
      }

      return 0L;
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public void setDropAction(ConstraintActionType var1) {
      this.dropAction = var1;
   }

   public int getType() {
      return 42;
   }
}
