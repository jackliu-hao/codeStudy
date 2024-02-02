package freemarker.core;

public class JSONOutputFormat extends OutputFormat {
   public static final JSONOutputFormat INSTANCE = new JSONOutputFormat();

   private JSONOutputFormat() {
   }

   public String getName() {
      return "JSON";
   }

   public String getMimeType() {
      return "application/json";
   }

   public boolean isOutputFormatMixingAllowed() {
      return false;
   }
}
