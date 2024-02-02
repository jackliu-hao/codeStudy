package org.h2.schema;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.table.InformationSchemaTable;
import org.h2.table.InformationSchemaTableLegacy;
import org.h2.table.Table;

public final class InformationSchema extends MetaSchema {
   private volatile HashMap<String, Table> newTables;
   private volatile HashMap<String, Table> oldTables;

   public InformationSchema(Database var1, User var2) {
      super(var1, -1, var1.sysIdentifier("INFORMATION_SCHEMA"), var2);
   }

   protected Map<String, Table> getMap(SessionLocal var1) {
      if (var1 == null) {
         return Collections.emptyMap();
      } else {
         boolean var2 = var1.isOldInformationSchema();
         HashMap var3 = var2 ? this.oldTables : this.newTables;
         if (var3 == null) {
            var3 = this.fillMap(var2);
         }

         return var3;
      }
   }

   private synchronized HashMap<String, Table> fillMap(boolean var1) {
      HashMap var2 = var1 ? this.oldTables : this.newTables;
      if (var2 == null) {
         var2 = this.database.newStringMap(64);
         int var3;
         if (var1) {
            for(var3 = 0; var3 < 36; ++var3) {
               InformationSchemaTableLegacy var4 = new InformationSchemaTableLegacy(this, -1 - var3, var3);
               var2.put(var4.getName(), var4);
            }

            this.oldTables = var2;
         } else {
            for(var3 = 0; var3 < 35; ++var3) {
               InformationSchemaTable var5 = new InformationSchemaTable(this, -1 - var3, var3);
               var2.put(var5.getName(), var5);
            }

            this.newTables = var2;
         }
      }

      return var2;
   }
}
