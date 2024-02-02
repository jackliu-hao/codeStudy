/*    */ package com.zaxxer.hikari;
/*    */ 
/*    */ import com.zaxxer.hikari.util.PropertyElf;
/*    */ import java.util.Enumeration;
/*    */ import java.util.Hashtable;
/*    */ import java.util.Properties;
/*    */ import java.util.Set;
/*    */ import javax.naming.Context;
/*    */ import javax.naming.InitialContext;
/*    */ import javax.naming.Name;
/*    */ import javax.naming.NamingException;
/*    */ import javax.naming.RefAddr;
/*    */ import javax.naming.Reference;
/*    */ import javax.naming.spi.ObjectFactory;
/*    */ import javax.sql.DataSource;
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
/*    */ public class HikariJNDIFactory
/*    */   implements ObjectFactory
/*    */ {
/*    */   public synchronized Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
/* 46 */     if (obj instanceof Reference && "javax.sql.DataSource".equals(((Reference)obj).getClassName())) {
/* 47 */       Reference ref = (Reference)obj;
/* 48 */       Set<String> hikariPropSet = PropertyElf.getPropertyNames(HikariConfig.class);
/*    */       
/* 50 */       Properties properties = new Properties();
/* 51 */       Enumeration<RefAddr> enumeration = ref.getAll();
/* 52 */       while (enumeration.hasMoreElements()) {
/* 53 */         RefAddr element = enumeration.nextElement();
/* 54 */         String type = element.getType();
/* 55 */         if (type.startsWith("dataSource.") || hikariPropSet.contains(type)) {
/* 56 */           properties.setProperty(type, element.getContent().toString());
/*    */         }
/*    */       } 
/* 59 */       return createDataSource(properties, nameCtx);
/*    */     } 
/* 61 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   private DataSource createDataSource(Properties properties, Context context) throws NamingException {
/* 66 */     String jndiName = properties.getProperty("dataSourceJNDI");
/* 67 */     if (jndiName != null) {
/* 68 */       return lookupJndiDataSource(properties, context, jndiName);
/*    */     }
/*    */     
/* 71 */     return new HikariDataSource(new HikariConfig(properties));
/*    */   }
/*    */ 
/*    */   
/*    */   private DataSource lookupJndiDataSource(Properties properties, Context context, String jndiName) throws NamingException {
/* 76 */     if (context == null) {
/* 77 */       throw new RuntimeException("JNDI context does not found for dataSourceJNDI : " + jndiName);
/*    */     }
/*    */     
/* 80 */     DataSource jndiDS = (DataSource)context.lookup(jndiName);
/* 81 */     if (jndiDS == null) {
/* 82 */       Context ic = new InitialContext();
/* 83 */       jndiDS = (DataSource)ic.lookup(jndiName);
/* 84 */       ic.close();
/*    */     } 
/*    */     
/* 87 */     if (jndiDS != null) {
/* 88 */       HikariConfig config = new HikariConfig(properties);
/* 89 */       config.setDataSource(jndiDS);
/* 90 */       return new HikariDataSource(config);
/*    */     } 
/*    */     
/* 93 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\HikariJNDIFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */