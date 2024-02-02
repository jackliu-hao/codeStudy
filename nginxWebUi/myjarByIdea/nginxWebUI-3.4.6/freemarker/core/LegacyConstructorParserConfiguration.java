package freemarker.core;

import freemarker.template.Version;

class LegacyConstructorParserConfiguration implements ParserConfiguration {
   private final int tagSyntax;
   private final int interpolationSyntax;
   private final int namingConvention;
   private final boolean whitespaceStripping;
   private final boolean strictSyntaxMode;
   private ArithmeticEngine arithmeticEngine;
   private Integer autoEscapingPolicy;
   private OutputFormat outputFormat;
   private Boolean recognizeStandardFileExtensions;
   private Integer tabSize;
   private final Version incompatibleImprovements;

   LegacyConstructorParserConfiguration(boolean strictSyntaxMode, boolean whitespaceStripping, int tagSyntax, int interpolationSyntax, int namingConvention, Integer autoEscaping, OutputFormat outputFormat, Boolean recognizeStandardFileExtensions, Integer tabSize, Version incompatibleImprovements, ArithmeticEngine arithmeticEngine) {
      this.tagSyntax = tagSyntax;
      this.interpolationSyntax = interpolationSyntax;
      this.namingConvention = namingConvention;
      this.whitespaceStripping = whitespaceStripping;
      this.strictSyntaxMode = strictSyntaxMode;
      this.autoEscapingPolicy = autoEscaping;
      this.outputFormat = outputFormat;
      this.recognizeStandardFileExtensions = recognizeStandardFileExtensions;
      this.tabSize = tabSize;
      this.incompatibleImprovements = incompatibleImprovements;
      this.arithmeticEngine = arithmeticEngine;
   }

   public int getTagSyntax() {
      return this.tagSyntax;
   }

   public int getInterpolationSyntax() {
      return this.interpolationSyntax;
   }

   public int getNamingConvention() {
      return this.namingConvention;
   }

   public boolean getWhitespaceStripping() {
      return this.whitespaceStripping;
   }

   public boolean getStrictSyntaxMode() {
      return this.strictSyntaxMode;
   }

   public Version getIncompatibleImprovements() {
      return this.incompatibleImprovements;
   }

   public ArithmeticEngine getArithmeticEngine() {
      if (this.arithmeticEngine == null) {
         throw new IllegalStateException();
      } else {
         return this.arithmeticEngine;
      }
   }

   void setArithmeticEngineIfNotSet(ArithmeticEngine arithmeticEngine) {
      if (this.arithmeticEngine == null) {
         this.arithmeticEngine = arithmeticEngine;
      }

   }

   public int getAutoEscapingPolicy() {
      if (this.autoEscapingPolicy == null) {
         throw new IllegalStateException();
      } else {
         return this.autoEscapingPolicy;
      }
   }

   void setAutoEscapingPolicyIfNotSet(int autoEscapingPolicy) {
      if (this.autoEscapingPolicy == null) {
         this.autoEscapingPolicy = autoEscapingPolicy;
      }

   }

   public OutputFormat getOutputFormat() {
      if (this.outputFormat == null) {
         throw new IllegalStateException();
      } else {
         return this.outputFormat;
      }
   }

   void setOutputFormatIfNotSet(OutputFormat outputFormat) {
      if (this.outputFormat == null) {
         this.outputFormat = outputFormat;
      }

   }

   public boolean getRecognizeStandardFileExtensions() {
      if (this.recognizeStandardFileExtensions == null) {
         throw new IllegalStateException();
      } else {
         return this.recognizeStandardFileExtensions;
      }
   }

   void setRecognizeStandardFileExtensionsIfNotSet(boolean recognizeStandardFileExtensions) {
      if (this.recognizeStandardFileExtensions == null) {
         this.recognizeStandardFileExtensions = recognizeStandardFileExtensions;
      }

   }

   public int getTabSize() {
      if (this.tabSize == null) {
         throw new IllegalStateException();
      } else {
         return this.tabSize;
      }
   }

   void setTabSizeIfNotSet(int tabSize) {
      if (this.tabSize == null) {
         this.tabSize = tabSize;
      }

   }
}
