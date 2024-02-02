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
/*    */ public class CPMethodOrField
/*    */   extends ConstantPoolEntry
/*    */   implements Comparable
/*    */ {
/*    */   private final CPClass className;
/*    */   private final CPNameAndType nameAndType;
/* 26 */   private int indexInClass = -1;
/* 27 */   private int indexInClassForConstructor = -1;
/*    */   
/*    */   public CPMethodOrField(CPClass className, CPNameAndType nameAndType) {
/* 30 */     this.className = className;
/* 31 */     this.nameAndType = nameAndType;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 36 */     return this.className + ": " + this.nameAndType;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(Object obj) {
/* 41 */     if (obj instanceof CPMethodOrField) {
/* 42 */       CPMethodOrField mof = (CPMethodOrField)obj;
/* 43 */       int compareName = this.className.compareTo(mof.className);
/* 44 */       if (compareName == 0) {
/* 45 */         return this.nameAndType.compareTo(mof.nameAndType);
/*    */       }
/* 47 */       return compareName;
/*    */     } 
/* 49 */     return 0;
/*    */   }
/*    */   
/*    */   public int getClassIndex() {
/* 53 */     return this.className.getIndex();
/*    */   }
/*    */   
/*    */   public CPClass getClassName() {
/* 57 */     return this.className;
/*    */   }
/*    */   
/*    */   public int getDescIndex() {
/* 61 */     return this.nameAndType.getIndex();
/*    */   }
/*    */   
/*    */   public CPNameAndType getDesc() {
/* 65 */     return this.nameAndType;
/*    */   }
/*    */   
/*    */   public int getIndexInClass() {
/* 69 */     return this.indexInClass;
/*    */   }
/*    */   
/*    */   public void setIndexInClass(int index) {
/* 73 */     this.indexInClass = index;
/*    */   }
/*    */   
/*    */   public int getIndexInClassForConstructor() {
/* 77 */     return this.indexInClassForConstructor;
/*    */   }
/*    */   
/*    */   public void setIndexInClassForConstructor(int index) {
/* 81 */     this.indexInClassForConstructor = index;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\CPMethodOrField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */