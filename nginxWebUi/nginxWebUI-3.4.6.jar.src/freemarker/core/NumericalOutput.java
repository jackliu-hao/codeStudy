/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.Locale;
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
/*     */ final class NumericalOutput
/*     */   extends Interpolation
/*     */ {
/*     */   private final Expression expression;
/*     */   private final boolean hasFormat;
/*     */   private final int minFracDigits;
/*     */   private final int maxFracDigits;
/*     */   private final MarkupOutputFormat autoEscapeOutputFormat;
/*     */   private volatile FormatHolder formatCache;
/*     */   
/*     */   NumericalOutput(Expression expression, MarkupOutputFormat autoEscapeOutputFormat) {
/*  47 */     this.expression = expression;
/*  48 */     this.hasFormat = false;
/*  49 */     this.minFracDigits = 0;
/*  50 */     this.maxFracDigits = 0;
/*  51 */     this.autoEscapeOutputFormat = autoEscapeOutputFormat;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   NumericalOutput(Expression expression, int minFracDigits, int maxFracDigits, MarkupOutputFormat autoEscapeOutputFormat) {
/*  57 */     this.expression = expression;
/*  58 */     this.hasFormat = true;
/*  59 */     this.minFracDigits = minFracDigits;
/*  60 */     this.maxFracDigits = maxFracDigits;
/*  61 */     this.autoEscapeOutputFormat = autoEscapeOutputFormat;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  66 */     String s = calculateInterpolatedStringOrMarkup(env);
/*  67 */     Writer out = env.getOut();
/*  68 */     if (this.autoEscapeOutputFormat != null) {
/*  69 */       this.autoEscapeOutputFormat.output(s, out);
/*     */     } else {
/*  71 */       out.write(s);
/*     */     } 
/*  73 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String calculateInterpolatedStringOrMarkup(Environment env) throws TemplateException {
/*  78 */     Number num = this.expression.evalToNumber(env);
/*     */     
/*  80 */     FormatHolder fmth = this.formatCache;
/*  81 */     if (fmth == null || !fmth.locale.equals(env.getLocale())) {
/*  82 */       synchronized (this) {
/*  83 */         fmth = this.formatCache;
/*  84 */         if (fmth == null || !fmth.locale.equals(env.getLocale())) {
/*  85 */           NumberFormat fmt = NumberFormat.getNumberInstance(env.getLocale());
/*  86 */           if (this.hasFormat) {
/*  87 */             fmt.setMinimumFractionDigits(this.minFracDigits);
/*  88 */             fmt.setMaximumFractionDigits(this.maxFracDigits);
/*     */           } else {
/*  90 */             fmt.setMinimumFractionDigits(0);
/*  91 */             fmt.setMaximumFractionDigits(50);
/*     */           } 
/*  93 */           fmt.setGroupingUsed(false);
/*  94 */           this.formatCache = new FormatHolder(fmt, env.getLocale());
/*  95 */           fmth = this.formatCache;
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 102 */     String s = fmth.format.format(num);
/* 103 */     return s;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical, boolean inStringLiteral) {
/* 108 */     StringBuilder buf = new StringBuilder("#{");
/* 109 */     String exprCF = this.expression.getCanonicalForm();
/* 110 */     buf.append(inStringLiteral ? StringUtil.FTLStringLiteralEnc(exprCF, '"') : exprCF);
/* 111 */     if (this.hasFormat) {
/* 112 */       buf.append(" ; ");
/* 113 */       buf.append("m");
/* 114 */       buf.append(this.minFracDigits);
/* 115 */       buf.append("M");
/* 116 */       buf.append(this.maxFracDigits);
/*     */     } 
/* 118 */     buf.append("}");
/* 119 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 124 */     return "#{...}";
/*     */   }
/*     */ 
/*     */   
/*     */   boolean heedsOpeningWhitespace() {
/* 129 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean heedsTrailingWhitespace() {
/* 134 */     return true;
/*     */   }
/*     */   
/*     */   private static class FormatHolder {
/*     */     final NumberFormat format;
/*     */     final Locale locale;
/*     */     
/*     */     FormatHolder(NumberFormat format, Locale locale) {
/* 142 */       this.format = format;
/* 143 */       this.locale = locale;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 149 */     return 3;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 154 */     switch (idx) { case 0:
/* 155 */         return this.expression;
/* 156 */       case 1: return this.hasFormat ? Integer.valueOf(this.minFracDigits) : null;
/* 157 */       case 2: return this.hasFormat ? Integer.valueOf(this.maxFracDigits) : null; }
/* 158 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 164 */     switch (idx) { case 0:
/* 165 */         return ParameterRole.CONTENT;
/* 166 */       case 1: return ParameterRole.MINIMUM_DECIMALS;
/* 167 */       case 2: return ParameterRole.MAXIMUM_DECIMALS; }
/* 168 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 174 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NumericalOutput.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */