/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ final class ConditionalBlock
/*     */   extends TemplateElement
/*     */ {
/*     */   static final int TYPE_IF = 0;
/*     */   static final int TYPE_ELSE = 1;
/*     */   static final int TYPE_ELSE_IF = 2;
/*     */   final Expression condition;
/*     */   private final int type;
/*     */   
/*     */   ConditionalBlock(Expression condition, TemplateElements children, int type) {
/*  41 */     this.condition = condition;
/*  42 */     setChildren(children);
/*  43 */     this.type = type;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  48 */     if (this.condition == null || this.condition.evalToBoolean(env)) {
/*  49 */       return getChildBuffer();
/*     */     }
/*  51 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  56 */     StringBuilder buf = new StringBuilder();
/*  57 */     if (canonical) buf.append('<'); 
/*  58 */     buf.append(getNodeTypeSymbol());
/*  59 */     if (this.condition != null) {
/*  60 */       buf.append(' ');
/*  61 */       buf.append(this.condition.getCanonicalForm());
/*     */     } 
/*  63 */     if (canonical) {
/*  64 */       buf.append(">");
/*  65 */       buf.append(getChildrenCanonicalForm());
/*  66 */       if (!(getParentElement() instanceof IfBlock)) {
/*  67 */         buf.append("</#if>");
/*     */       }
/*     */     } 
/*  70 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  75 */     if (this.type == 1)
/*  76 */       return "#else"; 
/*  77 */     if (this.type == 0)
/*  78 */       return "#if"; 
/*  79 */     if (this.type == 2) {
/*  80 */       return "#elseif";
/*     */     }
/*  82 */     throw new BugException("Unknown type");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  88 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/*  93 */     switch (idx) { case 0:
/*  94 */         return this.condition;
/*  95 */       case 1: return Integer.valueOf(this.type); }
/*  96 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 102 */     switch (idx) { case 0:
/* 103 */         return ParameterRole.CONDITION;
/* 104 */       case 1: return ParameterRole.AST_NODE_SUBTYPE; }
/* 105 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 111 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ConditionalBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */