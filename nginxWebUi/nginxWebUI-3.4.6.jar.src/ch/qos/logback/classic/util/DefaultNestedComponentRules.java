/*    */ package ch.qos.logback.classic.util;
/*    */ 
/*    */ import ch.qos.logback.classic.PatternLayout;
/*    */ import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
/*    */ import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
/*    */ import ch.qos.logback.core.AppenderBase;
/*    */ import ch.qos.logback.core.UnsynchronizedAppenderBase;
/*    */ import ch.qos.logback.core.filter.EvaluatorFilter;
/*    */ import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
/*    */ import ch.qos.logback.core.net.ssl.SSLNestedComponentRegistryRules;
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
/*    */ public class DefaultNestedComponentRules
/*    */ {
/*    */   public static void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
/* 35 */     registry.add(AppenderBase.class, "layout", PatternLayout.class);
/* 36 */     registry.add(UnsynchronizedAppenderBase.class, "layout", PatternLayout.class);
/*    */     
/* 38 */     registry.add(AppenderBase.class, "encoder", PatternLayoutEncoder.class);
/* 39 */     registry.add(UnsynchronizedAppenderBase.class, "encoder", PatternLayoutEncoder.class);
/*    */     
/* 41 */     registry.add(EvaluatorFilter.class, "evaluator", JaninoEventEvaluator.class);
/*    */     
/* 43 */     SSLNestedComponentRegistryRules.addDefaultNestedComponentRegistryRules(registry);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classi\\util\DefaultNestedComponentRules.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */