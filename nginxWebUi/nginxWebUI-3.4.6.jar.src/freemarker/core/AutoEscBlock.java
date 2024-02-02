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
/*    */ final class AutoEscBlock
/*    */   extends TemplateElement
/*    */ {
/*    */   AutoEscBlock(TemplateElements children) {
/* 32 */     setChildren(children);
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/* 37 */     return getChildBuffer();
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 42 */     if (canonical) {
/* 43 */       return "<" + getNodeTypeSymbol() + "\">" + getChildrenCanonicalForm() + "</" + getNodeTypeSymbol() + ">";
/*    */     }
/* 45 */     return getNodeTypeSymbol();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 51 */     return "#autoesc";
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
/*    */   boolean isIgnorable(boolean stripWhitespace) {
/* 71 */     return (getChildCount() == 0);
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 76 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\AutoEscBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */