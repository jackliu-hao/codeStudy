/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.ext.beans.BeansWrapper;
/*    */ import freemarker.template.Configuration;
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
/*    */ public class _SettingEvaluationEnvironment
/*    */ {
/* 33 */   private static final ThreadLocal CURRENT = new ThreadLocal();
/*    */   
/*    */   private BeansWrapper objectWrapper;
/*    */   
/*    */   public static _SettingEvaluationEnvironment getCurrent() {
/* 38 */     Object r = CURRENT.get();
/* 39 */     if (r != null) {
/* 40 */       return (_SettingEvaluationEnvironment)r;
/*    */     }
/* 42 */     return new _SettingEvaluationEnvironment();
/*    */   }
/*    */   
/*    */   public static _SettingEvaluationEnvironment startScope() {
/* 46 */     Object previous = CURRENT.get();
/* 47 */     CURRENT.set(new _SettingEvaluationEnvironment());
/* 48 */     return (_SettingEvaluationEnvironment)previous;
/*    */   }
/*    */   
/*    */   public static void endScope(_SettingEvaluationEnvironment previous) {
/* 52 */     CURRENT.set(previous);
/*    */   }
/*    */   
/*    */   public BeansWrapper getObjectWrapper() {
/* 56 */     if (this.objectWrapper == null) {
/* 57 */       this.objectWrapper = new BeansWrapper(Configuration.VERSION_2_3_21);
/*    */     }
/* 59 */     return this.objectWrapper;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_SettingEvaluationEnvironment.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */