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
