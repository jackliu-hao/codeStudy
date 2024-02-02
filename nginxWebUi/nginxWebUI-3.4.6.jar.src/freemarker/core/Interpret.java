/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template.TemplateTransformModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.io.Writer;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class Interpret
/*     */   extends OutputFormatBoundBuiltIn
/*     */ {
/*     */   protected TemplateModel calculateResult(Environment env) throws TemplateException {
/*     */     Template interpretedTemplate;
/*  66 */     TemplateModel model = this.target.eval(env);
/*  67 */     Expression sourceExpr = null;
/*  68 */     String id = "anonymous_interpreted";
/*  69 */     if (model instanceof TemplateSequenceModel) {
/*  70 */       sourceExpr = (Expression)(new DynamicKeyName(this.target, new NumberLiteral(Integer.valueOf(0)))).copyLocationFrom(this.target);
/*  71 */       if (((TemplateSequenceModel)model).size() > 1) {
/*  72 */         id = ((Expression)(new DynamicKeyName(this.target, new NumberLiteral(Integer.valueOf(1)))).copyLocationFrom(this.target)).evalAndCoerceToPlainText(env);
/*     */       }
/*  74 */     } else if (model instanceof TemplateScalarModel) {
/*  75 */       sourceExpr = this.target;
/*     */     } else {
/*  77 */       throw new UnexpectedTypeException(this.target, model, "sequence or string", new Class[] { TemplateSequenceModel.class, TemplateScalarModel.class }, env);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  82 */     String templateSource = sourceExpr.evalAndCoerceToPlainText(env);
/*     */     
/*  84 */     Template parentTemplate = (env.getConfiguration().getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_26) ? env.getCurrentTemplate() : env.getTemplate();
/*     */ 
/*     */     
/*     */     try {
/*  88 */       ParserConfiguration pCfg = parentTemplate.getParserConfiguration();
/*     */       
/*  90 */       if (pCfg.getOutputFormat() != this.outputFormat)
/*     */       {
/*  92 */         pCfg = new _ParserConfigurationWithInheritedFormat(pCfg, this.outputFormat, Integer.valueOf(this.autoEscapingPolicy));
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  98 */       interpretedTemplate = new Template(((parentTemplate.getName() != null) ? parentTemplate.getName() : "nameless_template") + "->" + id, null, new StringReader(templateSource), parentTemplate.getConfiguration(), pCfg, null);
/*     */     }
/* 100 */     catch (IOException e) {
/* 101 */       throw new _MiscTemplateException(this, e, env, new Object[] { "Template parsing with \"?", this.key, "\" has failed with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(e), "\n---end-message---", "\n\nThe failed expression:" });
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 109 */     interpretedTemplate.setLocale(env.getLocale());
/* 110 */     return (TemplateModel)new TemplateProcessorModel(interpretedTemplate);
/*     */   }
/*     */   
/*     */   private class TemplateProcessorModel
/*     */     implements TemplateTransformModel
/*     */   {
/*     */     private final Template template;
/*     */     
/*     */     TemplateProcessorModel(Template template) {
/* 119 */       this.template = template;
/*     */     }
/*     */ 
/*     */     
/*     */     public Writer getWriter(final Writer out, Map args) throws TemplateModelException, IOException {
/*     */       try {
/* 125 */         Environment env = Environment.getCurrentEnvironment();
/* 126 */         boolean lastFIRE = env.setFastInvalidReferenceExceptions(false);
/*     */         try {
/* 128 */           env.include(this.template);
/*     */         } finally {
/* 130 */           env.setFastInvalidReferenceExceptions(lastFIRE);
/*     */         } 
/* 132 */       } catch (Exception e) {
/* 133 */         throw new _TemplateModelException(e, new Object[] { "Template created with \"?", this.this$0.key, "\" has stopped with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(e), "\n---end-message---" });
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 140 */       return new Writer(out)
/*     */         {
/*     */           public void close() {}
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public void flush() throws IOException {
/* 148 */             out.flush();
/*     */           }
/*     */ 
/*     */           
/*     */           public void write(char[] cbuf, int off, int len) throws IOException {
/* 153 */             out.write(cbuf, off, len);
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Interpret.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */