/*    */ package freemarker.core;
/*    */ 
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
/*    */ final class FlushInstruction
/*    */   extends TemplateElement
/*    */ {
/*    */   TemplateElement[] accept(Environment env) throws IOException {
/* 31 */     env.getOut().flush();
/* 32 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 37 */     return canonical ? ("<" + getNodeTypeSymbol() + "/>") : getNodeTypeSymbol();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 42 */     return "#flush";
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 47 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 52 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 57 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 62 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\FlushInstruction.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */