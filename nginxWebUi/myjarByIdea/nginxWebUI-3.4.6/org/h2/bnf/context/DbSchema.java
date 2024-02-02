package org.h2.bnf.context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import org.h2.engine.SysProperties;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public class DbSchema {
   private static final String COLUMNS_QUERY_H2_197 = "SELECT COLUMN_NAME, ORDINAL_POSITION, COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ?1 AND TABLE_NAME = ?2";
   private static final String COLUMNS_QUERY_H2_202 = "SELECT COLUMN_NAME, ORDINAL_POSITION, DATA_TYPE_SQL(?1, ?2, 'TABLE', ORDINAL_POSITION) COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ?1 AND TABLE_NAME = ?2";
   public final String name;
   public final boolean isDefault;
   public final boolean isSystem;
   public final String quotedName;
   private final DbContents contents;
   private DbTableOrView[] tables;
   private DbProcedure[] procedures;

   DbSchema(DbContents var1, String var2, boolean var3) {
      this.contents = var1;
      this.name = var2;
      this.quotedName = var1.quoteIdentifier(var2);
      this.isDefault = var3;
      if (var2 == null) {
         this.isSystem = true;
      } else if ("INFORMATION_SCHEMA".equalsIgnoreCase(var2)) {
         this.isSystem = true;
      } else if (!var1.isH2() && StringUtils.toUpperEnglish(var2).startsWith("INFO")) {
         this.isSystem = true;
      } else if (var1.isPostgreSQL() && StringUtils.toUpperEnglish(var2).startsWith("PG_")) {
         this.isSystem = true;
      } else if (var1.isDerby() && var2.startsWith("SYS")) {
         this.isSystem = true;
      } else {
         this.isSystem = false;
      }

   }

   public DbContents getContents() {
      return this.contents;
   }

   public DbTableOrView[] getTables() {
      return this.tables;
   }

   public DbProcedure[] getProcedures() {
      return this.procedures;
   }

   public void readTables(DatabaseMetaData var1, String[] var2) throws SQLException {
      ResultSet var3 = var1.getTables((String)null, this.name, (String)null, var2);
      ArrayList var4 = new ArrayList();

      while(true) {
         DbTableOrView var5;
         do {
            if (!var3.next()) {
               var3.close();
               this.tables = (DbTableOrView[])var4.toArray(new DbTableOrView[0]);
               if (this.tables.length < SysProperties.CONSOLE_MAX_TABLES_LIST_COLUMNS) {
                  PreparedStatement var23 = this.contents.isH2() ? prepareColumnsQueryH2(var1.getConnection()) : null;
                  Throwable var6 = null;

                  try {
                     DbTableOrView[] var7 = this.tables;
                     int var8 = var7.length;

                     for(int var9 = 0; var9 < var8; ++var9) {
                        DbTableOrView var10 = var7[var9];

                        try {
                           var10.readColumns(var1, var23);
                        } catch (SQLException var20) {
                        }
                     }
                  } catch (Throwable var21) {
                     var6 = var21;
                     throw var21;
                  } finally {
                     if (var23 != null) {
                        if (var6 != null) {
                           try {
                              var23.close();
                           } catch (Throwable var19) {
                              var6.addSuppressed(var19);
                           }
                        } else {
                           var23.close();
                        }
                     }

                  }
               }

               return;
            }

            var5 = new DbTableOrView(this, var3);
         } while(this.contents.isOracle() && var5.getName().indexOf(36) > 0);

         var4.add(var5);
      }
   }

   private static PreparedStatement prepareColumnsQueryH2(Connection var0) throws SQLException {
      try {
         return var0.prepareStatement("SELECT COLUMN_NAME, ORDINAL_POSITION, DATA_TYPE_SQL(?1, ?2, 'TABLE', ORDINAL_POSITION) COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ?1 AND TABLE_NAME = ?2");
      } catch (SQLSyntaxErrorException var2) {
         return var0.prepareStatement("SELECT COLUMN_NAME, ORDINAL_POSITION, COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ?1 AND TABLE_NAME = ?2");
      }
   }

   public void readProcedures(DatabaseMetaData var1) throws SQLException {
      ResultSet var2 = var1.getProcedures((String)null, this.name, (String)null);
      ArrayList var3 = Utils.newSmallArrayList();

      while(var2.next()) {
         var3.add(new DbProcedure(this, var2));
      }

      var2.close();
      this.procedures = (DbProcedure[])var3.toArray(new DbProcedure[0]);
      if (this.procedures.length < SysProperties.CONSOLE_MAX_PROCEDURES_LIST_COLUMNS) {
         DbProcedure[] var4 = this.procedures;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            DbProcedure var7 = var4[var6];
            var7.readParameters(var1);
         }
      }

   }
}
