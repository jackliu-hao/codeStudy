/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateBooleanModel;
/*    */ import freemarker.template.TemplateModel;
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
/*    */ final class BooleanLiteral
/*    */   extends Expression
/*    */ {
/*    */   private final boolean val;
/*    */   
/*    */   public BooleanLiteral(boolean val) {
/* 30 */     this.val = val;
/*    */   }
/*    */   
/*    */   static TemplateBooleanModel getTemplateModel(boolean b) {
/* 34 */     return b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean evalToBoolean(Environment env) {
/* 39 */     return this.val;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCanonicalForm() {
/* 44 */     return this.val ? "true" : "false";
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 49 */     return getCanonicalForm();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 54 */     return this.val ? "true" : "false";
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateModel _eval(Environment env) {
/* 59 */     return this.val ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isLiteral() {
/* 64 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 70 */     return new BooleanLiteral(this.val);
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 75 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 80 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 85 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BooleanLiteral.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */