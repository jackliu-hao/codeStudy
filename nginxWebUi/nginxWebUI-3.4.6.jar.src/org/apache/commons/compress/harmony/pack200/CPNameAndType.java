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
/*    */ public class CPNameAndType
/*    */   extends ConstantPoolEntry
/*    */   implements Comparable
/*    */ {
/*    */   private final CPUTF8 name;
/*    */   private final CPSignature signature;
/*    */   
/*    */   public CPNameAndType(CPUTF8 name, CPSignature signature) {
/* 28 */     this.name = name;
/* 29 */     this.signature = signature;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 34 */     return this.name + ":" + this.signature;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(Object obj) {
/* 39 */     if (obj instanceof CPNameAndType) {
/* 40 */       CPNameAndType nat = (CPNameAndType)obj;
/* 41 */       int compareSignature = this.signature.compareTo(nat.signature);
/* 42 */       if (compareSignature == 0) {
/* 43 */         return this.name.compareTo(nat.name);
/*    */       }
/* 45 */       return compareSignature;
/*    */     } 
/* 47 */     return 0;
/*    */   }
/*    */   
/*    */   public int getNameIndex() {
/* 51 */     return this.name.getIndex();
/*    */   }
/*    */   
/*    */   public String getName() {
/* 55 */     return this.name.getUnderlyingString();
/*    */   }
/*    */   
/*    */   public int getTypeIndex() {
/* 59 */     return this.signature.getIndex();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\CPNameAndType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */