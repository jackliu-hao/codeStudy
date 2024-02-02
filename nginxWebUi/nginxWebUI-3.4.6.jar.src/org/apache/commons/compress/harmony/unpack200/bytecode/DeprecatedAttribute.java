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
/*    */ public class DeprecatedAttribute
/*    */   extends Attribute
/*    */ {
/*    */   private static CPUTF8 attributeName;
/*    */   
/*    */   public static void setAttributeName(CPUTF8 cpUTF8Value) {
/* 30 */     attributeName = cpUTF8Value;
/*    */   }
/*    */   
/*    */   public DeprecatedAttribute() {
/* 34 */     super(attributeName);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected int getLength() {
/* 44 */     return 0;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void writeBody(DataOutputStream dos) throws IOException {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 64 */     return "Deprecated Attribute";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\DeprecatedAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */