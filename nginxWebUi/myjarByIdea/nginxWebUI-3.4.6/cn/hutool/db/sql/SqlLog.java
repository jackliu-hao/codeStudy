package cn.hutool.db.sql;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.log.level.Level;

public enum SqlLog {
   INSTANCE;

   public static final String KEY_SHOW_SQL = "showSql";
   public static final String KEY_FORMAT_SQL = "formatSql";
   public static final String KEY_SHOW_PARAMS = "showParams";
   public static final String KEY_SQL_LEVEL = "sqlLevel";
   private static final Log log = LogFactory.get();
   private boolean showSql;
   private boolean formatSql;
   private boolean showParams;
   private Level level;

   private SqlLog() {
      this.level = Level.DEBUG;
   }

   public void init(boolean isShowSql, boolean isFormatSql, boolean isShowParams, Level level) {
      this.showSql = isShowSql;
      this.formatSql = isFormatSql;
      this.showParams = isShowParams;
      this.level = level;
   }

   public void log(String sql) {
      this.log(sql, (Object)null);
   }

   public void logForBatch(String sql) {
      if (this.showSql) {
         log.log(this.level, "\n[Batch SQL] -> {}", this.formatSql ? SqlFormatter.format(sql) : sql);
      }

   }

   public void log(String sql, Object paramValues) {
      if (this.showSql) {
         if (null != paramValues && this.showParams) {
            log.log(this.level, "\n[SQL] -> {}\nParams -> {}", this.formatSql ? SqlFormatter.format(sql) : sql, paramValues);
         } else {
            log.log(this.level, "\n[SQL] -> {}", this.formatSql ? SqlFormatter.format(sql) : sql);
         }
      }

   }
}
