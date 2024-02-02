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
/*     */ 
/*     */ final class RecurseNode
/*     */   extends TemplateElement
/*     */ {
/*     */   Expression targetNode;
/*     */   Expression namespaces;
/*     */   
/*     */   RecurseNode(Expression targetNode, Expression namespaces) {
/*  42 */     this.targetNode = targetNode;
/*  43 */     this.namespaces = namespaces;
/*     */   }
/*     */   TemplateElement[] accept(Environment env) throws IOException, TemplateException {
/*     */     TemplateSequenceModel templateSequenceModel;
/*     */     SimpleSequence simpleSequence;
/*  48 */     TemplateModel node = (this.targetNode == null) ? null : this.targetNode.eval(env);
/*  49 */     if (node != null && !(node instanceof TemplateNodeModel)) {
/*  50 */       throw new NonNodeException(this.targetNode, node, "node", env);
/*     */     }
/*     */     
/*  53 */     TemplateModel nss = (this.namespaces == null) ? null : this.namespaces.eval(env);
/*  54 */     if (this.namespaces instanceof StringLiteral) {
/*  55 */       Environment.Namespace namespace = env.importLib(((TemplateScalarModel)nss).getAsString(), (String)null);
/*  56 */     } else if (this.namespaces instanceof ListLiteral) {
/*  57 */       templateSequenceModel = ((ListLiteral)this.namespaces).evaluateStringsToNamespaces(env);
/*     */     } 
/*  59 */     if (templateSequenceModel != null) {
/*  60 */       if (templateSequenceModel instanceof freemarker.template.TemplateHashModel) {
/*  61 */         SimpleSequence ss = new SimpleSequence(1, (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/*  62 */         ss.add(templateSequenceModel);
/*  63 */         simpleSequence = ss;
/*  64 */       } else if (!(simpleSequence instanceof TemplateSequenceModel)) {
/*  65 */         if (this.namespaces != null) {
/*  66 */           throw new NonSequenceException(this.namespaces, simpleSequence, env);
/*     */         }
/*     */         
/*  69 */         throw new _MiscTemplateException(env, "Expecting a sequence of namespaces after \"using\"");
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  74 */     env.recurse((TemplateNodeModel)node, (TemplateSequenceModel)simpleSequence);
/*  75 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  80 */     StringBuilder sb = new StringBuilder();
/*  81 */     if (canonical) sb.append('<'); 
/*  82 */     sb.append(getNodeTypeSymbol());
/*  83 */     if (this.targetNode != null) {
/*  84 */       sb.append(' ');
/*  85 */       sb.append(this.targetNode.getCanonicalForm());
/*     */     } 
/*  87 */     if (this.namespaces != null) {
/*  88 */       sb.append(" using ");
/*  89 */       sb.append(this.namespaces.getCanonicalForm());
/*     */     } 
/*  91 */     if (canonical) sb.append("/>"); 
/*  92 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  97 */     return "#recurse";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 102 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 107 */     switch (idx) { case 0:
/* 108 */         return this.targetNode;
/* 109 */       case 1: return this.namespaces; }
/* 110 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 116 */     switch (idx) { case 0:
/* 117 */         return ParameterRole.NODE;
/* 118 */       case 1: return ParameterRole.NAMESPACE; }
/* 119 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 125 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isShownInStackTrace() {
/* 130 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\RecurseNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */