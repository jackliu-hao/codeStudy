package cn.hutool.core.lang.ansi;

public abstract class AnsiEncoder {
   private static final String ENCODE_JOIN = ";";
   private static final String ENCODE_START = "\u001b[";
   private static final String ENCODE_END = "m";
   private static final String RESET;

   public static String encode(Object... elements) {
      StringBuilder sb = new StringBuilder();
      buildEnabled(sb, elements);
      return sb.toString();
   }

   private static void buildEnabled(StringBuilder sb, Object[] elements) {
      boolean writingAnsi = false;
      boolean containsEncoding = false;
      Object[] var4 = elements;
      int var5 = elements.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Object element = var4[var6];
         if (null != element) {
            if (element instanceof AnsiElement) {
               containsEncoding = true;
               if (writingAnsi) {
                  sb.append(";");
               } else {
                  sb.append("\u001b[");
                  writingAnsi = true;
               }
            } else if (writingAnsi) {
               sb.append("m");
               writingAnsi = false;
            }

            sb.append(element);
         }
      }

      if (containsEncoding) {
         sb.append(writingAnsi ? ";" : "\u001b[");
         sb.append(RESET);
         sb.append("m");
      }

   }

   static {
      RESET = "0;" + AnsiColor.DEFAULT;
   }
}
