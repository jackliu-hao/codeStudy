/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateTransformModel;
/*    */ import freemarker.template.utility.StandardCompress;
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
/*    */ 
/*    */ final class CompressedBlock
/*    */   extends TemplateElement
/*    */ {
/*    */   CompressedBlock(TemplateElements children) {
/* 35 */     setChildren(children);
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/* 40 */     TemplateElement[] childBuffer = getChildBuffer();
/* 41 */     if (childBuffer != null) {
/* 42 */       env.visitAndTransform(childBuffer, (TemplateTransformModel)StandardCompress.INSTANCE, null);
/*    */     }
/* 44 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 49 */     if (canonical) {
/* 50 */       return "<" + getNodeTypeSymbol() + ">" + getChildrenCanonicalForm() + "</" + getNodeTypeSymbol() + ">";
/*    */     }
/* 52 */     return getNodeTypeSymbol();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 58 */     return "#compress";
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 63 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 68 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 73 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isIgnorable(boolean stripWhitespace) {
/* 78 */     return (getChildCount() == 0 && getParameterCount() == 0);
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 83 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\CompressedBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */