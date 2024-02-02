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
/*    */ final class X12Encoder
/*    */   extends C40Encoder
/*    */ {
/*    */   public int getEncodingMode() {
/* 23 */     return 3;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void encode(EncoderContext context) {
/* 29 */     StringBuilder buffer = new StringBuilder();
/* 30 */     while (context.hasMoreCharacters()) {
/* 31 */       char c = context.getCurrentChar();
/* 32 */       context.pos++;
/*    */       
/* 34 */       encodeChar(c, buffer);
/*    */ 
/*    */ 
/*    */       
/* 38 */       writeNextTriplet(context, buffer);
/*    */       
/*    */       int newMode;
/* 41 */       if (buffer.length() % 3 == 0 && (newMode = HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, getEncodingMode())) != getEncodingMode()) {
/* 42 */         context.signalEncoderChange(newMode);
/*    */         
/*    */         break;
/*    */       } 
/*    */     } 
/* 47 */     handleEOD(context, buffer);
/*    */   }
/*    */ 
/*    */   
/*    */   int encodeChar(char c, StringBuilder sb) {
/* 52 */     if (c == '\r') {
/* 53 */       sb.append(false);
/* 54 */     } else if (c == '*') {
/* 55 */       sb.append('\001');
/* 56 */     } else if (c == '>') {
/* 57 */       sb.append('\002');
/* 58 */     } else if (c == ' ') {
/* 59 */       sb.append('\003');
/* 60 */     } else if (c >= '0' && c <= '9') {
/* 61 */       sb.append((char)(c - 48 + 4));
/* 62 */     } else if (c >= 'A' && c <= 'Z') {
/* 63 */       sb.append((char)(c - 65 + 14));
/*    */     } else {
/* 65 */       HighLevelEncoder.illegalCharacter(c);
/*    */     } 
/* 67 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   void handleEOD(EncoderContext context, StringBuilder buffer) {
/* 72 */     context.updateSymbolInfo();
/* 73 */     int available = context.getSymbolInfo().getDataCapacity() - context.getCodewordCount();
/* 74 */     int count = buffer.length();
/* 75 */     context.pos -= count;
/* 76 */     if (context.getRemainingCharacters() > 1 || available > 1 || context
/* 77 */       .getRemainingCharacters() != available) {
/* 78 */       context.writeCodeword('þ');
/*    */     }
/* 80 */     if (context.getNewEncoding() < 0)
/* 81 */       context.signalEncoderChange(0); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\encoder\X12Encoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */