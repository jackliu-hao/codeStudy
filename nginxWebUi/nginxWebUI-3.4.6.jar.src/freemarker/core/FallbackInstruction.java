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
/*    */ final class FallbackInstruction
/*    */   extends TemplateElement
/*    */ {
/*    */   TemplateElement[] accept(Environment env) throws IOException, TemplateException {
/* 30 */     env.fallback();
/* 31 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 36 */     return canonical ? ("<" + getNodeTypeSymbol() + "/>") : getNodeTypeSymbol();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 41 */     return "#fallback";
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 46 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 51 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 56 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 61 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isShownInStackTrace() {
/* 66 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\FallbackInstruction.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */