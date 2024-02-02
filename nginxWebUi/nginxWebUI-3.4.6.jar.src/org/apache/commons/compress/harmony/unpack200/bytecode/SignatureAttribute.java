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
/*    */ 
/*    */ public class SignatureAttribute
/*    */   extends Attribute
/*    */ {
/*    */   private int signature_index;
/*    */   private final CPUTF8 signature;
/*    */   private static CPUTF8 attributeName;
/*    */   
/*    */   public static void setAttributeName(CPUTF8 cpUTF8Value) {
/* 33 */     attributeName = cpUTF8Value;
/*    */   }
/*    */   
/*    */   public SignatureAttribute(CPUTF8 value) {
/* 37 */     super(attributeName);
/* 38 */     this.signature = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected int getLength() {
/* 48 */     return 2;
/*    */   }
/*    */ 
/*    */   
/*    */   protected ClassFileEntry[] getNestedClassFileEntries() {
/* 53 */     return new ClassFileEntry[] { getAttributeName(), this.signature };
/*    */   }
/*    */ 
/*    */   
/*    */   protected void resolve(ClassConstantPool pool) {
/* 58 */     super.resolve(pool);
/* 59 */     this.signature.resolve(pool);
/* 60 */     this.signature_index = pool.indexOf(this.signature);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 70 */     dos.writeShort(this.signature_index);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 81 */     return "Signature: " + this.signature;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\SignatureAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */