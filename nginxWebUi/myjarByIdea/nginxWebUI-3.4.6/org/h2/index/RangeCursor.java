package org.h2.index;

import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.value.Value;
import org.h2.value.ValueBigint;

class RangeCursor implements Cursor {
   private boolean beforeFirst;
   private long current;
   private Row currentRow;
   private final long start;
   private final long end;
   private final long step;

   RangeCursor(long var1, long var3, long var5) {
      this.start = var1;
      this.end = var3;
      this.step = var5;
      this.beforeFirst = true;
   }

   public Row get() {
      return this.currentRow;
   }

   public SearchRow getSearchRow() {
      return this.currentRow;
   }

   public boolean next() {
      if (this.beforeFirst) {
         this.beforeFirst = false;
         this.current = this.start;
      } else {
         this.current += this.step;
      }

      this.currentRow = Row.get(new Value[]{ValueBigint.get(this.current)}, 1);
      return this.step > 0L ? this.current <= this.end : this.current >= this.end;
   }

   public boolean previous() {
      throw DbException.getInternalError(this.toString());
   }
}
