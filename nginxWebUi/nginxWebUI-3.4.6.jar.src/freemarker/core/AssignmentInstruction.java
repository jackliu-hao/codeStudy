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
/*     */ final class AssignmentInstruction
/*     */   extends TemplateElement
/*     */ {
/*     */   private int scope;
/*     */   private Expression namespaceExp;
/*     */   
/*     */   AssignmentInstruction(int scope) {
/*  37 */     this.scope = scope;
/*  38 */     setChildBufferCapacity(1);
/*     */   }
/*     */   
/*     */   void addAssignment(Assignment assignment) {
/*  42 */     addChild(assignment);
/*     */   }
/*     */   
/*     */   void setNamespaceExp(Expression namespaceExp) {
/*  46 */     this.namespaceExp = namespaceExp;
/*  47 */     int ln = getChildCount();
/*  48 */     for (int i = 0; i < ln; i++) {
/*  49 */       ((Assignment)getChild(i)).setNamespaceExp(namespaceExp);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  55 */     return getChildBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  60 */     StringBuilder buf = new StringBuilder();
/*  61 */     if (canonical) buf.append('<'); 
/*  62 */     buf.append(Assignment.getDirectiveName(this.scope));
/*  63 */     if (canonical) {
/*  64 */       buf.append(' ');
/*  65 */       int ln = getChildCount();
/*  66 */       for (int i = 0; i < ln; i++) {
/*  67 */         if (i != 0) {
/*  68 */           buf.append(", ");
/*     */         }
/*  70 */         Assignment assignment = (Assignment)getChild(i);
/*  71 */         buf.append(assignment.getCanonicalForm());
/*     */       } 
/*     */     } else {
/*  74 */       buf.append("-container");
/*     */     } 
/*  76 */     if (this.namespaceExp != null) {
/*  77 */       buf.append(" in ");
/*  78 */       buf.append(this.namespaceExp.getCanonicalForm());
/*     */     } 
/*  80 */     if (canonical) buf.append(">"); 
/*  81 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  86 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/*  91 */     switch (idx) { case 0:
/*  92 */         return Integer.valueOf(this.scope);
/*  93 */       case 1: return this.namespaceExp; }
/*  94 */      return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 100 */     switch (idx) { case 0:
/* 101 */         return ParameterRole.VARIABLE_SCOPE;
/* 102 */       case 1: return ParameterRole.NAMESPACE; }
/* 103 */      return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 109 */     return Assignment.getDirectiveName(this.scope);
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 114 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\AssignmentInstruction.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */