/*     */ package org.objectweb.asm.signature;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SignatureVisitor
/*     */ {
/*     */   public static final char EXTENDS = '+';
/*     */   public static final char SUPER = '-';
/*     */   public static final char INSTANCEOF = '=';
/*     */   protected final int api;
/*     */   
/*     */   public SignatureVisitor(int api) {
/*  75 */     if (api != 589824 && api != 524288 && api != 458752 && api != 393216 && api != 327680 && api != 262144 && api != 17432576)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  82 */       throw new IllegalArgumentException("Unsupported api " + api);
/*     */     }
/*  84 */     this.api = api;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitFormalTypeParameter(String name) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitClassBound() {
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitInterfaceBound() {
/* 109 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitSuperclass() {
/* 118 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitInterface() {
/* 127 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitParameterType() {
/* 136 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitReturnType() {
/* 145 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitExceptionType() {
/* 154 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitBaseType(char descriptor) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitTypeVariable(String name) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitArrayType() {
/* 177 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitClassType(String name) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitInnerClassType(String name) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitTypeArgument() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitTypeArgument(char wildcard) {
/* 204 */     return this;
/*     */   }
/*     */   
/*     */   public void visitEnd() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\signature\SignatureVisitor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */