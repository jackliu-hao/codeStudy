/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.MalformedTemplateNameException;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.utility.StringUtil;
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
/*     */ final class Include
/*     */   extends TemplateElement
/*     */ {
/*     */   private final Expression includedTemplateNameExp;
/*     */   private final Expression encodingExp;
/*     */   private final Expression parseExp;
/*     */   private final Expression ignoreMissingExp;
/*     */   private final String encoding;
/*     */   private final Boolean parse;
/*     */   private final Boolean ignoreMissingExpPrecalcedValue;
/*     */   
/*     */   Include(Template template, Expression includedTemplatePathExp, Expression encodingExp, Expression parseExp, Expression ignoreMissingExp) throws ParseException {
/*  52 */     this.includedTemplateNameExp = includedTemplatePathExp;
/*     */     
/*  54 */     this.encodingExp = encodingExp;
/*  55 */     if (encodingExp == null) {
/*  56 */       this.encoding = null;
/*     */     }
/*  58 */     else if (encodingExp.isLiteral()) {
/*     */       try {
/*  60 */         TemplateModel tm = encodingExp.eval((Environment)null);
/*  61 */         if (!(tm instanceof TemplateScalarModel)) {
/*  62 */           throw new ParseException("Expected a string as the value of the \"encoding\" argument", encodingExp);
/*     */         }
/*     */         
/*  65 */         this.encoding = ((TemplateScalarModel)tm).getAsString();
/*  66 */       } catch (TemplateException e) {
/*     */         
/*  68 */         throw new BugException(e);
/*     */       } 
/*     */     } else {
/*  71 */       this.encoding = null;
/*     */     } 
/*     */ 
/*     */     
/*  75 */     this.parseExp = parseExp;
/*  76 */     if (parseExp == null) {
/*  77 */       this.parse = Boolean.TRUE;
/*     */     }
/*  79 */     else if (parseExp.isLiteral()) {
/*     */       try {
/*  81 */         if (parseExp instanceof StringLiteral) {
/*     */           
/*  83 */           this.parse = Boolean.valueOf(StringUtil.getYesNo(parseExp.evalAndCoerceToPlainText((Environment)null)));
/*     */         } else {
/*     */           try {
/*  86 */             this.parse = Boolean.valueOf(parseExp.evalToBoolean(template.getConfiguration()));
/*  87 */           } catch (NonBooleanException e) {
/*  88 */             throw new ParseException("Expected a boolean or string as the value of the parse attribute", parseExp, e);
/*     */           }
/*     */         
/*     */         } 
/*  92 */       } catch (TemplateException e) {
/*     */         
/*  94 */         throw new BugException(e);
/*     */       } 
/*     */     } else {
/*  97 */       this.parse = null;
/*     */     } 
/*     */ 
/*     */     
/* 101 */     this.ignoreMissingExp = ignoreMissingExp;
/* 102 */     if (ignoreMissingExp != null && ignoreMissingExp.isLiteral()) {
/*     */       try {
/*     */         try {
/* 105 */           this.ignoreMissingExpPrecalcedValue = Boolean.valueOf(ignoreMissingExp
/* 106 */               .evalToBoolean(template.getConfiguration()));
/* 107 */         } catch (NonBooleanException e) {
/* 108 */           throw new ParseException("Expected a boolean as the value of the \"ignore_missing\" attribute", ignoreMissingExp, e);
/*     */         }
/*     */       
/* 111 */       } catch (TemplateException e) {
/*     */         
/* 113 */         throw new BugException(e);
/*     */       } 
/*     */     } else {
/* 116 */       this.ignoreMissingExpPrecalcedValue = null;
/*     */     } 
/*     */   } TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*     */     String fullIncludedTemplateName;
/*     */     boolean parse, ignoreMissing;
/*     */     Template includedTemplate;
/* 122 */     String includedTemplateName = this.includedTemplateNameExp.evalAndCoerceToPlainText(env);
/*     */     
/*     */     try {
/* 125 */       fullIncludedTemplateName = env.toFullTemplateName(getTemplate().getName(), includedTemplateName);
/* 126 */     } catch (MalformedTemplateNameException e) {
/* 127 */       throw new _MiscTemplateException(e, env, new Object[] { "Malformed template name ", new _DelayedJQuote(e
/* 128 */               .getTemplateName()), ":\n", e
/* 129 */             .getMalformednessDescription() });
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 135 */     String encoding = (this.encoding != null) ? this.encoding : ((this.encodingExp != null) ? this.encodingExp.evalAndCoerceToPlainText(env) : null);
/*     */ 
/*     */ 
/*     */     
/* 139 */     if (this.parse != null) {
/* 140 */       parse = this.parse.booleanValue();
/*     */     } else {
/* 142 */       TemplateModel tm = this.parseExp.eval(env);
/* 143 */       if (tm instanceof TemplateScalarModel) {
/*     */         
/* 145 */         parse = getYesNo(this.parseExp, EvalUtil.modelToString((TemplateScalarModel)tm, this.parseExp, env));
/*     */       } else {
/* 147 */         parse = this.parseExp.modelToBoolean(tm, env);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 152 */     if (this.ignoreMissingExpPrecalcedValue != null) {
/* 153 */       ignoreMissing = this.ignoreMissingExpPrecalcedValue.booleanValue();
/* 154 */     } else if (this.ignoreMissingExp != null) {
/* 155 */       ignoreMissing = this.ignoreMissingExp.evalToBoolean(env);
/*     */     } else {
/* 157 */       ignoreMissing = false;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 162 */       includedTemplate = env.getTemplateForInclusion(fullIncludedTemplateName, encoding, parse, ignoreMissing);
/* 163 */     } catch (IOException e) {
/* 164 */       throw new _MiscTemplateException(e, env, new Object[] { "Template inclusion failed (for parameter value ", new _DelayedJQuote(includedTemplateName), "):\n", new _DelayedGetMessage(e) });
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 170 */     if (includedTemplate != null) {
/* 171 */       env.include(includedTemplate);
/*     */     }
/* 173 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/* 178 */     StringBuilder buf = new StringBuilder();
/* 179 */     if (canonical) buf.append('<'); 
/* 180 */     buf.append(getNodeTypeSymbol());
/* 181 */     buf.append(' ');
/* 182 */     buf.append(this.includedTemplateNameExp.getCanonicalForm());
/* 183 */     if (this.encodingExp != null) {
/* 184 */       buf.append(" encoding=").append(this.encodingExp.getCanonicalForm());
/*     */     }
/* 186 */     if (this.parseExp != null) {
/* 187 */       buf.append(" parse=").append(this.parseExp.getCanonicalForm());
/*     */     }
/* 189 */     if (this.ignoreMissingExp != null) {
/* 190 */       buf.append(" ignore_missing=").append(this.ignoreMissingExp.getCanonicalForm());
/*     */     }
/* 192 */     if (canonical) buf.append("/>"); 
/* 193 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 198 */     return "#include";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 203 */     return 4;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 208 */     switch (idx) { case 0:
/* 209 */         return this.includedTemplateNameExp;
/* 210 */       case 1: return this.parseExp;
/* 211 */       case 2: return this.encodingExp;
/* 212 */       case 3: return this.ignoreMissingExp; }
/* 213 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 219 */     switch (idx) { case 0:
/* 220 */         return ParameterRole.TEMPLATE_NAME;
/* 221 */       case 1: return ParameterRole.PARSE_PARAMETER;
/* 222 */       case 2: return ParameterRole.ENCODING_PARAMETER;
/* 223 */       case 3: return ParameterRole.IGNORE_MISSING_PARAMETER; }
/* 224 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 230 */     return false;
/*     */   }
/*     */   
/*     */   private boolean getYesNo(Expression exp, String s) throws TemplateException {
/*     */     try {
/* 235 */       return StringUtil.getYesNo(s);
/* 236 */     } catch (IllegalArgumentException iae) {
/* 237 */       throw new _MiscTemplateException(exp, new Object[] { "Value must be boolean (or one of these strings: \"n\", \"no\", \"f\", \"false\", \"y\", \"yes\", \"t\", \"true\"), but it was ", new _DelayedJQuote(s), "." });
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
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isShownInStackTrace() {
/* 256 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Include.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */