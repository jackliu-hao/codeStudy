/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.utility.ClassUtil;
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
/*    */ public class _DelayedShortClassName
/*    */   extends _DelayedConversionToString
/*    */ {
/*    */   public _DelayedShortClassName(Class pClass) {
/* 27 */     super(pClass);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String doConversion(Object obj) {
/* 32 */     return ClassUtil.getShortClassName((Class)obj, true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_DelayedShortClassName.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */