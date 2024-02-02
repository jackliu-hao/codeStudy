/*    */ package com.cym.sqlhelper.utils;
/*    */ 
/*    */ import cn.hutool.core.util.ReflectUtil;
/*    */ import com.cym.sqlhelper.config.InitValue;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ import java.lang.reflect.Field;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Set;
/*    */ import org.noear.solon.annotation.Component;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ @Component
/*    */ public class TableUtils
/*    */ {
/* 19 */   static Logger logger = LoggerFactory.getLogger(TableUtils.class);
/*    */   
/*    */   @Inject
/*    */   JdbcTemplate jdbcTemplate;
/*    */   
/*    */   @Inject
/*    */   SqlUtils sqlUtils;
/*    */   
/*    */   public void initTable(Class<?> clazz) throws SQLException {
/* 28 */     Table table = clazz.<Table>getAnnotation(Table.class);
/* 29 */     if (table != null) {
/*    */       
/* 31 */       this.sqlUtils.checkOrCreateTable(clazz);
/*    */ 
/*    */       
/* 34 */       Set<String> columns = this.jdbcTemplate.queryForColumn(clazz);
/*    */ 
/*    */       
/* 37 */       Field[] fields = ReflectUtil.getFields(clazz);
/* 38 */       for (Field field : fields) {
/*    */         
/* 40 */         if (!field.getName().equals("id")) {
/* 41 */           this.sqlUtils.checkOrCreateColumn(clazz, field.getName(), columns);
/*    */         }
/*    */ 
/*    */         
/* 45 */         if (field.isAnnotationPresent((Class)InitValue.class)) {
/* 46 */           InitValue defaultValue = field.<InitValue>getAnnotation(InitValue.class);
/* 47 */           if (defaultValue.value() != null)
/* 48 */             this.sqlUtils.updateDefaultValue(clazz, field.getName(), defaultValue.value()); 
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelpe\\utils\TableUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */