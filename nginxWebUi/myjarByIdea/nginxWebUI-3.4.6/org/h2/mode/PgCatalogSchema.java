package org.h2.mode;

import java.util.HashMap;
import java.util.Map;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.schema.MetaSchema;
import org.h2.table.Table;

public final class PgCatalogSchema extends MetaSchema {
   private volatile HashMap<String, Table> tables;

   public PgCatalogSchema(Database var1, User var2) {
      super(var1, -1000, var1.sysIdentifier("PG_CATALOG"), var2);
   }

   protected Map<String, Table> getMap(SessionLocal var1) {
      HashMap var2 = this.tables;
      if (var2 == null) {
         var2 = this.fillMap();
      }

      return var2;
   }

   private synchronized HashMap<String, Table> fillMap() {
      HashMap var1 = this.tables;
      if (var1 == null) {
         var1 = this.database.newStringMap();

         for(int var2 = 0; var2 < 19; ++var2) {
            PgCatalogTable var3 = new PgCatalogTable(this, -1000 - var2, var2);
            var1.put(var3.getName(), var3);
         }

         this.tables = var1;
      }

      return var1;
   }
}
