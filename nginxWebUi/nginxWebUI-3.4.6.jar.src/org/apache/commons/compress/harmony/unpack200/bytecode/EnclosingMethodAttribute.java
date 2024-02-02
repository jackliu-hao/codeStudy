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
/*    */ public class EnclosingMethodAttribute
/*    */   extends Attribute
/*    */ {
/*    */   private int class_index;
/*    */   private int method_index;
/*    */   private final CPClass cpClass;
/*    */   private final CPNameAndType method;
/*    */   private static CPUTF8 attributeName;
/*    */   
/*    */   public static void setAttributeName(CPUTF8 cpUTF8Value) {
/* 34 */     attributeName = cpUTF8Value;
/*    */   }
/*    */   
/*    */   public EnclosingMethodAttribute(CPClass cpClass, CPNameAndType method) {
/* 38 */     super(attributeName);
/* 39 */     this.cpClass = cpClass;
/* 40 */     this.method = method;
/*    */   }
/*    */ 
/*    */   
/*    */   protected ClassFileEntry[] getNestedClassFileEntries() {
/* 45 */     if (this.method != null) {
/* 46 */       return new ClassFileEntry[] { attributeName, this.cpClass, this.method };
/*    */     }
/* 48 */     return new ClassFileEntry[] { attributeName, this.cpClass };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected int getLength() {
/* 58 */     return 4;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void resolve(ClassConstantPool pool) {
/* 63 */     super.resolve(pool);
/* 64 */     this.cpClass.resolve(pool);
/* 65 */     this.class_index = pool.indexOf(this.cpClass);
/* 66 */     if (this.method != null) {
/* 67 */       this.method.resolve(pool);
/* 68 */       this.method_index = pool.indexOf(this.method);
/*    */     } else {
/* 70 */       this.method_index = 0;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 81 */     dos.writeShort(this.class_index);
/* 82 */     dos.writeShort(this.method_index);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 92 */     return "EnclosingMethod";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\EnclosingMethodAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */