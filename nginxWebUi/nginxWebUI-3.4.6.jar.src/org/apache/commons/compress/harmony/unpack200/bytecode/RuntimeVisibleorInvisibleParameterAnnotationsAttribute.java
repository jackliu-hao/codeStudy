/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class RuntimeVisibleorInvisibleParameterAnnotationsAttribute
/*     */   extends AnnotationsAttribute
/*     */ {
/*     */   private final int num_parameters;
/*     */   private final ParameterAnnotation[] parameter_annotations;
/*     */   
/*     */   public RuntimeVisibleorInvisibleParameterAnnotationsAttribute(CPUTF8 name, ParameterAnnotation[] parameter_annotations) {
/*  35 */     super(name);
/*  36 */     this.num_parameters = parameter_annotations.length;
/*  37 */     this.parameter_annotations = parameter_annotations;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int getLength() {
/*  42 */     int length = 1;
/*  43 */     for (int i = 0; i < this.num_parameters; i++) {
/*  44 */       length += this.parameter_annotations[i].getLength();
/*     */     }
/*  46 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/*  51 */     super.resolve(pool);
/*  52 */     for (int i = 0; i < this.parameter_annotations.length; i++) {
/*  53 */       this.parameter_annotations[i].resolve(pool);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeBody(DataOutputStream dos) throws IOException {
/*  59 */     dos.writeByte(this.num_parameters);
/*  60 */     for (int i = 0; i < this.num_parameters; i++) {
/*  61 */       this.parameter_annotations[i].writeBody(dos);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  67 */     return this.attributeName.underlyingString() + ": " + this.num_parameters + " parameter annotations";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class ParameterAnnotation
/*     */   {
/*     */     private final AnnotationsAttribute.Annotation[] annotations;
/*     */     
/*     */     private final int num_annotations;
/*     */ 
/*     */     
/*     */     public ParameterAnnotation(AnnotationsAttribute.Annotation[] annotations) {
/*  79 */       this.num_annotations = annotations.length;
/*  80 */       this.annotations = annotations;
/*     */     }
/*     */     
/*     */     public void writeBody(DataOutputStream dos) throws IOException {
/*  84 */       dos.writeShort(this.num_annotations);
/*  85 */       for (int i = 0; i < this.annotations.length; i++) {
/*  86 */         this.annotations[i].writeBody(dos);
/*     */       }
/*     */     }
/*     */     
/*     */     public void resolve(ClassConstantPool pool) {
/*  91 */       for (int i = 0; i < this.annotations.length; i++) {
/*  92 */         this.annotations[i].resolve(pool);
/*     */       }
/*     */     }
/*     */     
/*     */     public int getLength() {
/*  97 */       int length = 2;
/*  98 */       for (int i = 0; i < this.annotations.length; i++) {
/*  99 */         length += this.annotations[i].getLength();
/*     */       }
/* 101 */       return length;
/*     */     }
/*     */     
/*     */     public List getClassFileEntries() {
/* 105 */       List nested = new ArrayList();
/* 106 */       for (int i = 0; i < this.annotations.length; i++) {
/* 107 */         nested.addAll(this.annotations[i].getClassFileEntries());
/*     */       }
/* 109 */       return nested;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/* 116 */     List<CPUTF8> nested = new ArrayList();
/* 117 */     nested.add(this.attributeName);
/* 118 */     for (int i = 0; i < this.parameter_annotations.length; i++) {
/* 119 */       nested.addAll(this.parameter_annotations[i].getClassFileEntries());
/*     */     }
/* 121 */     ClassFileEntry[] nestedEntries = new ClassFileEntry[nested.size()];
/* 122 */     for (int j = 0; j < nestedEntries.length; j++) {
/* 123 */       nestedEntries[j] = nested.get(j);
/*     */     }
/* 125 */     return nestedEntries;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\RuntimeVisibleorInvisibleParameterAnnotationsAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */