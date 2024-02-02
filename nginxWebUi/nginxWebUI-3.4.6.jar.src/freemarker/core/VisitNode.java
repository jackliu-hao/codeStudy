/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateNodeModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template._TemplateAPI;
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
/*     */ final class VisitNode
/*     */   extends TemplateElement
/*     */ {
/*     */   Expression targetNode;
/*     */   Expression namespaces;
/*     */   
/*     */   VisitNode(Expression targetNode, Expression namespaces) {
/*  41 */     this.targetNode = targetNode;
/*  42 */     this.namespaces = namespaces;
/*     */   }
/*     */   TemplateElement[] accept(Environment env) throws IOException, TemplateException {
/*     */     TemplateSequenceModel templateSequenceModel;
/*     */     SimpleSequence simpleSequence;
/*  47 */     TemplateModel node = this.targetNode.eval(env);
/*  48 */     if (!(node instanceof TemplateNodeModel)) {
/*  49 */       throw new NonNodeException(this.targetNode, node, env);
/*     */     }
/*     */     
/*  52 */     TemplateModel nss = (this.namespaces == null) ? null : this.namespaces.eval(env);
/*  53 */     if (this.namespaces instanceof StringLiteral) {
/*  54 */       Environment.Namespace namespace = env.importLib(((TemplateScalarModel)nss).getAsString(), (String)null);
/*  55 */     } else if (this.namespaces instanceof ListLiteral) {
/*  56 */       templateSequenceModel = ((ListLiteral)this.namespaces).evaluateStringsToNamespaces(env);
/*     */     } 
/*  58 */     if (templateSequenceModel != null) {
/*  59 */       if (templateSequenceModel instanceof Environment.Namespace) {
/*  60 */         SimpleSequence ss = new SimpleSequence(1, (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/*  61 */         ss.add(templateSequenceModel);
/*  62 */         simpleSequence = ss;
/*  63 */       } else if (!(simpleSequence instanceof TemplateSequenceModel)) {
/*  64 */         if (this.namespaces != null) {
/*  65 */           throw new NonSequenceException(this.namespaces, simpleSequence, env);
/*     */         }
/*     */         
/*  68 */         throw new _MiscTemplateException(env, "Expecting a sequence of namespaces after \"using\"");
/*     */       } 
/*     */     }
/*     */     
/*  72 */     env.invokeNodeHandlerFor((TemplateNodeModel)node, (TemplateSequenceModel)simpleSequence);
/*  73 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  78 */     StringBuilder sb = new StringBuilder();
/*  79 */     if (canonical) sb.append('<'); 
/*  80 */     sb.append(getNodeTypeSymbol());
/*  81 */     sb.append(' ');
/*  82 */     sb.append(this.targetNode.getCanonicalForm());
/*  83 */     if (this.namespaces != null) {
/*  84 */       sb.append(" using ");
/*  85 */       sb.append(this.namespaces.getCanonicalForm());
/*     */     } 
/*  87 */     if (canonical) sb.append("/>"); 
/*  88 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  93 */     return "#visit";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  98 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 103 */     switch (idx) { case 0:
/* 104 */         return this.targetNode;
/* 105 */       case 1: return this.namespaces; }
/* 106 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 112 */     switch (idx) { case 0:
/* 113 */         return ParameterRole.NODE;
/* 114 */       case 1: return ParameterRole.NAMESPACE; }
/* 115 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 121 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isShownInStackTrace() {
/* 126 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\VisitNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */