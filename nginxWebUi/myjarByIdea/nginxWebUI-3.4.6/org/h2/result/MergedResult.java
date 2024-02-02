package org.h2.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.h2.util.Utils;
import org.h2.value.Value;

public final class MergedResult {
   private final ArrayList<Map<SimpleResult.Column, Value>> data = Utils.newSmallArrayList();
   private final ArrayList<SimpleResult.Column> columns = Utils.newSmallArrayList();

   public void add(ResultInterface var1) {
      int var2 = var1.getVisibleColumnCount();
      if (var2 != 0) {
         SimpleResult.Column[] var3 = new SimpleResult.Column[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            SimpleResult.Column var5 = new SimpleResult.Column(var1.getAlias(var4), var1.getColumnName(var4), var1.getColumnType(var4));
            var3[var4] = var5;
            if (!this.columns.contains(var5)) {
               this.columns.add(var5);
            }
         }

         while(true) {
            while(var1.next()) {
               if (var2 == 1) {
                  this.data.add(Collections.singletonMap(var3[0], var1.currentRow()[0]));
               } else {
                  HashMap var7 = new HashMap();

                  for(int var8 = 0; var8 < var2; ++var8) {
                     SimpleResult.Column var6 = var3[var8];
                     var7.put(var6, var1.currentRow()[var8]);
                  }

                  this.data.add(var7);
               }
            }

            return;
         }
      }
   }

   public SimpleResult getResult() {
      SimpleResult var1 = new SimpleResult();
      Iterator var2 = this.columns.iterator();

      while(var2.hasNext()) {
         SimpleResult.Column var3 = (SimpleResult.Column)var2.next();
         var1.addColumn(var3);
      }

      var2 = this.data.iterator();

      while(var2.hasNext()) {
         Map var7 = (Map)var2.next();
         Value[] var4 = new Value[this.columns.size()];

         Map.Entry var6;
         for(Iterator var5 = var7.entrySet().iterator(); var5.hasNext(); var4[this.columns.indexOf(var6.getKey())] = (Value)var6.getValue()) {
            var6 = (Map.Entry)var5.next();
         }

         var1.addRow(var4);
      }

      return var1;
   }

   public String toString() {
      return this.columns + ": " + this.data.size();
   }
}
