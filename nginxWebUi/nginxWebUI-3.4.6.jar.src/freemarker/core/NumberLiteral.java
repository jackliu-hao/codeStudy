/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.SimpleNumber;
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateNumberModel;
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
/*    */ final class NumberLiteral
/*    */   extends Expression
/*    */   implements TemplateNumberModel
/*    */ {
/*    */   private final Number value;
/*    */   
/*    */   public NumberLiteral(Number value) {
/* 36 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateModel _eval(Environment env) {
/* 41 */     return (TemplateModel)new SimpleNumber(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public String evalAndCoerceToPlainText(Environment env) throws TemplateException {
/* 46 */     return env.formatNumberToPlainText(this, this, false);
/*    */   }
/*    */ 
/*    */   
/*    */   public Number getAsNumber() {
/* 51 */     return this.value;
/*    */   }
/*    */   
/*    */   String getName() {
/* 55 */     return "the number: '" + this.value + "'";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCanonicalForm() {
/* 60 */     return this.value.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 65 */     return getCanonicalForm();
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isLiteral() {
/* 70 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 76 */     return new NumberLiteral(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 81 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 86 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 91 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NumberLiteral.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */