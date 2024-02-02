package org.apache.http;

public class HttpException extends Exception {
   private static final int FIRST_VALID_CHAR = 32;
   private static final long serialVersionUID = -5437299376222011036L;

   static String clean(String message) {
      char[] chars = message.toCharArray();

      int i;
      for(i = 0; i < chars.length && chars[i] >= ' '; ++i) {
      }

      if (i == chars.length) {
         return message;
      } else {
         StringBuilder builder = new StringBuilder(chars.length * 2);

         for(i = 0; i < chars.length; ++i) {
            char ch = chars[i];
            if (ch < ' ') {
               builder.append("[0x");
               String hexString = Integer.toHexString(i);
               if (hexString.length() == 1) {
                  builder.append("0");
               }

               builder.append(hexString);
               builder.append("]");
            } else {
               builder.append(ch);
            }
         }

         return builder.toString();
      }
   }

   public HttpException() {
   }

   public HttpException(String message) {
      super(clean(message));
   }

   public HttpException(String message, Throwable cause) {
      super(clean(message));
      this.initCause(cause);
   }
}
