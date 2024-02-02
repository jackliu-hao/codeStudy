/*    */ package cn.hutool.log.dialect.jdk;
/*    */ 
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import cn.hutool.core.io.resource.ResourceUtil;
/*    */ import cn.hutool.core.lang.Console;
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ import java.io.InputStream;
/*    */ import java.util.logging.LogManager;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JdkLogFactory
/*    */   extends LogFactory
/*    */ {
/*    */   public JdkLogFactory() {
/* 21 */     super("JDK Logging");
/* 22 */     readConfig();
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(String name) {
/* 27 */     return (Log)new JdkLog(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(Class<?> clazz) {
/* 32 */     return (Log)new JdkLog(clazz);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void readConfig() {
/* 40 */     InputStream in = ResourceUtil.getStreamSafe("logging.properties");
/* 41 */     if (null == in) {
/* 42 */       System.err.println("[WARN] Can not find [logging.properties], use [%JRE_HOME%/lib/logging.properties] as default!");
/*    */       
/*    */       return;
/*    */     } 
/*    */     try {
/* 47 */       LogManager.getLogManager().readConfiguration(in);
/* 48 */     } catch (Exception e) {
/* 49 */       Console.error(e, "Read [logging.properties] from classpath error!", new Object[0]);
/*    */       try {
/* 51 */         LogManager.getLogManager().readConfiguration();
/* 52 */       } catch (Exception e1) {
/* 53 */         Console.error(e, "Read [logging.properties] from [%JRE_HOME%/lib/logging.properties] error!", new Object[0]);
/*    */       } 
/*    */     } finally {
/* 56 */       IoUtil.close(in);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\jdk\JdkLogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */