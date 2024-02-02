/*     */ package org.h2.bnf.context;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.util.ParserUtil;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DbContents
/*     */ {
/*     */   private DbSchema[] schemas;
/*     */   private DbSchema defaultSchema;
/*     */   private boolean isOracle;
/*     */   private boolean isH2;
/*     */   private boolean isPostgreSQL;
/*     */   private boolean isDerby;
/*     */   private boolean isSQLite;
/*     */   private boolean isMySQL;
/*     */   private boolean isFirebird;
/*     */   private boolean isMSSQLServer;
/*     */   private boolean isDB2;
/*     */   private boolean databaseToUpper;
/*     */   private boolean databaseToLower;
/*     */   private boolean mayHaveStandardViews = true;
/*     */   
/*     */   public DbSchema getDefaultSchema() {
/*  46 */     return this.defaultSchema;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDerby() {
/*  53 */     return this.isDerby;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFirebird() {
/*  60 */     return this.isFirebird;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isH2() {
/*  67 */     return this.isH2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMSSQLServer() {
/*  74 */     return this.isMSSQLServer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMySQL() {
/*  81 */     return this.isMySQL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOracle() {
/*  88 */     return this.isOracle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPostgreSQL() {
/*  95 */     return this.isPostgreSQL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSQLite() {
/* 102 */     return this.isSQLite;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDB2() {
/* 109 */     return this.isDB2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DbSchema[] getSchemas() {
/* 116 */     return this.schemas;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean mayHaveStandardViews() {
/* 125 */     return this.mayHaveStandardViews;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMayHaveStandardViews(boolean paramBoolean) {
/* 134 */     this.mayHaveStandardViews = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void readContents(String paramString, Connection paramConnection) throws SQLException {
/* 146 */     this.isH2 = paramString.startsWith("jdbc:h2:");
/* 147 */     this.isDB2 = paramString.startsWith("jdbc:db2:");
/* 148 */     this.isSQLite = paramString.startsWith("jdbc:sqlite:");
/* 149 */     this.isOracle = paramString.startsWith("jdbc:oracle:");
/*     */     
/* 151 */     this.isPostgreSQL = (paramString.startsWith("jdbc:postgresql:") || paramString.startsWith("jdbc:vertica:"));
/*     */     
/* 153 */     this.isMySQL = paramString.startsWith("jdbc:mysql:");
/* 154 */     this.isDerby = paramString.startsWith("jdbc:derby:");
/* 155 */     this.isFirebird = paramString.startsWith("jdbc:firebirdsql:");
/* 156 */     this.isMSSQLServer = paramString.startsWith("jdbc:sqlserver:");
/* 157 */     if (this.isH2) {
/* 158 */       Session.StaticSettings staticSettings = ((JdbcConnection)paramConnection).getStaticSettings();
/* 159 */       this.databaseToUpper = staticSettings.databaseToUpper;
/* 160 */       this.databaseToLower = staticSettings.databaseToLower;
/* 161 */     } else if (this.isMySQL || this.isPostgreSQL) {
/* 162 */       this.databaseToUpper = false;
/* 163 */       this.databaseToLower = true;
/*     */     } else {
/* 165 */       this.databaseToUpper = true;
/* 166 */       this.databaseToLower = false;
/*     */     } 
/* 168 */     DatabaseMetaData databaseMetaData = paramConnection.getMetaData();
/* 169 */     String str = getDefaultSchemaName(databaseMetaData);
/* 170 */     String[] arrayOfString = getSchemaNames(databaseMetaData);
/* 171 */     this.schemas = new DbSchema[arrayOfString.length];
/* 172 */     for (byte b = 0; b < arrayOfString.length; b++) {
/* 173 */       String str1 = arrayOfString[b];
/*     */       
/* 175 */       boolean bool = (str == null || str.equals(str1)) ? true : false;
/* 176 */       DbSchema dbSchema = new DbSchema(this, str1, bool);
/* 177 */       if (bool) {
/* 178 */         this.defaultSchema = dbSchema;
/*     */       }
/* 180 */       this.schemas[b] = dbSchema;
/* 181 */       String[] arrayOfString1 = { "TABLE", "SYSTEM TABLE", "VIEW", "SYSTEM VIEW", "TABLE LINK", "SYNONYM", "EXTERNAL" };
/*     */       
/* 183 */       dbSchema.readTables(databaseMetaData, arrayOfString1);
/* 184 */       if (!this.isPostgreSQL && !this.isDB2) {
/* 185 */         dbSchema.readProcedures(databaseMetaData);
/*     */       }
/*     */     } 
/* 188 */     if (this.defaultSchema == null) {
/* 189 */       String str1 = null;
/* 190 */       for (DbSchema dbSchema : this.schemas) {
/* 191 */         if ("dbo".equals(dbSchema.name)) {
/*     */           
/* 193 */           this.defaultSchema = dbSchema;
/*     */           break;
/*     */         } 
/* 196 */         if (this.defaultSchema == null || str1 == null || dbSchema.name
/*     */           
/* 198 */           .length() < str1.length()) {
/* 199 */           str1 = dbSchema.name;
/* 200 */           this.defaultSchema = dbSchema;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private String[] getSchemaNames(DatabaseMetaData paramDatabaseMetaData) throws SQLException {
/* 207 */     if (this.isMySQL || this.isSQLite)
/* 208 */       return new String[] { "" }; 
/* 209 */     if (this.isFirebird) {
/* 210 */       return new String[] { null };
/*     */     }
/* 212 */     ResultSet resultSet = paramDatabaseMetaData.getSchemas();
/* 213 */     ArrayList<String> arrayList = Utils.newSmallArrayList();
/* 214 */     while (resultSet.next()) {
/* 215 */       String str = resultSet.getString("TABLE_SCHEM");
/* 216 */       String[] arrayOfString = null;
/* 217 */       if (this.isOracle) {
/* 218 */         arrayOfString = new String[] { "CTXSYS", "DIP", "DBSNMP", "DMSYS", "EXFSYS", "FLOWS_020100", "FLOWS_FILES", "MDDATA", "MDSYS", "MGMT_VIEW", "OLAPSYS", "ORDSYS", "ORDPLUGINS", "OUTLN", "SI_INFORMTN_SCHEMA", "SYS", "SYSMAN", "SYSTEM", "TSMSYS", "WMSYS", "XDB" };
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 223 */       else if (this.isMSSQLServer) {
/* 224 */         arrayOfString = new String[] { "sys", "db_accessadmin", "db_backupoperator", "db_datareader", "db_datawriter", "db_ddladmin", "db_denydatareader", "db_denydatawriter", "db_owner", "db_securityadmin" };
/*     */ 
/*     */       
/*     */       }
/* 228 */       else if (this.isDB2) {
/* 229 */         arrayOfString = new String[] { "NULLID", "SYSFUN", "SYSIBMINTERNAL", "SYSIBMTS", "SYSPROC", "SYSPUBLIC", "SYSCAT", "SYSIBM", "SYSIBMADM", "SYSSTAT", "SYSTOOLS" };
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 237 */       if (arrayOfString != null) {
/* 238 */         for (String str1 : arrayOfString) {
/* 239 */           if (str1.equals(str)) {
/* 240 */             str = null;
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       }
/* 245 */       if (str == null) {
/*     */         continue;
/*     */       }
/* 248 */       arrayList.add(str);
/*     */     } 
/* 250 */     resultSet.close();
/* 251 */     return arrayList.<String>toArray(new String[0]);
/*     */   }
/*     */   
/*     */   private String getDefaultSchemaName(DatabaseMetaData paramDatabaseMetaData) {
/* 255 */     String str = "";
/*     */     try {
/* 257 */       if (this.isH2)
/* 258 */         return paramDatabaseMetaData.storesLowerCaseIdentifiers() ? "public" : "PUBLIC"; 
/* 259 */       if (this.isOracle)
/* 260 */         return paramDatabaseMetaData.getUserName(); 
/* 261 */       if (this.isPostgreSQL)
/* 262 */         return "public"; 
/* 263 */       if (this.isMySQL)
/* 264 */         return ""; 
/* 265 */       if (this.isDerby)
/* 266 */         return StringUtils.toUpperEnglish(paramDatabaseMetaData.getUserName()); 
/* 267 */       if (this.isFirebird) {
/* 268 */         return null;
/*     */       }
/* 270 */     } catch (SQLException sQLException) {}
/*     */ 
/*     */     
/* 273 */     return str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String quoteIdentifier(String paramString) {
/* 283 */     if (paramString == null) {
/* 284 */       return null;
/*     */     }
/* 286 */     if (ParserUtil.isSimpleIdentifier(paramString, this.databaseToUpper, this.databaseToLower)) {
/* 287 */       return paramString;
/*     */     }
/* 289 */     return StringUtils.quoteIdentifier(paramString);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\context\DbContents.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */