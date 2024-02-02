/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
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
/*     */ final class DollarVariable
/*     */   extends Interpolation
/*     */ {
/*     */   private final Expression expression;
/*     */   private final Expression escapedExpression;
/*     */   private final OutputFormat outputFormat;
/*     */   private final MarkupOutputFormat markupOutputFormat;
/*     */   private final boolean autoEscape;
/*     */   
/*     */   DollarVariable(Expression expression, Expression escapedExpression, OutputFormat outputFormat, boolean autoEscape) {
/*  50 */     this.expression = expression;
/*  51 */     this.escapedExpression = escapedExpression;
/*  52 */     this.outputFormat = outputFormat;
/*  53 */     this.markupOutputFormat = (outputFormat instanceof MarkupOutputFormat) ? (MarkupOutputFormat)outputFormat : null;
/*     */     
/*  55 */     this.autoEscape = autoEscape;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  63 */     Object moOrStr = calculateInterpolatedStringOrMarkup(env);
/*  64 */     Writer out = env.getOut();
/*  65 */     if (moOrStr instanceof String) {
/*  66 */       String s = (String)moOrStr;
/*  67 */       if (this.autoEscape) {
/*  68 */         this.markupOutputFormat.output(s, out);
/*     */       } else {
/*  70 */         out.write(s);
/*     */       } 
/*     */     } else {
/*  73 */       TemplateMarkupOutputModel mo = (TemplateMarkupOutputModel)moOrStr;
/*  74 */       MarkupOutputFormat<TemplateMarkupOutputModel> moOF = mo.getOutputFormat();
/*     */       
/*  76 */       if (moOF != this.outputFormat && !this.outputFormat.isOutputFormatMixingAllowed()) {
/*     */ 
/*     */         
/*  79 */         String srcPlainText = moOF.getSourcePlainText(mo);
/*  80 */         if (srcPlainText == null) {
/*  81 */           throw new _TemplateModelException(this.escapedExpression, new Object[] { "The value to print is in ", new _DelayedToString(moOF), " format, which differs from the current output format, ", new _DelayedToString(this.outputFormat), ". Format conversion wasn't possible." });
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*  86 */         if (this.outputFormat instanceof MarkupOutputFormat) {
/*  87 */           ((MarkupOutputFormat)this.outputFormat).output(srcPlainText, out);
/*     */         } else {
/*  89 */           out.write(srcPlainText);
/*     */         } 
/*     */       } else {
/*  92 */         moOF.output(mo, out);
/*     */       } 
/*     */     } 
/*  95 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object calculateInterpolatedStringOrMarkup(Environment env) throws TemplateException {
/* 100 */     return EvalUtil.coerceModelToStringOrMarkup(this.escapedExpression.eval(env), this.escapedExpression, null, env);
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical, boolean inStringLiteral) {
/* 105 */     StringBuilder sb = new StringBuilder();
/* 106 */     int syntax = getTemplate().getInterpolationSyntax();
/* 107 */     sb.append((syntax != 22) ? "${" : "[=");
/* 108 */     String exprCF = this.expression.getCanonicalForm();
/* 109 */     sb.append(inStringLiteral ? StringUtil.FTLStringLiteralEnc(exprCF, '"') : exprCF);
/* 110 */     sb.append((syntax != 22) ? "}" : "]");
/* 111 */     if (!canonical && this.expression != this.escapedExpression) {
/* 112 */       sb.append(" auto-escaped");
/*     */     }
/* 114 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 119 */     return "${...}";
/*     */   }
/*     */ 
/*     */   
/*     */   boolean heedsOpeningWhitespace() {
/* 124 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean heedsTrailingWhitespace() {
/* 129 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 134 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 139 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 140 */     return this.expression;
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 145 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 146 */     return ParameterRole.CONTENT;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 151 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\DollarVariable.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */