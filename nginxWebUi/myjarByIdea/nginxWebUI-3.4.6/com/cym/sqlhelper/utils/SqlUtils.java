package com.cym.sqlhelper.utils;

import cn.hutool.core.util.StrUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SqlUtils {
   static Logger logger = LoggerFactory.getLogger(SqlUtils.class);
   @Inject("${project.sqlPrint:false}")
   Boolean print;
   @Inject
   JdbcTemplate jdbcTemplate;
   String separator = System.getProperty("line.separator");

   public String formatSql(String sql) {
      if (StrUtil.isEmpty(sql)) {
         return "";
      } else {
         sql = sql.replace("FROM", this.separator + "FROM").replace("WHERE", this.separator + "WHERE").replace("ORDER", this.separator + "ORDER").replace("LIMIT", this.separator + "LIMIT").replace("VALUES", this.separator + "VALUES");
         return sql;
      }
   }

   public void checkOrCreateTable(Class<?> clazz) throws SQLException {
      String sql = "CREATE TABLE IF NOT EXISTS `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` (id VARCHAR(32) NOT NULL PRIMARY KEY)";
      this.logQuery(this.formatSql(sql));
      this.jdbcTemplate.execute(this.formatSql(sql));
   }

   public void logQuery(String sql) {
      this.logQuery(sql, (Object[])null);
   }

   public void logQuery(String sql, Object[] params) {
      if (this.print) {
         try {
            if (params != null) {
               Object[] var3 = params;
               int var4 = params.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Object object = var3[var5];
                  if (object instanceof String) {
                     Object object = object.toString().replace("$", "RDS_CHAR_DOLLAR");
                     sql = sql.replaceFirst("\\?", "'" + object + "'").replace("RDS_CHAR_DOLLAR", "$");
                  } else {
                     sql = sql.replaceFirst("\\?", String.valueOf(object));
                  }
               }
            }

            logger.info(sql);
         } catch (Exception var7) {
            logger.error((String)var7.getMessage(), (Throwable)var7);
         }
      }

   }

   public void checkOrCreateIndex(Class<?> clazz, String name, boolean unique, List<Map<String, Object>> indexs) throws SQLException {
      this.checkOrCreateIndex(clazz, new String[]{name}, unique, indexs);
   }

   public void checkOrCreateIndex(Class<?> clazz, String[] colums, boolean unique, List<Map<String, Object>> indexs) throws SQLException {
      List<String> columList = new ArrayList();
      String[] var6 = colums;
      int var7 = colums.length;

      String length;
      for(int var8 = 0; var8 < var7; ++var8) {
         length = var6[var8];
         columList.add(StrUtil.toUnderlineCase(length));
      }

      String name = StrUtil.join("&", columList) + "@" + StrUtil.toUnderlineCase(clazz.getSimpleName());
      Boolean hasIndex = false;
      Iterator var16 = indexs.iterator();

      while(true) {
         Map map;
         do {
            if (!var16.hasNext()) {
               if (!hasIndex) {
                  String type = unique ? "UNIQUE INDEX" : "INDEX";
                  length = "";
                  columList = new ArrayList();
                  String[] var10 = colums;
                  int var11 = colums.length;

                  for(int var12 = 0; var12 < var11; ++var12) {
                     String colum = var10[var12];
                     columList.add(StrUtil.toUnderlineCase("`" + colum + "`" + length));
                  }

                  String sql = "CREATE " + type + "  `" + StrUtil.toUnderlineCase(name) + "` ON `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`(" + StrUtil.join(",", columList) + ")";
                  this.logQuery(this.formatSql(sql));
                  this.jdbcTemplate.execute(this.formatSql(sql));
               }

               return;
            }

            map = (Map)var16.next();
         } while(!StrUtil.toUnderlineCase(name).equalsIgnoreCase((String)map.get("name")) && !StrUtil.toUnderlineCase(name).equalsIgnoreCase((String)map.get("Key_name")));

         hasIndex = true;
      }
   }

   public void checkOrCreateColumn(Class<?> clazz, String name, Set<String> columns) throws SQLException {
      if (!columns.contains(StrUtil.toUnderlineCase(name).toLowerCase())) {
         String sql = "ALTER TABLE `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` ADD COLUMN `" + StrUtil.toUnderlineCase(name) + "` LONGTEXT";
         this.logQuery(this.formatSql(sql));
         this.jdbcTemplate.execute(this.formatSql(sql));
      }

   }

   public void updateDefaultValue(Class<?> clazz, String column, String value) throws SQLException {
      String sql = "SELECT COUNT(*) FROM `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` WHERE `" + StrUtil.toUnderlineCase(column) + "` IS NULL";
      this.logQuery(this.formatSql(sql));
      Long count = this.jdbcTemplate.queryForCount(this.formatSql(sql));
      if (count != null && count > 0L) {
         sql = "UPDATE `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` SET `" + StrUtil.toUnderlineCase(column) + "` = ? WHERE `" + StrUtil.toUnderlineCase(column) + "` IS NULL";
         this.logQuery(this.formatSql(sql));
         this.jdbcTemplate.execute(this.formatSql(sql), value);
      }

   }
}
