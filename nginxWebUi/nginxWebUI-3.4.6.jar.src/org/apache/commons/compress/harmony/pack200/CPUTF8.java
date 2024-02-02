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
/*    */ public class CPUTF8
/*    */   extends ConstantPoolEntry
/*    */   implements Comparable
/*    */ {
/*    */   private final String string;
/*    */   
/*    */   public CPUTF8(String string) {
/* 27 */     this.string = string;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(Object arg0) {
/* 32 */     return this.string.compareTo(((CPUTF8)arg0).string);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 37 */     return this.string;
/*    */   }
/*    */   
/*    */   public String getUnderlyingString() {
/* 41 */     return this.string;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\CPUTF8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */