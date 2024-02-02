/*    */ package com.zaxxer.hikari.hibernate;
/*    */ 
/*    */ import com.zaxxer.hikari.HikariConfig;
/*    */ import java.util.Map;
/*    */ import java.util.Properties;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HikariConfigurationUtil
/*    */ {
/*    */   public static final String CONFIG_PREFIX = "hibernate.hikari.";
/*    */   public static final String CONFIG_PREFIX_DATASOURCE = "hibernate.hikari.dataSource.";
/*    */   
/*    */   public static HikariConfig loadConfiguration(Map props) {
/* 45 */     Properties hikariProps = new Properties();
/* 46 */     copyProperty("hibernate.connection.isolation", props, "transactionIsolation", hikariProps);
/* 47 */     copyProperty("hibernate.connection.autocommit", props, "autoCommit", hikariProps);
/* 48 */     copyProperty("hibernate.connection.driver_class", props, "driverClassName", hikariProps);
/* 49 */     copyProperty("hibernate.connection.url", props, "jdbcUrl", hikariProps);
/* 50 */     copyProperty("hibernate.connection.username", props, "username", hikariProps);
/* 51 */     copyProperty("hibernate.connection.password", props, "password", hikariProps);
/*    */     
/* 53 */     for (Object keyo : props.keySet()) {
/* 54 */       String key = (String)keyo;
/* 55 */       if (key.startsWith("hibernate.hikari.")) {
/* 56 */         hikariProps.setProperty(key.substring("hibernate.hikari.".length()), (String)props.get(key));
/*    */       }
/*    */     } 
/*    */     
/* 60 */     return new HikariConfig(hikariProps);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static void copyProperty(String srcKey, Map src, String dstKey, Properties dst) {
/* 66 */     if (src.containsKey(srcKey))
/* 67 */       dst.setProperty(dstKey, (String)src.get(srcKey)); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\hibernate\HikariConfigurationUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */