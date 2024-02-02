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
/*    */ final class AttemptBlock
/*    */   extends TemplateElement
/*    */ {
/*    */   private TemplateElement attemptedSection;
/*    */   private RecoveryBlock recoverySection;
/*    */   
/*    */   AttemptBlock(TemplateElements attemptedSectionChildren, RecoveryBlock recoverySection) {
/* 35 */     TemplateElement attemptedSection = attemptedSectionChildren.asSingleElement();
/* 36 */     this.attemptedSection = attemptedSection;
/* 37 */     this.recoverySection = recoverySection;
/* 38 */     setChildBufferCapacity(2);
/* 39 */     addChild(attemptedSection);
/* 40 */     addChild(recoverySection);
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/* 45 */     env.visitAttemptRecover(this, this.attemptedSection, this.recoverySection);
/* 46 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 51 */     if (!canonical) {
/* 52 */       return getNodeTypeSymbol();
/*    */     }
/* 54 */     StringBuilder buf = new StringBuilder();
/* 55 */     buf.append("<").append(getNodeTypeSymbol()).append(">");
/* 56 */     buf.append(getChildrenCanonicalForm());
/* 57 */     buf.append("</").append(getNodeTypeSymbol()).append(">");
/* 58 */     return buf.toString();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 64 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 69 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 70 */     return this.recoverySection;
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 75 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 76 */     return ParameterRole.ERROR_HANDLER;
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 81 */     return "#attempt";
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 86 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\AttemptBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */