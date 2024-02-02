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
/*     */ final class RecordComponentWriter
/*     */   extends RecordComponentVisitor
/*     */ {
/*     */   private final SymbolTable symbolTable;
/*     */   private final int nameIndex;
/*     */   private final int descriptorIndex;
/*     */   private int signatureIndex;
/*     */   private AnnotationWriter lastRuntimeVisibleAnnotation;
/*     */   private AnnotationWriter lastRuntimeInvisibleAnnotation;
/*     */   private AnnotationWriter lastRuntimeVisibleTypeAnnotation;
/*     */   private AnnotationWriter lastRuntimeInvisibleTypeAnnotation;
/*     */   private Attribute firstAttribute;
/*     */   
/*     */   RecordComponentWriter(SymbolTable symbolTable, String name, String descriptor, String signature) {
/*  97 */     super(589824);
/*  98 */     this.symbolTable = symbolTable;
/*  99 */     this.nameIndex = symbolTable.addConstantUtf8(name);
/* 100 */     this.descriptorIndex = symbolTable.addConstantUtf8(descriptor);
/* 101 */     if (signature != null) {
/* 102 */       this.signatureIndex = symbolTable.addConstantUtf8(signature);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
/* 112 */     if (visible) {
/* 113 */       return this
/* 114 */         .lastRuntimeVisibleAnnotation = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeVisibleAnnotation);
/*     */     }
/* 116 */     return this
/* 117 */       .lastRuntimeInvisibleAnnotation = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeInvisibleAnnotation);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
/* 124 */     if (visible) {
/* 125 */       return this
/* 126 */         .lastRuntimeVisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeVisibleTypeAnnotation);
/*     */     }
/*     */     
/* 129 */     return this
/* 130 */       .lastRuntimeInvisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeInvisibleTypeAnnotation);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitAttribute(Attribute attribute) {
/* 138 */     attribute.nextAttribute = this.firstAttribute;
/* 139 */     this.firstAttribute = attribute;
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
/*     */   public void visitEnd() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int computeRecordComponentInfoSize() {
/* 160 */     int size = 6;
/* 161 */     size += Attribute.computeAttributesSize(this.symbolTable, 0, this.signatureIndex);
/* 162 */     size += 
/* 163 */       AnnotationWriter.computeAnnotationsSize(this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 168 */     if (this.firstAttribute != null) {
/* 169 */       size += this.firstAttribute.computeAttributesSize(this.symbolTable);
/*     */     }
/* 171 */     return size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void putRecordComponentInfo(ByteVector output) {
/* 181 */     output.putShort(this.nameIndex).putShort(this.descriptorIndex);
/*     */ 
/*     */     
/* 184 */     int attributesCount = 0;
/* 185 */     if (this.signatureIndex != 0) {
/* 186 */       attributesCount++;
/*     */     }
/* 188 */     if (this.lastRuntimeVisibleAnnotation != null) {
/* 189 */       attributesCount++;
/*     */     }
/* 191 */     if (this.lastRuntimeInvisibleAnnotation != null) {
/* 192 */       attributesCount++;
/*     */     }
/* 194 */     if (this.lastRuntimeVisibleTypeAnnotation != null) {
/* 195 */       attributesCount++;
/*     */     }
/* 197 */     if (this.lastRuntimeInvisibleTypeAnnotation != null) {
/* 198 */       attributesCount++;
/*     */     }
/* 200 */     if (this.firstAttribute != null) {
/* 201 */       attributesCount += this.firstAttribute.getAttributeCount();
/*     */     }
/* 203 */     output.putShort(attributesCount);
/* 204 */     Attribute.putAttributes(this.symbolTable, 0, this.signatureIndex, output);
/* 205 */     AnnotationWriter.putAnnotations(this.symbolTable, this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation, output);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 212 */     if (this.firstAttribute != null) {
/* 213 */       this.firstAttribute.putAttributes(this.symbolTable, output);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void collectAttributePrototypes(Attribute.Set attributePrototypes) {
/* 223 */     attributePrototypes.addAttributes(this.firstAttribute);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\RecordComponentWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */