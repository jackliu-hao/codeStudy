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
/*     */ final class AnnotationWriter
/*     */   extends AnnotationVisitor
/*     */ {
/*     */   private final SymbolTable symbolTable;
/*     */   private final boolean useNamedValues;
/*     */   private final ByteVector annotation;
/*     */   private final int numElementValuePairsOffset;
/*     */   private int numElementValuePairs;
/*     */   private final AnnotationWriter previousAnnotation;
/*     */   private AnnotationWriter nextAnnotation;
/*     */   
/*     */   AnnotationWriter(SymbolTable symbolTable, boolean useNamedValues, ByteVector annotation, AnnotationWriter previousAnnotation) {
/* 115 */     super(589824);
/* 116 */     this.symbolTable = symbolTable;
/* 117 */     this.useNamedValues = useNamedValues;
/* 118 */     this.annotation = annotation;
/*     */     
/* 120 */     this.numElementValuePairsOffset = (annotation.length == 0) ? -1 : (annotation.length - 2);
/* 121 */     this.previousAnnotation = previousAnnotation;
/* 122 */     if (previousAnnotation != null) {
/* 123 */       previousAnnotation.nextAnnotation = this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static AnnotationWriter create(SymbolTable symbolTable, String descriptor, AnnotationWriter previousAnnotation) {
/* 143 */     ByteVector annotation = new ByteVector();
/*     */     
/* 145 */     annotation.putShort(symbolTable.addConstantUtf8(descriptor)).putShort(0);
/* 146 */     return new AnnotationWriter(symbolTable, true, annotation, previousAnnotation);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static AnnotationWriter create(SymbolTable symbolTable, int typeRef, TypePath typePath, String descriptor, AnnotationWriter previousAnnotation) {
/* 175 */     ByteVector typeAnnotation = new ByteVector();
/*     */     
/* 177 */     TypeReference.putTarget(typeRef, typeAnnotation);
/* 178 */     TypePath.put(typePath, typeAnnotation);
/*     */     
/* 180 */     typeAnnotation.putShort(symbolTable.addConstantUtf8(descriptor)).putShort(0);
/* 181 */     return new AnnotationWriter(symbolTable, true, typeAnnotation, previousAnnotation);
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
/*     */   public void visit(String name, Object value) {
/* 193 */     this.numElementValuePairs++;
/* 194 */     if (this.useNamedValues) {
/* 195 */       this.annotation.putShort(this.symbolTable.addConstantUtf8(name));
/*     */     }
/* 197 */     if (value instanceof String) {
/* 198 */       this.annotation.put12(115, this.symbolTable.addConstantUtf8((String)value));
/* 199 */     } else if (value instanceof Byte) {
/* 200 */       this.annotation.put12(66, (this.symbolTable.addConstantInteger(((Byte)value).byteValue())).index);
/* 201 */     } else if (value instanceof Boolean) {
/* 202 */       int booleanValue = ((Boolean)value).booleanValue() ? 1 : 0;
/* 203 */       this.annotation.put12(90, (this.symbolTable.addConstantInteger(booleanValue)).index);
/* 204 */     } else if (value instanceof Character) {
/* 205 */       this.annotation.put12(67, (this.symbolTable.addConstantInteger(((Character)value).charValue())).index);
/* 206 */     } else if (value instanceof Short) {
/* 207 */       this.annotation.put12(83, (this.symbolTable.addConstantInteger(((Short)value).shortValue())).index);
/* 208 */     } else if (value instanceof Type) {
/* 209 */       this.annotation.put12(99, this.symbolTable.addConstantUtf8(((Type)value).getDescriptor()));
/* 210 */     } else if (value instanceof byte[]) {
/* 211 */       byte[] byteArray = (byte[])value;
/* 212 */       this.annotation.put12(91, byteArray.length);
/* 213 */       for (byte byteValue : byteArray) {
/* 214 */         this.annotation.put12(66, (this.symbolTable.addConstantInteger(byteValue)).index);
/*     */       }
/* 216 */     } else if (value instanceof boolean[]) {
/* 217 */       boolean[] booleanArray = (boolean[])value;
/* 218 */       this.annotation.put12(91, booleanArray.length);
/* 219 */       for (boolean booleanValue : booleanArray) {
/* 220 */         this.annotation.put12(90, (this.symbolTable.addConstantInteger(booleanValue ? 1 : 0)).index);
/*     */       }
/* 222 */     } else if (value instanceof short[]) {
/* 223 */       short[] shortArray = (short[])value;
/* 224 */       this.annotation.put12(91, shortArray.length);
/* 225 */       for (short shortValue : shortArray) {
/* 226 */         this.annotation.put12(83, (this.symbolTable.addConstantInteger(shortValue)).index);
/*     */       }
/* 228 */     } else if (value instanceof char[]) {
/* 229 */       char[] charArray = (char[])value;
/* 230 */       this.annotation.put12(91, charArray.length);
/* 231 */       for (char charValue : charArray) {
/* 232 */         this.annotation.put12(67, (this.symbolTable.addConstantInteger(charValue)).index);
/*     */       }
/* 234 */     } else if (value instanceof int[]) {
/* 235 */       int[] intArray = (int[])value;
/* 236 */       this.annotation.put12(91, intArray.length);
/* 237 */       for (int intValue : intArray) {
/* 238 */         this.annotation.put12(73, (this.symbolTable.addConstantInteger(intValue)).index);
/*     */       }
/* 240 */     } else if (value instanceof long[]) {
/* 241 */       long[] longArray = (long[])value;
/* 242 */       this.annotation.put12(91, longArray.length);
/* 243 */       for (long longValue : longArray) {
/* 244 */         this.annotation.put12(74, (this.symbolTable.addConstantLong(longValue)).index);
/*     */       }
/* 246 */     } else if (value instanceof float[]) {
/* 247 */       float[] floatArray = (float[])value;
/* 248 */       this.annotation.put12(91, floatArray.length);
/* 249 */       for (float floatValue : floatArray) {
/* 250 */         this.annotation.put12(70, (this.symbolTable.addConstantFloat(floatValue)).index);
/*     */       }
/* 252 */     } else if (value instanceof double[]) {
/* 253 */       double[] doubleArray = (double[])value;
/* 254 */       this.annotation.put12(91, doubleArray.length);
/* 255 */       for (double doubleValue : doubleArray) {
/* 256 */         this.annotation.put12(68, (this.symbolTable.addConstantDouble(doubleValue)).index);
/*     */       }
/*     */     } else {
/* 259 */       Symbol symbol = this.symbolTable.addConstant(value);
/* 260 */       this.annotation.put12(".s.IFJDCS".charAt(symbol.tag), symbol.index);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitEnum(String name, String descriptor, String value) {
/* 268 */     this.numElementValuePairs++;
/* 269 */     if (this.useNamedValues) {
/* 270 */       this.annotation.putShort(this.symbolTable.addConstantUtf8(name));
/*     */     }
/* 272 */     this.annotation
/* 273 */       .put12(101, this.symbolTable.addConstantUtf8(descriptor))
/* 274 */       .putShort(this.symbolTable.addConstantUtf8(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotationVisitor visitAnnotation(String name, String descriptor) {
/* 281 */     this.numElementValuePairs++;
/* 282 */     if (this.useNamedValues) {
/* 283 */       this.annotation.putShort(this.symbolTable.addConstantUtf8(name));
/*     */     }
/*     */     
/* 286 */     this.annotation.put12(64, this.symbolTable.addConstantUtf8(descriptor)).putShort(0);
/* 287 */     return new AnnotationWriter(this.symbolTable, true, this.annotation, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotationVisitor visitArray(String name) {
/* 294 */     this.numElementValuePairs++;
/* 295 */     if (this.useNamedValues) {
/* 296 */       this.annotation.putShort(this.symbolTable.addConstantUtf8(name));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 305 */     this.annotation.put12(91, 0);
/* 306 */     return new AnnotationWriter(this.symbolTable, false, this.annotation, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitEnd() {
/* 311 */     if (this.numElementValuePairsOffset != -1) {
/* 312 */       byte[] data = this.annotation.data;
/* 313 */       data[this.numElementValuePairsOffset] = (byte)(this.numElementValuePairs >>> 8);
/* 314 */       data[this.numElementValuePairsOffset + 1] = (byte)this.numElementValuePairs;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int computeAnnotationsSize(String attributeName) {
/* 333 */     if (attributeName != null) {
/* 334 */       this.symbolTable.addConstantUtf8(attributeName);
/*     */     }
/*     */     
/* 337 */     int attributeSize = 8;
/* 338 */     AnnotationWriter annotationWriter = this;
/* 339 */     while (annotationWriter != null) {
/* 340 */       attributeSize += annotationWriter.annotation.length;
/* 341 */       annotationWriter = annotationWriter.previousAnnotation;
/*     */     } 
/* 343 */     return attributeSize;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int computeAnnotationsSize(AnnotationWriter lastRuntimeVisibleAnnotation, AnnotationWriter lastRuntimeInvisibleAnnotation, AnnotationWriter lastRuntimeVisibleTypeAnnotation, AnnotationWriter lastRuntimeInvisibleTypeAnnotation) {
/* 372 */     int size = 0;
/* 373 */     if (lastRuntimeVisibleAnnotation != null) {
/* 374 */       size += lastRuntimeVisibleAnnotation
/* 375 */         .computeAnnotationsSize("RuntimeVisibleAnnotations");
/*     */     }
/*     */     
/* 378 */     if (lastRuntimeInvisibleAnnotation != null) {
/* 379 */       size += lastRuntimeInvisibleAnnotation
/* 380 */         .computeAnnotationsSize("RuntimeInvisibleAnnotations");
/*     */     }
/*     */     
/* 383 */     if (lastRuntimeVisibleTypeAnnotation != null) {
/* 384 */       size += lastRuntimeVisibleTypeAnnotation
/* 385 */         .computeAnnotationsSize("RuntimeVisibleTypeAnnotations");
/*     */     }
/*     */     
/* 388 */     if (lastRuntimeInvisibleTypeAnnotation != null) {
/* 389 */       size += lastRuntimeInvisibleTypeAnnotation
/* 390 */         .computeAnnotationsSize("RuntimeInvisibleTypeAnnotations");
/*     */     }
/*     */     
/* 393 */     return size;
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
/*     */   void putAnnotations(int attributeNameIndex, ByteVector output) {
/* 406 */     int attributeLength = 2;
/* 407 */     int numAnnotations = 0;
/* 408 */     AnnotationWriter annotationWriter = this;
/* 409 */     AnnotationWriter firstAnnotation = null;
/* 410 */     while (annotationWriter != null) {
/*     */       
/* 412 */       annotationWriter.visitEnd();
/* 413 */       attributeLength += annotationWriter.annotation.length;
/* 414 */       numAnnotations++;
/* 415 */       firstAnnotation = annotationWriter;
/* 416 */       annotationWriter = annotationWriter.previousAnnotation;
/*     */     } 
/* 418 */     output.putShort(attributeNameIndex);
/* 419 */     output.putInt(attributeLength);
/* 420 */     output.putShort(numAnnotations);
/* 421 */     annotationWriter = firstAnnotation;
/* 422 */     while (annotationWriter != null) {
/* 423 */       output.putByteArray(annotationWriter.annotation.data, 0, annotationWriter.annotation.length);
/* 424 */       annotationWriter = annotationWriter.nextAnnotation;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void putAnnotations(SymbolTable symbolTable, AnnotationWriter lastRuntimeVisibleAnnotation, AnnotationWriter lastRuntimeInvisibleAnnotation, AnnotationWriter lastRuntimeVisibleTypeAnnotation, AnnotationWriter lastRuntimeInvisibleTypeAnnotation, ByteVector output) {
/* 455 */     if (lastRuntimeVisibleAnnotation != null) {
/* 456 */       lastRuntimeVisibleAnnotation.putAnnotations(symbolTable
/* 457 */           .addConstantUtf8("RuntimeVisibleAnnotations"), output);
/*     */     }
/* 459 */     if (lastRuntimeInvisibleAnnotation != null) {
/* 460 */       lastRuntimeInvisibleAnnotation.putAnnotations(symbolTable
/* 461 */           .addConstantUtf8("RuntimeInvisibleAnnotations"), output);
/*     */     }
/* 463 */     if (lastRuntimeVisibleTypeAnnotation != null) {
/* 464 */       lastRuntimeVisibleTypeAnnotation.putAnnotations(symbolTable
/* 465 */           .addConstantUtf8("RuntimeVisibleTypeAnnotations"), output);
/*     */     }
/* 467 */     if (lastRuntimeInvisibleTypeAnnotation != null) {
/* 468 */       lastRuntimeInvisibleTypeAnnotation.putAnnotations(symbolTable
/* 469 */           .addConstantUtf8("RuntimeInvisibleTypeAnnotations"), output);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int computeParameterAnnotationsSize(String attributeName, AnnotationWriter[] annotationWriters, int annotableParameterCount) {
/* 496 */     int attributeSize = 7 + 2 * annotableParameterCount;
/* 497 */     for (int i = 0; i < annotableParameterCount; i++) {
/* 498 */       AnnotationWriter annotationWriter = annotationWriters[i];
/* 499 */       attributeSize += 
/* 500 */         (annotationWriter == null) ? 0 : (annotationWriter.computeAnnotationsSize(attributeName) - 8);
/*     */     } 
/* 502 */     return attributeSize;
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
/*     */ 
/*     */   
/*     */   static void putParameterAnnotations(int attributeNameIndex, AnnotationWriter[] annotationWriters, int annotableParameterCount, ByteVector output) {
/* 524 */     int attributeLength = 1 + 2 * annotableParameterCount; int i;
/* 525 */     for (i = 0; i < annotableParameterCount; i++) {
/* 526 */       AnnotationWriter annotationWriter = annotationWriters[i];
/* 527 */       attributeLength += 
/* 528 */         (annotationWriter == null) ? 0 : (annotationWriter.computeAnnotationsSize(null) - 8);
/*     */     } 
/* 530 */     output.putShort(attributeNameIndex);
/* 531 */     output.putInt(attributeLength);
/* 532 */     output.putByte(annotableParameterCount);
/* 533 */     for (i = 0; i < annotableParameterCount; i++) {
/* 534 */       AnnotationWriter annotationWriter = annotationWriters[i];
/* 535 */       AnnotationWriter firstAnnotation = null;
/* 536 */       int numAnnotations = 0;
/* 537 */       while (annotationWriter != null) {
/*     */         
/* 539 */         annotationWriter.visitEnd();
/* 540 */         numAnnotations++;
/* 541 */         firstAnnotation = annotationWriter;
/* 542 */         annotationWriter = annotationWriter.previousAnnotation;
/*     */       } 
/* 544 */       output.putShort(numAnnotations);
/* 545 */       annotationWriter = firstAnnotation;
/* 546 */       while (annotationWriter != null) {
/* 547 */         output.putByteArray(annotationWriter.annotation.data, 0, annotationWriter.annotation.length);
/*     */         
/* 549 */         annotationWriter = annotationWriter.nextAnnotation;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\AnnotationWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */