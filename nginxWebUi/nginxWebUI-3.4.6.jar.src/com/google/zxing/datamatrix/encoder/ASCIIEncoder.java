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
/*    */ final class ASCIIEncoder
/*    */   implements Encoder
/*    */ {
/*    */   public int getEncodingMode() {
/* 23 */     return 0;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void encode(EncoderContext context) {
/* 29 */     if (HighLevelEncoder.determineConsecutiveDigitCount(context.getMessage(), context.pos) >= 
/* 30 */       2) {
/* 31 */       context.writeCodeword(encodeASCIIDigits(context.getMessage().charAt(context.pos), context
/* 32 */             .getMessage().charAt(context.pos + 1)));
/* 33 */       context.pos += 2; return;
/*    */     } 
/* 35 */     char c = context.getCurrentChar();
/*    */     int newMode;
/* 37 */     if ((newMode = HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, getEncodingMode())) != getEncodingMode()) {
/* 38 */       switch (newMode) {
/*    */         case 5:
/* 40 */           context.writeCodeword('ç');
/* 41 */           context.signalEncoderChange(5);
/*    */           return;
/*    */         case 1:
/* 44 */           context.writeCodeword('æ');
/* 45 */           context.signalEncoderChange(1);
/*    */           return;
/*    */         case 3:
/* 48 */           context.writeCodeword('î');
/* 49 */           context.signalEncoderChange(3);
/*    */           return;
/*    */         case 2:
/* 52 */           context.writeCodeword('ï');
/* 53 */           context.signalEncoderChange(2);
/*    */           return;
/*    */         case 4:
/* 56 */           context.writeCodeword('ð');
/* 57 */           context.signalEncoderChange(4);
/*    */           return;
/*    */       } 
/* 60 */       throw new IllegalStateException("Illegal mode: " + newMode);
/*    */     } 
/* 62 */     if (HighLevelEncoder.isExtendedASCII(c)) {
/* 63 */       context.writeCodeword('ë');
/* 64 */       context.writeCodeword((char)(c - 128 + 1));
/* 65 */       context.pos++; return;
/*    */     } 
/* 67 */     context.writeCodeword((char)(c + 1));
/* 68 */     context.pos++;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static char encodeASCIIDigits(char digit1, char digit2) {
/* 75 */     if (HighLevelEncoder.isDigit(digit1) && HighLevelEncoder.isDigit(digit2))
/*    */     {
/* 77 */       return (char)((digit1 - 48) * 10 + digit2 - 48 + 130);
/*    */     }
/* 79 */     throw new IllegalArgumentException("not digits: " + digit1 + digit2);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\encoder\ASCIIEncoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */