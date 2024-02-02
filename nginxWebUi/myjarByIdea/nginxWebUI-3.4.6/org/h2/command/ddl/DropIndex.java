package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.constraint.Constraint;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

public class DropIndex extends SchemaCommand {
   private String indexName;
   private boolean ifExists;

   public DropIndex(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public void setIndexName(String var1) {
      this.indexName = var1;
   }

   public long update() {
      Database var1 = this.session.getDatabase();
      Index var2 = this.getSchema().findIndex(this.session, this.indexName);
      if (var2 == null) {
         if (!this.ifExists) {
            throw DbException.get(42112, (String)this.indexName);
         }
      } else {
         Table var3 = var2.getTable();
         this.session.getUser().checkTableRight(var2.getTable(), 32);
         Constraint var4 = null;
         ArrayList var5 = var3.getConstraints();

         for(int var6 = 0; var5 != null && var6 < var5.size(); ++var6) {
            Constraint var7 = (Constraint)var5.get(var6);
            if (var7.usesIndex(var2)) {
               if (Constraint.Type.PRIMARY_KEY != var7.getConstraintType()) {
                  throw DbException.get(90085, this.indexName, var7.getName());
               }

               Iterator var8 = var5.iterator();

               while(var8.hasNext()) {
                  Constraint var9 = (Constraint)var8.next();
                  if (var9.getReferencedConstraint() == var7) {
                     throw DbException.get(90085, this.indexName, var7.getName());
                  }
               }

               var4 = var7;
            }
         }

         var2.getTable().setModified();
         if (var4 != null) {
            var1.removeSchemaObject(this.session, var4);
         } else {
            var1.removeSchemaObject(this.session, var2);
         }
      }

      return 0L;
   }

   public int getType() {
      return 40;
   }
}
