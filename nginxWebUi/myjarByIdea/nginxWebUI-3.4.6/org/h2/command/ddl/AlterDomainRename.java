package org.h2.command.ddl;

import java.util.function.BiPredicate;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;

public class AlterDomainRename extends AlterDomain {
   private String newDomainName;

   public AlterDomainRename(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setNewDomainName(String var1) {
      this.newDomainName = var1;
   }

   long update(Schema var1, Domain var2) {
      Domain var3 = var1.findDomain(this.newDomainName);
      if (var3 != null) {
         if (var2 != var3) {
            throw DbException.get(90119, this.newDomainName);
         }

         if (this.newDomainName.equals(var2.getName())) {
            return 0L;
         }
      }

      this.session.getDatabase().renameSchemaObject(this.session, var2, this.newDomainName);
      forAllDependencies(this.session, var2, (BiPredicate)null, (BiPredicate)null, false);
      return 0L;
   }

   public int getType() {
      return 96;
   }
}
