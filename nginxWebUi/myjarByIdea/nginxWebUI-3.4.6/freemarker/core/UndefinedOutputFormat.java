package freemarker.core;

public final class UndefinedOutputFormat extends OutputFormat {
   public static final UndefinedOutputFormat INSTANCE = new UndefinedOutputFormat();

   private UndefinedOutputFormat() {
   }

   public boolean isOutputFormatMixingAllowed() {
      return true;
   }

   public String getName() {
      return "undefined";
   }

   public String getMimeType() {
      return null;
   }
}
