package freemarker.core;

import freemarker.template.utility.StringUtil;

public class _ObjectBuilderSettingEvaluationException extends Exception {
   public _ObjectBuilderSettingEvaluationException(String message, Throwable cause) {
      super(message, cause);
   }

   public _ObjectBuilderSettingEvaluationException(String message) {
      super(message);
   }

   public _ObjectBuilderSettingEvaluationException(String expected, String src, int location) {
      super("Expression syntax error: Expected a(n) " + expected + ", but " + (location < src.length() ? "found character " + StringUtil.jQuote("" + src.charAt(location)) + " at position " + (location + 1) + "." : "the end of the parsed string was reached."));
   }
}
