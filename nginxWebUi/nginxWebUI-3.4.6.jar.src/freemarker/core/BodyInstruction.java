/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ final class BodyInstruction
/*     */   extends TemplateElement
/*     */ {
/*     */   private List bodyParameters;
/*     */   
/*     */   BodyInstruction(List bodyParameters) {
/*  41 */     this.bodyParameters = bodyParameters;
/*     */   }
/*     */   
/*     */   List getBodyParameters() {
/*  45 */     return this.bodyParameters;
/*     */   }
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
/*     */   TemplateElement[] accept(Environment env) throws IOException, TemplateException {
/*  59 */     Context bodyContext = new Context(env);
/*  60 */     env.invokeNestedContent(bodyContext);
/*  61 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  66 */     StringBuilder sb = new StringBuilder();
/*  67 */     if (canonical) sb.append('<'); 
/*  68 */     sb.append(getNodeTypeSymbol());
/*  69 */     if (this.bodyParameters != null) {
/*  70 */       for (int i = 0; i < this.bodyParameters.size(); i++) {
/*  71 */         sb.append(' ');
/*  72 */         sb.append(((Expression)this.bodyParameters.get(i)).getCanonicalForm());
/*     */       } 
/*     */     }
/*  75 */     if (canonical) sb.append('>'); 
/*  76 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  81 */     return "#nested";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  86 */     return (this.bodyParameters != null) ? this.bodyParameters.size() : 0;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/*  91 */     checkIndex(idx);
/*  92 */     return this.bodyParameters.get(idx);
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/*  97 */     checkIndex(idx);
/*  98 */     return ParameterRole.PASSED_VALUE;
/*     */   }
/*     */   
/*     */   private void checkIndex(int idx) {
/* 102 */     if (this.bodyParameters == null || idx >= this.bodyParameters.size()) {
/* 103 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */   }
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
/*     */   boolean isShownInStackTrace() {
/* 119 */     return true;
/*     */   }
/*     */   
/*     */   class Context implements LocalContext {
/*     */     Macro.Context invokingMacroContext;
/*     */     Environment.Namespace bodyVars;
/*     */     
/*     */     Context(Environment env) throws TemplateException {
/* 127 */       this.invokingMacroContext = env.getCurrentMacroContext();
/* 128 */       List<String> bodyParameterNames = this.invokingMacroContext.nestedContentParameterNames;
/* 129 */       if (BodyInstruction.this.bodyParameters != null) {
/* 130 */         for (int i = 0; i < BodyInstruction.this.bodyParameters.size(); i++) {
/* 131 */           Expression exp = BodyInstruction.this.bodyParameters.get(i);
/* 132 */           TemplateModel tm = exp.eval(env);
/* 133 */           if (bodyParameterNames != null && i < bodyParameterNames.size()) {
/* 134 */             String bodyParameterName = bodyParameterNames.get(i);
/* 135 */             if (this.bodyVars == null) {
/* 136 */               env.getClass(); this.bodyVars = new Environment.Namespace(env);
/*     */             } 
/* 138 */             this.bodyVars.put(bodyParameterName, (tm != null) ? tm : (
/*     */ 
/*     */ 
/*     */                 
/* 142 */                 BodyInstruction.this.getTemplate().getConfiguration().getFallbackOnNullLoopVariable() ? null : TemplateNullModel.INSTANCE));
/*     */           } 
/*     */         } 
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public TemplateModel getLocalVariable(String name) throws TemplateModelException {
/* 151 */       return (this.bodyVars == null) ? null : this.bodyVars.get(name);
/*     */     }
/*     */ 
/*     */     
/*     */     public Collection getLocalVariableNames() {
/* 156 */       List<String> bodyParameterNames = this.invokingMacroContext.nestedContentParameterNames;
/* 157 */       return (bodyParameterNames == null) ? Collections.EMPTY_LIST : bodyParameterNames;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 163 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BodyInstruction.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */