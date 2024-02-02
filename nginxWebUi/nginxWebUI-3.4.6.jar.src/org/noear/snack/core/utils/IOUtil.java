/*    */ package org.noear.snack.core.utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class IOUtil
/*    */ {
/*    */   public static final char EOI = '\000';
/*  8 */   public static final char[] DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*  9 */   public static final int[] DIGITS_MARK = new int[103];
/* 10 */   public static final char[] CHARS_MARK = new char[93];
/* 11 */   public static final char[] CHARS_MARK_REV = new char[120]; static {
/*    */     int i;
/* 13 */     for (i = 48; i <= 57; i++) {
/* 14 */       DIGITS_MARK[i] = i - 48;
/*    */     }
/*    */     
/* 17 */     for (i = 97; i <= 102; i++) {
/* 18 */       DIGITS_MARK[i] = i - 97 + 10;
/*    */     }
/* 20 */     for (i = 65; i <= 70; i++) {
/* 21 */       DIGITS_MARK[i] = i - 65 + 10;
/*    */     }
/*    */     
/* 24 */     CHARS_MARK[0] = '0';
/* 25 */     CHARS_MARK[1] = '1';
/* 26 */     CHARS_MARK[2] = '2';
/* 27 */     CHARS_MARK[3] = '3';
/* 28 */     CHARS_MARK[4] = '4';
/* 29 */     CHARS_MARK[5] = '5';
/* 30 */     CHARS_MARK[6] = '6';
/* 31 */     CHARS_MARK[7] = '7';
/* 32 */     CHARS_MARK[8] = 'b';
/* 33 */     CHARS_MARK[9] = 't';
/* 34 */     CHARS_MARK[10] = 'n';
/* 35 */     CHARS_MARK[11] = 'v';
/* 36 */     CHARS_MARK[12] = 'f';
/* 37 */     CHARS_MARK[13] = 'r';
/* 38 */     CHARS_MARK[34] = '"';
/* 39 */     CHARS_MARK[39] = '\'';
/* 40 */     CHARS_MARK[47] = '/';
/* 41 */     CHARS_MARK[92] = '\\';
/*    */     
/* 43 */     CHARS_MARK_REV[48] = Character.MIN_VALUE;
/* 44 */     CHARS_MARK_REV[49] = '\001';
/* 45 */     CHARS_MARK_REV[50] = '\002';
/* 46 */     CHARS_MARK_REV[51] = '\003';
/* 47 */     CHARS_MARK_REV[52] = '\004';
/* 48 */     CHARS_MARK_REV[53] = '\005';
/* 49 */     CHARS_MARK_REV[54] = '\006';
/* 50 */     CHARS_MARK_REV[55] = '\007';
/* 51 */     CHARS_MARK_REV[98] = '\b';
/* 52 */     CHARS_MARK_REV[116] = '\t';
/* 53 */     CHARS_MARK_REV[110] = '\n';
/* 54 */     CHARS_MARK_REV[118] = '\013';
/* 55 */     CHARS_MARK_REV[102] = '\f';
/* 56 */     CHARS_MARK_REV[114] = '\r';
/* 57 */     CHARS_MARK_REV[34] = '"';
/* 58 */     CHARS_MARK_REV[39] = '\'';
/* 59 */     CHARS_MARK_REV[47] = '/';
/* 60 */     CHARS_MARK_REV[92] = '\\';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\cor\\utils\IOUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */