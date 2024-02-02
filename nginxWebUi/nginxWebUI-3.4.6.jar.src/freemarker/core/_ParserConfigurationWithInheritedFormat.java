/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.Version;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class _ParserConfigurationWithInheritedFormat
/*    */   implements ParserConfiguration
/*    */ {
/*    */   private final OutputFormat outputFormat;
/*    */   private final Integer autoEscapingPolicy;
/*    */   private final ParserConfiguration wrappedPCfg;
/*    */   
/*    */   public _ParserConfigurationWithInheritedFormat(ParserConfiguration wrappedPCfg, OutputFormat outputFormat, Integer autoEscapingPolicy) {
/* 34 */     this.outputFormat = outputFormat;
/* 35 */     this.autoEscapingPolicy = autoEscapingPolicy;
/* 36 */     this.wrappedPCfg = wrappedPCfg;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean getWhitespaceStripping() {
/* 41 */     return this.wrappedPCfg.getWhitespaceStripping();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getTagSyntax() {
/* 46 */     return this.wrappedPCfg.getTagSyntax();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getInterpolationSyntax() {
/* 51 */     return this.wrappedPCfg.getInterpolationSyntax();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean getStrictSyntaxMode() {
/* 56 */     return this.wrappedPCfg.getStrictSyntaxMode();
/*    */   }
/*    */ 
/*    */   
/*    */   public OutputFormat getOutputFormat() {
/* 61 */     return (this.outputFormat != null) ? this.outputFormat : this.wrappedPCfg.getOutputFormat();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean getRecognizeStandardFileExtensions() {
/* 66 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getNamingConvention() {
/* 71 */     return this.wrappedPCfg.getNamingConvention();
/*    */   }
/*    */ 
/*    */   
/*    */   public Version getIncompatibleImprovements() {
/* 76 */     return this.wrappedPCfg.getIncompatibleImprovements();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getAutoEscapingPolicy() {
/* 81 */     return (this.autoEscapingPolicy != null) ? this.autoEscapingPolicy.intValue() : this.wrappedPCfg.getAutoEscapingPolicy();
/*    */   }
/*    */ 
/*    */   
/*    */   public ArithmeticEngine getArithmeticEngine() {
/* 86 */     return this.wrappedPCfg.getArithmeticEngine();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getTabSize() {
/* 91 */     return this.wrappedPCfg.getTabSize();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_ParserConfigurationWithInheritedFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */