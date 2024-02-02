package org.h2.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.table.Table;

public abstract class MetaSchema extends Schema {
   public MetaSchema(Database var1, int var2, String var3, User var4) {
      super(var1, var2, var3, var4, true);
   }

   public Table findTableOrView(SessionLocal var1, String var2) {
      Map var3 = this.getMap(var1);
      Table var4 = (Table)var3.get(var2);
      return var4 != null ? var4 : super.findTableOrView(var1, var2);
   }

   public Collection<Table> getAllTablesAndViews(SessionLocal var1) {
      Collection var2 = super.getAllTablesAndViews(var1);
      if (var1 == null) {
         return var2;
      } else {
         Collection var3 = this.getMap(var1).values();
         if (var2.isEmpty()) {
            return var3;
         } else {
            ArrayList var4 = new ArrayList(var3.size() + var2.size());
            var4.addAll(var3);
            var4.addAll(var2);
            return var4;
         }
      }
   }

   public Table getTableOrView(SessionLocal var1, String var2) {
      Map var3 = this.getMap(var1);
      Table var4 = (Table)var3.get(var2);
      return var4 != null ? var4 : super.getTableOrView(var1, var2);
   }

   public Table getTableOrViewByName(SessionLocal var1, String var2) {
      Map var3 = this.getMap(var1);
      Table var4 = (Table)var3.get(var2);
      return var4 != null ? var4 : super.getTableOrViewByName(var1, var2);
   }

   protected abstract Map<String, Table> getMap(SessionLocal var1);

   public boolean isEmpty() {
      return false;
   }
}
