package org.h2.table;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.index.MetaIndex;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.schema.Schema;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;
import org.h2.value.ValueVarcharIgnoreCase;

public abstract class MetaTable extends Table {
   public static final long ROW_COUNT_APPROXIMATION = 1000L;
   protected final int type;
   protected int indexColumn;
   protected MetaIndex metaIndex;

   protected MetaTable(Schema var1, int var2, int var3) {
      super(var1, var2, (String)null, true, true);
      this.type = var3;
   }

   protected final void setMetaTableName(String var1) {
      this.setObjectName(this.database.sysIdentifier(var1));
   }

   final Column column(String var1) {
      return new Column(this.database.sysIdentifier(var1), this.database.getSettings().caseInsensitiveIdentifiers ? TypeInfo.TYPE_VARCHAR_IGNORECASE : TypeInfo.TYPE_VARCHAR);
   }

   protected final Column column(String var1, TypeInfo var2) {
      return new Column(this.database.sysIdentifier(var1), var2);
   }

   public final String getCreateSQL() {
      return null;
   }

   public final Index addIndex(SessionLocal var1, String var2, int var3, IndexColumn[] var4, int var5, IndexType var6, boolean var7, String var8) {
      throw DbException.getUnsupportedException("META");
   }

   protected final String identifier(String var1) {
      if (this.database.getSettings().databaseToLower) {
         var1 = var1 == null ? null : StringUtils.toLowerEnglish(var1);
      }

      return var1;
   }

   protected final boolean checkIndex(SessionLocal var1, String var2, Value var3, Value var4) {
      if (var2 != null && (var3 != null || var4 != null)) {
         Object var5;
         if (this.database.getSettings().caseInsensitiveIdentifiers) {
            var5 = ValueVarcharIgnoreCase.get(var2);
         } else {
            var5 = ValueVarchar.get(var2);
         }

         if (var3 != null && var1.compare((Value)var5, var3) < 0) {
            return false;
         } else {
            return var4 == null || var1.compare((Value)var5, var4) <= 0;
         }
      } else {
         return true;
      }
   }

   protected final boolean hideTable(Table var1, SessionLocal var2) {
      return var1.isHidden() && var2 != this.database.getSystemSession();
   }

   public abstract ArrayList<Row> generateRows(SessionLocal var1, SearchRow var2, SearchRow var3);

   public boolean isInsertable() {
      return false;
   }

   public final void removeRow(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("META");
   }

   public final void addRow(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("META");
   }

   public final void removeChildrenAndResources(SessionLocal var1) {
      throw DbException.getUnsupportedException("META");
   }

   public final void close(SessionLocal var1) {
   }

   protected final void add(SessionLocal var1, ArrayList<Row> var2, Object... var3) {
      Value[] var4 = new Value[var3.length];

      for(int var5 = 0; var5 < var3.length; ++var5) {
         Object var6 = var3[var5];
         Object var7 = var6 == null ? ValueNull.INSTANCE : (var6 instanceof String ? ValueVarchar.get((String)var6) : (Value)var6);
         var4[var5] = this.columns[var5].convert(var1, (Value)var7);
      }

      var2.add(Row.get(var4, 1, (long)var2.size()));
   }

   public final void checkRename() {
      throw DbException.getUnsupportedException("META");
   }

   public final void checkSupportAlter() {
      throw DbException.getUnsupportedException("META");
   }

   public final long truncate(SessionLocal var1) {
      throw DbException.getUnsupportedException("META");
   }

   public long getRowCount(SessionLocal var1) {
      throw DbException.getInternalError(this.toString());
   }

   public boolean canGetRowCount(SessionLocal var1) {
      return false;
   }

   public final boolean canDrop() {
      return false;
   }

   public final TableType getTableType() {
      return TableType.SYSTEM_TABLE;
   }

   public final Index getScanIndex(SessionLocal var1) {
      return new MetaIndex(this, IndexColumn.wrap(this.columns), true);
   }

   public final ArrayList<Index> getIndexes() {
      ArrayList var1 = new ArrayList(2);
      if (this.metaIndex == null) {
         return var1;
      } else {
         var1.add(new MetaIndex(this, IndexColumn.wrap(this.columns), true));
         var1.add(this.metaIndex);
         return var1;
      }
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return 1000L;
   }

   public final boolean isDeterministic() {
      return true;
   }

   public final boolean canReference() {
      return false;
   }
}
