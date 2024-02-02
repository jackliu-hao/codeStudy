/*    */ package ch.qos.logback.solon.integration;
/*    */ 
/*    */ import ch.qos.logback.classic.Level;
/*    */ import ch.qos.logback.classic.Logger;
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.core.Context;
/*    */ import ch.qos.logback.core.joran.spi.JoranException;
/*    */ import ch.qos.logback.solon.SolonConfigurator;
/*    */ import java.net.URL;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.Plugin;
/*    */ import org.noear.solon.logging.LogOptions;
/*    */ import org.noear.solon.logging.model.LoggerLevelEntity;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class XPluginImp
/*    */   implements Plugin
/*    */ {
/*    */   public void start(AopContext context) {
/* 25 */     URL url = Utils.getResource("logback.xml");
/* 26 */     if (url == null) {
/*    */       
/* 28 */       if (Utils.isNotEmpty(Solon.cfg().env())) {
/* 29 */         url = Utils.getResource("logback-solon-" + Solon.cfg().env() + ".xml");
/*    */       }
/*    */ 
/*    */       
/* 33 */       if (url == null) {
/* 34 */         url = Utils.getResource("logback-solon.xml");
/*    */       }
/*    */ 
/*    */       
/* 38 */       if (url == null) {
/* 39 */         url = Utils.getResource("META-INF/solon/logging/logback-def.xml");
/*    */       }
/*    */       
/* 42 */       if (url == null) {
/*    */         return;
/*    */       }
/*    */       
/* 46 */       initDo(url);
/*    */     } 
/*    */   }
/*    */   
/*    */   private void initDo(URL url) {
/*    */     try {
/* 52 */       LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
/* 53 */       SolonConfigurator jc = new SolonConfigurator();
/* 54 */       jc.setContext((Context)loggerContext);
/* 55 */       loggerContext.reset();
/* 56 */       jc.doConfigure(url);
/*    */ 
/*    */       
/* 59 */       if (LogOptions.getLoggerLevels().size() > 0) {
/* 60 */         for (LoggerLevelEntity lle : LogOptions.getLoggerLevels()) {
/* 61 */           Logger logger = loggerContext.getLogger(lle.getLoggerExpr());
/* 62 */           logger.setLevel(Level.valueOf(lle.getLevel().name()));
/*    */         } 
/*    */       }
/* 65 */     } catch (JoranException e) {
/* 66 */       throw new RuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\solon\integration\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */