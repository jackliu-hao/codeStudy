package org.h2.index;

import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.value.Value;

class VirtualTableCursor implements Cursor {
   private final VirtualTableIndex index;
   private final SearchRow first;
   private final SearchRow last;
   private final ResultInterface result;
   Value[] values;
   Row row;

   VirtualTableCursor(VirtualTableIndex var1, SearchRow var2, SearchRow var3, ResultInterface var4) {
      this.index = var1;
      this.first = var2;
      this.last = var3;
      this.result = var4;
   }

   public Row get() {
      if (this.values == null) {
         return null;
      } else {
         if (this.row == null) {
            this.row = Row.get(this.values, 1);
         }

         return this.row;
      }
   }

   public SearchRow getSearchRow() {
      return this.get();
   }

   public boolean next() {
      SearchRow var1 = this.first;
      SearchRow var2 = this.last;
      if (var1 == null && var2 == null) {
         return this.nextImpl();
      } else {
         int var4;
         do {
            Row var3;
            do {
               if (!this.nextImpl()) {
                  return false;
               }

               var3 = this.get();
               if (var1 == null) {
                  break;
               }

               var4 = this.index.compareRows(var3, var1);
            } while(var4 < 0);

            if (var2 == null) {
               break;
            }

            var4 = this.index.compareRows(var3, var2);
         } while(var4 > 0);

         return true;
      }
   }

   private boolean nextImpl() {
      this.row = null;
      if (this.result != null && this.result.next()) {
         this.values = this.result.currentRow();
      } else {
         this.values = null;
      }

      return this.values != null;
   }

   public boolean previous() {
      throw DbException.getInternalError(this.toString());
   }
}
