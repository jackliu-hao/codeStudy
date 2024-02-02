/*    */ package org.apache.http.message;
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
/*    */ 
/*    */ public class ParserCursor
/*    */ {
/*    */   private final int lowerBound;
/*    */   private final int upperBound;
/*    */   private int pos;
/*    */   
/*    */   public ParserCursor(int lowerBound, int upperBound) {
/* 47 */     if (lowerBound < 0) {
/* 48 */       throw new IndexOutOfBoundsException("Lower bound cannot be negative");
/*    */     }
/* 50 */     if (lowerBound > upperBound) {
/* 51 */       throw new IndexOutOfBoundsException("Lower bound cannot be greater then upper bound");
/*    */     }
/* 53 */     this.lowerBound = lowerBound;
/* 54 */     this.upperBound = upperBound;
/* 55 */     this.pos = lowerBound;
/*    */   }
/*    */   
/*    */   public int getLowerBound() {
/* 59 */     return this.lowerBound;
/*    */   }
/*    */   
/*    */   public int getUpperBound() {
/* 63 */     return this.upperBound;
/*    */   }
/*    */   
/*    */   public int getPos() {
/* 67 */     return this.pos;
/*    */   }
/*    */   
/*    */   public void updatePos(int pos) {
/* 71 */     if (pos < this.lowerBound) {
/* 72 */       throw new IndexOutOfBoundsException("pos: " + pos + " < lowerBound: " + this.lowerBound);
/*    */     }
/* 74 */     if (pos > this.upperBound) {
/* 75 */       throw new IndexOutOfBoundsException("pos: " + pos + " > upperBound: " + this.upperBound);
/*    */     }
/* 77 */     this.pos = pos;
/*    */   }
/*    */   
/*    */   public boolean atEnd() {
/* 81 */     return (this.pos >= this.upperBound);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 86 */     StringBuilder buffer = new StringBuilder();
/* 87 */     buffer.append('[');
/* 88 */     buffer.append(Integer.toString(this.lowerBound));
/* 89 */     buffer.append('>');
/* 90 */     buffer.append(Integer.toString(this.pos));
/* 91 */     buffer.append('>');
/* 92 */     buffer.append(Integer.toString(this.upperBound));
/* 93 */     buffer.append(']');
/* 94 */     return buffer.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\ParserCursor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */