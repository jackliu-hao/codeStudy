/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateDirectiveBody;
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.utility.StringUtil;
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
/*    */ class NestedContentNotSupportedException
/*    */   extends TemplateException
/*    */ {
/*    */   public static void check(TemplateDirectiveBody body) throws NestedContentNotSupportedException {
/* 36 */     if (body == null) {
/*    */       return;
/*    */     }
/* 39 */     if (body instanceof Environment.NestedElementTemplateDirectiveBody) {
/* 40 */       TemplateElement[] tes = ((Environment.NestedElementTemplateDirectiveBody)body).getChildrenBuffer();
/* 41 */       if (tes == null || tes.length == 0 || (tes[0] instanceof ThreadInterruptionSupportTemplatePostProcessor.ThreadInterruptionCheck && (tes.length == 1 || tes[1] == null))) {
/*    */         return;
/*    */       }
/*    */     } 
/*    */     
/* 46 */     throw new NestedContentNotSupportedException(Environment.getCurrentEnvironment());
/*    */   }
/*    */ 
/*    */   
/*    */   private NestedContentNotSupportedException(Environment env) {
/* 51 */     this(null, null, env);
/*    */   }
/*    */   
/*    */   private NestedContentNotSupportedException(Exception cause, Environment env) {
/* 55 */     this(null, cause, env);
/*    */   }
/*    */   
/*    */   private NestedContentNotSupportedException(String description, Environment env) {
/* 59 */     this(description, null, env);
/*    */   }
/*    */   
/*    */   private NestedContentNotSupportedException(String description, Exception cause, Environment env) {
/* 63 */     super("Nested content (body) not supported." + ((description != null) ? (" " + 
/* 64 */         StringUtil.jQuote(description)) : ""), cause, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NestedContentNotSupportedException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */