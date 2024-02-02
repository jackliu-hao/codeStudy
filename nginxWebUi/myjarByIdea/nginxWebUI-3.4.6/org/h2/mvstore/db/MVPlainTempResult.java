package org.h2.mvstore.db;

import org.h2.engine.Database;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.mvstore.Cursor;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.type.LongDataType;
import org.h2.result.ResultExternal;
import org.h2.result.RowFactory;
import org.h2.table.IndexColumn;
import org.h2.value.Value;
import org.h2.value.ValueRow;

class MVPlainTempResult extends MVTempResult {
   private final MVMap<Long, ValueRow> map;
   private long counter;
   private Cursor<Long, ValueRow> cursor;

   private MVPlainTempResult(MVPlainTempResult var1) {
      super(var1);
      this.map = var1.map;
   }

   MVPlainTempResult(Database var1, Expression[] var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      ValueDataType var5 = new ValueDataType(var1, new int[var4]);
      var5.setRowFactory(RowFactory.DefaultRowFactory.INSTANCE.createRowFactory(var1, var1.getCompareMode(), var1, var2, (IndexColumn[])null, false));
      MVMap.Builder var6 = (new MVMap.Builder()).keyType(LongDataType.INSTANCE).valueType(var5).singleWriter();
      this.map = this.store.openMap("tmp", var6);
   }

   public int addRow(Value[] var1) {
      assert this.parent == null;

      this.map.append(Long.valueOf((long)(this.counter++)), ValueRow.get(var1));
      return ++this.rowCount;
   }

   public boolean contains(Value[] var1) {
      throw DbException.getUnsupportedException("contains()");
   }

   public synchronized ResultExternal createShallowCopy() {
      if (this.parent != null) {
         return this.parent.createShallowCopy();
      } else if (this.closed) {
         return null;
      } else {
         ++this.childCount;
         return new MVPlainTempResult(this);
      }
   }

   public Value[] next() {
      if (this.cursor == null) {
         this.cursor = this.map.cursor((Object)null);
      }

      if (!this.cursor.hasNext()) {
         return null;
      } else {
         this.cursor.next();
         return ((ValueRow)this.cursor.getValue()).getList();
      }
   }

   public int removeRow(Value[] var1) {
      throw DbException.getUnsupportedException("removeRow()");
   }

   public void reset() {
      this.cursor = null;
   }
}
