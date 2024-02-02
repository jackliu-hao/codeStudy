/*     */ package com.cym.sqlhelper.utils;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.noear.solon.annotation.Component;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ @Component
/*     */ public class SqlUtils
/*     */ {
/*  18 */   static Logger logger = LoggerFactory.getLogger(SqlUtils.class);
/*     */   
/*     */   @Inject("${project.sqlPrint:false}")
/*     */   Boolean print;
/*     */   
/*     */   @Inject
/*     */   JdbcTemplate jdbcTemplate;
/*  25 */   String separator = System.getProperty("line.separator");
/*     */   
/*     */   public String formatSql(String sql) {
/*  28 */     if (StrUtil.isEmpty(sql)) {
/*  29 */       return "";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  36 */     sql = sql.replace("FROM", this.separator + "FROM").replace("WHERE", this.separator + "WHERE").replace("ORDER", this.separator + "ORDER").replace("LIMIT", this.separator + "LIMIT").replace("VALUES", this.separator + "VALUES");
/*     */     
/*  38 */     return sql;
/*     */   }
/*     */   
/*     */   public void checkOrCreateTable(Class<?> clazz) throws SQLException {
/*  42 */     String sql = "CREATE TABLE IF NOT EXISTS `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` (id VARCHAR(32) NOT NULL PRIMARY KEY)";
/*  43 */     logQuery(formatSql(sql));
/*  44 */     this.jdbcTemplate.execute(formatSql(sql), new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void logQuery(String sql) {
/*  49 */     logQuery(sql, null);
/*     */   }
/*     */   
/*     */   public void logQuery(String sql, Object[] params) {
/*  53 */     if (this.print.booleanValue()) {
/*     */       try {
/*  55 */         if (params != null) {
/*  56 */           for (Object object : params) {
/*     */             
/*  58 */             if (object instanceof String) {
/*  59 */               object = object.toString().replace("$", "RDS_CHAR_DOLLAR");
/*  60 */               sql = sql.replaceFirst("\\?", "'" + object + "'").replace("RDS_CHAR_DOLLAR", "$");
/*     */             } else {
/*  62 */               sql = sql.replaceFirst("\\?", String.valueOf(object));
/*     */             } 
/*     */           } 
/*     */         }
/*     */         
/*  67 */         logger.info(sql);
/*  68 */       } catch (Exception e) {
/*  69 */         logger.error(e.getMessage(), e);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void checkOrCreateIndex(Class<?> clazz, String name, boolean unique, List<Map<String, Object>> indexs) throws SQLException {
/*  75 */     checkOrCreateIndex(clazz, new String[] { name }, unique, indexs);
/*     */   }
/*     */   
/*     */   public void checkOrCreateIndex(Class<?> clazz, String[] colums, boolean unique, List<Map<String, Object>> indexs) throws SQLException {
/*  79 */     List<String> columList = new ArrayList<>();
/*  80 */     for (String colum : colums) {
/*  81 */       columList.add(StrUtil.toUnderlineCase(colum));
/*     */     }
/*  83 */     String name = StrUtil.join("&", columList) + "@" + StrUtil.toUnderlineCase(clazz.getSimpleName());
/*     */     
/*  85 */     Boolean hasIndex = Boolean.valueOf(false);
/*  86 */     for (Map<String, Object> map : indexs) {
/*  87 */       if (StrUtil.toUnderlineCase(name).equalsIgnoreCase((String)map.get("name")) || StrUtil.toUnderlineCase(name).equalsIgnoreCase((String)map.get("Key_name"))) {
/*  88 */         hasIndex = Boolean.valueOf(true);
/*     */       }
/*     */     } 
/*     */     
/*  92 */     if (!hasIndex.booleanValue()) {
/*  93 */       String type = unique ? "UNIQUE INDEX" : "INDEX";
/*  94 */       String length = "";
/*     */       
/*  96 */       columList = new ArrayList<>();
/*  97 */       for (String colum : colums) {
/*  98 */         columList.add(StrUtil.toUnderlineCase("`" + colum + "`" + length));
/*     */       }
/*     */       
/* 101 */       String sql = "CREATE " + type + "  `" + StrUtil.toUnderlineCase(name) + "` ON `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`(" + StrUtil.join(",", columList) + ")";
/* 102 */       logQuery(formatSql(sql));
/* 103 */       this.jdbcTemplate.execute(formatSql(sql), new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkOrCreateColumn(Class<?> clazz, String name, Set<String> columns) throws SQLException {
/* 109 */     if (!columns.contains(StrUtil.toUnderlineCase(name).toLowerCase())) {
/* 110 */       String sql = "ALTER TABLE `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` ADD COLUMN `" + StrUtil.toUnderlineCase(name) + "` LONGTEXT";
/* 111 */       logQuery(formatSql(sql));
/* 112 */       this.jdbcTemplate.execute(formatSql(sql), new Object[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateDefaultValue(Class<?> clazz, String column, String value) throws SQLException {
/* 118 */     String sql = "SELECT COUNT(*) FROM `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` WHERE `" + StrUtil.toUnderlineCase(column) + "` IS NULL";
/* 119 */     logQuery(formatSql(sql));
/* 120 */     Long count = this.jdbcTemplate.queryForCount(formatSql(sql), new Object[0]);
/* 121 */     if (count != null && count.longValue() > 0L) {
/* 122 */       sql = "UPDATE `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` SET `" + StrUtil.toUnderlineCase(column) + "` = ? WHERE `" + StrUtil.toUnderlineCase(column) + "` IS NULL";
/* 123 */       logQuery(formatSql(sql));
/* 124 */       this.jdbcTemplate.execute(formatSql(sql), new Object[] { value });
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelpe\\utils\SqlUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */