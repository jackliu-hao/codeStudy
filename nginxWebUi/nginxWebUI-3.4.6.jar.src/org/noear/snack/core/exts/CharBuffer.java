/*    */ package org.noear.snack.core.exts;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CharBuffer
/*    */ {
/*    */   private char[] buffer;
/*    */   private int length;
/*    */   public boolean isString = false;
/*    */   
/*    */   public CharBuffer() {
/* 13 */     this(5120);
/*    */   }
/*    */   public CharBuffer(int capacity) {
/* 16 */     this.buffer = new char[capacity];
/* 17 */     this.length = 0;
/*    */   }
/*    */   
/*    */   public void append(char c) {
/* 21 */     if (this.length == this.buffer.length) {
/* 22 */       char[] newbuf = new char[this.buffer.length * 2];
/* 23 */       System.arraycopy(this.buffer, 0, newbuf, 0, this.buffer.length);
/* 24 */       this.buffer = newbuf;
/*    */     } 
/* 26 */     this.buffer[this.length++] = c;
/*    */   }
/*    */   
/*    */   public char charAt(int idx) {
/* 30 */     return this.buffer[idx];
/*    */   }
/*    */   
/*    */   public int length() {
/* 34 */     return this.length;
/*    */   }
/*    */   
/*    */   public void setLength(int len) {
/* 38 */     this.length = len;
/* 39 */     this.isString = false;
/*    */   }
/*    */   
/*    */   public void clear() {
/* 43 */     this.length = 0;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 47 */     return new String(this.buffer, 0, this.length);
/*    */   }
/*    */   
/*    */   public void trimLast() {
/* 51 */     while (this.length > 0 && 
/* 52 */       this.buffer[this.length - 1] == ' ')
/*    */     {
/*    */       
/* 55 */       this.length--;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\exts\CharBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */