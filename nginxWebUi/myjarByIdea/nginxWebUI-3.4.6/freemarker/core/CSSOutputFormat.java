package freemarker.core;

public class CSSOutputFormat extends OutputFormat {
   public static final CSSOutputFormat INSTANCE = new CSSOutputFormat();

   private CSSOutputFormat() {
   }

   public String getName() {
      return "CSS";
   }

   public String getMimeType() {
      return "text/css";
   }

   public boolean isOutputFormatMixingAllowed() {
      return false;
   }
}
