/*    */ package cn.hutool.db.sql;
/*    */ 
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ import cn.hutool.log.level.Level;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum SqlLog
/*    */ {
/* 14 */   INSTANCE;
/*    */ 
/*    */   
/*    */   private Level level;
/*    */ 
/*    */   
/*    */   private boolean showParams;
/*    */ 
/*    */   
/*    */   private boolean formatSql;
/*    */ 
/*    */   
/*    */   private boolean showSql;
/*    */ 
/*    */   
/*    */   private static final Log log;
/*    */ 
/*    */   
/*    */   public static final String KEY_SQL_LEVEL = "sqlLevel";
/*    */   
/*    */   public static final String KEY_SHOW_PARAMS = "showParams";
/*    */   
/*    */   public static final String KEY_FORMAT_SQL = "formatSql";
/*    */   
/*    */   public static final String KEY_SHOW_SQL = "showSql";
/*    */ 
/*    */   
/*    */   SqlLog() {
/* 42 */     this.level = Level.DEBUG;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   static {
/*    */     log = LogFactory.get();
/*    */   }
/*    */ 
/*    */   
/*    */   public void init(boolean isShowSql, boolean isFormatSql, boolean isShowParams, Level level) {
/* 53 */     this.showSql = isShowSql;
/* 54 */     this.formatSql = isFormatSql;
/* 55 */     this.showParams = isShowParams;
/* 56 */     this.level = level;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void log(String sql) {
/* 66 */     log(sql, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void logForBatch(String sql) {
/* 76 */     if (this.showSql) {
/* 77 */       log.log(this.level, "\n[Batch SQL] -> {}", new Object[] { this.formatSql ? SqlFormatter.format(sql) : sql });
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void log(String sql, Object paramValues) {
/* 88 */     if (this.showSql)
/* 89 */       if (null != paramValues && this.showParams) {
/* 90 */         log.log(this.level, "\n[SQL] -> {}\nParams -> {}", new Object[] { this.formatSql ? SqlFormatter.format(sql) : sql, paramValues });
/*    */       } else {
/* 92 */         log.log(this.level, "\n[SQL] -> {}", new Object[] { this.formatSql ? SqlFormatter.format(sql) : sql });
/*    */       }  
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\SqlLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */