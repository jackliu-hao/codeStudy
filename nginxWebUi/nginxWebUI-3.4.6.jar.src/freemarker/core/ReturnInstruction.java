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
/*    */ public final class ReturnInstruction
/*    */   extends TemplateElement
/*    */ {
/*    */   private Expression exp;
/*    */   
/*    */   ReturnInstruction(Expression exp) {
/* 32 */     this.exp = exp;
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateElement[] accept(Environment env) throws TemplateException {
/* 37 */     if (this.exp != null) {
/* 38 */       env.setLastReturnValue(this.exp.eval(env));
/*    */     }
/* 40 */     if (nextSibling() == null && getParentElement() instanceof Macro)
/*    */     {
/* 42 */       return null;
/*    */     }
/* 44 */     throw Return.INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 49 */     StringBuilder sb = new StringBuilder();
/* 50 */     if (canonical) sb.append('<'); 
/* 51 */     sb.append(getNodeTypeSymbol());
/* 52 */     if (this.exp != null) {
/* 53 */       sb.append(' ');
/* 54 */       sb.append(this.exp.getCanonicalForm());
/*    */     } 
/* 56 */     if (canonical) sb.append("/>"); 
/* 57 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 62 */     return "#return";
/*    */   }
/*    */   
/*    */   public static class Return extends FlowControlException {
/* 66 */     static final Return INSTANCE = new Return();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 73 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 78 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 79 */     return this.exp;
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 84 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 85 */     return ParameterRole.VALUE;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 90 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ReturnInstruction.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */