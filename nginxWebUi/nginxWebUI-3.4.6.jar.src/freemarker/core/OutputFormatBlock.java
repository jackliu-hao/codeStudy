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
/*    */ final class OutputFormatBlock
/*    */   extends TemplateElement
/*    */ {
/*    */   private final Expression paramExp;
/*    */   
/*    */   OutputFormatBlock(TemplateElements children, Expression paramExp) {
/* 34 */     this.paramExp = paramExp;
/* 35 */     setChildren(children);
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/* 40 */     return getChildBuffer();
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 45 */     if (canonical) {
/* 46 */       return "<" + getNodeTypeSymbol() + " \"" + this.paramExp.getCanonicalForm() + "\">" + 
/* 47 */         getChildrenCanonicalForm() + "</" + getNodeTypeSymbol() + ">";
/*    */     }
/* 49 */     return getNodeTypeSymbol();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 55 */     return "#outputformat";
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 60 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 65 */     if (idx == 0) return this.paramExp;
/*    */     
/* 67 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 72 */     if (idx == 0) return ParameterRole.VALUE;
/*    */     
/* 74 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isIgnorable(boolean stripWhitespace) {
/* 79 */     return (getChildCount() == 0);
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 84 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\OutputFormatBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */