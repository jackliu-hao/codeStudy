package org.noear.snack.core.utils;

public final class IOUtil {
   public static final char EOI = '\u0000';
   public static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   public static final int[] DIGITS_MARK = new int[103];
   public static final char[] CHARS_MARK = new char[93];
   public static final char[] CHARS_MARK_REV = new char[120];

   static {
      int i;
      for(i = 48; i <= 57; ++i) {
         DIGITS_MARK[i] = i - 48;
      }

      for(i = 97; i <= 102; ++i) {
         DIGITS_MARK[i] = i - 97 + 10;
      }

      for(i = 65; i <= 70; ++i) {
         DIGITS_MARK[i] = i - 65 + 10;
      }

      CHARS_MARK[0] = '0';
      CHARS_MARK[1] = '1';
      CHARS_MARK[2] = '2';
      CHARS_MARK[3] = '3';
      CHARS_MARK[4] = '4';
      CHARS_MARK[5] = '5';
      CHARS_MARK[6] = '6';
      CHARS_MARK[7] = '7';
      CHARS_MARK[8] = 'b';
      CHARS_MARK[9] = 't';
      CHARS_MARK[10] = 'n';
      CHARS_MARK[11] = 'v';
      CHARS_MARK[12] = 'f';
      CHARS_MARK[13] = 'r';
      CHARS_MARK[34] = '"';
      CHARS_MARK[39] = '\'';
      CHARS_MARK[47] = '/';
      CHARS_MARK[92] = '\\';
      CHARS_MARK_REV[48] = 0;
      CHARS_MARK_REV[49] = 1;
      CHARS_MARK_REV[50] = 2;
      CHARS_MARK_REV[51] = 3;
      CHARS_MARK_REV[52] = 4;
      CHARS_MARK_REV[53] = 5;
      CHARS_MARK_REV[54] = 6;
      CHARS_MARK_REV[55] = 7;
      CHARS_MARK_REV[98] = '\b';
      CHARS_MARK_REV[116] = '\t';
      CHARS_MARK_REV[110] = '\n';
      CHARS_MARK_REV[118] = 11;
      CHARS_MARK_REV[102] = '\f';
      CHARS_MARK_REV[114] = '\r';
      CHARS_MARK_REV[34] = '"';
      CHARS_MARK_REV[39] = '\'';
      CHARS_MARK_REV[47] = '/';
      CHARS_MARK_REV[92] = '\\';
   }
}
