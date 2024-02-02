/*    */ package org.apache.commons.compress.harmony.pack200;
/*    */ 
/*    */ import java.util.List;
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
/*    */ public class CPSignature
/*    */   extends ConstantPoolEntry
/*    */   implements Comparable
/*    */ {
/*    */   private final CPUTF8 signatureForm;
/*    */   private final List classes;
/*    */   private final String signature;
/*    */   private final boolean formStartsWithBracket;
/*    */   
/*    */   public CPSignature(String signature, CPUTF8 signatureForm, List classes) {
/* 32 */     this.signature = signature;
/* 33 */     this.signatureForm = signatureForm;
/* 34 */     this.classes = classes;
/* 35 */     this.formStartsWithBracket = signatureForm.toString().startsWith("(");
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(Object arg0) {
/* 40 */     if (this.signature.equals(((CPSignature)arg0).signature)) {
/* 41 */       return 0;
/*    */     }
/* 43 */     if (this.formStartsWithBracket && !((CPSignature)arg0).formStartsWithBracket) {
/* 44 */       return 1;
/*    */     }
/* 46 */     if (((CPSignature)arg0).formStartsWithBracket && !this.formStartsWithBracket) {
/* 47 */       return -1;
/*    */     }
/* 49 */     if (this.classes.size() - ((CPSignature)arg0).classes.size() != 0) {
/* 50 */       return this.classes.size() - ((CPSignature)arg0).classes.size();
/*    */     }
/* 52 */     if (this.classes.size() > 0) {
/* 53 */       for (int i = this.classes.size() - 1; i >= 0; i--) {
/* 54 */         CPClass cpClass = this.classes.get(i);
/* 55 */         CPClass compareClass = ((CPSignature)arg0).classes.get(i);
/* 56 */         int classComp = cpClass.compareTo(compareClass);
/* 57 */         if (classComp != 0) {
/* 58 */           return classComp;
/*    */         }
/*    */       } 
/*    */     }
/* 62 */     return this.signature.compareTo(((CPSignature)arg0).signature);
/*    */   }
/*    */   
/*    */   public int getIndexInCpUtf8() {
/* 66 */     return this.signatureForm.getIndex();
/*    */   }
/*    */   
/*    */   public List getClasses() {
/* 70 */     return this.classes;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 75 */     return this.signature;
/*    */   }
/*    */   
/*    */   public String getUnderlyingString() {
/* 79 */     return this.signature;
/*    */   }
/*    */   
/*    */   public CPUTF8 getSignatureForm() {
/* 83 */     return this.signatureForm;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\CPSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */