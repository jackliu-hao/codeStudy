/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.util.Arrays;
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
/*     */ final class PropertySetting
/*     */   extends TemplateElement
/*     */ {
/*     */   private final String key;
/*     */   private final Expression value;
/*  42 */   static final String[] SETTING_NAMES = new String[] { "booleanFormat", "boolean_format", "classicCompatible", "classic_compatible", "dateFormat", "date_format", "datetimeFormat", "datetime_format", "locale", "numberFormat", "number_format", "outputEncoding", "output_encoding", "sqlDateAndTimeTimeZone", "sql_date_and_time_time_zone", "timeFormat", "timeZone", "time_format", "time_zone", "urlEscapingCharset", "url_escaping_charset" };
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
/*     */   PropertySetting(Token keyTk, FMParserTokenManager tokenManager, Expression value, Configuration cfg) throws ParseException {
/*  69 */     String key = keyTk.image;
/*  70 */     if (Arrays.binarySearch((Object[])SETTING_NAMES, key) < 0) {
/*  71 */       StringBuilder sb = new StringBuilder();
/*  72 */       if (_TemplateAPI.getConfigurationSettingNames(cfg, true).contains(key) || 
/*  73 */         _TemplateAPI.getConfigurationSettingNames(cfg, false).contains(key)) {
/*  74 */         sb.append("The setting name is recognized, but changing this setting from inside a template isn't supported.");
/*     */       } else {
/*     */         
/*  77 */         sb.append("Unknown setting name: ");
/*  78 */         sb.append(StringUtil.jQuote(key)).append(".");
/*  79 */         sb.append(" The allowed setting names are: ");
/*     */ 
/*     */ 
/*     */         
/*  83 */         int namingConvention = tokenManager.namingConvention;
/*  84 */         int shownNamingConvention = (namingConvention != 10) ? namingConvention : 11;
/*     */ 
/*     */ 
/*     */         
/*  88 */         boolean first = true;
/*  89 */         for (int i = 0; i < SETTING_NAMES.length; i++) {
/*  90 */           String correctName = SETTING_NAMES[i];
/*  91 */           int correctNameNamingConvetion = _CoreStringUtils.getIdentifierNamingConvention(correctName);
/*  92 */           if ((shownNamingConvention == 12) ? (correctNameNamingConvetion != 11) : (correctNameNamingConvetion != 12)) {
/*     */ 
/*     */             
/*  95 */             if (first) {
/*  96 */               first = false;
/*     */             } else {
/*  98 */               sb.append(", ");
/*     */             } 
/*     */             
/* 101 */             sb.append(SETTING_NAMES[i]);
/*     */           } 
/*     */         } 
/*     */       } 
/* 105 */       throw new ParseException(sb.toString(), null, keyTk);
/*     */     } 
/*     */     
/* 108 */     this.key = key;
/* 109 */     this.value = value;
/*     */   }
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException {
/*     */     String strval;
/* 114 */     TemplateModel mval = this.value.eval(env);
/*     */     
/* 116 */     if (mval instanceof TemplateScalarModel) {
/* 117 */       strval = ((TemplateScalarModel)mval).getAsString();
/* 118 */     } else if (mval instanceof TemplateBooleanModel) {
/* 119 */       strval = ((TemplateBooleanModel)mval).getAsBoolean() ? "true" : "false";
/* 120 */     } else if (mval instanceof TemplateNumberModel) {
/* 121 */       strval = ((TemplateNumberModel)mval).getAsNumber().toString();
/*     */     } else {
/* 123 */       strval = this.value.evalAndCoerceToStringOrUnsupportedMarkup(env);
/*     */     } 
/* 125 */     env.setSetting(this.key, strval);
/* 126 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/* 131 */     StringBuilder sb = new StringBuilder();
/* 132 */     if (canonical) sb.append('<'); 
/* 133 */     sb.append(getNodeTypeSymbol());
/* 134 */     sb.append(' ');
/* 135 */     sb.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.key));
/* 136 */     sb.append('=');
/* 137 */     sb.append(this.value.getCanonicalForm());
/* 138 */     if (canonical) sb.append("/>"); 
/* 139 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 144 */     return "#setting";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 149 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 154 */     switch (idx) { case 0:
/* 155 */         return this.key;
/* 156 */       case 1: return this.value; }
/* 157 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 163 */     switch (idx) { case 0:
/* 164 */         return ParameterRole.ITEM_KEY;
/* 165 */       case 1: return ParameterRole.ITEM_VALUE; }
/* 166 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 172 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\PropertySetting.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */