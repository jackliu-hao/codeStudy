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
/*    */ public class CPClass
/*    */   extends CPConstant
/*    */   implements Comparable
/*    */ {
/*    */   private final String className;
/*    */   private final CPUTF8 utf8;
/*    */   private final boolean isInnerClass;
/*    */   
/*    */   public CPClass(CPUTF8 utf8) {
/* 29 */     this.utf8 = utf8;
/* 30 */     this.className = utf8.getUnderlyingString();
/* 31 */     char[] chars = this.className.toCharArray();
/* 32 */     for (int i = 0; i < chars.length; i++) {
/* 33 */       if (chars[i] <= '-') {
/* 34 */         this.isInnerClass = true;
/*    */         return;
/*    */       } 
/*    */     } 
/* 38 */     this.isInnerClass = false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(Object arg0) {
/* 43 */     return this.className.compareTo(((CPClass)arg0).className);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 48 */     return this.className;
/*    */   }
/*    */   
/*    */   public int getIndexInCpUtf8() {
/* 52 */     return this.utf8.getIndex();
/*    */   }
/*    */   
/*    */   public boolean isInnerClass() {
/* 56 */     return this.isInnerClass;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\CPClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */