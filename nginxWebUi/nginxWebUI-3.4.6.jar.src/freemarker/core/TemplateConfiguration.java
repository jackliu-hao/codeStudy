/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.Version;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.NullArgumentException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
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
/*     */ public final class TemplateConfiguration
/*     */   extends Configurable
/*     */   implements ParserConfiguration
/*     */ {
/*     */   private boolean parentConfigurationSet;
/*     */   private Integer tagSyntax;
/*     */   private Integer interpolationSyntax;
/*     */   private Integer namingConvention;
/*     */   private Boolean whitespaceStripping;
/*     */   private Boolean strictSyntaxMode;
/*     */   private Integer autoEscapingPolicy;
/*     */   private Boolean recognizeStandardFileExtensions;
/*     */   private OutputFormat outputFormat;
/*     */   private String encoding;
/*     */   private Integer tabSize;
/*     */   
/*     */   public TemplateConfiguration() {
/*  97 */     super((Configurable)Configuration.getDefaultConfiguration());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setParent(Configurable cfg) {
/* 105 */     NullArgumentException.check("cfg", cfg);
/* 106 */     if (!(cfg instanceof Configuration)) {
/* 107 */       throw new IllegalArgumentException("The parent of a TemplateConfiguration can only be a Configuration");
/*     */     }
/*     */     
/* 110 */     if (this.parentConfigurationSet) {
/* 111 */       if (getParent() != cfg) {
/* 112 */         throw new IllegalStateException("This TemplateConfiguration is already associated with a different Configuration instance.");
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 118 */     if (((Configuration)cfg).getIncompatibleImprovements().intValue() < _TemplateAPI.VERSION_INT_2_3_22 && 
/* 119 */       hasAnyConfigurableSet()) {
/* 120 */       throw new IllegalStateException("This TemplateConfiguration can't be associated to a Configuration that has incompatibleImprovements less than 2.3.22, because it changes non-parser settings.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 125 */     super.setParent(cfg);
/* 126 */     this.parentConfigurationSet = true;
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
/*     */   public void setParentConfiguration(Configuration cfg) {
/* 142 */     setParent((Configurable)cfg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Configuration getParentConfiguration() {
/* 149 */     return this.parentConfigurationSet ? (Configuration)getParent() : null;
/*     */   }
/*     */   
/*     */   private Configuration getNonNullParentConfiguration() {
/* 153 */     checkParentConfigurationSet();
/* 154 */     return (Configuration)getParent();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void merge(TemplateConfiguration tc) {
/* 163 */     if (tc.isAPIBuiltinEnabledSet()) {
/* 164 */       setAPIBuiltinEnabled(tc.isAPIBuiltinEnabled());
/*     */     }
/* 166 */     if (tc.isArithmeticEngineSet()) {
/* 167 */       setArithmeticEngine(tc.getArithmeticEngine());
/*     */     }
/* 169 */     if (tc.isAutoEscapingPolicySet()) {
/* 170 */       setAutoEscapingPolicy(tc.getAutoEscapingPolicy());
/*     */     }
/* 172 */     if (tc.isAutoFlushSet()) {
/* 173 */       setAutoFlush(tc.getAutoFlush());
/*     */     }
/* 175 */     if (tc.isBooleanFormatSet()) {
/* 176 */       setBooleanFormat(tc.getBooleanFormat());
/*     */     }
/* 178 */     if (tc.isClassicCompatibleSet()) {
/* 179 */       setClassicCompatibleAsInt(tc.getClassicCompatibleAsInt());
/*     */     }
/* 181 */     if (tc.isCustomDateFormatsSet()) {
/* 182 */       setCustomDateFormats(mergeMaps(getCustomDateFormats(), tc.getCustomDateFormats(), false));
/*     */     }
/* 184 */     if (tc.isCustomNumberFormatsSet()) {
/* 185 */       setCustomNumberFormats(mergeMaps(getCustomNumberFormats(), tc.getCustomNumberFormats(), false));
/*     */     }
/* 187 */     if (tc.isDateFormatSet()) {
/* 188 */       setDateFormat(tc.getDateFormat());
/*     */     }
/* 190 */     if (tc.isDateTimeFormatSet()) {
/* 191 */       setDateTimeFormat(tc.getDateTimeFormat());
/*     */     }
/* 193 */     if (tc.isEncodingSet()) {
/* 194 */       setEncoding(tc.getEncoding());
/*     */     }
/* 196 */     if (tc.isLocaleSet()) {
/* 197 */       setLocale(tc.getLocale());
/*     */     }
/* 199 */     if (tc.isLogTemplateExceptionsSet()) {
/* 200 */       setLogTemplateExceptions(tc.getLogTemplateExceptions());
/*     */     }
/* 202 */     if (tc.isWrapUncheckedExceptionsSet()) {
/* 203 */       setWrapUncheckedExceptions(tc.getWrapUncheckedExceptions());
/*     */     }
/* 205 */     if (tc.isNamingConventionSet()) {
/* 206 */       setNamingConvention(tc.getNamingConvention());
/*     */     }
/* 208 */     if (tc.isNewBuiltinClassResolverSet()) {
/* 209 */       setNewBuiltinClassResolver(tc.getNewBuiltinClassResolver());
/*     */     }
/* 211 */     if (tc.isTruncateBuiltinAlgorithmSet()) {
/* 212 */       setTruncateBuiltinAlgorithm(tc.getTruncateBuiltinAlgorithm());
/*     */     }
/* 214 */     if (tc.isNumberFormatSet()) {
/* 215 */       setNumberFormat(tc.getNumberFormat());
/*     */     }
/* 217 */     if (tc.isObjectWrapperSet()) {
/* 218 */       setObjectWrapper(tc.getObjectWrapper());
/*     */     }
/* 220 */     if (tc.isOutputEncodingSet()) {
/* 221 */       setOutputEncoding(tc.getOutputEncoding());
/*     */     }
/* 223 */     if (tc.isOutputFormatSet()) {
/* 224 */       setOutputFormat(tc.getOutputFormat());
/*     */     }
/* 226 */     if (tc.isRecognizeStandardFileExtensionsSet()) {
/* 227 */       setRecognizeStandardFileExtensions(tc.getRecognizeStandardFileExtensions());
/*     */     }
/* 229 */     if (tc.isShowErrorTipsSet()) {
/* 230 */       setShowErrorTips(tc.getShowErrorTips());
/*     */     }
/* 232 */     if (tc.isSQLDateAndTimeTimeZoneSet()) {
/* 233 */       setSQLDateAndTimeTimeZone(tc.getSQLDateAndTimeTimeZone());
/*     */     }
/* 235 */     if (tc.isStrictSyntaxModeSet()) {
/* 236 */       setStrictSyntaxMode(tc.getStrictSyntaxMode());
/*     */     }
/* 238 */     if (tc.isTagSyntaxSet()) {
/* 239 */       setTagSyntax(tc.getTagSyntax());
/*     */     }
/* 241 */     if (tc.isInterpolationSyntaxSet()) {
/* 242 */       setInterpolationSyntax(tc.getInterpolationSyntax());
/*     */     }
/* 244 */     if (tc.isTemplateExceptionHandlerSet()) {
/* 245 */       setTemplateExceptionHandler(tc.getTemplateExceptionHandler());
/*     */     }
/* 247 */     if (tc.isAttemptExceptionReporterSet()) {
/* 248 */       setAttemptExceptionReporter(tc.getAttemptExceptionReporter());
/*     */     }
/* 250 */     if (tc.isTimeFormatSet()) {
/* 251 */       setTimeFormat(tc.getTimeFormat());
/*     */     }
/* 253 */     if (tc.isTimeZoneSet()) {
/* 254 */       setTimeZone(tc.getTimeZone());
/*     */     }
/* 256 */     if (tc.isURLEscapingCharsetSet()) {
/* 257 */       setURLEscapingCharset(tc.getURLEscapingCharset());
/*     */     }
/* 259 */     if (tc.isWhitespaceStrippingSet()) {
/* 260 */       setWhitespaceStripping(tc.getWhitespaceStripping());
/*     */     }
/* 262 */     if (tc.isTabSizeSet()) {
/* 263 */       setTabSize(tc.getTabSize());
/*     */     }
/* 265 */     if (tc.isLazyImportsSet()) {
/* 266 */       setLazyImports(tc.getLazyImports());
/*     */     }
/* 268 */     if (tc.isLazyAutoImportsSet()) {
/* 269 */       setLazyAutoImports(tc.getLazyAutoImports());
/*     */     }
/* 271 */     if (tc.isAutoImportsSet()) {
/* 272 */       setAutoImports(mergeMaps(getAutoImportsWithoutFallback(), tc.getAutoImportsWithoutFallback(), true));
/*     */     }
/* 274 */     if (tc.isAutoIncludesSet()) {
/* 275 */       setAutoIncludes(mergeLists(getAutoIncludesWithoutFallback(), tc.getAutoIncludesWithoutFallback()));
/*     */     }
/*     */     
/* 278 */     tc.copyDirectCustomAttributes(this, true);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public void apply(Template template) {
/* 299 */     Configuration cfg = getNonNullParentConfiguration();
/* 300 */     if (template.getConfiguration() != cfg)
/*     */     {
/* 302 */       throw new IllegalArgumentException("The argument Template doesn't belong to the same Configuration as the TemplateConfiguration");
/*     */     }
/*     */ 
/*     */     
/* 306 */     if (isAPIBuiltinEnabledSet() && !template.isAPIBuiltinEnabledSet()) {
/* 307 */       template.setAPIBuiltinEnabled(isAPIBuiltinEnabled());
/*     */     }
/* 309 */     if (isArithmeticEngineSet() && !template.isArithmeticEngineSet()) {
/* 310 */       template.setArithmeticEngine(getArithmeticEngine());
/*     */     }
/* 312 */     if (isAutoFlushSet() && !template.isAutoFlushSet()) {
/* 313 */       template.setAutoFlush(getAutoFlush());
/*     */     }
/* 315 */     if (isBooleanFormatSet() && !template.isBooleanFormatSet()) {
/* 316 */       template.setBooleanFormat(getBooleanFormat());
/*     */     }
/* 318 */     if (isClassicCompatibleSet() && !template.isClassicCompatibleSet()) {
/* 319 */       template.setClassicCompatibleAsInt(getClassicCompatibleAsInt());
/*     */     }
/* 321 */     if (isCustomDateFormatsSet()) {
/* 322 */       template.setCustomDateFormats(
/* 323 */           mergeMaps(getCustomDateFormats(), template.getCustomDateFormatsWithoutFallback(), false));
/*     */     }
/* 325 */     if (isCustomNumberFormatsSet()) {
/* 326 */       template.setCustomNumberFormats(
/* 327 */           mergeMaps(getCustomNumberFormats(), template.getCustomNumberFormatsWithoutFallback(), false));
/*     */     }
/* 329 */     if (isDateFormatSet() && !template.isDateFormatSet()) {
/* 330 */       template.setDateFormat(getDateFormat());
/*     */     }
/* 332 */     if (isDateTimeFormatSet() && !template.isDateTimeFormatSet()) {
/* 333 */       template.setDateTimeFormat(getDateTimeFormat());
/*     */     }
/* 335 */     if (isEncodingSet() && template.getEncoding() == null) {
/* 336 */       template.setEncoding(getEncoding());
/*     */     }
/* 338 */     if (isLocaleSet() && !template.isLocaleSet()) {
/* 339 */       template.setLocale(getLocale());
/*     */     }
/* 341 */     if (isLogTemplateExceptionsSet() && !template.isLogTemplateExceptionsSet()) {
/* 342 */       template.setLogTemplateExceptions(getLogTemplateExceptions());
/*     */     }
/* 344 */     if (isWrapUncheckedExceptionsSet() && !template.isWrapUncheckedExceptionsSet()) {
/* 345 */       template.setWrapUncheckedExceptions(getWrapUncheckedExceptions());
/*     */     }
/* 347 */     if (isNewBuiltinClassResolverSet() && !template.isNewBuiltinClassResolverSet()) {
/* 348 */       template.setNewBuiltinClassResolver(getNewBuiltinClassResolver());
/*     */     }
/* 350 */     if (isTruncateBuiltinAlgorithmSet() && !template.isTruncateBuiltinAlgorithmSet()) {
/* 351 */       template.setTruncateBuiltinAlgorithm(getTruncateBuiltinAlgorithm());
/*     */     }
/* 353 */     if (isNumberFormatSet() && !template.isNumberFormatSet()) {
/* 354 */       template.setNumberFormat(getNumberFormat());
/*     */     }
/* 356 */     if (isObjectWrapperSet() && !template.isObjectWrapperSet()) {
/* 357 */       template.setObjectWrapper(getObjectWrapper());
/*     */     }
/* 359 */     if (isOutputEncodingSet() && !template.isOutputEncodingSet()) {
/* 360 */       template.setOutputEncoding(getOutputEncoding());
/*     */     }
/* 362 */     if (isShowErrorTipsSet() && !template.isShowErrorTipsSet()) {
/* 363 */       template.setShowErrorTips(getShowErrorTips());
/*     */     }
/* 365 */     if (isSQLDateAndTimeTimeZoneSet() && !template.isSQLDateAndTimeTimeZoneSet()) {
/* 366 */       template.setSQLDateAndTimeTimeZone(getSQLDateAndTimeTimeZone());
/*     */     }
/* 368 */     if (isTemplateExceptionHandlerSet() && !template.isTemplateExceptionHandlerSet()) {
/* 369 */       template.setTemplateExceptionHandler(getTemplateExceptionHandler());
/*     */     }
/* 371 */     if (isAttemptExceptionReporterSet() && !template.isAttemptExceptionReporterSet()) {
/* 372 */       template.setAttemptExceptionReporter(getAttemptExceptionReporter());
/*     */     }
/* 374 */     if (isTimeFormatSet() && !template.isTimeFormatSet()) {
/* 375 */       template.setTimeFormat(getTimeFormat());
/*     */     }
/* 377 */     if (isTimeZoneSet() && !template.isTimeZoneSet()) {
/* 378 */       template.setTimeZone(getTimeZone());
/*     */     }
/* 380 */     if (isURLEscapingCharsetSet() && !template.isURLEscapingCharsetSet()) {
/* 381 */       template.setURLEscapingCharset(getURLEscapingCharset());
/*     */     }
/* 383 */     if (isLazyImportsSet() && !template.isLazyImportsSet()) {
/* 384 */       template.setLazyImports(getLazyImports());
/*     */     }
/* 386 */     if (isLazyAutoImportsSet() && !template.isLazyAutoImportsSet()) {
/* 387 */       template.setLazyAutoImports(getLazyAutoImports());
/*     */     }
/* 389 */     if (isAutoImportsSet())
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 394 */       template.setAutoImports(mergeMaps(getAutoImports(), template.getAutoImportsWithoutFallback(), true));
/*     */     }
/* 396 */     if (isAutoIncludesSet()) {
/* 397 */       template.setAutoIncludes(mergeLists(getAutoIncludes(), template.getAutoIncludesWithoutFallback()));
/*     */     }
/*     */     
/* 400 */     copyDirectCustomAttributes((Configurable)template, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTagSyntax(int tagSyntax) {
/* 407 */     _TemplateAPI.valideTagSyntaxValue(tagSyntax);
/* 408 */     this.tagSyntax = Integer.valueOf(tagSyntax);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTagSyntax() {
/* 416 */     return (this.tagSyntax != null) ? this.tagSyntax.intValue() : getNonNullParentConfiguration().getTagSyntax();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTagSyntaxSet() {
/* 423 */     return (this.tagSyntax != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInterpolationSyntax(int interpolationSyntax) {
/* 430 */     _TemplateAPI.valideInterpolationSyntaxValue(interpolationSyntax);
/* 431 */     this.interpolationSyntax = Integer.valueOf(interpolationSyntax);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getInterpolationSyntax() {
/* 439 */     return (this.interpolationSyntax != null) ? this.interpolationSyntax.intValue() : 
/* 440 */       getNonNullParentConfiguration().getInterpolationSyntax();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInterpolationSyntaxSet() {
/* 447 */     return (this.interpolationSyntax != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNamingConvention(int namingConvention) {
/* 454 */     _TemplateAPI.validateNamingConventionValue(namingConvention);
/* 455 */     this.namingConvention = Integer.valueOf(namingConvention);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNamingConvention() {
/* 463 */     return (this.namingConvention != null) ? this.namingConvention.intValue() : 
/* 464 */       getNonNullParentConfiguration().getNamingConvention();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNamingConventionSet() {
/* 471 */     return (this.namingConvention != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWhitespaceStripping(boolean whitespaceStripping) {
/* 478 */     this.whitespaceStripping = Boolean.valueOf(whitespaceStripping);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getWhitespaceStripping() {
/* 486 */     return (this.whitespaceStripping != null) ? this.whitespaceStripping.booleanValue() : 
/* 487 */       getNonNullParentConfiguration().getWhitespaceStripping();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWhitespaceStrippingSet() {
/* 494 */     return (this.whitespaceStripping != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAutoEscapingPolicy(int autoEscapingPolicy) {
/* 501 */     _TemplateAPI.validateAutoEscapingPolicyValue(autoEscapingPolicy);
/* 502 */     this.autoEscapingPolicy = Integer.valueOf(autoEscapingPolicy);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAutoEscapingPolicy() {
/* 510 */     return (this.autoEscapingPolicy != null) ? this.autoEscapingPolicy.intValue() : 
/* 511 */       getNonNullParentConfiguration().getAutoEscapingPolicy();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAutoEscapingPolicySet() {
/* 518 */     return (this.autoEscapingPolicy != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOutputFormat(OutputFormat outputFormat) {
/* 525 */     NullArgumentException.check("outputFormat", outputFormat);
/* 526 */     this.outputFormat = outputFormat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputFormat getOutputFormat() {
/* 534 */     return (this.outputFormat != null) ? this.outputFormat : getNonNullParentConfiguration().getOutputFormat();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOutputFormatSet() {
/* 541 */     return (this.outputFormat != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRecognizeStandardFileExtensions(boolean recognizeStandardFileExtensions) {
/* 548 */     this.recognizeStandardFileExtensions = Boolean.valueOf(recognizeStandardFileExtensions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getRecognizeStandardFileExtensions() {
/* 556 */     return (this.recognizeStandardFileExtensions != null) ? this.recognizeStandardFileExtensions.booleanValue() : 
/* 557 */       getNonNullParentConfiguration().getRecognizeStandardFileExtensions();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRecognizeStandardFileExtensionsSet() {
/* 564 */     return (this.recognizeStandardFileExtensions != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStrictSyntaxMode(boolean strictSyntaxMode) {
/* 571 */     this.strictSyntaxMode = Boolean.valueOf(strictSyntaxMode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getStrictSyntaxMode() {
/* 579 */     return (this.strictSyntaxMode != null) ? this.strictSyntaxMode.booleanValue() : 
/* 580 */       getNonNullParentConfiguration().getStrictSyntaxMode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStrictSyntaxModeSet() {
/* 587 */     return (this.strictSyntaxMode != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStrictBeanModels(boolean strict) {
/* 592 */     throw new UnsupportedOperationException("Setting strictBeanModels on " + TemplateConfiguration.class
/* 593 */         .getSimpleName() + " level isn't supported.");
/*     */   }
/*     */   
/*     */   public String getEncoding() {
/* 597 */     return (this.encoding != null) ? this.encoding : getNonNullParentConfiguration().getDefaultEncoding();
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
/*     */   public void setEncoding(String encoding) {
/* 614 */     NullArgumentException.check("encoding", encoding);
/* 615 */     this.encoding = encoding;
/*     */   }
/*     */   
/*     */   public boolean isEncodingSet() {
/* 619 */     return (this.encoding != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTabSize(int tabSize) {
/* 628 */     this.tabSize = Integer.valueOf(tabSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTabSize() {
/* 638 */     return (this.tabSize != null) ? this.tabSize.intValue() : 
/* 639 */       getNonNullParentConfiguration().getTabSize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTabSizeSet() {
/* 648 */     return (this.tabSize != null);
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
/*     */   public Version getIncompatibleImprovements() {
/* 660 */     return getNonNullParentConfiguration().getIncompatibleImprovements();
/*     */   }
/*     */   
/*     */   private void checkParentConfigurationSet() {
/* 664 */     if (!this.parentConfigurationSet) {
/* 665 */       throw new IllegalStateException("The TemplateConfiguration wasn't associated with a Configuration yet.");
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean hasAnyConfigurableSet() {
/* 670 */     return (
/* 671 */       isAPIBuiltinEnabledSet() || 
/* 672 */       isArithmeticEngineSet() || 
/* 673 */       isAutoFlushSet() || 
/* 674 */       isAutoImportsSet() || 
/* 675 */       isAutoIncludesSet() || 
/* 676 */       isBooleanFormatSet() || 
/* 677 */       isClassicCompatibleSet() || 
/* 678 */       isCustomDateFormatsSet() || 
/* 679 */       isCustomNumberFormatsSet() || 
/* 680 */       isDateFormatSet() || 
/* 681 */       isDateTimeFormatSet() || 
/* 682 */       isLazyImportsSet() || 
/* 683 */       isLazyAutoImportsSet() || 
/* 684 */       isLocaleSet() || 
/* 685 */       isLogTemplateExceptionsSet() || 
/* 686 */       isWrapUncheckedExceptionsSet() || 
/* 687 */       isNewBuiltinClassResolverSet() || 
/* 688 */       isTruncateBuiltinAlgorithmSet() || 
/* 689 */       isNumberFormatSet() || 
/* 690 */       isObjectWrapperSet() || 
/* 691 */       isOutputEncodingSet() || 
/* 692 */       isShowErrorTipsSet() || 
/* 693 */       isSQLDateAndTimeTimeZoneSet() || 
/* 694 */       isTemplateExceptionHandlerSet() || 
/* 695 */       isAttemptExceptionReporterSet() || 
/* 696 */       isTimeFormatSet() || 
/* 697 */       isTimeZoneSet() || 
/* 698 */       isURLEscapingCharsetSet());
/*     */   }
/*     */   
/*     */   private Map mergeMaps(Map<?, ?> m1, Map<?, ?> m2, boolean overwriteUpdatesOrder) {
/* 702 */     if (m1 == null) return m2; 
/* 703 */     if (m2 == null) return m1; 
/* 704 */     if (m1.isEmpty()) return m2; 
/* 705 */     if (m2.isEmpty()) return m1;
/*     */     
/* 707 */     LinkedHashMap<Object, Object> mergedM = new LinkedHashMap<>((m1.size() + m2.size()) * 4 / 3 + 1, 0.75F);
/* 708 */     mergedM.putAll(m1);
/* 709 */     for (Object m2Key : m2.keySet()) {
/* 710 */       mergedM.remove(m2Key);
/*     */     }
/* 712 */     mergedM.putAll(m2);
/* 713 */     return mergedM;
/*     */   }
/*     */   
/*     */   private List<String> mergeLists(List<String> list1, List<String> list2) {
/* 717 */     if (list1 == null) return list2; 
/* 718 */     if (list2 == null) return list1; 
/* 719 */     if (list1.isEmpty()) return list2; 
/* 720 */     if (list2.isEmpty()) return list1;
/*     */     
/* 722 */     ArrayList<String> mergedList = new ArrayList<>(list1.size() + list2.size());
/* 723 */     mergedList.addAll(list1);
/* 724 */     mergedList.addAll(list2);
/* 725 */     return mergedList;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateConfiguration.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */