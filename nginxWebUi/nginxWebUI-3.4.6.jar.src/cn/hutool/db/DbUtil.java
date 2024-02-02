/*     */ package cn.hutool.db;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.db.dialect.Dialect;
/*     */ import cn.hutool.db.dialect.DialectFactory;
/*     */ import cn.hutool.db.ds.DSFactory;
/*     */ import cn.hutool.log.Log;
/*     */ import cn.hutool.log.level.Level;
/*     */ import cn.hutool.setting.Setting;
/*     */ import java.sql.Connection;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.NamingException;
/*     */ import javax.sql.DataSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class DbUtil
/*     */ {
/*  24 */   private static final Log log = Log.get();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SqlConnRunner newSqlConnRunner(Dialect dialect) {
/*  33 */     return SqlConnRunner.create(dialect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SqlConnRunner newSqlConnRunner(DataSource ds) {
/*  43 */     return SqlConnRunner.create(ds);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SqlConnRunner newSqlConnRunner(Connection conn) {
/*  53 */     return SqlConnRunner.create(DialectFactory.newDialect(conn));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Db use() {
/*  62 */     return Db.use();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Db use(DataSource ds) {
/*  72 */     return Db.use(ds);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Db use(DataSource ds, Dialect dialect) {
/*  83 */     return Db.use(ds, dialect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Session newSession() {
/*  92 */     return Session.create(getDs());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Session newSession(DataSource ds) {
/* 102 */     return Session.create(ds);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void close(Object... objsToClose) {
/* 112 */     for (Object obj : objsToClose) {
/* 113 */       if (null != obj) {
/* 114 */         if (obj instanceof AutoCloseable) {
/* 115 */           IoUtil.close((AutoCloseable)obj);
/*     */         } else {
/* 117 */           log.warn("Object {} not a ResultSet or Statement or PreparedStatement or Connection!", new Object[] { obj.getClass().getName() });
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataSource getDs() {
/* 129 */     return DSFactory.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataSource getDs(String group) {
/* 139 */     return DSFactory.get(group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataSource getJndiDsWithLog(String jndiName) {
/*     */     try {
/* 150 */       return getJndiDs(jndiName);
/* 151 */     } catch (DbRuntimeException e) {
/* 152 */       log.error(e.getCause(), "Find JNDI datasource error!", new Object[0]);
/*     */       
/* 154 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataSource getJndiDs(String jndiName) {
/*     */     try {
/* 165 */       return (DataSource)(new InitialContext()).lookup(jndiName);
/* 166 */     } catch (NamingException e) {
/* 167 */       throw new DbRuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void removeShowSqlParams(Setting setting) {
/* 179 */     setting.remove("showSql");
/* 180 */     setting.remove("formatSql");
/* 181 */     setting.remove("showParams");
/* 182 */     setting.remove("sqlLevel");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setShowSqlGlobal(Setting setting) {
/* 193 */     boolean isShowSql = Convert.toBool(setting.remove("showSql"), Boolean.valueOf(false)).booleanValue();
/* 194 */     boolean isFormatSql = Convert.toBool(setting.remove("formatSql"), Boolean.valueOf(false)).booleanValue();
/* 195 */     boolean isShowParams = Convert.toBool(setting.remove("showParams"), Boolean.valueOf(false)).booleanValue();
/* 196 */     String sqlLevelStr = setting.remove("sqlLevel");
/* 197 */     if (null != sqlLevelStr) {
/* 198 */       sqlLevelStr = sqlLevelStr.toUpperCase();
/*     */     }
/* 200 */     Level level = (Level)Convert.toEnum(Level.class, sqlLevelStr, (Enum)Level.DEBUG);
/* 201 */     log.debug("Show sql: [{}], format sql: [{}], show params: [{}], level: [{}]", new Object[] { Boolean.valueOf(isShowSql), Boolean.valueOf(isFormatSql), Boolean.valueOf(isShowParams), level });
/* 202 */     setShowSqlGlobal(isShowSql, isFormatSql, isShowParams, level);
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
/*     */ 
/*     */   
/*     */   public static void setShowSqlGlobal(boolean isShowSql, boolean isFormatSql, boolean isShowParams, Level level) {
/* 216 */     GlobalDbConfig.setShowSql(isShowSql, isFormatSql, isShowParams, level);
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
/*     */   public static void setCaseInsensitiveGlobal(boolean caseInsensitive) {
/* 228 */     GlobalDbConfig.setCaseInsensitive(caseInsensitive);
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
/*     */   
/*     */   public static void setReturnGeneratedKeyGlobal(boolean returnGeneratedKey) {
/* 241 */     GlobalDbConfig.setReturnGeneratedKey(returnGeneratedKey);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setDbSettingPathGlobal(String dbSettingPath) {
/* 252 */     GlobalDbConfig.setDbSettingPath(dbSettingPath);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\DbUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */