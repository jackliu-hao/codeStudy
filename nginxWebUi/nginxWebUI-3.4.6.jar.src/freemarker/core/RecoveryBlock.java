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
/*    */ final class RecoveryBlock
/*    */   extends TemplateElement
/*    */ {
/*    */   RecoveryBlock(TemplateElements children) {
/* 29 */     setChildren(children);
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/* 34 */     return getChildBuffer();
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 39 */     if (canonical) {
/* 40 */       StringBuilder buf = new StringBuilder();
/* 41 */       buf.append('<').append(getNodeTypeSymbol()).append('>');
/* 42 */       buf.append(getChildrenCanonicalForm());
/* 43 */       return buf.toString();
/*    */     } 
/* 45 */     return getNodeTypeSymbol();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 51 */     return "#recover";
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 56 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 61 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 66 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 71 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\RecoveryBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */