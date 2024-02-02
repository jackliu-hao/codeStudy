package org.h2.command.query;

import java.util.ArrayList;
import java.util.HashMap;
import org.h2.expression.ExpressionVisitor;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableFilter;

public class AllColumnsForPlan {
   private final TableFilter[] filters;
   private HashMap<Table, ArrayList<Column>> map;

   public AllColumnsForPlan(TableFilter[] var1) {
      this.filters = var1;
   }

   public void add(Column var1) {
      ArrayList var2 = (ArrayList)this.map.get(var1.getTable());
      if (var2 == null) {
         var2 = new ArrayList();
         this.map.put(var1.getTable(), var2);
      }

      if (!var2.contains(var1)) {
         var2.add(var1);
      }

   }

   public ArrayList<Column> get(Table var1) {
      if (this.map == null) {
         this.map = new HashMap();
         ExpressionVisitor.allColumnsForTableFilters(this.filters, this);
      }

      return (ArrayList)this.map.get(var1);
   }
}
