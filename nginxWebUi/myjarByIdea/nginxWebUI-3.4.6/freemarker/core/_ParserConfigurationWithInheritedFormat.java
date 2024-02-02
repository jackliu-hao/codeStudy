package freemarker.core;

import freemarker.template.Version;

public final class _ParserConfigurationWithInheritedFormat implements ParserConfiguration {
   private final OutputFormat outputFormat;
   private final Integer autoEscapingPolicy;
   private final ParserConfiguration wrappedPCfg;

   public _ParserConfigurationWithInheritedFormat(ParserConfiguration wrappedPCfg, OutputFormat outputFormat, Integer autoEscapingPolicy) {
      this.outputFormat = outputFormat;
      this.autoEscapingPolicy = autoEscapingPolicy;
      this.wrappedPCfg = wrappedPCfg;
   }

   public boolean getWhitespaceStripping() {
      return this.wrappedPCfg.getWhitespaceStripping();
   }

   public int getTagSyntax() {
      return this.wrappedPCfg.getTagSyntax();
   }

   public int getInterpolationSyntax() {
      return this.wrappedPCfg.getInterpolationSyntax();
   }

   public boolean getStrictSyntaxMode() {
      return this.wrappedPCfg.getStrictSyntaxMode();
   }

   public OutputFormat getOutputFormat() {
      return this.outputFormat != null ? this.outputFormat : this.wrappedPCfg.getOutputFormat();
   }

   public boolean getRecognizeStandardFileExtensions() {
      return false;
   }

   public int getNamingConvention() {
      return this.wrappedPCfg.getNamingConvention();
   }

   public Version getIncompatibleImprovements() {
      return this.wrappedPCfg.getIncompatibleImprovements();
   }

   public int getAutoEscapingPolicy() {
      return this.autoEscapingPolicy != null ? this.autoEscapingPolicy : this.wrappedPCfg.getAutoEscapingPolicy();
   }

   public ArithmeticEngine getArithmeticEngine() {
      return this.wrappedPCfg.getArithmeticEngine();
   }

   public int getTabSize() {
      return this.wrappedPCfg.getTabSize();
   }
}
