/*     */ package freemarker.core;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class TrimInstruction
/*     */   extends TemplateElement
/*     */ {
/*     */   static final int TYPE_T = 0;
/*     */   static final int TYPE_LT = 1;
/*     */   static final int TYPE_RT = 2;
/*     */   static final int TYPE_NT = 3;
/*     */   final boolean left;
/*     */   final boolean right;
/*     */   
/*     */   TrimInstruction(boolean left, boolean right) {
/*  36 */     this.left = left;
/*  37 */     this.right = right;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) {
/*  43 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  48 */     StringBuilder sb = new StringBuilder();
/*  49 */     if (canonical) sb.append('<'); 
/*  50 */     sb.append(getNodeTypeSymbol());
/*  51 */     if (canonical) sb.append("/>"); 
/*  52 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  57 */     if (this.left && this.right)
/*  58 */       return "#t"; 
/*  59 */     if (this.left)
/*  60 */       return "#lt"; 
/*  61 */     if (this.right) {
/*  62 */       return "#rt";
/*     */     }
/*  64 */     return "#nt";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isIgnorable(boolean stripWhitespace) {
/*  70 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  75 */     return 1;
/*     */   }
/*     */   
/*     */   Object getParameterValue(int idx) {
/*     */     int type;
/*  80 */     if (idx != 0) throw new IndexOutOfBoundsException();
/*     */     
/*  82 */     if (this.left && this.right) {
/*  83 */       type = 0;
/*  84 */     } else if (this.left) {
/*  85 */       type = 1;
/*  86 */     } else if (this.right) {
/*  87 */       type = 2;
/*     */     } else {
/*  89 */       type = 3;
/*     */     } 
/*  91 */     return Integer.valueOf(type);
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/*  96 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/*  97 */     return ParameterRole.AST_NODE_SUBTYPE;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isOutputCacheable() {
/* 102 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 107 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TrimInstruction.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */