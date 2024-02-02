/*    */ package com.google.protobuf;
/*    */ 
/*    */ import java.util.Arrays;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class TextFormatParseLocation
/*    */ {
/* 43 */   public static final TextFormatParseLocation EMPTY = new TextFormatParseLocation(-1, -1);
/*    */ 
/*    */   
/*    */   private final int line;
/*    */ 
/*    */   
/*    */   private final int column;
/*    */ 
/*    */   
/*    */   static TextFormatParseLocation create(int line, int column) {
/* 53 */     if (line == -1 && column == -1) {
/* 54 */       return EMPTY;
/*    */     }
/* 56 */     if (line < 0 || column < 0) {
/* 57 */       throw new IllegalArgumentException(
/* 58 */           String.format("line and column values must be >= 0: line %d, column: %d", new Object[] { Integer.valueOf(line), Integer.valueOf(column) }));
/*    */     }
/* 60 */     return new TextFormatParseLocation(line, column);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private TextFormatParseLocation(int line, int column) {
/* 67 */     this.line = line;
/* 68 */     this.column = column;
/*    */   }
/*    */   
/*    */   public int getLine() {
/* 72 */     return this.line;
/*    */   }
/*    */   
/*    */   public int getColumn() {
/* 76 */     return this.column;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 81 */     return String.format("ParseLocation{line=%d, column=%d}", new Object[] { Integer.valueOf(this.line), Integer.valueOf(this.column) });
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 86 */     if (o == this) {
/* 87 */       return true;
/*    */     }
/* 89 */     if (!(o instanceof TextFormatParseLocation)) {
/* 90 */       return false;
/*    */     }
/* 92 */     TextFormatParseLocation that = (TextFormatParseLocation)o;
/* 93 */     return (this.line == that.getLine() && this.column == that.getColumn());
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 98 */     int[] values = { this.line, this.column };
/* 99 */     return Arrays.hashCode(values);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\TextFormatParseLocation.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */