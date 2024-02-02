/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*    */ 
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
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
/*    */ public class CPLong
/*    */   extends CPConstantNumber
/*    */ {
/*    */   public CPLong(Long value, int globalIndex) {
/* 28 */     super((byte)5, value, globalIndex);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 33 */     dos.writeLong(getNumber().longValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 38 */     return "Long: " + getValue();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */