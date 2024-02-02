/*     */ package org.objectweb.asm;
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
/*     */ public abstract class RecordComponentVisitor
/*     */ {
/*     */   protected final int api;
/*     */   RecordComponentVisitor delegate;
/*     */   
/*     */   public RecordComponentVisitor(int api) {
/*  57 */     this(api, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RecordComponentVisitor(int api, RecordComponentVisitor recordComponentVisitor) {
/*  69 */     if (api != 589824 && api != 524288 && api != 458752 && api != 393216 && api != 327680 && api != 262144 && api != 17432576)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  76 */       throw new IllegalArgumentException("Unsupported api " + api);
/*     */     }
/*  78 */     if (api == 17432576) {
/*  79 */       Constants.checkAsmExperimental(this);
/*     */     }
/*  81 */     this.api = api;
/*  82 */     this.delegate = recordComponentVisitor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RecordComponentVisitor getDelegate() {
/*  91 */     return this.delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
/* 103 */     if (this.delegate != null) {
/* 104 */       return this.delegate.visitAnnotation(descriptor, visible);
/*     */     }
/* 106 */     return null;
/*     */   }
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
/*     */   public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
/* 126 */     if (this.delegate != null) {
/* 127 */       return this.delegate.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
/*     */     }
/* 129 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitAttribute(Attribute attribute) {
/* 138 */     if (this.delegate != null) {
/* 139 */       this.delegate.visitAttribute(attribute);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitEnd() {
/* 148 */     if (this.delegate != null)
/* 149 */       this.delegate.visitEnd(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\RecordComponentVisitor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */