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
/*     */ 
/*     */ 
/*     */ public abstract class AnnotationVisitor
/*     */ {
/*     */   protected final int api;
/*     */   protected AnnotationVisitor av;
/*     */   
/*     */   public AnnotationVisitor(int api) {
/*  59 */     this(api, null);
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
/*     */   public AnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
/*  71 */     if (api != 589824 && api != 524288 && api != 458752 && api != 393216 && api != 327680 && api != 262144 && api != 17432576)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  78 */       throw new IllegalArgumentException("Unsupported api " + api);
/*     */     }
/*  80 */     if (api == 17432576) {
/*  81 */       Constants.checkAsmExperimental(this);
/*     */     }
/*  83 */     this.api = api;
/*  84 */     this.av = annotationVisitor;
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
/*     */   public void visit(String name, Object value) {
/*  99 */     if (this.av != null) {
/* 100 */       this.av.visit(name, value);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitEnum(String name, String descriptor, String value) {
/* 112 */     if (this.av != null) {
/* 113 */       this.av.visitEnum(name, descriptor, value);
/*     */     }
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
/*     */   public AnnotationVisitor visitAnnotation(String name, String descriptor) {
/* 127 */     if (this.av != null) {
/* 128 */       return this.av.visitAnnotation(name, descriptor);
/*     */     }
/* 130 */     return null;
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
/*     */   public AnnotationVisitor visitArray(String name) {
/* 145 */     if (this.av != null) {
/* 146 */       return this.av.visitArray(name);
/*     */     }
/* 148 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitEnd() {
/* 153 */     if (this.av != null)
/* 154 */       this.av.visitEnd(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\AnnotationVisitor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */