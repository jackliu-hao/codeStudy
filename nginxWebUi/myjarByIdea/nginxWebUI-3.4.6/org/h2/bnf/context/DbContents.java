package org.h2.bnf.context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.h2.engine.Session;
import org.h2.jdbc.JdbcConnection;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public class DbContents {
   private DbSchema[] schemas;
   private DbSchema defaultSchema;
   private boolean isOracle;
   private boolean isH2;
   private boolean isPostgreSQL;
   private boolean isDerby;
   private boolean isSQLite;
   private boolean isMySQL;
   private boolean isFirebird;
   private boolean isMSSQLServer;
   private boolean isDB2;
   private boolean databaseToUpper;
   private boolean databaseToLower;
   private boolean mayHaveStandardViews = true;

   public DbSchema getDefaultSchema() {
      return this.defaultSchema;
   }

   public boolean isDerby() {
      return this.isDerby;
   }

   public boolean isFirebird() {
      return this.isFirebird;
   }

   public boolean isH2() {
      return this.isH2;
   }

   public boolean isMSSQLServer() {
      return this.isMSSQLServer;
   }

   public boolean isMySQL() {
      return this.isMySQL;
   }

   public boolean isOracle() {
      return this.isOracle;
   }

   public boolean isPostgreSQL() {
      return this.isPostgreSQL;
   }

   public boolean isSQLite() {
      return this.isSQLite;
   }

   public boolean isDB2() {
      return this.isDB2;
   }

   public DbSchema[] getSchemas() {
      return this.schemas;
   }

   public boolean mayHaveStandardViews() {
      return this.mayHaveStandardViews;
   }

   public void setMayHaveStandardViews(boolean var1) {
      this.mayHaveStandardViews = var1;
   }

   public synchronized void readContents(String var1, Connection var2) throws SQLException {
      this.isH2 = var1.startsWith("jdbc:h2:");
      this.isDB2 = var1.startsWith("jdbc:db2:");
      this.isSQLite = var1.startsWith("jdbc:sqlite:");
      this.isOracle = var1.startsWith("jdbc:oracle:");
      this.isPostgreSQL = var1.startsWith("jdbc:postgresql:") || var1.startsWith("jdbc:vertica:");
      this.isMySQL = var1.startsWith("jdbc:mysql:");
      this.isDerby = var1.startsWith("jdbc:derby:");
      this.isFirebird = var1.startsWith("jdbc:firebirdsql:");
      this.isMSSQLServer = var1.startsWith("jdbc:sqlserver:");
      if (this.isH2) {
         Session.StaticSettings var3 = ((JdbcConnection)var2).getStaticSettings();
         this.databaseToUpper = var3.databaseToUpper;
         this.databaseToLower = var3.databaseToLower;
      } else if (!this.isMySQL && !this.isPostgreSQL) {
         this.databaseToUpper = true;
         this.databaseToLower = false;
      } else {
         this.databaseToUpper = false;
         this.databaseToLower = true;
      }

      DatabaseMetaData var11 = var2.getMetaData();
      String var4 = this.getDefaultSchemaName(var11);
      String[] var5 = this.getSchemaNames(var11);
      this.schemas = new DbSchema[var5.length];

      for(int var6 = 0; var6 < var5.length; ++var6) {
         String var7 = var5[var6];
         boolean var8 = var4 == null || var4.equals(var7);
         DbSchema var9 = new DbSchema(this, var7, var8);
         if (var8) {
            this.defaultSchema = var9;
         }

         this.schemas[var6] = var9;
         String[] var10 = new String[]{"TABLE", "SYSTEM TABLE", "VIEW", "SYSTEM VIEW", "TABLE LINK", "SYNONYM", "EXTERNAL"};
         var9.readTables(var11, var10);
         if (!this.isPostgreSQL && !this.isDB2) {
            var9.readProcedures(var11);
         }
      }

      if (this.defaultSchema == null) {
         String var12 = null;
         DbSchema[] var13 = this.schemas;
         int var14 = var13.length;

         for(int var15 = 0; var15 < var14; ++var15) {
            DbSchema var16 = var13[var15];
            if ("dbo".equals(var16.name)) {
               this.defaultSchema = var16;
               break;
            }

            if (this.defaultSchema == null || var12 == null || var16.name.length() < var12.length()) {
               var12 = var16.name;
               this.defaultSchema = var16;
            }
         }
      }

   }

   private String[] getSchemaNames(DatabaseMetaData var1) throws SQLException {
      if (!this.isMySQL && !this.isSQLite) {
         if (this.isFirebird) {
            return new String[]{null};
         } else {
            ResultSet var2 = var1.getSchemas();
            ArrayList var3 = Utils.newSmallArrayList();

            while(var2.next()) {
               String var4 = var2.getString("TABLE_SCHEM");
               String[] var5 = null;
               if (this.isOracle) {
                  var5 = new String[]{"CTXSYS", "DIP", "DBSNMP", "DMSYS", "EXFSYS", "FLOWS_020100", "FLOWS_FILES", "MDDATA", "MDSYS", "MGMT_VIEW", "OLAPSYS", "ORDSYS", "ORDPLUGINS", "OUTLN", "SI_INFORMTN_SCHEMA", "SYS", "SYSMAN", "SYSTEM", "TSMSYS", "WMSYS", "XDB"};
               } else if (this.isMSSQLServer) {
                  var5 = new String[]{"sys", "db_accessadmin", "db_backupoperator", "db_datareader", "db_datawriter", "db_ddladmin", "db_denydatareader", "db_denydatawriter", "db_owner", "db_securityadmin"};
               } else if (this.isDB2) {
                  var5 = new String[]{"NULLID", "SYSFUN", "SYSIBMINTERNAL", "SYSIBMTS", "SYSPROC", "SYSPUBLIC", "SYSCAT", "SYSIBM", "SYSIBMADM", "SYSSTAT", "SYSTOOLS"};
               }

               if (var5 != null) {
                  String[] var6 = var5;
                  int var7 = var5.length;

                  for(int var8 = 0; var8 < var7; ++var8) {
                     String var9 = var6[var8];
                     if (var9.equals(var4)) {
                        var4 = null;
                        break;
                     }
                  }
               }

               if (var4 != null) {
                  var3.add(var4);
               }
            }

            var2.close();
            return (String[])var3.toArray(new String[0]);
         }
      } else {
         return new String[]{""};
      }
   }

   private String getDefaultSchemaName(DatabaseMetaData var1) {
      String var2 = "";

      try {
         if (this.isH2) {
            return var1.storesLowerCaseIdentifiers() ? "public" : "PUBLIC";
         }

         if (this.isOracle) {
            return var1.getUserName();
         }

         if (this.isPostgreSQL) {
            return "public";
         }

         if (this.isMySQL) {
            return "";
         }

         if (this.isDerby) {
            return StringUtils.toUpperEnglish(var1.getUserName());
         }

         if (this.isFirebird) {
            return null;
         }
      } catch (SQLException var4) {
      }

      return var2;
   }

   public String quoteIdentifier(String var1) {
      if (var1 == null) {
         return null;
      } else {
         return ParserUtil.isSimpleIdentifier(var1, this.databaseToUpper, this.databaseToLower) ? var1 : StringUtils.quoteIdentifier(var1);
      }
   }
}
