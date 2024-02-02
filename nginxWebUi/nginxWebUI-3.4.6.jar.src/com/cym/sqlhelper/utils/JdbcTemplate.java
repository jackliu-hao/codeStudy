/*     */ package com.cym.sqlhelper.utils;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.Db;
/*     */ import cn.hutool.db.Entity;
/*     */ import com.cym.sqlhelper.config.DataSourceEmbed;
/*     */ import java.io.Reader;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.h2.jdbc.JdbcClob;
/*     */ import org.noear.solon.annotation.Component;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Component
/*     */ public class JdbcTemplate
/*     */ {
/*     */   @Inject
/*     */   DataSourceEmbed dataSourceEmbed;
/*  29 */   SnowFlake snowFlake = new SnowFlake(1L, 1L);
/*  30 */   static Logger logger = LoggerFactory.getLogger(JdbcTemplate.class);
/*     */   
/*     */   public List<Map<String, Object>> queryForList(String formatSql, Object... array) {
/*     */     try {
/*  34 */       List<Entity> list = Db.use(this.dataSourceEmbed.getDataSource()).query(formatSql, array);
/*     */       
/*  36 */       List<Map<String, Object>> mapList = new ArrayList<>();
/*  37 */       for (Entity entity : list) {
/*  38 */         Map<String, Object> map = new HashMap<>();
/*  39 */         for (Map.Entry entry : entity.entrySet()) {
/*  40 */           if (entry.getValue() instanceof JdbcClob) {
/*  41 */             map.put(entry.getKey().toString(), clobToStr((JdbcClob)entry.getValue())); continue;
/*     */           } 
/*  43 */           map.put(entry.getKey().toString(), entry.getValue());
/*     */         } 
/*     */ 
/*     */         
/*  47 */         mapList.add(map);
/*     */       } 
/*     */       
/*  50 */       return mapList;
/*  51 */     } catch (Exception e) {
/*  52 */       logger.error(e.getMessage(), e);
/*  53 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String clobToStr(JdbcClob jdbcClob) {
/*     */     try {
/*  59 */       StringBuilder builder = new StringBuilder();
/*  60 */       Reader rd = jdbcClob.getCharacterStream();
/*  61 */       char[] str = new char[1];
/*  62 */       while (rd.read(str) != -1) {
/*  63 */         builder.append(new String(str));
/*     */       }
/*  65 */       return builder.toString();
/*  66 */     } catch (Exception e) {
/*  67 */       e.printStackTrace();
/*     */ 
/*     */       
/*  70 */       return null;
/*     */     } 
/*     */   }
/*     */   public Set<String> queryForColumn(Class clazz) throws SQLException {
/*  74 */     Set<String> set = new HashSet<>();
/*  75 */     String uuid = this.snowFlake.nextId();
/*  76 */     Entity entity = new Entity();
/*  77 */     entity.setTableName("`" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`");
/*  78 */     entity.set("id", uuid);
/*  79 */     Db.use(this.dataSourceEmbed.getDataSource()).insert(entity);
/*  80 */     List<Entity> list = Db.use(this.dataSourceEmbed.getDataSource()).query("select * from `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` where id='" + uuid + "'", new Object[0]);
/*     */     
/*  82 */     for (Entity entityOne : list) {
/*  83 */       set = entityOne.getFieldNames();
/*     */     }
/*  85 */     Db.use(this.dataSourceEmbed.getDataSource()).del(entity);
/*     */     
/*  87 */     return set;
/*     */   }
/*     */   
/*     */   public Long queryForCount(String formatSql, Object... array) {
/*  91 */     List<Map<String, Object>> list = queryForList(formatSql, array);
/*  92 */     if (list != null && list.size() != 0) {
/*  93 */       Map<String, Object> map = list.get(0);
/*  94 */       for (Map.Entry<String, Object> entity : map.entrySet()) {
/*  95 */         if (entity.getValue() instanceof Long) {
/*  96 */           return (Long)entity.getValue();
/*     */         }
/*  98 */         if (entity.getValue() instanceof Integer) {
/*  99 */           return Long.valueOf(((Integer)entity.getValue()).longValue());
/*     */         }
/* 101 */         if (entity.getValue() instanceof Short) {
/* 102 */           return Long.valueOf(((Short)entity.getValue()).longValue());
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 107 */     return Long.valueOf(0L);
/*     */   }
/*     */   
/*     */   public void execute(String formatSql, Object... array) {
/*     */     try {
/* 112 */       Db.use(this.dataSourceEmbed.getDataSource()).execute(formatSql, array);
/* 113 */     } catch (SQLException e) {
/* 114 */       logger.error(e.getMessage(), e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelpe\\utils\JdbcTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */