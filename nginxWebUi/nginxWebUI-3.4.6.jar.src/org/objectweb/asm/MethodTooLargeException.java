/*    */ package org.objectweb.asm;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class MethodTooLargeException
/*    */   extends IndexOutOfBoundsException
/*    */ {
/*    */   private static final long serialVersionUID = 6807380416709738314L;
/*    */   private final String className;
/*    */   private final String methodName;
/*    */   private final String descriptor;
/*    */   private final int codeSize;
/*    */   
/*    */   public MethodTooLargeException(String className, String methodName, String descriptor, int codeSize) {
/* 57 */     super("Method too large: " + className + "." + methodName + " " + descriptor);
/* 58 */     this.className = className;
/* 59 */     this.methodName = methodName;
/* 60 */     this.descriptor = descriptor;
/* 61 */     this.codeSize = codeSize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getClassName() {
/* 70 */     return this.className;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMethodName() {
/* 79 */     return this.methodName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getDescriptor() {
/* 88 */     return this.descriptor;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCodeSize() {
/* 97 */     return this.codeSize;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\MethodTooLargeException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */