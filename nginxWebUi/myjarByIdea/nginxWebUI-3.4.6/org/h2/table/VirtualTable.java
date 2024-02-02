package org.h2.table;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.schema.Schema;

public abstract class VirtualTable extends Table {
   protected VirtualTable(Schema var1, int var2, String var3) {
      super(var1, var2, var3, false, true);
   }

   public void close(SessionLocal var1) {
   }

   public Index addIndex(SessionLocal var1, String var2, int var3, IndexColumn[] var4, int var5, IndexType var6, boolean var7, String var8) {
      throw DbException.getUnsupportedException("Virtual table");
   }

   public boolean isInsertable() {
      return false;
   }

   public void removeRow(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("Virtual table");
   }

   public long truncate(SessionLocal var1) {
      throw DbException.getUnsupportedException("Virtual table");
   }

   public void addRow(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("Virtual table");
   }

   public void checkSupportAlter() {
      throw DbException.getUnsupportedException("Virtual table");
   }

   public TableType getTableType() {
      return null;
   }

   public ArrayList<Index> getIndexes() {
      return null;
   }

   public boolean canReference() {
      return false;
   }

   public boolean canDrop() {
      throw DbException.getInternalError(this.toString());
   }

   public String getCreateSQL() {
      return null;
   }

   public void checkRename() {
      throw DbException.getUnsupportedException("Virtual table");
   }
}
