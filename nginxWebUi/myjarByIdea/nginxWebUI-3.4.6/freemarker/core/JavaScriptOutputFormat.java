package freemarker.core;

public class JavaScriptOutputFormat extends OutputFormat {
   public static final JavaScriptOutputFormat INSTANCE = new JavaScriptOutputFormat();

   private JavaScriptOutputFormat() {
   }

   public String getName() {
      return "JavaScript";
   }

   public String getMimeType() {
      return "application/javascript";
   }

   public boolean isOutputFormatMixingAllowed() {
      return false;
   }
}
