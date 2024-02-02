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
/*    */ final class Base256Encoder
/*    */   implements Encoder
/*    */ {
/*    */   public int getEncodingMode() {
/* 23 */     return 5;
/*    */   }
/*    */ 
/*    */   
/*    */   public void encode(EncoderContext context) {
/*    */     StringBuilder buffer;
/* 29 */     (buffer = new StringBuilder()).append(false);
/* 30 */     while (context.hasMoreCharacters()) {
/* 31 */       char c1 = context.getCurrentChar();
/* 32 */       buffer.append(c1);
/*    */       
/* 34 */       context.pos++;
/*    */       
/*    */       int newMode;
/* 37 */       if ((newMode = HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, getEncodingMode())) != getEncodingMode()) {
/* 38 */         context.signalEncoderChange(newMode);
/*    */         break;
/*    */       } 
/*    */     } 
/* 42 */     int dataCount = buffer.length() - 1;
/*    */     
/* 44 */     int currentSize = context.getCodewordCount() + dataCount + 1;
/* 45 */     context.updateSymbolInfo(currentSize);
/* 46 */     boolean mustPad = (context.getSymbolInfo().getDataCapacity() - currentSize > 0);
/* 47 */     if (context.hasMoreCharacters() || mustPad) {
/* 48 */       if (dataCount <= 249) {
/* 49 */         buffer.setCharAt(0, (char)dataCount);
/* 50 */       } else if (dataCount <= 1555) {
/* 51 */         buffer.setCharAt(0, (char)(dataCount / 250 + 249));
/* 52 */         buffer.insert(1, (char)(dataCount % 250));
/*    */       } else {
/* 54 */         throw new IllegalStateException("Message length not in valid ranges: " + dataCount);
/*    */       } 
/*    */     }
/*    */     
/* 58 */     for (int i = 0, c = buffer.length(); i < c; i++) {
/* 59 */       context.writeCodeword(randomize255State(buffer
/* 60 */             .charAt(i), context.getCodewordCount() + 1));
/*    */     }
/*    */   }
/*    */   
/*    */   private static char randomize255State(char ch, int codewordPosition) {
/* 65 */     int pseudoRandom = codewordPosition * 149 % 255 + 1;
/*    */     int tempVariable;
/* 67 */     if ((tempVariable = ch + pseudoRandom) <= 255) {
/* 68 */       return (char)tempVariable;
/*    */     }
/* 70 */     return (char)(tempVariable - 256);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\encoder\Base256Encoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */