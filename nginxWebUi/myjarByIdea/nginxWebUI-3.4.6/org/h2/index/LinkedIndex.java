package org.h2.index;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.table.TableLink;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public class LinkedIndex extends Index {
   private final TableLink link;
   private final String targetTableName;
   private long rowCount;
   private final int sqlFlags = 1;

   public LinkedIndex(TableLink var1, int var2, IndexColumn[] var3, int var4, IndexType var5) {
      super(var1, var2, (String)null, var3, var4, var5);
      this.link = var1;
      this.targetTableName = this.link.getQualifiedTable();
   }

   public String getCreateSQL() {
      return null;
   }

   public void close(SessionLocal var1) {
   }

   private static boolean isNull(Value var0) {
      return var0 == null || var0 == ValueNull.INSTANCE;
   }

   public void add(SessionLocal var1, Row var2) {
      ArrayList var3 = Utils.newSmallArrayList();
      StringBuilder var4 = new StringBuilder("INSERT INTO ");
      var4.append(this.targetTableName).append(" VALUES(");

      for(int var5 = 0; var5 < var2.getColumnCount(); ++var5) {
         Value var6 = var2.getValue(var5);
         if (var5 > 0) {
            var4.append(", ");
         }

         if (var6 == null) {
            var4.append("DEFAULT");
         } else if (isNull(var6)) {
            var4.append("NULL");
         } else {
            var4.append('?');
            var3.add(var6);
         }
      }

      var4.append(')');
      String var8 = var4.toString();

      try {
         this.link.execute(var8, var3, true, var1);
         ++this.rowCount;
      } catch (Exception var7) {
         throw TableLink.wrapException(var8, var7);
      }
   }

   public Cursor find(SessionLocal var1, SearchRow var2, SearchRow var3) {
      ArrayList var4 = Utils.newSmallArrayList();
      StringBuilder var5 = (new StringBuilder("SELECT * FROM ")).append(this.targetTableName).append(" T");
      boolean var6 = false;

      int var7;
      Value var8;
      Column var9;
      for(var7 = 0; var2 != null && var7 < var2.getColumnCount(); ++var7) {
         var8 = var2.getValue(var7);
         if (var8 != null) {
            var5.append(var6 ? " AND " : " WHERE ");
            var6 = true;
            var9 = this.table.getColumn(var7);
            var9.getSQL(var5, 1);
            if (var8 == ValueNull.INSTANCE) {
               var5.append(" IS NULL");
            } else {
               var5.append(">=");
               this.addParameter(var5, var9);
               var4.add(var8);
            }
         }
      }

      for(var7 = 0; var3 != null && var7 < var3.getColumnCount(); ++var7) {
         var8 = var3.getValue(var7);
         if (var8 != null) {
            var5.append(var6 ? " AND " : " WHERE ");
            var6 = true;
            var9 = this.table.getColumn(var7);
            var9.getSQL(var5, 1);
            if (var8 == ValueNull.INSTANCE) {
               var5.append(" IS NULL");
            } else {
               var5.append("<=");
               this.addParameter(var5, var9);
               var4.add(var8);
            }
         }
      }

      String var11 = var5.toString();

      try {
         PreparedStatement var12 = this.link.execute(var11, var4, false, var1);
         ResultSet var13 = var12.getResultSet();
         return new LinkedCursor(this.link, var13, var1, var11, var12);
      } catch (Exception var10) {
         throw TableLink.wrapException(var11, var10);
      }
   }

   private void addParameter(StringBuilder var1, Column var2) {
      TypeInfo var3 = var2.getType();
      if (var3.getValueType() == 1 && this.link.isOracle()) {
         var1.append("CAST(? AS CHAR(").append(var3.getPrecision()).append("))");
      } else {
         var1.append('?');
      }

   }

   public double getCost(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      return (double)(100L + this.getCostRangeIndex(var2, this.rowCount + 1000L, var3, var4, var5, false, var6));
   }

   public void remove(SessionLocal var1) {
   }

   public void truncate(SessionLocal var1) {
   }

   public void checkRename() {
      throw DbException.getUnsupportedException("LINKED");
   }

   public boolean needRebuild() {
      return false;
   }

   public void remove(SessionLocal var1, Row var2) {
      ArrayList var3 = Utils.newSmallArrayList();
      StringBuilder var4 = (new StringBuilder("DELETE FROM ")).append(this.targetTableName).append(" WHERE ");

      for(int var5 = 0; var5 < var2.getColumnCount(); ++var5) {
         if (var5 > 0) {
            var4.append("AND ");
         }

         Column var6 = this.table.getColumn(var5);
         var6.getSQL(var4, 1);
         Value var7 = var2.getValue(var5);
         if (isNull(var7)) {
            var4.append(" IS NULL ");
         } else {
            var4.append('=');
            this.addParameter(var4, var6);
            var3.add(var7);
            var4.append(' ');
         }
      }

      String var9 = var4.toString();

      try {
         PreparedStatement var10 = this.link.execute(var9, var3, false, var1);
         int var11 = var10.executeUpdate();
         this.link.reusePreparedStatement(var10, var9);
         this.rowCount -= (long)var11;
      } catch (Exception var8) {
         throw TableLink.wrapException(var9, var8);
      }
   }

   public void update(Row var1, Row var2, SessionLocal var3) {
      ArrayList var4 = Utils.newSmallArrayList();
      StringBuilder var5 = (new StringBuilder("UPDATE ")).append(this.targetTableName).append(" SET ");

      int var6;
      for(var6 = 0; var6 < var2.getColumnCount(); ++var6) {
         if (var6 > 0) {
            var5.append(", ");
         }

         this.table.getColumn(var6).getSQL(var5, 1).append('=');
         Value var7 = var2.getValue(var6);
         if (var7 == null) {
            var5.append("DEFAULT");
         } else {
            var5.append('?');
            var4.add(var7);
         }
      }

      var5.append(" WHERE ");

      for(var6 = 0; var6 < var1.getColumnCount(); ++var6) {
         Column var11 = this.table.getColumn(var6);
         if (var6 > 0) {
            var5.append(" AND ");
         }

         var11.getSQL(var5, 1);
         Value var8 = var1.getValue(var6);
         if (isNull(var8)) {
            var5.append(" IS NULL");
         } else {
            var5.append('=');
            var4.add(var8);
            this.addParameter(var5, var11);
         }
      }

      String var10 = var5.toString();

      try {
         this.link.execute(var10, var4, true, var3);
      } catch (Exception var9) {
         throw TableLink.wrapException(var10, var9);
      }
   }

   public long getRowCount(SessionLocal var1) {
      return this.rowCount;
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return this.rowCount;
   }
}
