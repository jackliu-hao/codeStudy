/*     */ package freemarker.core;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ThreadInterruptionSupportTemplatePostProcessor
/*     */   extends TemplatePostProcessor
/*     */ {
/*     */   public void postProcess(Template t) throws TemplatePostProcessorException {
/*  52 */     TemplateElement te = t.getRootTreeNode();
/*  53 */     addInterruptionChecks(te);
/*     */   }
/*     */   
/*     */   private void addInterruptionChecks(TemplateElement te) throws TemplatePostProcessorException {
/*  57 */     if (te == null) {
/*     */       return;
/*     */     }
/*     */     
/*  61 */     int childCount = te.getChildCount();
/*  62 */     for (int i = 0; i < childCount; i++) {
/*  63 */       addInterruptionChecks(te.getChild(i));
/*     */     }
/*     */     
/*  66 */     if (te.isNestedBlockRepeater()) {
/*     */       try {
/*  68 */         te.addChild(0, new ThreadInterruptionCheck(te));
/*  69 */       } catch (ParseException e) {
/*  70 */         throw new TemplatePostProcessorException("Unexpected error; see cause", e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class ThreadInterruptionCheck
/*     */     extends TemplateElement
/*     */   {
/*     */     private ThreadInterruptionCheck(TemplateElement te) throws ParseException {
/*  82 */       setLocation(te.getTemplate(), te.beginColumn, te.beginLine, te.beginColumn, te.beginLine);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  89 */       if (Thread.currentThread().isInterrupted()) {
/*  90 */         throw new ThreadInterruptionSupportTemplatePostProcessor.TemplateProcessingThreadInterruptedException();
/*     */       }
/*  92 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     protected String dump(boolean canonical) {
/*  97 */       return canonical ? "" : ("<#--" + getNodeTypeSymbol() + "--#>");
/*     */     }
/*     */ 
/*     */     
/*     */     String getNodeTypeSymbol() {
/* 102 */       return "##threadInterruptionCheck";
/*     */     }
/*     */ 
/*     */     
/*     */     int getParameterCount() {
/* 107 */       return 0;
/*     */     }
/*     */ 
/*     */     
/*     */     Object getParameterValue(int idx) {
/* 112 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */ 
/*     */     
/*     */     ParameterRole getParameterRole(int idx) {
/* 117 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */ 
/*     */     
/*     */     boolean isNestedBlockRepeater() {
/* 122 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class TemplateProcessingThreadInterruptedException
/*     */     extends FlowControlException
/*     */   {
/*     */     TemplateProcessingThreadInterruptedException() {
/* 136 */       super("Template processing thread \"interrupted\" flag was set.");
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ThreadInterruptionSupportTemplatePostProcessor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */