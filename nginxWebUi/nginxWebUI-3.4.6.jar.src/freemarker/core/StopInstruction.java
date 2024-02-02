/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
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
/*    */ final class StopInstruction
/*    */   extends TemplateElement
/*    */ {
/*    */   private Expression exp;
/*    */   
/*    */   StopInstruction(Expression exp) {
/* 32 */     this.exp = exp;
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateElement[] accept(Environment env) throws TemplateException {
/* 37 */     if (this.exp == null) {
/* 38 */       throw new StopException(env);
/*    */     }
/* 40 */     throw new StopException(env, this.exp.evalAndCoerceToPlainText(env));
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 45 */     StringBuilder sb = new StringBuilder();
/* 46 */     if (canonical) sb.append('<'); 
/* 47 */     sb.append(getNodeTypeSymbol());
/* 48 */     if (this.exp != null) {
/* 49 */       sb.append(' ');
/* 50 */       sb.append(this.exp.getCanonicalForm());
/*    */     } 
/* 52 */     if (canonical) sb.append("/>"); 
/* 53 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 58 */     return "#stop";
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 63 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 68 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 69 */     return this.exp;
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 74 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 75 */     return ParameterRole.MESSAGE;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 80 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\StopInstruction.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */