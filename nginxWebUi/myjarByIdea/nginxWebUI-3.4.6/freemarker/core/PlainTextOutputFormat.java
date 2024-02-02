package freemarker.core;

public final class PlainTextOutputFormat extends OutputFormat {
   public static final PlainTextOutputFormat INSTANCE = new PlainTextOutputFormat();

   private PlainTextOutputFormat() {
   }

   public boolean isOutputFormatMixingAllowed() {
      return false;
   }

   public String getName() {
      return "plainText";
   }

   public String getMimeType() {
      return "text/plain";
   }
}
