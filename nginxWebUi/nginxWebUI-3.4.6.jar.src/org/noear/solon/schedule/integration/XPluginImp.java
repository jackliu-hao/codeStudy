/*    */ package org.noear.solon.schedule.integration;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.BeanWrap;
/*    */ import org.noear.solon.core.Plugin;
/*    */ import org.noear.solon.core.event.AppLoadEndEvent;
/*    */ import org.noear.solon.schedule.JobManager;
/*    */ import org.noear.solon.schedule.MethodRunnable;
/*    */ import org.noear.solon.schedule.annotation.EnableScheduling;
/*    */ import org.noear.solon.schedule.annotation.Scheduled;
/*    */ 
/*    */ 
/*    */ public class XPluginImp
/*    */   implements Plugin
/*    */ {
/*    */   public void start(AopContext context) {
/* 20 */     if (Solon.app().source().getAnnotation(EnableScheduling.class) == null) {
/*    */       return;
/*    */     }
/*    */     
/* 24 */     context.beanBuilderAdd(Scheduled.class, (clz, bw, anno) -> {
/*    */           if (Runnable.class.isAssignableFrom(clz)) {
/*    */             String name = Utils.annoAlias(anno.name(), clz.getSimpleName());
/*    */             
/*    */             if (anno.fixedRate() > 0L) {
/*    */               JobManager.add(name, anno.fixedRate(), anno.fixedDelay(), anno.concurrent(), (Runnable)bw.raw());
/*    */             } else {
/*    */               JobManager.add(name, anno.cron(), anno.zone(), anno.concurrent(), (Runnable)bw.raw());
/*    */             } 
/*    */           } 
/*    */         });
/*    */     
/* 36 */     context.beanExtractorAdd(Scheduled.class, (bw, method, anno) -> {
/*    */           MethodRunnable runnable = new MethodRunnable(bw.raw(), method);
/*    */           
/*    */           String name = Utils.annoAlias(anno.name(), method.getName());
/*    */           
/*    */           if (anno.fixedRate() > 0L) {
/*    */             JobManager.add(name, anno.fixedRate(), anno.fixedDelay(), anno.concurrent(), (Runnable)runnable);
/*    */           } else {
/*    */             JobManager.add(name, anno.cron(), anno.zone(), anno.concurrent(), (Runnable)runnable);
/*    */           } 
/*    */         });
/*    */     
/* 48 */     Solon.app().onEvent(AppLoadEndEvent.class, e -> JobManager.start());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void stop() throws Throwable {
/* 55 */     JobManager.stop();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\schedule\integration\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */