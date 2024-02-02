/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.MalformedTemplateNameException;
/*     */ import freemarker.template.Template;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public final class LibraryLoad
/*     */   extends TemplateElement
/*     */ {
/*     */   private Expression importedTemplateNameExp;
/*     */   private String targetNsVarName;
/*     */   
/*     */   LibraryLoad(Template template, Expression templateName, String targetNsVarName) {
/*  48 */     this.targetNsVarName = targetNsVarName;
/*  49 */     this.importedTemplateNameExp = templateName;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  54 */     String fullImportedTemplateName, importedTemplateName = this.importedTemplateNameExp.evalAndCoerceToPlainText(env);
/*     */     
/*     */     try {
/*  57 */       fullImportedTemplateName = env.toFullTemplateName(getTemplate().getName(), importedTemplateName);
/*  58 */     } catch (MalformedTemplateNameException e) {
/*  59 */       throw new _MiscTemplateException(e, env, new Object[] { "Malformed template name ", new _DelayedJQuote(e
/*  60 */               .getTemplateName()), ":\n", e
/*  61 */             .getMalformednessDescription() });
/*     */     } 
/*     */     
/*     */     try {
/*  65 */       env.importLib(fullImportedTemplateName, this.targetNsVarName);
/*  66 */     } catch (IOException e) {
/*  67 */       throw new _MiscTemplateException(e, env, new Object[] { "Template importing failed (for parameter value ", new _DelayedJQuote(importedTemplateName), "):\n", new _DelayedGetMessage(e) });
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  72 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  77 */     StringBuilder buf = new StringBuilder();
/*  78 */     if (canonical) buf.append('<'); 
/*  79 */     buf.append(getNodeTypeSymbol());
/*  80 */     buf.append(' ');
/*  81 */     buf.append(this.importedTemplateNameExp.getCanonicalForm());
/*  82 */     buf.append(" as ");
/*  83 */     buf.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.targetNsVarName));
/*  84 */     if (canonical) buf.append("/>"); 
/*  85 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  90 */     return "#import";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  95 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 100 */     switch (idx) { case 0:
/* 101 */         return this.importedTemplateNameExp;
/* 102 */       case 1: return this.targetNsVarName; }
/* 103 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 109 */     switch (idx) { case 0:
/* 110 */         return ParameterRole.TEMPLATE_NAME;
/* 111 */       case 1: return ParameterRole.NAMESPACE; }
/* 112 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTemplateName() {
/* 117 */     return this.importedTemplateNameExp.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 122 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isShownInStackTrace() {
/* 127 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\LibraryLoad.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */