/*    */ package org.apache.commons.codec;
/*    */ 
/*    */ import java.util.Comparator;
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
/*    */ public class StringEncoderComparator
/*    */   implements Comparator
/*    */ {
/*    */   private final StringEncoder stringEncoder;
/*    */   
/*    */   @Deprecated
/*    */   public StringEncoderComparator() {
/* 47 */     this.stringEncoder = null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StringEncoderComparator(StringEncoder stringEncoder) {
/* 57 */     this.stringEncoder = stringEncoder;
/*    */   }
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
/*    */   public int compare(Object o1, Object o2) {
/* 76 */     int compareCode = 0;
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 81 */       Comparable<Comparable<?>> s1 = (Comparable<Comparable<?>>)this.stringEncoder.encode(o1);
/* 82 */       Comparable<?> s2 = (Comparable)this.stringEncoder.encode(o2);
/* 83 */       compareCode = s1.compareTo(s2);
/* 84 */     } catch (EncoderException ee) {
/* 85 */       compareCode = 0;
/*    */     } 
/* 87 */     return compareCode;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\StringEncoderComparator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */