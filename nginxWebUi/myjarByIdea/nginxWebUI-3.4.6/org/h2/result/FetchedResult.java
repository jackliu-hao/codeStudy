package org.h2.result;

import org.h2.engine.Session;
import org.h2.value.Value;

public abstract class FetchedResult implements ResultInterface {
   long rowId = -1L;
   Value[] currentRow;
   Value[] nextRow;
   boolean afterLast;

   FetchedResult() {
   }

   public final Value[] currentRow() {
      return this.currentRow;
   }

   public final boolean next() {
      if (this.hasNext()) {
         ++this.rowId;
         this.currentRow = this.nextRow;
         this.nextRow = null;
         return true;
      } else {
         if (!this.afterLast) {
            ++this.rowId;
            this.currentRow = null;
            this.afterLast = true;
         }

         return false;
      }
   }

   public final boolean isAfterLast() {
      return this.afterLast;
   }

   public final long getRowId() {
      return this.rowId;
   }

   public final boolean needToClose() {
      return true;
   }

   public final ResultInterface createShallowCopy(Session var1) {
      return null;
   }
}
