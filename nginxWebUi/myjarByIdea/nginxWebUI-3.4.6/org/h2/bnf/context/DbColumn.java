package org.h2.bnf.context;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbColumn {
   private final String name;
   private final String quotedName;
   private final String dataType;
   private final int position;

   private DbColumn(DbContents var1, ResultSet var2, boolean var3) throws SQLException {
      this.name = var2.getString("COLUMN_NAME");
      this.quotedName = var1.quoteIdentifier(this.name);
      this.position = var2.getInt("ORDINAL_POSITION");
      if (var1.isH2() && !var3) {
         this.dataType = var2.getString("COLUMN_TYPE");
      } else {
         String var4 = var2.getString("TYPE_NAME");
         String var5;
         String var6;
         if (var3) {
            var5 = "PRECISION";
            var6 = "SCALE";
         } else {
            var5 = "COLUMN_SIZE";
            var6 = "DECIMAL_DIGITS";
         }

         int var7 = var2.getInt(var5);
         if (var7 > 0 && !var1.isSQLite()) {
            int var8 = var2.getInt(var6);
            if (var8 > 0) {
               var4 = var4 + '(' + var7 + ", " + var8 + ')';
            } else {
               var4 = var4 + '(' + var7 + ')';
            }
         }

         if (var2.getInt("NULLABLE") == 0) {
            var4 = var4 + " NOT NULL";
         }

         this.dataType = var4;
      }
   }

   public static DbColumn getProcedureColumn(DbContents var0, ResultSet var1) throws SQLException {
      return new DbColumn(var0, var1, true);
   }

   public static DbColumn getColumn(DbContents var0, ResultSet var1) throws SQLException {
      return new DbColumn(var0, var1, false);
   }

   public String getDataType() {
      return this.dataType;
   }

   public String getName() {
      return this.name;
   }

   public String getQuotedName() {
      return this.quotedName;
   }

   public int getPosition() {
      return this.position;
   }
}
