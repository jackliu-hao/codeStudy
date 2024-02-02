package org.h2.mvstore.db;

import java.util.List;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.mvstore.MVMap;
import org.h2.result.Row;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.value.VersionedValue;

public abstract class MVIndex<K, V> extends Index {
   protected MVIndex(Table var1, int var2, String var3, IndexColumn[] var4, int var5, IndexType var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public abstract void addRowsToBuffer(List<Row> var1, String var2);

   public abstract void addBufferedRows(List<String> var1);

   public abstract MVMap<K, VersionedValue<V>> getMVMap();
}
