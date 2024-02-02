/*      */ package org.objectweb.asm;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ClassWriter
/*      */   extends ClassVisitor
/*      */ {
/*      */   public static final int COMPUTE_MAXS = 1;
/*      */   public static final int COMPUTE_FRAMES = 2;
/*      */   private int version;
/*      */   private final SymbolTable symbolTable;
/*      */   private int accessFlags;
/*      */   private int thisClass;
/*      */   private int superClass;
/*      */   private int interfaceCount;
/*      */   private int[] interfaces;
/*      */   private FieldWriter firstField;
/*      */   private FieldWriter lastField;
/*      */   private MethodWriter firstMethod;
/*      */   private MethodWriter lastMethod;
/*      */   private int numberOfInnerClasses;
/*      */   private ByteVector innerClasses;
/*      */   private int enclosingClassIndex;
/*      */   private int enclosingMethodIndex;
/*      */   private int signatureIndex;
/*      */   private int sourceFileIndex;
/*      */   private ByteVector debugExtension;
/*      */   private AnnotationWriter lastRuntimeVisibleAnnotation;
/*      */   private AnnotationWriter lastRuntimeInvisibleAnnotation;
/*      */   private AnnotationWriter lastRuntimeVisibleTypeAnnotation;
/*      */   private AnnotationWriter lastRuntimeInvisibleTypeAnnotation;
/*      */   private ModuleWriter moduleWriter;
/*      */   private int nestHostClassIndex;
/*      */   private int numberOfNestMemberClasses;
/*      */   private ByteVector nestMemberClasses;
/*      */   private int numberOfPermittedSubclasses;
/*      */   private ByteVector permittedSubclasses;
/*      */   private RecordComponentWriter firstRecordComponent;
/*      */   private RecordComponentWriter lastRecordComponent;
/*      */   private Attribute firstAttribute;
/*      */   private int compute;
/*      */   
/*      */   public ClassWriter(int flags) {
/*  229 */     this(null, flags);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClassWriter(ClassReader classReader, int flags) {
/*  257 */     super(589824);
/*  258 */     this.symbolTable = (classReader == null) ? new SymbolTable(this) : new SymbolTable(this, classReader);
/*  259 */     if ((flags & 0x2) != 0) {
/*  260 */       this.compute = 4;
/*  261 */     } else if ((flags & 0x1) != 0) {
/*  262 */       this.compute = 1;
/*      */     } else {
/*  264 */       this.compute = 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
/*  280 */     this.version = version;
/*  281 */     this.accessFlags = access;
/*  282 */     this.thisClass = this.symbolTable.setMajorVersionAndClassName(version & 0xFFFF, name);
/*  283 */     if (signature != null) {
/*  284 */       this.signatureIndex = this.symbolTable.addConstantUtf8(signature);
/*      */     }
/*  286 */     this.superClass = (superName == null) ? 0 : (this.symbolTable.addConstantClass(superName)).index;
/*  287 */     if (interfaces != null && interfaces.length > 0) {
/*  288 */       this.interfaceCount = interfaces.length;
/*  289 */       this.interfaces = new int[this.interfaceCount];
/*  290 */       for (int i = 0; i < this.interfaceCount; i++) {
/*  291 */         this.interfaces[i] = (this.symbolTable.addConstantClass(interfaces[i])).index;
/*      */       }
/*      */     } 
/*  294 */     if (this.compute == 1 && (version & 0xFFFF) >= 51) {
/*  295 */       this.compute = 2;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public final void visitSource(String file, String debug) {
/*  301 */     if (file != null) {
/*  302 */       this.sourceFileIndex = this.symbolTable.addConstantUtf8(file);
/*      */     }
/*  304 */     if (debug != null) {
/*  305 */       this.debugExtension = (new ByteVector()).encodeUtf8(debug, 0, 2147483647);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final ModuleVisitor visitModule(String name, int access, String version) {
/*  312 */     return this
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  317 */       .moduleWriter = new ModuleWriter(this.symbolTable, (this.symbolTable.addConstantModule(name)).index, access, (version == null) ? 0 : this.symbolTable.addConstantUtf8(version));
/*      */   }
/*      */ 
/*      */   
/*      */   public final void visitNestHost(String nestHost) {
/*  322 */     this.nestHostClassIndex = (this.symbolTable.addConstantClass(nestHost)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void visitOuterClass(String owner, String name, String descriptor) {
/*  328 */     this.enclosingClassIndex = (this.symbolTable.addConstantClass(owner)).index;
/*  329 */     if (name != null && descriptor != null) {
/*  330 */       this.enclosingMethodIndex = this.symbolTable.addConstantNameAndType(name, descriptor);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public final AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
/*  336 */     if (visible) {
/*  337 */       return this
/*  338 */         .lastRuntimeVisibleAnnotation = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeVisibleAnnotation);
/*      */     }
/*  340 */     return this
/*  341 */       .lastRuntimeInvisibleAnnotation = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeInvisibleAnnotation);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
/*  348 */     if (visible) {
/*  349 */       return this
/*  350 */         .lastRuntimeVisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeVisibleTypeAnnotation);
/*      */     }
/*      */     
/*  353 */     return this
/*  354 */       .lastRuntimeInvisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeInvisibleTypeAnnotation);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void visitAttribute(Attribute attribute) {
/*  362 */     attribute.nextAttribute = this.firstAttribute;
/*  363 */     this.firstAttribute = attribute;
/*      */   }
/*      */ 
/*      */   
/*      */   public final void visitNestMember(String nestMember) {
/*  368 */     if (this.nestMemberClasses == null) {
/*  369 */       this.nestMemberClasses = new ByteVector();
/*      */     }
/*  371 */     this.numberOfNestMemberClasses++;
/*  372 */     this.nestMemberClasses.putShort((this.symbolTable.addConstantClass(nestMember)).index);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void visitPermittedSubclass(String permittedSubclass) {
/*  377 */     if (this.permittedSubclasses == null) {
/*  378 */       this.permittedSubclasses = new ByteVector();
/*      */     }
/*  380 */     this.numberOfPermittedSubclasses++;
/*  381 */     this.permittedSubclasses.putShort((this.symbolTable.addConstantClass(permittedSubclass)).index);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void visitInnerClass(String name, String outerName, String innerName, int access) {
/*  387 */     if (this.innerClasses == null) {
/*  388 */       this.innerClasses = new ByteVector();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  396 */     Symbol nameSymbol = this.symbolTable.addConstantClass(name);
/*  397 */     if (nameSymbol.info == 0) {
/*  398 */       this.numberOfInnerClasses++;
/*  399 */       this.innerClasses.putShort(nameSymbol.index);
/*  400 */       this.innerClasses.putShort((outerName == null) ? 0 : (this.symbolTable.addConstantClass(outerName)).index);
/*  401 */       this.innerClasses.putShort((innerName == null) ? 0 : this.symbolTable.addConstantUtf8(innerName));
/*  402 */       this.innerClasses.putShort(access);
/*  403 */       nameSymbol.info = this.numberOfInnerClasses;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
/*  412 */     RecordComponentWriter recordComponentWriter = new RecordComponentWriter(this.symbolTable, name, descriptor, signature);
/*      */     
/*  414 */     if (this.firstRecordComponent == null) {
/*  415 */       this.firstRecordComponent = recordComponentWriter;
/*      */     } else {
/*  417 */       this.lastRecordComponent.delegate = recordComponentWriter;
/*      */     } 
/*  419 */     return this.lastRecordComponent = recordComponentWriter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
/*  429 */     FieldWriter fieldWriter = new FieldWriter(this.symbolTable, access, name, descriptor, signature, value);
/*      */     
/*  431 */     if (this.firstField == null) {
/*  432 */       this.firstField = fieldWriter;
/*      */     } else {
/*  434 */       this.lastField.fv = fieldWriter;
/*      */     } 
/*  436 */     return this.lastField = fieldWriter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
/*  446 */     MethodWriter methodWriter = new MethodWriter(this.symbolTable, access, name, descriptor, signature, exceptions, this.compute);
/*      */     
/*  448 */     if (this.firstMethod == null) {
/*  449 */       this.firstMethod = methodWriter;
/*      */     } else {
/*  451 */       this.lastMethod.mv = methodWriter;
/*      */     } 
/*  453 */     return this.lastMethod = methodWriter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void visitEnd() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] toByteArray() {
/*  477 */     int size = 24 + 2 * this.interfaceCount;
/*  478 */     int fieldsCount = 0;
/*  479 */     FieldWriter fieldWriter = this.firstField;
/*  480 */     while (fieldWriter != null) {
/*  481 */       fieldsCount++;
/*  482 */       size += fieldWriter.computeFieldInfoSize();
/*  483 */       fieldWriter = (FieldWriter)fieldWriter.fv;
/*      */     } 
/*  485 */     int methodsCount = 0;
/*  486 */     MethodWriter methodWriter = this.firstMethod;
/*  487 */     while (methodWriter != null) {
/*  488 */       methodsCount++;
/*  489 */       size += methodWriter.computeMethodInfoSize();
/*  490 */       methodWriter = (MethodWriter)methodWriter.mv;
/*      */     } 
/*      */ 
/*      */     
/*  494 */     int attributesCount = 0;
/*  495 */     if (this.innerClasses != null) {
/*  496 */       attributesCount++;
/*  497 */       size += 8 + this.innerClasses.length;
/*  498 */       this.symbolTable.addConstantUtf8("InnerClasses");
/*      */     } 
/*  500 */     if (this.enclosingClassIndex != 0) {
/*  501 */       attributesCount++;
/*  502 */       size += 10;
/*  503 */       this.symbolTable.addConstantUtf8("EnclosingMethod");
/*      */     } 
/*  505 */     if ((this.accessFlags & 0x1000) != 0 && (this.version & 0xFFFF) < 49) {
/*  506 */       attributesCount++;
/*  507 */       size += 6;
/*  508 */       this.symbolTable.addConstantUtf8("Synthetic");
/*      */     } 
/*  510 */     if (this.signatureIndex != 0) {
/*  511 */       attributesCount++;
/*  512 */       size += 8;
/*  513 */       this.symbolTable.addConstantUtf8("Signature");
/*      */     } 
/*  515 */     if (this.sourceFileIndex != 0) {
/*  516 */       attributesCount++;
/*  517 */       size += 8;
/*  518 */       this.symbolTable.addConstantUtf8("SourceFile");
/*      */     } 
/*  520 */     if (this.debugExtension != null) {
/*  521 */       attributesCount++;
/*  522 */       size += 6 + this.debugExtension.length;
/*  523 */       this.symbolTable.addConstantUtf8("SourceDebugExtension");
/*      */     } 
/*  525 */     if ((this.accessFlags & 0x20000) != 0) {
/*  526 */       attributesCount++;
/*  527 */       size += 6;
/*  528 */       this.symbolTable.addConstantUtf8("Deprecated");
/*      */     } 
/*  530 */     if (this.lastRuntimeVisibleAnnotation != null) {
/*  531 */       attributesCount++;
/*  532 */       size += this.lastRuntimeVisibleAnnotation
/*  533 */         .computeAnnotationsSize("RuntimeVisibleAnnotations");
/*      */     } 
/*      */     
/*  536 */     if (this.lastRuntimeInvisibleAnnotation != null) {
/*  537 */       attributesCount++;
/*  538 */       size += this.lastRuntimeInvisibleAnnotation
/*  539 */         .computeAnnotationsSize("RuntimeInvisibleAnnotations");
/*      */     } 
/*      */     
/*  542 */     if (this.lastRuntimeVisibleTypeAnnotation != null) {
/*  543 */       attributesCount++;
/*  544 */       size += this.lastRuntimeVisibleTypeAnnotation
/*  545 */         .computeAnnotationsSize("RuntimeVisibleTypeAnnotations");
/*      */     } 
/*      */     
/*  548 */     if (this.lastRuntimeInvisibleTypeAnnotation != null) {
/*  549 */       attributesCount++;
/*  550 */       size += this.lastRuntimeInvisibleTypeAnnotation
/*  551 */         .computeAnnotationsSize("RuntimeInvisibleTypeAnnotations");
/*      */     } 
/*      */     
/*  554 */     if (this.symbolTable.computeBootstrapMethodsSize() > 0) {
/*  555 */       attributesCount++;
/*  556 */       size += this.symbolTable.computeBootstrapMethodsSize();
/*      */     } 
/*  558 */     if (this.moduleWriter != null) {
/*  559 */       attributesCount += this.moduleWriter.getAttributeCount();
/*  560 */       size += this.moduleWriter.computeAttributesSize();
/*      */     } 
/*  562 */     if (this.nestHostClassIndex != 0) {
/*  563 */       attributesCount++;
/*  564 */       size += 8;
/*  565 */       this.symbolTable.addConstantUtf8("NestHost");
/*      */     } 
/*  567 */     if (this.nestMemberClasses != null) {
/*  568 */       attributesCount++;
/*  569 */       size += 8 + this.nestMemberClasses.length;
/*  570 */       this.symbolTable.addConstantUtf8("NestMembers");
/*      */     } 
/*  572 */     if (this.permittedSubclasses != null) {
/*  573 */       attributesCount++;
/*  574 */       size += 8 + this.permittedSubclasses.length;
/*  575 */       this.symbolTable.addConstantUtf8("PermittedSubclasses");
/*      */     } 
/*  577 */     int recordComponentCount = 0;
/*  578 */     int recordSize = 0;
/*  579 */     if ((this.accessFlags & 0x10000) != 0 || this.firstRecordComponent != null) {
/*  580 */       RecordComponentWriter recordComponentWriter = this.firstRecordComponent;
/*  581 */       while (recordComponentWriter != null) {
/*  582 */         recordComponentCount++;
/*  583 */         recordSize += recordComponentWriter.computeRecordComponentInfoSize();
/*  584 */         recordComponentWriter = (RecordComponentWriter)recordComponentWriter.delegate;
/*      */       } 
/*  586 */       attributesCount++;
/*  587 */       size += 8 + recordSize;
/*  588 */       this.symbolTable.addConstantUtf8("Record");
/*      */     } 
/*  590 */     if (this.firstAttribute != null) {
/*  591 */       attributesCount += this.firstAttribute.getAttributeCount();
/*  592 */       size += this.firstAttribute.computeAttributesSize(this.symbolTable);
/*      */     } 
/*      */ 
/*      */     
/*  596 */     size += this.symbolTable.getConstantPoolLength();
/*  597 */     int constantPoolCount = this.symbolTable.getConstantPoolCount();
/*  598 */     if (constantPoolCount > 65535) {
/*  599 */       throw new ClassTooLargeException(this.symbolTable.getClassName(), constantPoolCount);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  604 */     ByteVector result = new ByteVector(size);
/*  605 */     result.putInt(-889275714).putInt(this.version);
/*  606 */     this.symbolTable.putConstantPool(result);
/*  607 */     int mask = ((this.version & 0xFFFF) < 49) ? 4096 : 0;
/*  608 */     result.putShort(this.accessFlags & (mask ^ 0xFFFFFFFF)).putShort(this.thisClass).putShort(this.superClass);
/*  609 */     result.putShort(this.interfaceCount);
/*  610 */     for (int i = 0; i < this.interfaceCount; i++) {
/*  611 */       result.putShort(this.interfaces[i]);
/*      */     }
/*  613 */     result.putShort(fieldsCount);
/*  614 */     fieldWriter = this.firstField;
/*  615 */     while (fieldWriter != null) {
/*  616 */       fieldWriter.putFieldInfo(result);
/*  617 */       fieldWriter = (FieldWriter)fieldWriter.fv;
/*      */     } 
/*  619 */     result.putShort(methodsCount);
/*  620 */     boolean hasFrames = false;
/*  621 */     boolean hasAsmInstructions = false;
/*  622 */     methodWriter = this.firstMethod;
/*  623 */     while (methodWriter != null) {
/*  624 */       hasFrames |= methodWriter.hasFrames();
/*  625 */       hasAsmInstructions |= methodWriter.hasAsmInstructions();
/*  626 */       methodWriter.putMethodInfo(result);
/*  627 */       methodWriter = (MethodWriter)methodWriter.mv;
/*      */     } 
/*      */     
/*  630 */     result.putShort(attributesCount);
/*  631 */     if (this.innerClasses != null) {
/*  632 */       result
/*  633 */         .putShort(this.symbolTable.addConstantUtf8("InnerClasses"))
/*  634 */         .putInt(this.innerClasses.length + 2)
/*  635 */         .putShort(this.numberOfInnerClasses)
/*  636 */         .putByteArray(this.innerClasses.data, 0, this.innerClasses.length);
/*      */     }
/*  638 */     if (this.enclosingClassIndex != 0) {
/*  639 */       result
/*  640 */         .putShort(this.symbolTable.addConstantUtf8("EnclosingMethod"))
/*  641 */         .putInt(4)
/*  642 */         .putShort(this.enclosingClassIndex)
/*  643 */         .putShort(this.enclosingMethodIndex);
/*      */     }
/*  645 */     if ((this.accessFlags & 0x1000) != 0 && (this.version & 0xFFFF) < 49) {
/*  646 */       result.putShort(this.symbolTable.addConstantUtf8("Synthetic")).putInt(0);
/*      */     }
/*  648 */     if (this.signatureIndex != 0) {
/*  649 */       result
/*  650 */         .putShort(this.symbolTable.addConstantUtf8("Signature"))
/*  651 */         .putInt(2)
/*  652 */         .putShort(this.signatureIndex);
/*      */     }
/*  654 */     if (this.sourceFileIndex != 0) {
/*  655 */       result
/*  656 */         .putShort(this.symbolTable.addConstantUtf8("SourceFile"))
/*  657 */         .putInt(2)
/*  658 */         .putShort(this.sourceFileIndex);
/*      */     }
/*  660 */     if (this.debugExtension != null) {
/*  661 */       int length = this.debugExtension.length;
/*  662 */       result
/*  663 */         .putShort(this.symbolTable.addConstantUtf8("SourceDebugExtension"))
/*  664 */         .putInt(length)
/*  665 */         .putByteArray(this.debugExtension.data, 0, length);
/*      */     } 
/*  667 */     if ((this.accessFlags & 0x20000) != 0) {
/*  668 */       result.putShort(this.symbolTable.addConstantUtf8("Deprecated")).putInt(0);
/*      */     }
/*  670 */     AnnotationWriter.putAnnotations(this.symbolTable, this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation, result);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  677 */     this.symbolTable.putBootstrapMethods(result);
/*  678 */     if (this.moduleWriter != null) {
/*  679 */       this.moduleWriter.putAttributes(result);
/*      */     }
/*  681 */     if (this.nestHostClassIndex != 0) {
/*  682 */       result
/*  683 */         .putShort(this.symbolTable.addConstantUtf8("NestHost"))
/*  684 */         .putInt(2)
/*  685 */         .putShort(this.nestHostClassIndex);
/*      */     }
/*  687 */     if (this.nestMemberClasses != null) {
/*  688 */       result
/*  689 */         .putShort(this.symbolTable.addConstantUtf8("NestMembers"))
/*  690 */         .putInt(this.nestMemberClasses.length + 2)
/*  691 */         .putShort(this.numberOfNestMemberClasses)
/*  692 */         .putByteArray(this.nestMemberClasses.data, 0, this.nestMemberClasses.length);
/*      */     }
/*  694 */     if (this.permittedSubclasses != null) {
/*  695 */       result
/*  696 */         .putShort(this.symbolTable.addConstantUtf8("PermittedSubclasses"))
/*  697 */         .putInt(this.permittedSubclasses.length + 2)
/*  698 */         .putShort(this.numberOfPermittedSubclasses)
/*  699 */         .putByteArray(this.permittedSubclasses.data, 0, this.permittedSubclasses.length);
/*      */     }
/*  701 */     if ((this.accessFlags & 0x10000) != 0 || this.firstRecordComponent != null) {
/*  702 */       result
/*  703 */         .putShort(this.symbolTable.addConstantUtf8("Record"))
/*  704 */         .putInt(recordSize + 2)
/*  705 */         .putShort(recordComponentCount);
/*  706 */       RecordComponentWriter recordComponentWriter = this.firstRecordComponent;
/*  707 */       while (recordComponentWriter != null) {
/*  708 */         recordComponentWriter.putRecordComponentInfo(result);
/*  709 */         recordComponentWriter = (RecordComponentWriter)recordComponentWriter.delegate;
/*      */       } 
/*      */     } 
/*  712 */     if (this.firstAttribute != null) {
/*  713 */       this.firstAttribute.putAttributes(this.symbolTable, result);
/*      */     }
/*      */ 
/*      */     
/*  717 */     if (hasAsmInstructions) {
/*  718 */       return replaceAsmInstructions(result.data, hasFrames);
/*      */     }
/*  720 */     return result.data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private byte[] replaceAsmInstructions(byte[] classFile, boolean hasFrames) {
/*  735 */     Attribute[] attributes = getAttributePrototypes();
/*  736 */     this.firstField = null;
/*  737 */     this.lastField = null;
/*  738 */     this.firstMethod = null;
/*  739 */     this.lastMethod = null;
/*  740 */     this.lastRuntimeVisibleAnnotation = null;
/*  741 */     this.lastRuntimeInvisibleAnnotation = null;
/*  742 */     this.lastRuntimeVisibleTypeAnnotation = null;
/*  743 */     this.lastRuntimeInvisibleTypeAnnotation = null;
/*  744 */     this.moduleWriter = null;
/*  745 */     this.nestHostClassIndex = 0;
/*  746 */     this.numberOfNestMemberClasses = 0;
/*  747 */     this.nestMemberClasses = null;
/*  748 */     this.numberOfPermittedSubclasses = 0;
/*  749 */     this.permittedSubclasses = null;
/*  750 */     this.firstRecordComponent = null;
/*  751 */     this.lastRecordComponent = null;
/*  752 */     this.firstAttribute = null;
/*  753 */     this.compute = hasFrames ? 3 : 0;
/*  754 */     (new ClassReader(classFile, 0, false))
/*  755 */       .accept(this, attributes, (
/*      */ 
/*      */         
/*  758 */         hasFrames ? 8 : 0) | 0x100);
/*  759 */     return toByteArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Attribute[] getAttributePrototypes() {
/*  768 */     Attribute.Set attributePrototypes = new Attribute.Set();
/*  769 */     attributePrototypes.addAttributes(this.firstAttribute);
/*  770 */     FieldWriter fieldWriter = this.firstField;
/*  771 */     while (fieldWriter != null) {
/*  772 */       fieldWriter.collectAttributePrototypes(attributePrototypes);
/*  773 */       fieldWriter = (FieldWriter)fieldWriter.fv;
/*      */     } 
/*  775 */     MethodWriter methodWriter = this.firstMethod;
/*  776 */     while (methodWriter != null) {
/*  777 */       methodWriter.collectAttributePrototypes(attributePrototypes);
/*  778 */       methodWriter = (MethodWriter)methodWriter.mv;
/*      */     } 
/*  780 */     RecordComponentWriter recordComponentWriter = this.firstRecordComponent;
/*  781 */     while (recordComponentWriter != null) {
/*  782 */       recordComponentWriter.collectAttributePrototypes(attributePrototypes);
/*  783 */       recordComponentWriter = (RecordComponentWriter)recordComponentWriter.delegate;
/*      */     } 
/*  785 */     return attributePrototypes.toArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newConst(Object value) {
/*  802 */     return (this.symbolTable.addConstant(value)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newUTF8(String value) {
/*  815 */     return this.symbolTable.addConstantUtf8(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newClass(String value) {
/*  827 */     return (this.symbolTable.addConstantClass(value)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newMethodType(String methodDescriptor) {
/*  839 */     return (this.symbolTable.addConstantMethodType(methodDescriptor)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newModule(String moduleName) {
/*  851 */     return (this.symbolTable.addConstantModule(moduleName)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newPackage(String packageName) {
/*  863 */     return (this.symbolTable.addConstantPackage(packageName)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public int newHandle(int tag, String owner, String name, String descriptor) {
/*  885 */     return newHandle(tag, owner, name, descriptor, (tag == 9));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newHandle(int tag, String owner, String name, String descriptor, boolean isInterface) {
/*  909 */     return (this.symbolTable.addConstantMethodHandle(tag, owner, name, descriptor, isInterface)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newConstantDynamic(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
/*  928 */     return (this.symbolTable.addConstantDynamic(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newInvokeDynamic(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
/*  949 */     return (this.symbolTable.addConstantInvokeDynamic(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newField(String owner, String name, String descriptor) {
/*  965 */     return (this.symbolTable.addConstantFieldref(owner, name, descriptor)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newMethod(String owner, String name, String descriptor, boolean isInterface) {
/*  981 */     return (this.symbolTable.addConstantMethodref(owner, name, descriptor, isInterface)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newNameType(String name, String descriptor) {
/*  994 */     return this.symbolTable.addConstantNameAndType(name, descriptor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String getCommonSuperClass(String type1, String type2) {
/*      */     Class<?> class1, class2;
/* 1014 */     ClassLoader classLoader = getClassLoader();
/*      */     
/*      */     try {
/* 1017 */       class1 = Class.forName(type1.replace('/', '.'), false, classLoader);
/* 1018 */     } catch (ClassNotFoundException e) {
/* 1019 */       throw new TypeNotPresentException(type1, e);
/*      */     } 
/*      */     
/*      */     try {
/* 1023 */       class2 = Class.forName(type2.replace('/', '.'), false, classLoader);
/* 1024 */     } catch (ClassNotFoundException e) {
/* 1025 */       throw new TypeNotPresentException(type2, e);
/*      */     } 
/* 1027 */     if (class1.isAssignableFrom(class2)) {
/* 1028 */       return type1;
/*      */     }
/* 1030 */     if (class2.isAssignableFrom(class1)) {
/* 1031 */       return type2;
/*      */     }
/* 1033 */     if (class1.isInterface() || class2.isInterface()) {
/* 1034 */       return "java/lang/Object";
/*      */     }
/*      */     while (true) {
/* 1037 */       class1 = class1.getSuperclass();
/* 1038 */       if (class1.isAssignableFrom(class2)) {
/* 1039 */         return class1.getName().replace('.', '/');
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ClassLoader getClassLoader() {
/* 1051 */     return getClass().getClassLoader();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\ClassWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */