package org.h2.index;

import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.value.Value;

class DualCursor implements Cursor {
   private Row currentRow;

   public Row get() {
      return this.currentRow;
   }

   public SearchRow getSearchRow() {
      return this.currentRow;
   }

   public boolean next() {
      if (this.currentRow == null) {
         this.currentRow = Row.get(Value.EMPTY_VALUES, 1);
         return true;
      } else {
         return false;
      }
   }

   public boolean previous() {
      throw DbException.getInternalError(this.toString());
   }
}
