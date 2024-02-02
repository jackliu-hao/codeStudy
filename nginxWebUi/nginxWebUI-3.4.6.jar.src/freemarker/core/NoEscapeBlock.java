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
/*    */ class NoEscapeBlock
/*    */   extends TemplateElement
/*    */ {
/*    */   NoEscapeBlock(TemplateElements children) {
/* 31 */     setChildren(children);
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/* 36 */     return getChildBuffer();
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 41 */     if (canonical) {
/* 42 */       return "<" + getNodeTypeSymbol() + '>' + getChildrenCanonicalForm() + "</" + 
/* 43 */         getNodeTypeSymbol() + '>';
/*    */     }
/* 45 */     return getNodeTypeSymbol();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 51 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 56 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 61 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 66 */     return "#noescape";
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isOutputCacheable() {
/* 71 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 76 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NoEscapeBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */