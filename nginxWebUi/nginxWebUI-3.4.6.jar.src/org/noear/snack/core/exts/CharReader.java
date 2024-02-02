/*    */ package org.noear.snack.core.exts;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CharReader
/*    */ {
/*    */   private String chars;
/*    */   private int _length;
/* 12 */   private int _next = 0;
/*    */   private char _val;
/*    */   
/*    */   public CharReader(String s) {
/* 16 */     this.chars = s;
/* 17 */     this._length = s.length();
/*    */   }
/*    */   
/*    */   public boolean read() {
/* 21 */     if (this._next >= this._length) {
/* 22 */       return false;
/*    */     }
/* 24 */     this._val = this.chars.charAt(this._next++);
/* 25 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public char next() {
/* 30 */     if (read()) {
/* 31 */       return this._val;
/*    */     }
/* 33 */     return Character.MIN_VALUE;
/*    */   }
/*    */ 
/*    */   
/*    */   public int length() {
/* 38 */     return this._length;
/*    */   }
/*    */   
/*    */   public char value() {
/* 42 */     return this._val;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\exts\CharReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */