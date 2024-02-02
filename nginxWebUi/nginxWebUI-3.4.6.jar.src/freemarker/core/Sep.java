/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
/*    */ import java.io.IOException;
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
/*    */ class Sep
/*    */   extends TemplateElement
/*    */ {
/*    */   public Sep(TemplateElements children) {
/* 32 */     setChildren(children);
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/* 37 */     IteratorBlock.IterationContext iterCtx = env.findClosestEnclosingIterationContext();
/* 38 */     if (iterCtx == null)
/*    */     {
/* 40 */       throw new _MiscTemplateException(env, new Object[] {
/* 41 */             getNodeTypeSymbol(), " without iteration in context"
/*    */           });
/*    */     }
/* 44 */     if (iterCtx.hasNext()) {
/* 45 */       return getChildBuffer();
/*    */     }
/* 47 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 52 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 57 */     StringBuilder sb = new StringBuilder();
/* 58 */     if (canonical) sb.append('<'); 
/* 59 */     sb.append(getNodeTypeSymbol());
/* 60 */     if (canonical) {
/* 61 */       sb.append('>');
/* 62 */       sb.append(getChildrenCanonicalForm());
/* 63 */       sb.append("</");
/* 64 */       sb.append(getNodeTypeSymbol());
/* 65 */       sb.append('>');
/*    */     } 
/* 67 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 72 */     return "#sep";
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 77 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 82 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 87 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Sep.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */