package org.h2.index;

import java.util.ArrayList;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;

public class MetaCursor implements Cursor {
   private Row current;
   private final ArrayList<Row> rows;
   private int index;

   MetaCursor(ArrayList<Row> var1) {
      this.rows = var1;
   }

   public Row get() {
      return this.current;
   }

   public SearchRow getSearchRow() {
      return this.current;
   }

   public boolean next() {
      this.current = this.index >= this.rows.size() ? null : (Row)this.rows.get(this.index++);
      return this.current != null;
   }

   public boolean previous() {
      throw DbException.getInternalError(this.toString());
   }
}
