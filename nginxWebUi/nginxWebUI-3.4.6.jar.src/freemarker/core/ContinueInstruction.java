/*    */ package freemarker.core;
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
/*    */ final class ContinueInstruction
/*    */   extends TemplateElement
/*    */ {
/*    */   TemplateElement[] accept(Environment env) {
/* 29 */     throw BreakOrContinueException.CONTINUE_INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 34 */     return canonical ? ("<" + getNodeTypeSymbol() + "/>") : getNodeTypeSymbol();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 39 */     return "#continue";
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 44 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 49 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 54 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 59 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ContinueInstruction.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */