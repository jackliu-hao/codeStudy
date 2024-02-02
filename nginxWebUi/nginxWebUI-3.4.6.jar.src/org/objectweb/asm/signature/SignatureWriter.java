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
/*     */ public class SignatureWriter
/*     */   extends SignatureVisitor
/*     */ {
/*  44 */   private final StringBuilder stringBuilder = new StringBuilder();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasFormals;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasParameters;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int argumentStack;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SignatureWriter() {
/*  73 */     super(589824);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitFormalTypeParameter(String name) {
/*  82 */     if (!this.hasFormals) {
/*  83 */       this.hasFormals = true;
/*  84 */       this.stringBuilder.append('<');
/*     */     } 
/*  86 */     this.stringBuilder.append(name);
/*  87 */     this.stringBuilder.append(':');
/*     */   }
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitClassBound() {
/*  92 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitInterfaceBound() {
/*  97 */     this.stringBuilder.append(':');
/*  98 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitSuperclass() {
/* 103 */     endFormals();
/* 104 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitInterface() {
/* 109 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitParameterType() {
/* 114 */     endFormals();
/* 115 */     if (!this.hasParameters) {
/* 116 */       this.hasParameters = true;
/* 117 */       this.stringBuilder.append('(');
/*     */     } 
/* 119 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitReturnType() {
/* 124 */     endFormals();
/* 125 */     if (!this.hasParameters) {
/* 126 */       this.stringBuilder.append('(');
/*     */     }
/* 128 */     this.stringBuilder.append(')');
/* 129 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitExceptionType() {
/* 134 */     this.stringBuilder.append('^');
/* 135 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitBaseType(char descriptor) {
/* 140 */     this.stringBuilder.append(descriptor);
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitTypeVariable(String name) {
/* 145 */     this.stringBuilder.append('T');
/* 146 */     this.stringBuilder.append(name);
/* 147 */     this.stringBuilder.append(';');
/*     */   }
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitArrayType() {
/* 152 */     this.stringBuilder.append('[');
/* 153 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitClassType(String name) {
/* 158 */     this.stringBuilder.append('L');
/* 159 */     this.stringBuilder.append(name);
/*     */ 
/*     */     
/* 162 */     this.argumentStack *= 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitInnerClassType(String name) {
/* 167 */     endArguments();
/* 168 */     this.stringBuilder.append('.');
/* 169 */     this.stringBuilder.append(name);
/*     */ 
/*     */     
/* 172 */     this.argumentStack *= 2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitTypeArgument() {
/* 180 */     if (this.argumentStack % 2 == 0) {
/* 181 */       this.argumentStack |= 0x1;
/* 182 */       this.stringBuilder.append('<');
/*     */     } 
/* 184 */     this.stringBuilder.append('*');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SignatureVisitor visitTypeArgument(char wildcard) {
/* 192 */     if (this.argumentStack % 2 == 0) {
/* 193 */       this.argumentStack |= 0x1;
/* 194 */       this.stringBuilder.append('<');
/*     */     } 
/* 196 */     if (wildcard != '=') {
/* 197 */       this.stringBuilder.append(wildcard);
/*     */     }
/* 199 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitEnd() {
/* 204 */     endArguments();
/* 205 */     this.stringBuilder.append(';');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 215 */     return this.stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void endFormals() {
/* 224 */     if (this.hasFormals) {
/* 225 */       this.hasFormals = false;
/* 226 */       this.stringBuilder.append('>');
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void endArguments() {
/* 235 */     if (this.argumentStack % 2 == 1) {
/* 236 */       this.stringBuilder.append('>');
/*     */     }
/* 238 */     this.argumentStack /= 2;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\signature\SignatureWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */