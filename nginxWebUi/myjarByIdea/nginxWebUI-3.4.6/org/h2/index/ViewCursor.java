package org.h2.index;

import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.table.Table;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public class ViewCursor implements Cursor {
   private final Table table;
   private final ViewIndex index;
   private final ResultInterface result;
   private final SearchRow first;
   private final SearchRow last;
   private Row current;

   public ViewCursor(ViewIndex var1, ResultInterface var2, SearchRow var3, SearchRow var4) {
      this.table = var1.getTable();
      this.index = var1;
      this.result = var2;
      this.first = var3;
      this.last = var4;
   }

   public Row get() {
      return this.current;
   }

   public SearchRow getSearchRow() {
      return this.current;
   }

   public boolean next() {
      while(true) {
         boolean var1 = this.result.next();
         if (!var1) {
            if (this.index.isRecursive()) {
               this.result.reset();
            } else {
               this.result.close();
            }

            this.current = null;
            return false;
         }

         this.current = this.table.getTemplateRow();
         Value[] var2 = this.result.currentRow();
         int var3 = 0;

         for(int var4 = this.current.getColumnCount(); var3 < var4; ++var3) {
            Object var5 = var3 < var2.length ? var2[var3] : ValueNull.INSTANCE;
            this.current.setValue(var3, (Value)var5);
         }

         if (this.first != null) {
            var3 = this.index.compareRows(this.current, this.first);
            if (var3 < 0) {
               continue;
            }
         }

         if (this.last != null) {
            var3 = this.index.compareRows(this.current, this.last);
            if (var3 > 0) {
               continue;
            }
         }

         return true;
      }
   }

   public boolean previous() {
      throw DbException.getInternalError(this.toString());
   }
}
