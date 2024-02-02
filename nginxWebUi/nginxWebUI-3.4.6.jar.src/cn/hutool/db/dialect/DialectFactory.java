/*     */ package cn.hutool.db.dialect;
/*     */ 
/*     */ import cn.hutool.core.util.ClassLoaderUtil;
/*     */ import cn.hutool.core.util.ReUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.dialect.impl.AnsiSqlDialect;
/*     */ import cn.hutool.db.dialect.impl.H2Dialect;
/*     */ import cn.hutool.db.dialect.impl.MysqlDialect;
/*     */ import cn.hutool.db.dialect.impl.OracleDialect;
/*     */ import cn.hutool.db.dialect.impl.PhoenixDialect;
/*     */ import cn.hutool.db.dialect.impl.PostgresqlDialect;
/*     */ import cn.hutool.db.dialect.impl.SqlServer2012Dialect;
/*     */ import cn.hutool.db.dialect.impl.Sqlite3Dialect;
/*     */ import cn.hutool.log.StaticLog;
/*     */ import java.sql.Connection;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.sql.DataSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DialectFactory
/*     */   implements DriverNamePool
/*     */ {
/*  29 */   private static final Map<DataSource, Dialect> DIALECT_POOL = new ConcurrentHashMap<>();
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
/*     */   public static Dialect newDialect(String driverName) {
/*  42 */     Dialect dialect = internalNewDialect(driverName);
/*  43 */     StaticLog.debug("Use Dialect: [{}].", new Object[] { dialect.getClass().getSimpleName() });
/*  44 */     return dialect;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Dialect internalNewDialect(String driverName) {
/*  55 */     if (StrUtil.isNotBlank(driverName)) {
/*  56 */       if ("com.mysql.jdbc.Driver".equalsIgnoreCase(driverName) || "com.mysql.cj.jdbc.Driver".equalsIgnoreCase(driverName))
/*  57 */         return (Dialect)new MysqlDialect(); 
/*  58 */       if ("oracle.jdbc.OracleDriver".equalsIgnoreCase(driverName) || "oracle.jdbc.driver.OracleDriver".equalsIgnoreCase(driverName))
/*  59 */         return (Dialect)new OracleDialect(); 
/*  60 */       if ("org.sqlite.JDBC".equalsIgnoreCase(driverName))
/*  61 */         return (Dialect)new Sqlite3Dialect(); 
/*  62 */       if ("org.postgresql.Driver".equalsIgnoreCase(driverName))
/*  63 */         return (Dialect)new PostgresqlDialect(); 
/*  64 */       if ("org.h2.Driver".equalsIgnoreCase(driverName))
/*  65 */         return (Dialect)new H2Dialect(); 
/*  66 */       if ("com.microsoft.sqlserver.jdbc.SQLServerDriver".equalsIgnoreCase(driverName))
/*  67 */         return (Dialect)new SqlServer2012Dialect(); 
/*  68 */       if ("org.apache.phoenix.jdbc.PhoenixDriver".equalsIgnoreCase(driverName)) {
/*  69 */         return (Dialect)new PhoenixDialect();
/*     */       }
/*     */     } 
/*     */     
/*  73 */     return (Dialect)new AnsiSqlDialect();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String identifyDriver(String nameContainsProductInfo) {
/*  83 */     if (StrUtil.isBlank(nameContainsProductInfo)) {
/*  84 */       return null;
/*     */     }
/*     */     
/*  87 */     nameContainsProductInfo = StrUtil.cleanBlank(nameContainsProductInfo.toLowerCase());
/*     */ 
/*     */     
/*  90 */     String name = ReUtil.getGroup1("jdbc:(.*?):", nameContainsProductInfo);
/*  91 */     if (StrUtil.isNotBlank(name)) {
/*  92 */       nameContainsProductInfo = name;
/*     */     }
/*     */     
/*  95 */     String driver = null;
/*  96 */     if (nameContainsProductInfo.contains("mysql") || nameContainsProductInfo.contains("cobar")) {
/*  97 */       driver = ClassLoaderUtil.isPresent("com.mysql.cj.jdbc.Driver") ? "com.mysql.cj.jdbc.Driver" : "com.mysql.jdbc.Driver";
/*  98 */     } else if (nameContainsProductInfo.contains("oracle")) {
/*  99 */       driver = ClassLoaderUtil.isPresent("oracle.jdbc.OracleDriver") ? "oracle.jdbc.OracleDriver" : "oracle.jdbc.driver.OracleDriver";
/* 100 */     } else if (nameContainsProductInfo.contains("postgresql")) {
/* 101 */       driver = "org.postgresql.Driver";
/* 102 */     } else if (nameContainsProductInfo.contains("sqlite")) {
/* 103 */       driver = "org.sqlite.JDBC";
/* 104 */     } else if (nameContainsProductInfo.contains("sqlserver") || nameContainsProductInfo.contains("microsoft")) {
/* 105 */       driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
/* 106 */     } else if (nameContainsProductInfo.contains("hive")) {
/* 107 */       driver = "org.apache.hadoop.hive.jdbc.HiveDriver";
/* 108 */     } else if (nameContainsProductInfo.contains("h2")) {
/* 109 */       driver = "org.h2.Driver";
/* 110 */     } else if (nameContainsProductInfo.contains("derby")) {
/*     */       
/* 112 */       driver = "org.apache.derby.jdbc.AutoloadedDriver";
/* 113 */     } else if (nameContainsProductInfo.contains("hsqldb")) {
/*     */       
/* 115 */       driver = "org.hsqldb.jdbc.JDBCDriver";
/* 116 */     } else if (nameContainsProductInfo.contains("dm")) {
/*     */       
/* 118 */       driver = "dm.jdbc.driver.DmDriver";
/* 119 */     } else if (nameContainsProductInfo.contains("kingbase8")) {
/*     */       
/* 121 */       driver = "com.kingbase8.Driver";
/* 122 */     } else if (nameContainsProductInfo.contains("ignite")) {
/*     */       
/* 124 */       driver = "org.apache.ignite.IgniteJdbcThinDriver";
/* 125 */     } else if (nameContainsProductInfo.contains("clickhouse")) {
/*     */       
/* 127 */       driver = "ru.yandex.clickhouse.ClickHouseDriver";
/* 128 */     } else if (nameContainsProductInfo.contains("highgo")) {
/*     */       
/* 130 */       driver = "com.highgo.jdbc.Driver";
/* 131 */     } else if (nameContainsProductInfo.contains("db2")) {
/*     */       
/* 133 */       driver = "com.ibm.db2.jdbc.app.DB2Driver";
/* 134 */     } else if (nameContainsProductInfo.contains("xugu")) {
/*     */       
/* 136 */       driver = "com.xugu.cloudjdbc.Driver";
/* 137 */     } else if (nameContainsProductInfo.contains("phoenix")) {
/*     */       
/* 139 */       driver = "org.apache.phoenix.jdbc.PhoenixDriver";
/* 140 */     } else if (nameContainsProductInfo.contains("zenith")) {
/*     */       
/* 142 */       driver = "com.huawei.gauss.jdbc.ZenithDriver";
/* 143 */     } else if (nameContainsProductInfo.contains("gbase")) {
/*     */       
/* 145 */       driver = "com.gbase.jdbc.Driver";
/* 146 */     } else if (nameContainsProductInfo.contains("oscar")) {
/*     */       
/* 148 */       driver = "com.oscar.Driver";
/* 149 */     } else if (nameContainsProductInfo.contains("sybase")) {
/*     */       
/* 151 */       driver = "com.sybase.jdbc4.jdbc.SybDriver";
/* 152 */     } else if (nameContainsProductInfo.contains("xugu")) {
/*     */       
/* 154 */       driver = "com.xugu.cloudjdbc.Driver";
/*     */     } 
/*     */     
/* 157 */     return driver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Dialect getDialect(DataSource ds) {
/* 166 */     Dialect dialect = DIALECT_POOL.get(ds);
/* 167 */     if (null == dialect)
/*     */     {
/*     */       
/* 170 */       synchronized (ds) {
/* 171 */         dialect = DIALECT_POOL.get(ds);
/* 172 */         if (null == dialect) {
/* 173 */           dialect = newDialect(ds);
/* 174 */           DIALECT_POOL.put(ds, dialect);
/*     */         } 
/*     */       } 
/*     */     }
/* 178 */     return dialect;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Dialect newDialect(DataSource ds) {
/* 188 */     return newDialect(DriverUtil.identifyDriver(ds));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Dialect newDialect(Connection conn) {
/* 198 */     return newDialect(DriverUtil.identifyDriver(conn));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\dialect\DialectFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */