/*    */ package com.sun.jna;
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
/*    */ public final class WString
/*    */   implements CharSequence, Comparable
/*    */ {
/*    */   private String string;
/*    */   
/*    */   public WString(String s) {
/* 32 */     if (s == null) {
/* 33 */       throw new NullPointerException("String initializer must be non-null");
/*    */     }
/* 35 */     this.string = s;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 39 */     return this.string;
/*    */   }
/*    */   
/*    */   public boolean equals(Object o) {
/* 43 */     return (o instanceof WString && toString().equals(o.toString()));
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 47 */     return toString().hashCode();
/*    */   }
/*    */   
/*    */   public int compareTo(Object o) {
/* 51 */     return toString().compareTo(o.toString());
/*    */   }
/*    */   
/*    */   public int length() {
/* 55 */     return toString().length();
/*    */   }
/*    */   
/*    */   public char charAt(int index) {
/* 59 */     return toString().charAt(index);
/*    */   }
/*    */   
/*    */   public CharSequence subSequence(int start, int end) {
/* 63 */     return toString().subSequence(start, end);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\WString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */