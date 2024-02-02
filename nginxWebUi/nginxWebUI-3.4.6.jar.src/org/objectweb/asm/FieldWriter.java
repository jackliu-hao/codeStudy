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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class FieldWriter
/*     */   extends FieldVisitor
/*     */ {
/*     */   private final SymbolTable symbolTable;
/*     */   private final int accessFlags;
/*     */   private final int nameIndex;
/*     */   private final int descriptorIndex;
/*     */   private int signatureIndex;
/*     */   private int constantValueIndex;
/*     */   private AnnotationWriter lastRuntimeVisibleAnnotation;
/*     */   private AnnotationWriter lastRuntimeInvisibleAnnotation;
/*     */   private AnnotationWriter lastRuntimeVisibleTypeAnnotation;
/*     */   private AnnotationWriter lastRuntimeInvisibleTypeAnnotation;
/*     */   private Attribute firstAttribute;
/*     */   
/*     */   FieldWriter(SymbolTable symbolTable, int access, String name, String descriptor, String signature, Object constantValue) {
/* 127 */     super(589824);
/* 128 */     this.symbolTable = symbolTable;
/* 129 */     this.accessFlags = access;
/* 130 */     this.nameIndex = symbolTable.addConstantUtf8(name);
/* 131 */     this.descriptorIndex = symbolTable.addConstantUtf8(descriptor);
/* 132 */     if (signature != null) {
/* 133 */       this.signatureIndex = symbolTable.addConstantUtf8(signature);
/*     */     }
/* 135 */     if (constantValue != null) {
/* 136 */       this.constantValueIndex = (symbolTable.addConstant(constantValue)).index;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
/* 146 */     if (visible) {
/* 147 */       return this
/* 148 */         .lastRuntimeVisibleAnnotation = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeVisibleAnnotation);
/*     */     }
/* 150 */     return this
/* 151 */       .lastRuntimeInvisibleAnnotation = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeInvisibleAnnotation);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
/* 158 */     if (visible) {
/* 159 */       return this
/* 160 */         .lastRuntimeVisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeVisibleTypeAnnotation);
/*     */     }
/*     */     
/* 163 */     return this
/* 164 */       .lastRuntimeInvisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeInvisibleTypeAnnotation);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitAttribute(Attribute attribute) {
/* 172 */     attribute.nextAttribute = this.firstAttribute;
/* 173 */     this.firstAttribute = attribute;
/*     */   }
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
/*     */   int computeFieldInfoSize() {
/* 193 */     int size = 8;
/*     */     
/* 195 */     if (this.constantValueIndex != 0) {
/*     */       
/* 197 */       this.symbolTable.addConstantUtf8("ConstantValue");
/* 198 */       size += 8;
/*     */     } 
/* 200 */     size += Attribute.computeAttributesSize(this.symbolTable, this.accessFlags, this.signatureIndex);
/* 201 */     size += 
/* 202 */       AnnotationWriter.computeAnnotationsSize(this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 207 */     if (this.firstAttribute != null) {
/* 208 */       size += this.firstAttribute.computeAttributesSize(this.symbolTable);
/*     */     }
/* 210 */     return size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void putFieldInfo(ByteVector output) {
/* 220 */     boolean useSyntheticAttribute = (this.symbolTable.getMajorVersion() < 49);
/*     */     
/* 222 */     int mask = useSyntheticAttribute ? 4096 : 0;
/* 223 */     output.putShort(this.accessFlags & (mask ^ 0xFFFFFFFF)).putShort(this.nameIndex).putShort(this.descriptorIndex);
/*     */ 
/*     */     
/* 226 */     int attributesCount = 0;
/* 227 */     if (this.constantValueIndex != 0) {
/* 228 */       attributesCount++;
/*     */     }
/* 230 */     if ((this.accessFlags & 0x1000) != 0 && useSyntheticAttribute) {
/* 231 */       attributesCount++;
/*     */     }
/* 233 */     if (this.signatureIndex != 0) {
/* 234 */       attributesCount++;
/*     */     }
/* 236 */     if ((this.accessFlags & 0x20000) != 0) {
/* 237 */       attributesCount++;
/*     */     }
/* 239 */     if (this.lastRuntimeVisibleAnnotation != null) {
/* 240 */       attributesCount++;
/*     */     }
/* 242 */     if (this.lastRuntimeInvisibleAnnotation != null) {
/* 243 */       attributesCount++;
/*     */     }
/* 245 */     if (this.lastRuntimeVisibleTypeAnnotation != null) {
/* 246 */       attributesCount++;
/*     */     }
/* 248 */     if (this.lastRuntimeInvisibleTypeAnnotation != null) {
/* 249 */       attributesCount++;
/*     */     }
/* 251 */     if (this.firstAttribute != null) {
/* 252 */       attributesCount += this.firstAttribute.getAttributeCount();
/*     */     }
/* 254 */     output.putShort(attributesCount);
/*     */ 
/*     */     
/* 257 */     if (this.constantValueIndex != 0) {
/* 258 */       output
/* 259 */         .putShort(this.symbolTable.addConstantUtf8("ConstantValue"))
/* 260 */         .putInt(2)
/* 261 */         .putShort(this.constantValueIndex);
/*     */     }
/* 263 */     Attribute.putAttributes(this.symbolTable, this.accessFlags, this.signatureIndex, output);
/* 264 */     AnnotationWriter.putAnnotations(this.symbolTable, this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation, output);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 271 */     if (this.firstAttribute != null) {
/* 272 */       this.firstAttribute.putAttributes(this.symbolTable, output);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void collectAttributePrototypes(Attribute.Set attributePrototypes) {
/* 282 */     attributePrototypes.addAttributes(this.firstAttribute);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\FieldWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */