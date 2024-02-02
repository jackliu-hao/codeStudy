/*    */ package org.apache.commons.compress.harmony.pack200;
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
/*    */ public class CPString
/*    */   extends CPConstant
/*    */ {
/*    */   private final String string;
/*    */   private final CPUTF8 utf8;
/*    */   
/*    */   public CPString(CPUTF8 utf8) {
/* 28 */     this.utf8 = utf8;
/* 29 */     this.string = utf8.getUnderlyingString();
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(Object arg0) {
/* 34 */     return this.string.compareTo(((CPString)arg0).string);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 39 */     return this.string;
/*    */   }
/*    */   
/*    */   public int getIndexInCpUtf8() {
/* 43 */     return this.utf8.getIndex();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\CPString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */