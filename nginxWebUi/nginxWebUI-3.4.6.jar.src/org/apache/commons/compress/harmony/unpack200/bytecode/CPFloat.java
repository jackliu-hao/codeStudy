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
/*    */ public class CPFloat
/*    */   extends CPConstantNumber
/*    */ {
/*    */   public CPFloat(Float value, int globalIndex) {
/* 28 */     super((byte)4, value, globalIndex);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 33 */     dos.writeFloat(getNumber().floatValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 38 */     return "Float: " + getValue();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPFloat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */