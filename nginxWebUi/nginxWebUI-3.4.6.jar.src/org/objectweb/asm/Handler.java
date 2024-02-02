/*     */ package org.objectweb.asm;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class Handler
/*     */ {
/*     */   final Label startPc;
/*     */   final Label endPc;
/*     */   final Label handlerPc;
/*     */   final int catchType;
/*     */   final String catchTypeDescriptor;
/*     */   Handler nextHandler;
/*     */   
/*     */   Handler(Label startPc, Label endPc, Label handlerPc, int catchType, String catchTypeDescriptor) {
/*  91 */     this.startPc = startPc;
/*  92 */     this.endPc = endPc;
/*  93 */     this.handlerPc = handlerPc;
/*  94 */     this.catchType = catchType;
/*  95 */     this.catchTypeDescriptor = catchTypeDescriptor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Handler(Handler handler, Label startPc, Label endPc) {
/* 106 */     this(startPc, endPc, handler.handlerPc, handler.catchType, handler.catchTypeDescriptor);
/* 107 */     this.nextHandler = handler.nextHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Handler removeRange(Handler firstHandler, Label start, Label end) {
/* 120 */     if (firstHandler == null) {
/* 121 */       return null;
/*     */     }
/* 123 */     firstHandler.nextHandler = removeRange(firstHandler.nextHandler, start, end);
/*     */     
/* 125 */     int handlerStart = firstHandler.startPc.bytecodeOffset;
/* 126 */     int handlerEnd = firstHandler.endPc.bytecodeOffset;
/* 127 */     int rangeStart = start.bytecodeOffset;
/* 128 */     int rangeEnd = (end == null) ? Integer.MAX_VALUE : end.bytecodeOffset;
/*     */     
/* 130 */     if (rangeStart >= handlerEnd || rangeEnd <= handlerStart) {
/* 131 */       return firstHandler;
/*     */     }
/* 133 */     if (rangeStart <= handlerStart) {
/* 134 */       if (rangeEnd >= handlerEnd)
/*     */       {
/* 136 */         return firstHandler.nextHandler;
/*     */       }
/*     */       
/* 139 */       return new Handler(firstHandler, end, firstHandler.endPc);
/*     */     } 
/* 141 */     if (rangeEnd >= handlerEnd)
/*     */     {
/* 143 */       return new Handler(firstHandler, firstHandler.startPc, start);
/*     */     }
/*     */ 
/*     */     
/* 147 */     firstHandler.nextHandler = new Handler(firstHandler, end, firstHandler.endPc);
/* 148 */     return new Handler(firstHandler, firstHandler.startPc, start);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int getExceptionTableLength(Handler firstHandler) {
/* 159 */     int length = 0;
/* 160 */     Handler handler = firstHandler;
/* 161 */     while (handler != null) {
/* 162 */       length++;
/* 163 */       handler = handler.nextHandler;
/*     */     } 
/* 165 */     return length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int getExceptionTableSize(Handler firstHandler) {
/* 176 */     return 2 + 8 * getExceptionTableLength(firstHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void putExceptionTable(Handler firstHandler, ByteVector output) {
/* 187 */     output.putShort(getExceptionTableLength(firstHandler));
/* 188 */     Handler handler = firstHandler;
/* 189 */     while (handler != null) {
/* 190 */       output
/* 191 */         .putShort(handler.startPc.bytecodeOffset)
/* 192 */         .putShort(handler.endPc.bytecodeOffset)
/* 193 */         .putShort(handler.handlerPc.bytecodeOffset)
/* 194 */         .putShort(handler.catchType);
/* 195 */       handler = handler.nextHandler;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\Handler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */