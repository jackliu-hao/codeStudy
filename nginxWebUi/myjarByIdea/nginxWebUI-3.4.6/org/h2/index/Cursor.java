package org.h2.index;

import org.h2.result.Row;
import org.h2.result.SearchRow;

public interface Cursor {
   Row get();

   SearchRow getSearchRow();

   boolean next();

   boolean previous();
}
