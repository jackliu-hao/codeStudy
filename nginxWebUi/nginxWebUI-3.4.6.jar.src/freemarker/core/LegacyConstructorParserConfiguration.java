/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Version;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class LegacyConstructorParserConfiguration
/*     */   implements ParserConfiguration
/*     */ {
/*     */   private final int tagSyntax;
/*     */   private final int interpolationSyntax;
/*     */   private final int namingConvention;
/*     */   private final boolean whitespaceStripping;
/*     */   private final boolean strictSyntaxMode;
/*     */   private ArithmeticEngine arithmeticEngine;
/*     */   private Integer autoEscapingPolicy;
/*     */   private OutputFormat outputFormat;
/*     */   private Boolean recognizeStandardFileExtensions;
/*     */   private Integer tabSize;
/*     */   private final Version incompatibleImprovements;
/*     */   
/*     */   LegacyConstructorParserConfiguration(boolean strictSyntaxMode, boolean whitespaceStripping, int tagSyntax, int interpolationSyntax, int namingConvention, Integer autoEscaping, OutputFormat outputFormat, Boolean recognizeStandardFileExtensions, Integer tabSize, Version incompatibleImprovements, ArithmeticEngine arithmeticEngine) {
/*  47 */     this.tagSyntax = tagSyntax;
/*  48 */     this.interpolationSyntax = interpolationSyntax;
/*  49 */     this.namingConvention = namingConvention;
/*  50 */     this.whitespaceStripping = whitespaceStripping;
/*  51 */     this.strictSyntaxMode = strictSyntaxMode;
/*  52 */     this.autoEscapingPolicy = autoEscaping;
/*  53 */     this.outputFormat = outputFormat;
/*  54 */     this.recognizeStandardFileExtensions = recognizeStandardFileExtensions;
/*  55 */     this.tabSize = tabSize;
/*  56 */     this.incompatibleImprovements = incompatibleImprovements;
/*  57 */     this.arithmeticEngine = arithmeticEngine;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTagSyntax() {
/*  62 */     return this.tagSyntax;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInterpolationSyntax() {
/*  67 */     return this.interpolationSyntax;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNamingConvention() {
/*  72 */     return this.namingConvention;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWhitespaceStripping() {
/*  77 */     return this.whitespaceStripping;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getStrictSyntaxMode() {
/*  82 */     return this.strictSyntaxMode;
/*     */   }
/*     */ 
/*     */   
/*     */   public Version getIncompatibleImprovements() {
/*  87 */     return this.incompatibleImprovements;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArithmeticEngine getArithmeticEngine() {
/*  92 */     if (this.arithmeticEngine == null) {
/*  93 */       throw new IllegalStateException();
/*     */     }
/*  95 */     return this.arithmeticEngine;
/*     */   }
/*     */   
/*     */   void setArithmeticEngineIfNotSet(ArithmeticEngine arithmeticEngine) {
/*  99 */     if (this.arithmeticEngine == null) {
/* 100 */       this.arithmeticEngine = arithmeticEngine;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int getAutoEscapingPolicy() {
/* 106 */     if (this.autoEscapingPolicy == null) {
/* 107 */       throw new IllegalStateException();
/*     */     }
/* 109 */     return this.autoEscapingPolicy.intValue();
/*     */   }
/*     */   
/*     */   void setAutoEscapingPolicyIfNotSet(int autoEscapingPolicy) {
/* 113 */     if (this.autoEscapingPolicy == null) {
/* 114 */       this.autoEscapingPolicy = Integer.valueOf(autoEscapingPolicy);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public OutputFormat getOutputFormat() {
/* 120 */     if (this.outputFormat == null) {
/* 121 */       throw new IllegalStateException();
/*     */     }
/* 123 */     return this.outputFormat;
/*     */   }
/*     */   
/*     */   void setOutputFormatIfNotSet(OutputFormat outputFormat) {
/* 127 */     if (this.outputFormat == null) {
/* 128 */       this.outputFormat = outputFormat;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getRecognizeStandardFileExtensions() {
/* 134 */     if (this.recognizeStandardFileExtensions == null) {
/* 135 */       throw new IllegalStateException();
/*     */     }
/* 137 */     return this.recognizeStandardFileExtensions.booleanValue();
/*     */   }
/*     */   
/*     */   void setRecognizeStandardFileExtensionsIfNotSet(boolean recognizeStandardFileExtensions) {
/* 141 */     if (this.recognizeStandardFileExtensions == null) {
/* 142 */       this.recognizeStandardFileExtensions = Boolean.valueOf(recognizeStandardFileExtensions);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTabSize() {
/* 148 */     if (this.tabSize == null) {
/* 149 */       throw new IllegalStateException();
/*     */     }
/* 151 */     return this.tabSize.intValue();
/*     */   }
/*     */   
/*     */   void setTabSizeIfNotSet(int tabSize) {
/* 155 */     if (this.tabSize == null)
/* 156 */       this.tabSize = Integer.valueOf(tabSize); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\LegacyConstructorParserConfiguration.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */