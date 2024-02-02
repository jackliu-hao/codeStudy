package freemarker.core;

import freemarker.template.Version;

public interface ParserConfiguration {
  int getTagSyntax();
  
  int getInterpolationSyntax();
  
  int getNamingConvention();
  
  boolean getWhitespaceStripping();
  
  ArithmeticEngine getArithmeticEngine();
  
  boolean getStrictSyntaxMode();
  
  int getAutoEscapingPolicy();
  
  OutputFormat getOutputFormat();
  
  boolean getRecognizeStandardFileExtensions();
  
  Version getIncompatibleImprovements();
  
  int getTabSize();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ParserConfiguration.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */