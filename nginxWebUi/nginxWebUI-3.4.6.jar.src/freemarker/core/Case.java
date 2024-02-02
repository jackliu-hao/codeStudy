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
/*    */ final class Case
/*    */   extends TemplateElement
/*    */ {
/*    */   static final int TYPE_CASE = 0;
/*    */   static final int TYPE_DEFAULT = 1;
/*    */   Expression condition;
/*    */   
/*    */   Case(Expression matchingValue, TemplateElements children) {
/* 33 */     this.condition = matchingValue;
/* 34 */     setChildren(children);
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateElement[] accept(Environment env) {
/* 39 */     return getChildBuffer();
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 44 */     StringBuilder sb = new StringBuilder();
/* 45 */     if (canonical) sb.append('<'); 
/* 46 */     sb.append(getNodeTypeSymbol());
/* 47 */     if (this.condition != null) {
/* 48 */       sb.append(' ');
/* 49 */       sb.append(this.condition.getCanonicalForm());
/*    */     } 
/* 51 */     if (canonical) {
/* 52 */       sb.append('>');
/* 53 */       sb.append(getChildrenCanonicalForm());
/*    */     } 
/* 55 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 60 */     return (this.condition != null) ? "#case" : "#default";
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 65 */     return 2;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 70 */     switch (idx) { case 0:
/* 71 */         return this.condition;
/* 72 */       case 1: return Integer.valueOf((this.condition != null) ? 0 : 1); }
/* 73 */      throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 79 */     switch (idx) { case 0:
/* 80 */         return ParameterRole.CONDITION;
/* 81 */       case 1: return ParameterRole.AST_NODE_SUBTYPE; }
/* 82 */      throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 88 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Case.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */