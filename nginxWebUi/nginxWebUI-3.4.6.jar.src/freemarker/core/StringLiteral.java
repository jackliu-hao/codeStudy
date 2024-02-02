/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.StringReader;
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
/*     */ final class StringLiteral
/*     */   extends Expression
/*     */   implements TemplateScalarModel
/*     */ {
/*     */   private final String value;
/*     */   private List<Object> dynamicValue;
/*     */   
/*     */   StringLiteral(String value) {
/*  41 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void parseValue(FMParser parentParser, OutputFormat outputFormat) throws ParseException {
/*  51 */     Template parentTemplate = getTemplate();
/*  52 */     ParserConfiguration pcfg = parentTemplate.getParserConfiguration();
/*  53 */     int intSyn = pcfg.getInterpolationSyntax();
/*  54 */     if (this.value.length() > 3 && (((intSyn == 20 || intSyn == 21) && (this.value
/*     */ 
/*     */       
/*  57 */       .indexOf("${") != -1 || (intSyn == 20 && this.value
/*  58 */       .indexOf("#{") != -1))) || (intSyn == 22 && this.value
/*  59 */       .indexOf("[=") != -1))) {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/*  64 */         SimpleCharStream simpleCharacterStream = new SimpleCharStream(new StringReader(this.value), this.beginLine, this.beginColumn + 1, this.value.length());
/*  65 */         simpleCharacterStream.setTabSize(pcfg.getTabSize());
/*     */         
/*  67 */         FMParserTokenManager tkMan = new FMParserTokenManager(simpleCharacterStream);
/*     */ 
/*     */         
/*  70 */         FMParser parser = new FMParser(parentTemplate, false, tkMan, pcfg);
/*     */         
/*  72 */         parser.setupStringLiteralMode(parentParser, outputFormat);
/*     */         try {
/*  74 */           this.dynamicValue = parser.StaticTextAndInterpolations();
/*     */         } finally {
/*     */           
/*  77 */           parser.tearDownStringLiteralMode(parentParser);
/*     */         } 
/*  79 */       } catch (ParseException e) {
/*  80 */         e.setTemplateName(parentTemplate.getSourceName());
/*  81 */         throw e;
/*     */       } 
/*  83 */       this.constantValue = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*  89 */     if (this.dynamicValue == null) {
/*  90 */       return (TemplateModel)new SimpleScalar(this.value);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  97 */     StringBuilder plainTextResult = null;
/*  98 */     TemplateMarkupOutputModel<?> markupResult = null;
/*     */     
/* 100 */     for (Object part : this.dynamicValue) {
/*     */ 
/*     */       
/* 103 */       Object calcedPart = (part instanceof String) ? part : ((Interpolation)part).calculateInterpolatedStringOrMarkup(env);
/* 104 */       if (markupResult != null) {
/*     */         
/* 106 */         TemplateMarkupOutputModel<?> partMO = (calcedPart instanceof String) ? markupResult.getOutputFormat().fromPlainTextByEscaping((String)calcedPart) : (TemplateMarkupOutputModel)calcedPart;
/*     */         
/* 108 */         markupResult = EvalUtil.concatMarkupOutputs(this, markupResult, partMO); continue;
/*     */       } 
/* 110 */       if (calcedPart instanceof String) {
/* 111 */         String partStr = (String)calcedPart;
/* 112 */         if (plainTextResult == null) {
/* 113 */           plainTextResult = new StringBuilder(partStr); continue;
/*     */         } 
/* 115 */         plainTextResult.append(partStr);
/*     */         continue;
/*     */       } 
/* 118 */       TemplateMarkupOutputModel<?> moPart = (TemplateMarkupOutputModel)calcedPart;
/* 119 */       if (plainTextResult != null) {
/*     */         
/* 121 */         TemplateMarkupOutputModel<?> leftHandMO = moPart.getOutputFormat().fromPlainTextByEscaping(plainTextResult.toString());
/* 122 */         markupResult = EvalUtil.concatMarkupOutputs(this, leftHandMO, moPart);
/* 123 */         plainTextResult = null; continue;
/*     */       } 
/* 125 */       markupResult = moPart;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 130 */     return (markupResult != null) ? markupResult : ((plainTextResult != null) ? (TemplateModel)new SimpleScalar(plainTextResult
/* 131 */         .toString()) : SimpleScalar.EMPTY_STRING);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAsString() {
/* 138 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isSingleInterpolationLiteral() {
/* 145 */     return (this.dynamicValue != null && this.dynamicValue.size() == 1 && this.dynamicValue
/* 146 */       .get(0) instanceof Interpolation);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/* 151 */     if (this.dynamicValue == null) {
/* 152 */       return StringUtil.ftlQuote(this.value);
/*     */     }
/* 154 */     StringBuilder sb = new StringBuilder();
/* 155 */     sb.append('"');
/* 156 */     for (Object child : this.dynamicValue) {
/* 157 */       if (child instanceof Interpolation) {
/* 158 */         sb.append(((Interpolation)child).getCanonicalFormInStringLiteral()); continue;
/*     */       } 
/* 160 */       sb.append(StringUtil.FTLStringLiteralEnc((String)child, '"'));
/*     */     } 
/*     */     
/* 163 */     sb.append('"');
/* 164 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 170 */     return (this.dynamicValue == null) ? getCanonicalForm() : "dynamic \"...\"";
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/* 175 */     return (this.dynamicValue == null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 181 */     StringLiteral cloned = new StringLiteral(this.value);
/*     */     
/* 183 */     cloned.dynamicValue = this.dynamicValue;
/* 184 */     return cloned;
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 189 */     return (this.dynamicValue == null) ? 0 : this.dynamicValue.size();
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 194 */     checkIndex(idx);
/* 195 */     return this.dynamicValue.get(idx);
/*     */   }
/*     */   
/*     */   private void checkIndex(int idx) {
/* 199 */     if (this.dynamicValue == null || idx >= this.dynamicValue.size()) {
/* 200 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 206 */     checkIndex(idx);
/* 207 */     return ParameterRole.VALUE_PART;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\StringLiteral.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */