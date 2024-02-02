/*    */ package ch.qos.logback.core.joran.action;
/*    */ 
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import ch.qos.logback.core.spi.ContextAware;
/*    */ import ch.qos.logback.core.util.ContextUtil;
/*    */ import ch.qos.logback.core.util.OptionHelper;
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
/*    */ public class ActionUtil
/*    */ {
/*    */   public enum Scope
/*    */   {
/* 25 */     LOCAL, CONTEXT, SYSTEM;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Scope stringToScope(String scopeStr) {
/* 34 */     if (Scope.SYSTEM.toString().equalsIgnoreCase(scopeStr))
/* 35 */       return Scope.SYSTEM; 
/* 36 */     if (Scope.CONTEXT.toString().equalsIgnoreCase(scopeStr)) {
/* 37 */       return Scope.CONTEXT;
/*    */     }
/* 39 */     return Scope.LOCAL;
/*    */   }
/*    */   
/*    */   public static void setProperty(InterpretationContext ic, String key, String value, Scope scope) {
/* 43 */     switch (scope) {
/*    */       case LOCAL:
/* 45 */         ic.addSubstitutionProperty(key, value);
/*    */         break;
/*    */       case CONTEXT:
/* 48 */         ic.getContext().putProperty(key, value);
/*    */         break;
/*    */       case SYSTEM:
/* 51 */         OptionHelper.setSystemProperty((ContextAware)ic, key, value);
/*    */         break;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static void setProperties(InterpretationContext ic, Properties props, Scope scope) {
/*    */     ContextUtil cu;
/* 60 */     switch (scope) {
/*    */       case LOCAL:
/* 62 */         ic.addSubstitutionProperties(props);
/*    */         break;
/*    */       case CONTEXT:
/* 65 */         cu = new ContextUtil(ic.getContext());
/* 66 */         cu.addProperties(props);
/*    */         break;
/*    */       case SYSTEM:
/* 69 */         OptionHelper.setSystemProperties((ContextAware)ic, props);
/*    */         break;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\ActionUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */