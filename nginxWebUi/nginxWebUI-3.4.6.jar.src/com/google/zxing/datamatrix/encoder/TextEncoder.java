/*    */ package com.google.zxing.datamatrix.encoder;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class TextEncoder
/*    */   extends C40Encoder
/*    */ {
/*    */   public int getEncodingMode() {
/* 23 */     return 2;
/*    */   }
/*    */ 
/*    */   
/*    */   int encodeChar(char c, StringBuilder sb) {
/* 28 */     if (c == ' ') {
/* 29 */       sb.append('\003');
/* 30 */       return 1;
/*    */     } 
/* 32 */     if (c >= '0' && c <= '9') {
/* 33 */       sb.append((char)(c - 48 + 4));
/* 34 */       return 1;
/*    */     } 
/* 36 */     if (c >= 'a' && c <= 'z') {
/* 37 */       sb.append((char)(c - 97 + 14));
/* 38 */       return 1;
/*    */     } 
/* 40 */     if (c >= '\000' && c <= '\037') {
/* 41 */       sb.append(false);
/* 42 */       sb.append(c);
/* 43 */       return 2;
/*    */     } 
/* 45 */     if (c >= '!' && c <= '/') {
/* 46 */       sb.append('\001');
/* 47 */       sb.append((char)(c - 33));
/* 48 */       return 2;
/*    */     } 
/* 50 */     if (c >= ':' && c <= '@') {
/* 51 */       sb.append('\001');
/* 52 */       sb.append((char)(c - 58 + 15));
/* 53 */       return 2;
/*    */     } 
/* 55 */     if (c >= '[' && c <= '_') {
/* 56 */       sb.append('\001');
/* 57 */       sb.append((char)(c - 91 + 22));
/* 58 */       return 2;
/*    */     } 
/* 60 */     if (c == '`') {
/* 61 */       sb.append('\002');
/* 62 */       sb.append((char)(c - 96));
/* 63 */       return 2;
/*    */     } 
/* 65 */     if (c >= 'A' && c <= 'Z') {
/* 66 */       sb.append('\002');
/* 67 */       sb.append((char)(c - 65 + 1));
/* 68 */       return 2;
/*    */     } 
/* 70 */     if (c >= '{' && c <= '') {
/* 71 */       sb.append('\002');
/* 72 */       sb.append((char)(c - 123 + 27));
/* 73 */       return 2;
/*    */     } 
/* 75 */     if (c >= '') {
/* 76 */       sb.append("\001\036");
/*    */       
/* 78 */       return 2 + encodeChar((char)(c - 128), sb);
/*    */     } 
/*    */     
/* 81 */     HighLevelEncoder.illegalCharacter(c);
/* 82 */     return -1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\encoder\TextEncoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */