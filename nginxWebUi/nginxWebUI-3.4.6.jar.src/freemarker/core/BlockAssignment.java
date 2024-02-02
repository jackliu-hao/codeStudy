/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.io.IOException;
/*     */ import java.io.StringWriter;
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
/*     */ final class BlockAssignment
/*     */   extends TemplateElement
/*     */ {
/*     */   private final String varName;
/*     */   private final Expression namespaceExp;
/*     */   private final int scope;
/*     */   private final MarkupOutputFormat<?> markupOutputFormat;
/*     */   
/*     */   BlockAssignment(TemplateElements children, String varName, int scope, Expression namespaceExp, MarkupOutputFormat<?> markupOutputFormat) {
/*  41 */     setChildren(children);
/*  42 */     this.varName = varName;
/*  43 */     this.namespaceExp = namespaceExp;
/*  44 */     this.scope = scope;
/*  45 */     this.markupOutputFormat = markupOutputFormat;
/*     */   }
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*     */     TemplateModel value;
/*  50 */     TemplateElement[] children = getChildBuffer();
/*     */ 
/*     */     
/*  53 */     if (children != null) {
/*  54 */       StringWriter out = new StringWriter();
/*  55 */       env.visit(children, out);
/*  56 */       value = capturedStringToModel(out.toString());
/*     */     } else {
/*  58 */       value = capturedStringToModel("");
/*     */     } 
/*     */     
/*  61 */     if (this.namespaceExp != null) {
/*     */       Environment.Namespace namespace;
/*  63 */       TemplateModel uncheckedNamespace = this.namespaceExp.eval(env);
/*     */       try {
/*  65 */         namespace = (Environment.Namespace)uncheckedNamespace;
/*  66 */       } catch (ClassCastException e) {
/*  67 */         throw new NonNamespaceException(this.namespaceExp, uncheckedNamespace, env);
/*     */       } 
/*  69 */       if (namespace == null) {
/*  70 */         throw InvalidReferenceException.getInstance(this.namespaceExp, env);
/*     */       }
/*  72 */       namespace.put(this.varName, value);
/*  73 */     } else if (this.scope == 1) {
/*  74 */       env.setVariable(this.varName, value);
/*  75 */     } else if (this.scope == 3) {
/*  76 */       env.setGlobalVariable(this.varName, value);
/*  77 */     } else if (this.scope == 2) {
/*  78 */       env.setLocalVariable(this.varName, value);
/*     */     } else {
/*  80 */       throw new BugException("Unhandled scope");
/*     */     } 
/*     */     
/*  83 */     return null;
/*     */   }
/*     */   
/*     */   private TemplateModel capturedStringToModel(String s) throws TemplateModelException {
/*  87 */     return (this.markupOutputFormat == null) ? (TemplateModel)new SimpleScalar(s) : (TemplateModel)this.markupOutputFormat.fromMarkup(s);
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  92 */     StringBuilder sb = new StringBuilder();
/*  93 */     if (canonical) sb.append("<"); 
/*  94 */     sb.append(getNodeTypeSymbol());
/*  95 */     sb.append(' ');
/*  96 */     sb.append(this.varName);
/*  97 */     if (this.namespaceExp != null) {
/*  98 */       sb.append(" in ");
/*  99 */       sb.append(this.namespaceExp.getCanonicalForm());
/*     */     } 
/* 101 */     if (canonical) {
/* 102 */       sb.append('>');
/* 103 */       sb.append(getChildrenCanonicalForm());
/* 104 */       sb.append("</");
/* 105 */       sb.append(getNodeTypeSymbol());
/* 106 */       sb.append('>');
/*     */     } else {
/* 108 */       sb.append(" = .nested_output");
/*     */     } 
/* 110 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 115 */     return Assignment.getDirectiveName(this.scope);
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 120 */     return 3;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 125 */     switch (idx) { case 0:
/* 126 */         return this.varName;
/* 127 */       case 1: return Integer.valueOf(this.scope);
/* 128 */       case 2: return this.namespaceExp; }
/* 129 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 135 */     switch (idx) { case 0:
/* 136 */         return ParameterRole.ASSIGNMENT_TARGET;
/* 137 */       case 1: return ParameterRole.VARIABLE_SCOPE;
/* 138 */       case 2: return ParameterRole.NAMESPACE; }
/* 139 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 145 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BlockAssignment.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */