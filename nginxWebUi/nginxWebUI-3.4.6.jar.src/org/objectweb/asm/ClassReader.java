/*      */ package org.objectweb.asm;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ClassReader
/*      */ {
/*      */   public static final int SKIP_CODE = 1;
/*      */   public static final int SKIP_DEBUG = 2;
/*      */   public static final int SKIP_FRAMES = 4;
/*      */   public static final int EXPAND_FRAMES = 8;
/*      */   static final int EXPAND_ASM_INSNS = 256;
/*      */   private static final int MAX_BUFFER_SIZE = 1048576;
/*      */   private static final int INPUT_STREAM_DATA_CHUNK_SIZE = 4096;
/*      */   @Deprecated
/*      */   public final byte[] b;
/*      */   public final int header;
/*      */   final byte[] classFileBuffer;
/*      */   private final int[] cpInfoOffsets;
/*      */   private final String[] constantUtf8Values;
/*      */   private final ConstantDynamic[] constantDynamicValues;
/*      */   private final int[] bootstrapMethodOffsets;
/*      */   private final int maxStringLength;
/*      */   
/*      */   public ClassReader(byte[] classFile) {
/*  166 */     this(classFile, 0, classFile.length);
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
/*      */   public ClassReader(byte[] classFileBuffer, int classFileOffset, int classFileLength) {
/*  180 */     this(classFileBuffer, classFileOffset, true);
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
/*      */   ClassReader(byte[] classFileBuffer, int classFileOffset, boolean checkClassVersion) {
/*  193 */     this.classFileBuffer = classFileBuffer;
/*  194 */     this.b = classFileBuffer;
/*      */ 
/*      */     
/*  197 */     if (checkClassVersion && readShort(classFileOffset + 6) > 62) {
/*  198 */       throw new IllegalArgumentException("Unsupported class file major version " + 
/*  199 */           readShort(classFileOffset + 6));
/*      */     }
/*      */ 
/*      */     
/*  203 */     int constantPoolCount = readUnsignedShort(classFileOffset + 8);
/*  204 */     this.cpInfoOffsets = new int[constantPoolCount];
/*  205 */     this.constantUtf8Values = new String[constantPoolCount];
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  210 */     int currentCpInfoIndex = 1;
/*  211 */     int currentCpInfoOffset = classFileOffset + 10;
/*  212 */     int currentMaxStringLength = 0;
/*  213 */     boolean hasBootstrapMethods = false;
/*  214 */     boolean hasConstantDynamic = false;
/*      */     
/*  216 */     while (currentCpInfoIndex < constantPoolCount) {
/*  217 */       int cpInfoSize; this.cpInfoOffsets[currentCpInfoIndex++] = currentCpInfoOffset + 1;
/*      */       
/*  219 */       switch (classFileBuffer[currentCpInfoOffset]) {
/*      */         case 3:
/*      */         case 4:
/*      */         case 9:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*  226 */           cpInfoSize = 5;
/*      */           break;
/*      */         case 17:
/*  229 */           cpInfoSize = 5;
/*  230 */           hasBootstrapMethods = true;
/*  231 */           hasConstantDynamic = true;
/*      */           break;
/*      */         case 18:
/*  234 */           cpInfoSize = 5;
/*  235 */           hasBootstrapMethods = true;
/*      */           break;
/*      */         case 5:
/*      */         case 6:
/*  239 */           cpInfoSize = 9;
/*  240 */           currentCpInfoIndex++;
/*      */           break;
/*      */         case 1:
/*  243 */           cpInfoSize = 3 + readUnsignedShort(currentCpInfoOffset + 1);
/*  244 */           if (cpInfoSize > currentMaxStringLength)
/*      */           {
/*      */ 
/*      */             
/*  248 */             currentMaxStringLength = cpInfoSize;
/*      */           }
/*      */           break;
/*      */         case 15:
/*  252 */           cpInfoSize = 4;
/*      */           break;
/*      */         case 7:
/*      */         case 8:
/*      */         case 16:
/*      */         case 19:
/*      */         case 20:
/*  259 */           cpInfoSize = 3;
/*      */           break;
/*      */         default:
/*  262 */           throw new IllegalArgumentException();
/*      */       } 
/*  264 */       currentCpInfoOffset += cpInfoSize;
/*      */     } 
/*  266 */     this.maxStringLength = currentMaxStringLength;
/*      */     
/*  268 */     this.header = currentCpInfoOffset;
/*      */ 
/*      */     
/*  271 */     this.constantDynamicValues = hasConstantDynamic ? new ConstantDynamic[constantPoolCount] : null;
/*      */ 
/*      */     
/*  274 */     this
/*  275 */       .bootstrapMethodOffsets = hasBootstrapMethods ? readBootstrapMethodsAttribute(currentMaxStringLength) : null;
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
/*      */   public ClassReader(InputStream inputStream) throws IOException {
/*  287 */     this(readStream(inputStream, false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClassReader(String className) throws IOException {
/*  298 */     this(
/*  299 */         readStream(
/*  300 */           ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class"), true));
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
/*      */   private static byte[] readStream(InputStream inputStream, boolean close) throws IOException {
/*  313 */     if (inputStream == null) {
/*  314 */       throw new IOException("Class not found");
/*      */     }
/*  316 */     int bufferSize = calculateBufferSize(inputStream); try {
/*  317 */       ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*      */     finally {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  331 */       if (close) {
/*  332 */         inputStream.close();
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private static int calculateBufferSize(InputStream inputStream) throws IOException {
/*  338 */     int expectedLength = inputStream.available();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  345 */     if (expectedLength < 256) {
/*  346 */       return 4096;
/*      */     }
/*  348 */     return Math.min(expectedLength, 1048576);
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
/*      */   public int getAccess() {
/*  363 */     return readUnsignedShort(this.header);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getClassName() {
/*  374 */     return readClass(this.header + 2, new char[this.maxStringLength]);
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
/*      */   public String getSuperName() {
/*  386 */     return readClass(this.header + 4, new char[this.maxStringLength]);
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
/*      */   public String[] getInterfaces() {
/*  398 */     int currentOffset = this.header + 6;
/*  399 */     int interfacesCount = readUnsignedShort(currentOffset);
/*  400 */     String[] interfaces = new String[interfacesCount];
/*  401 */     if (interfacesCount > 0) {
/*  402 */       char[] charBuffer = new char[this.maxStringLength];
/*  403 */       for (int i = 0; i < interfacesCount; i++) {
/*  404 */         currentOffset += 2;
/*  405 */         interfaces[i] = readClass(currentOffset, charBuffer);
/*      */       } 
/*      */     } 
/*  408 */     return interfaces;
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
/*      */   public void accept(ClassVisitor classVisitor, int parsingOptions) {
/*  424 */     accept(classVisitor, new Attribute[0], parsingOptions);
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
/*      */   public void accept(ClassVisitor classVisitor, Attribute[] attributePrototypes, int parsingOptions) {
/*  445 */     Context context = new Context();
/*  446 */     context.attributePrototypes = attributePrototypes;
/*  447 */     context.parsingOptions = parsingOptions;
/*  448 */     context.charBuffer = new char[this.maxStringLength];
/*      */ 
/*      */     
/*  451 */     char[] charBuffer = context.charBuffer;
/*  452 */     int currentOffset = this.header;
/*  453 */     int accessFlags = readUnsignedShort(currentOffset);
/*  454 */     String thisClass = readClass(currentOffset + 2, charBuffer);
/*  455 */     String superClass = readClass(currentOffset + 4, charBuffer);
/*  456 */     String[] interfaces = new String[readUnsignedShort(currentOffset + 6)];
/*  457 */     currentOffset += 8;
/*  458 */     for (int i = 0; i < interfaces.length; i++) {
/*  459 */       interfaces[i] = readClass(currentOffset, charBuffer);
/*  460 */       currentOffset += 2;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  466 */     int innerClassesOffset = 0;
/*      */     
/*  468 */     int enclosingMethodOffset = 0;
/*      */     
/*  470 */     String signature = null;
/*      */     
/*  472 */     String sourceFile = null;
/*      */     
/*  474 */     String sourceDebugExtension = null;
/*      */     
/*  476 */     int runtimeVisibleAnnotationsOffset = 0;
/*      */     
/*  478 */     int runtimeInvisibleAnnotationsOffset = 0;
/*      */     
/*  480 */     int runtimeVisibleTypeAnnotationsOffset = 0;
/*      */     
/*  482 */     int runtimeInvisibleTypeAnnotationsOffset = 0;
/*      */     
/*  484 */     int moduleOffset = 0;
/*      */     
/*  486 */     int modulePackagesOffset = 0;
/*      */     
/*  488 */     String moduleMainClass = null;
/*      */     
/*  490 */     String nestHostClass = null;
/*      */     
/*  492 */     int nestMembersOffset = 0;
/*      */     
/*  494 */     int permittedSubclassesOffset = 0;
/*      */     
/*  496 */     int recordOffset = 0;
/*      */ 
/*      */     
/*  499 */     Attribute attributes = null;
/*      */     
/*  501 */     int currentAttributeOffset = getFirstAttributeOffset();
/*  502 */     for (int j = readUnsignedShort(currentAttributeOffset - 2); j > 0; j--) {
/*      */       
/*  504 */       String attributeName = readUTF8(currentAttributeOffset, charBuffer);
/*  505 */       int attributeLength = readInt(currentAttributeOffset + 2);
/*  506 */       currentAttributeOffset += 6;
/*      */ 
/*      */       
/*  509 */       if ("SourceFile".equals(attributeName)) {
/*  510 */         sourceFile = readUTF8(currentAttributeOffset, charBuffer);
/*  511 */       } else if ("InnerClasses".equals(attributeName)) {
/*  512 */         innerClassesOffset = currentAttributeOffset;
/*  513 */       } else if ("EnclosingMethod".equals(attributeName)) {
/*  514 */         enclosingMethodOffset = currentAttributeOffset;
/*  515 */       } else if ("NestHost".equals(attributeName)) {
/*  516 */         nestHostClass = readClass(currentAttributeOffset, charBuffer);
/*  517 */       } else if ("NestMembers".equals(attributeName)) {
/*  518 */         nestMembersOffset = currentAttributeOffset;
/*  519 */       } else if ("PermittedSubclasses".equals(attributeName)) {
/*  520 */         permittedSubclassesOffset = currentAttributeOffset;
/*  521 */       } else if ("Signature".equals(attributeName)) {
/*  522 */         signature = readUTF8(currentAttributeOffset, charBuffer);
/*  523 */       } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
/*  524 */         runtimeVisibleAnnotationsOffset = currentAttributeOffset;
/*  525 */       } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
/*  526 */         runtimeVisibleTypeAnnotationsOffset = currentAttributeOffset;
/*  527 */       } else if ("Deprecated".equals(attributeName)) {
/*  528 */         accessFlags |= 0x20000;
/*  529 */       } else if ("Synthetic".equals(attributeName)) {
/*  530 */         accessFlags |= 0x1000;
/*  531 */       } else if ("SourceDebugExtension".equals(attributeName)) {
/*  532 */         if (attributeLength > this.classFileBuffer.length - currentAttributeOffset) {
/*  533 */           throw new IllegalArgumentException();
/*      */         }
/*      */         
/*  536 */         sourceDebugExtension = readUtf(currentAttributeOffset, attributeLength, new char[attributeLength]);
/*  537 */       } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
/*  538 */         runtimeInvisibleAnnotationsOffset = currentAttributeOffset;
/*  539 */       } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
/*  540 */         runtimeInvisibleTypeAnnotationsOffset = currentAttributeOffset;
/*  541 */       } else if ("Record".equals(attributeName)) {
/*  542 */         recordOffset = currentAttributeOffset;
/*  543 */         accessFlags |= 0x10000;
/*  544 */       } else if ("Module".equals(attributeName)) {
/*  545 */         moduleOffset = currentAttributeOffset;
/*  546 */       } else if ("ModuleMainClass".equals(attributeName)) {
/*  547 */         moduleMainClass = readClass(currentAttributeOffset, charBuffer);
/*  548 */       } else if ("ModulePackages".equals(attributeName)) {
/*  549 */         modulePackagesOffset = currentAttributeOffset;
/*  550 */       } else if (!"BootstrapMethods".equals(attributeName)) {
/*      */ 
/*      */         
/*  553 */         Attribute attribute = readAttribute(attributePrototypes, attributeName, currentAttributeOffset, attributeLength, charBuffer, -1, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  561 */         attribute.nextAttribute = attributes;
/*  562 */         attributes = attribute;
/*      */       } 
/*  564 */       currentAttributeOffset += attributeLength;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  569 */     classVisitor.visit(
/*  570 */         readInt(this.cpInfoOffsets[1] - 7), accessFlags, thisClass, signature, superClass, interfaces);
/*      */ 
/*      */     
/*  573 */     if ((parsingOptions & 0x2) == 0 && (sourceFile != null || sourceDebugExtension != null))
/*      */     {
/*  575 */       classVisitor.visitSource(sourceFile, sourceDebugExtension);
/*      */     }
/*      */ 
/*      */     
/*  579 */     if (moduleOffset != 0) {
/*  580 */       readModuleAttributes(classVisitor, context, moduleOffset, modulePackagesOffset, moduleMainClass);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  585 */     if (nestHostClass != null) {
/*  586 */       classVisitor.visitNestHost(nestHostClass);
/*      */     }
/*      */ 
/*      */     
/*  590 */     if (enclosingMethodOffset != 0) {
/*  591 */       String className = readClass(enclosingMethodOffset, charBuffer);
/*  592 */       int methodIndex = readUnsignedShort(enclosingMethodOffset + 2);
/*  593 */       String name = (methodIndex == 0) ? null : readUTF8(this.cpInfoOffsets[methodIndex], charBuffer);
/*  594 */       String type = (methodIndex == 0) ? null : readUTF8(this.cpInfoOffsets[methodIndex] + 2, charBuffer);
/*  595 */       classVisitor.visitOuterClass(className, name, type);
/*      */     } 
/*      */ 
/*      */     
/*  599 */     if (runtimeVisibleAnnotationsOffset != 0) {
/*  600 */       int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
/*  601 */       int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
/*  602 */       while (numAnnotations-- > 0) {
/*      */         
/*  604 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/*  605 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/*  608 */         currentAnnotationOffset = readElementValues(classVisitor
/*  609 */             .visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  617 */     if (runtimeInvisibleAnnotationsOffset != 0) {
/*  618 */       int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
/*  619 */       int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
/*  620 */       while (numAnnotations-- > 0) {
/*      */         
/*  622 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/*  623 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/*  626 */         currentAnnotationOffset = readElementValues(classVisitor
/*  627 */             .visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  635 */     if (runtimeVisibleTypeAnnotationsOffset != 0) {
/*  636 */       int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
/*  637 */       int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
/*  638 */       while (numAnnotations-- > 0) {
/*      */         
/*  640 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/*  642 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/*  643 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/*  646 */         currentAnnotationOffset = readElementValues(classVisitor
/*  647 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  659 */     if (runtimeInvisibleTypeAnnotationsOffset != 0) {
/*  660 */       int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
/*  661 */       int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
/*  662 */       while (numAnnotations-- > 0) {
/*      */         
/*  664 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/*  666 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/*  667 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/*  670 */         currentAnnotationOffset = readElementValues(classVisitor
/*  671 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  683 */     while (attributes != null) {
/*      */       
/*  685 */       Attribute nextAttribute = attributes.nextAttribute;
/*  686 */       attributes.nextAttribute = null;
/*  687 */       classVisitor.visitAttribute(attributes);
/*  688 */       attributes = nextAttribute;
/*      */     } 
/*      */ 
/*      */     
/*  692 */     if (nestMembersOffset != 0) {
/*  693 */       int numberOfNestMembers = readUnsignedShort(nestMembersOffset);
/*  694 */       int currentNestMemberOffset = nestMembersOffset + 2;
/*  695 */       while (numberOfNestMembers-- > 0) {
/*  696 */         classVisitor.visitNestMember(readClass(currentNestMemberOffset, charBuffer));
/*  697 */         currentNestMemberOffset += 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  702 */     if (permittedSubclassesOffset != 0) {
/*  703 */       int numberOfPermittedSubclasses = readUnsignedShort(permittedSubclassesOffset);
/*  704 */       int currentPermittedSubclassesOffset = permittedSubclassesOffset + 2;
/*  705 */       while (numberOfPermittedSubclasses-- > 0) {
/*  706 */         classVisitor.visitPermittedSubclass(
/*  707 */             readClass(currentPermittedSubclassesOffset, charBuffer));
/*  708 */         currentPermittedSubclassesOffset += 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  713 */     if (innerClassesOffset != 0) {
/*  714 */       int numberOfClasses = readUnsignedShort(innerClassesOffset);
/*  715 */       int currentClassesOffset = innerClassesOffset + 2;
/*  716 */       while (numberOfClasses-- > 0) {
/*  717 */         classVisitor.visitInnerClass(
/*  718 */             readClass(currentClassesOffset, charBuffer), 
/*  719 */             readClass(currentClassesOffset + 2, charBuffer), 
/*  720 */             readUTF8(currentClassesOffset + 4, charBuffer), 
/*  721 */             readUnsignedShort(currentClassesOffset + 6));
/*  722 */         currentClassesOffset += 8;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  727 */     if (recordOffset != 0) {
/*  728 */       int recordComponentsCount = readUnsignedShort(recordOffset);
/*  729 */       recordOffset += 2;
/*  730 */       while (recordComponentsCount-- > 0) {
/*  731 */         recordOffset = readRecordComponent(classVisitor, context, recordOffset);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  736 */     int fieldsCount = readUnsignedShort(currentOffset);
/*  737 */     currentOffset += 2;
/*  738 */     while (fieldsCount-- > 0) {
/*  739 */       currentOffset = readField(classVisitor, context, currentOffset);
/*      */     }
/*  741 */     int methodsCount = readUnsignedShort(currentOffset);
/*  742 */     currentOffset += 2;
/*  743 */     while (methodsCount-- > 0) {
/*  744 */       currentOffset = readMethod(classVisitor, context, currentOffset);
/*      */     }
/*      */ 
/*      */     
/*  748 */     classVisitor.visitEnd();
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
/*      */   private void readModuleAttributes(ClassVisitor classVisitor, Context context, int moduleOffset, int modulePackagesOffset, String moduleMainClass) {
/*  773 */     char[] buffer = context.charBuffer;
/*      */ 
/*      */     
/*  776 */     int currentOffset = moduleOffset;
/*  777 */     String moduleName = readModule(currentOffset, buffer);
/*  778 */     int moduleFlags = readUnsignedShort(currentOffset + 2);
/*  779 */     String moduleVersion = readUTF8(currentOffset + 4, buffer);
/*  780 */     currentOffset += 6;
/*  781 */     ModuleVisitor moduleVisitor = classVisitor.visitModule(moduleName, moduleFlags, moduleVersion);
/*  782 */     if (moduleVisitor == null) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  787 */     if (moduleMainClass != null) {
/*  788 */       moduleVisitor.visitMainClass(moduleMainClass);
/*      */     }
/*      */ 
/*      */     
/*  792 */     if (modulePackagesOffset != 0) {
/*  793 */       int packageCount = readUnsignedShort(modulePackagesOffset);
/*  794 */       int currentPackageOffset = modulePackagesOffset + 2;
/*  795 */       while (packageCount-- > 0) {
/*  796 */         moduleVisitor.visitPackage(readPackage(currentPackageOffset, buffer));
/*  797 */         currentPackageOffset += 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  802 */     int requiresCount = readUnsignedShort(currentOffset);
/*  803 */     currentOffset += 2;
/*  804 */     while (requiresCount-- > 0) {
/*      */       
/*  806 */       String requires = readModule(currentOffset, buffer);
/*  807 */       int requiresFlags = readUnsignedShort(currentOffset + 2);
/*  808 */       String requiresVersion = readUTF8(currentOffset + 4, buffer);
/*  809 */       currentOffset += 6;
/*  810 */       moduleVisitor.visitRequire(requires, requiresFlags, requiresVersion);
/*      */     } 
/*      */ 
/*      */     
/*  814 */     int exportsCount = readUnsignedShort(currentOffset);
/*  815 */     currentOffset += 2;
/*  816 */     while (exportsCount-- > 0) {
/*      */ 
/*      */       
/*  819 */       String exports = readPackage(currentOffset, buffer);
/*  820 */       int exportsFlags = readUnsignedShort(currentOffset + 2);
/*  821 */       int exportsToCount = readUnsignedShort(currentOffset + 4);
/*  822 */       currentOffset += 6;
/*  823 */       String[] exportsTo = null;
/*  824 */       if (exportsToCount != 0) {
/*  825 */         exportsTo = new String[exportsToCount];
/*  826 */         for (int i = 0; i < exportsToCount; i++) {
/*  827 */           exportsTo[i] = readModule(currentOffset, buffer);
/*  828 */           currentOffset += 2;
/*      */         } 
/*      */       } 
/*  831 */       moduleVisitor.visitExport(exports, exportsFlags, exportsTo);
/*      */     } 
/*      */ 
/*      */     
/*  835 */     int opensCount = readUnsignedShort(currentOffset);
/*  836 */     currentOffset += 2;
/*  837 */     while (opensCount-- > 0) {
/*      */       
/*  839 */       String opens = readPackage(currentOffset, buffer);
/*  840 */       int opensFlags = readUnsignedShort(currentOffset + 2);
/*  841 */       int opensToCount = readUnsignedShort(currentOffset + 4);
/*  842 */       currentOffset += 6;
/*  843 */       String[] opensTo = null;
/*  844 */       if (opensToCount != 0) {
/*  845 */         opensTo = new String[opensToCount];
/*  846 */         for (int i = 0; i < opensToCount; i++) {
/*  847 */           opensTo[i] = readModule(currentOffset, buffer);
/*  848 */           currentOffset += 2;
/*      */         } 
/*      */       } 
/*  851 */       moduleVisitor.visitOpen(opens, opensFlags, opensTo);
/*      */     } 
/*      */ 
/*      */     
/*  855 */     int usesCount = readUnsignedShort(currentOffset);
/*  856 */     currentOffset += 2;
/*  857 */     while (usesCount-- > 0) {
/*  858 */       moduleVisitor.visitUse(readClass(currentOffset, buffer));
/*  859 */       currentOffset += 2;
/*      */     } 
/*      */ 
/*      */     
/*  863 */     int providesCount = readUnsignedShort(currentOffset);
/*  864 */     currentOffset += 2;
/*  865 */     while (providesCount-- > 0) {
/*      */       
/*  867 */       String provides = readClass(currentOffset, buffer);
/*  868 */       int providesWithCount = readUnsignedShort(currentOffset + 2);
/*  869 */       currentOffset += 4;
/*  870 */       String[] providesWith = new String[providesWithCount];
/*  871 */       for (int i = 0; i < providesWithCount; i++) {
/*  872 */         providesWith[i] = readClass(currentOffset, buffer);
/*  873 */         currentOffset += 2;
/*      */       } 
/*  875 */       moduleVisitor.visitProvide(provides, providesWith);
/*      */     } 
/*      */ 
/*      */     
/*  879 */     moduleVisitor.visitEnd();
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
/*      */   private int readRecordComponent(ClassVisitor classVisitor, Context context, int recordComponentOffset) {
/*  892 */     char[] charBuffer = context.charBuffer;
/*      */     
/*  894 */     int currentOffset = recordComponentOffset;
/*  895 */     String name = readUTF8(currentOffset, charBuffer);
/*  896 */     String descriptor = readUTF8(currentOffset + 2, charBuffer);
/*  897 */     currentOffset += 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  904 */     String signature = null;
/*      */     
/*  906 */     int runtimeVisibleAnnotationsOffset = 0;
/*      */     
/*  908 */     int runtimeInvisibleAnnotationsOffset = 0;
/*      */     
/*  910 */     int runtimeVisibleTypeAnnotationsOffset = 0;
/*      */     
/*  912 */     int runtimeInvisibleTypeAnnotationsOffset = 0;
/*      */ 
/*      */     
/*  915 */     Attribute attributes = null;
/*      */     
/*  917 */     int attributesCount = readUnsignedShort(currentOffset);
/*  918 */     currentOffset += 2;
/*  919 */     while (attributesCount-- > 0) {
/*      */       
/*  921 */       String attributeName = readUTF8(currentOffset, charBuffer);
/*  922 */       int attributeLength = readInt(currentOffset + 2);
/*  923 */       currentOffset += 6;
/*      */ 
/*      */       
/*  926 */       if ("Signature".equals(attributeName)) {
/*  927 */         signature = readUTF8(currentOffset, charBuffer);
/*  928 */       } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
/*  929 */         runtimeVisibleAnnotationsOffset = currentOffset;
/*  930 */       } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
/*  931 */         runtimeVisibleTypeAnnotationsOffset = currentOffset;
/*  932 */       } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
/*  933 */         runtimeInvisibleAnnotationsOffset = currentOffset;
/*  934 */       } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
/*  935 */         runtimeInvisibleTypeAnnotationsOffset = currentOffset;
/*      */       } else {
/*      */         
/*  938 */         Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset, attributeLength, charBuffer, -1, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  946 */         attribute.nextAttribute = attributes;
/*  947 */         attributes = attribute;
/*      */       } 
/*  949 */       currentOffset += attributeLength;
/*      */     } 
/*      */ 
/*      */     
/*  953 */     RecordComponentVisitor recordComponentVisitor = classVisitor.visitRecordComponent(name, descriptor, signature);
/*  954 */     if (recordComponentVisitor == null) {
/*  955 */       return currentOffset;
/*      */     }
/*      */ 
/*      */     
/*  959 */     if (runtimeVisibleAnnotationsOffset != 0) {
/*  960 */       int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
/*  961 */       int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
/*  962 */       while (numAnnotations-- > 0) {
/*      */         
/*  964 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/*  965 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/*  968 */         currentAnnotationOffset = readElementValues(recordComponentVisitor
/*  969 */             .visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  977 */     if (runtimeInvisibleAnnotationsOffset != 0) {
/*  978 */       int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
/*  979 */       int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
/*  980 */       while (numAnnotations-- > 0) {
/*      */         
/*  982 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/*  983 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/*  986 */         currentAnnotationOffset = readElementValues(recordComponentVisitor
/*  987 */             .visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  995 */     if (runtimeVisibleTypeAnnotationsOffset != 0) {
/*  996 */       int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
/*  997 */       int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
/*  998 */       while (numAnnotations-- > 0) {
/*      */         
/* 1000 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/* 1002 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1003 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1006 */         currentAnnotationOffset = readElementValues(recordComponentVisitor
/* 1007 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1019 */     if (runtimeInvisibleTypeAnnotationsOffset != 0) {
/* 1020 */       int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
/* 1021 */       int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
/* 1022 */       while (numAnnotations-- > 0) {
/*      */         
/* 1024 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/* 1026 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1027 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1030 */         currentAnnotationOffset = readElementValues(recordComponentVisitor
/* 1031 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1043 */     while (attributes != null) {
/*      */       
/* 1045 */       Attribute nextAttribute = attributes.nextAttribute;
/* 1046 */       attributes.nextAttribute = null;
/* 1047 */       recordComponentVisitor.visitAttribute(attributes);
/* 1048 */       attributes = nextAttribute;
/*      */     } 
/*      */ 
/*      */     
/* 1052 */     recordComponentVisitor.visitEnd();
/* 1053 */     return currentOffset;
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
/*      */   private int readField(ClassVisitor classVisitor, Context context, int fieldInfoOffset) {
/* 1066 */     char[] charBuffer = context.charBuffer;
/*      */ 
/*      */     
/* 1069 */     int currentOffset = fieldInfoOffset;
/* 1070 */     int accessFlags = readUnsignedShort(currentOffset);
/* 1071 */     String name = readUTF8(currentOffset + 2, charBuffer);
/* 1072 */     String descriptor = readUTF8(currentOffset + 4, charBuffer);
/* 1073 */     currentOffset += 6;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1078 */     Object constantValue = null;
/*      */     
/* 1080 */     String signature = null;
/*      */     
/* 1082 */     int runtimeVisibleAnnotationsOffset = 0;
/*      */     
/* 1084 */     int runtimeInvisibleAnnotationsOffset = 0;
/*      */     
/* 1086 */     int runtimeVisibleTypeAnnotationsOffset = 0;
/*      */     
/* 1088 */     int runtimeInvisibleTypeAnnotationsOffset = 0;
/*      */ 
/*      */     
/* 1091 */     Attribute attributes = null;
/*      */     
/* 1093 */     int attributesCount = readUnsignedShort(currentOffset);
/* 1094 */     currentOffset += 2;
/* 1095 */     while (attributesCount-- > 0) {
/*      */       
/* 1097 */       String attributeName = readUTF8(currentOffset, charBuffer);
/* 1098 */       int attributeLength = readInt(currentOffset + 2);
/* 1099 */       currentOffset += 6;
/*      */ 
/*      */       
/* 1102 */       if ("ConstantValue".equals(attributeName)) {
/* 1103 */         int constantvalueIndex = readUnsignedShort(currentOffset);
/* 1104 */         constantValue = (constantvalueIndex == 0) ? null : readConst(constantvalueIndex, charBuffer);
/* 1105 */       } else if ("Signature".equals(attributeName)) {
/* 1106 */         signature = readUTF8(currentOffset, charBuffer);
/* 1107 */       } else if ("Deprecated".equals(attributeName)) {
/* 1108 */         accessFlags |= 0x20000;
/* 1109 */       } else if ("Synthetic".equals(attributeName)) {
/* 1110 */         accessFlags |= 0x1000;
/* 1111 */       } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
/* 1112 */         runtimeVisibleAnnotationsOffset = currentOffset;
/* 1113 */       } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
/* 1114 */         runtimeVisibleTypeAnnotationsOffset = currentOffset;
/* 1115 */       } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
/* 1116 */         runtimeInvisibleAnnotationsOffset = currentOffset;
/* 1117 */       } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
/* 1118 */         runtimeInvisibleTypeAnnotationsOffset = currentOffset;
/*      */       } else {
/*      */         
/* 1121 */         Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset, attributeLength, charBuffer, -1, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1129 */         attribute.nextAttribute = attributes;
/* 1130 */         attributes = attribute;
/*      */       } 
/* 1132 */       currentOffset += attributeLength;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1137 */     FieldVisitor fieldVisitor = classVisitor.visitField(accessFlags, name, descriptor, signature, constantValue);
/* 1138 */     if (fieldVisitor == null) {
/* 1139 */       return currentOffset;
/*      */     }
/*      */ 
/*      */     
/* 1143 */     if (runtimeVisibleAnnotationsOffset != 0) {
/* 1144 */       int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
/* 1145 */       int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
/* 1146 */       while (numAnnotations-- > 0) {
/*      */         
/* 1148 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1149 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1152 */         currentAnnotationOffset = readElementValues(fieldVisitor
/* 1153 */             .visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1161 */     if (runtimeInvisibleAnnotationsOffset != 0) {
/* 1162 */       int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
/* 1163 */       int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
/* 1164 */       while (numAnnotations-- > 0) {
/*      */         
/* 1166 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1167 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1170 */         currentAnnotationOffset = readElementValues(fieldVisitor
/* 1171 */             .visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1179 */     if (runtimeVisibleTypeAnnotationsOffset != 0) {
/* 1180 */       int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
/* 1181 */       int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
/* 1182 */       while (numAnnotations-- > 0) {
/*      */         
/* 1184 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/* 1186 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1187 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1190 */         currentAnnotationOffset = readElementValues(fieldVisitor
/* 1191 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1203 */     if (runtimeInvisibleTypeAnnotationsOffset != 0) {
/* 1204 */       int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
/* 1205 */       int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
/* 1206 */       while (numAnnotations-- > 0) {
/*      */         
/* 1208 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/* 1210 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1211 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1214 */         currentAnnotationOffset = readElementValues(fieldVisitor
/* 1215 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1227 */     while (attributes != null) {
/*      */       
/* 1229 */       Attribute nextAttribute = attributes.nextAttribute;
/* 1230 */       attributes.nextAttribute = null;
/* 1231 */       fieldVisitor.visitAttribute(attributes);
/* 1232 */       attributes = nextAttribute;
/*      */     } 
/*      */ 
/*      */     
/* 1236 */     fieldVisitor.visitEnd();
/* 1237 */     return currentOffset;
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
/*      */   private int readMethod(ClassVisitor classVisitor, Context context, int methodInfoOffset) {
/* 1250 */     char[] charBuffer = context.charBuffer;
/*      */ 
/*      */     
/* 1253 */     int currentOffset = methodInfoOffset;
/* 1254 */     context.currentMethodAccessFlags = readUnsignedShort(currentOffset);
/* 1255 */     context.currentMethodName = readUTF8(currentOffset + 2, charBuffer);
/* 1256 */     context.currentMethodDescriptor = readUTF8(currentOffset + 4, charBuffer);
/* 1257 */     currentOffset += 6;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1262 */     int codeOffset = 0;
/*      */     
/* 1264 */     int exceptionsOffset = 0;
/*      */     
/* 1266 */     String[] exceptions = null;
/*      */     
/* 1268 */     boolean synthetic = false;
/*      */     
/* 1270 */     int signatureIndex = 0;
/*      */     
/* 1272 */     int runtimeVisibleAnnotationsOffset = 0;
/*      */     
/* 1274 */     int runtimeInvisibleAnnotationsOffset = 0;
/*      */     
/* 1276 */     int runtimeVisibleParameterAnnotationsOffset = 0;
/*      */     
/* 1278 */     int runtimeInvisibleParameterAnnotationsOffset = 0;
/*      */     
/* 1280 */     int runtimeVisibleTypeAnnotationsOffset = 0;
/*      */     
/* 1282 */     int runtimeInvisibleTypeAnnotationsOffset = 0;
/*      */     
/* 1284 */     int annotationDefaultOffset = 0;
/*      */     
/* 1286 */     int methodParametersOffset = 0;
/*      */ 
/*      */     
/* 1289 */     Attribute attributes = null;
/*      */     
/* 1291 */     int attributesCount = readUnsignedShort(currentOffset);
/* 1292 */     currentOffset += 2;
/* 1293 */     while (attributesCount-- > 0) {
/*      */       
/* 1295 */       String attributeName = readUTF8(currentOffset, charBuffer);
/* 1296 */       int attributeLength = readInt(currentOffset + 2);
/* 1297 */       currentOffset += 6;
/*      */ 
/*      */       
/* 1300 */       if ("Code".equals(attributeName)) {
/* 1301 */         if ((context.parsingOptions & 0x1) == 0) {
/* 1302 */           codeOffset = currentOffset;
/*      */         }
/* 1304 */       } else if ("Exceptions".equals(attributeName)) {
/* 1305 */         exceptionsOffset = currentOffset;
/* 1306 */         exceptions = new String[readUnsignedShort(exceptionsOffset)];
/* 1307 */         int currentExceptionOffset = exceptionsOffset + 2;
/* 1308 */         for (int i = 0; i < exceptions.length; i++) {
/* 1309 */           exceptions[i] = readClass(currentExceptionOffset, charBuffer);
/* 1310 */           currentExceptionOffset += 2;
/*      */         } 
/* 1312 */       } else if ("Signature".equals(attributeName)) {
/* 1313 */         signatureIndex = readUnsignedShort(currentOffset);
/* 1314 */       } else if ("Deprecated".equals(attributeName)) {
/* 1315 */         context.currentMethodAccessFlags |= 0x20000;
/* 1316 */       } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
/* 1317 */         runtimeVisibleAnnotationsOffset = currentOffset;
/* 1318 */       } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
/* 1319 */         runtimeVisibleTypeAnnotationsOffset = currentOffset;
/* 1320 */       } else if ("AnnotationDefault".equals(attributeName)) {
/* 1321 */         annotationDefaultOffset = currentOffset;
/* 1322 */       } else if ("Synthetic".equals(attributeName)) {
/* 1323 */         synthetic = true;
/* 1324 */         context.currentMethodAccessFlags |= 0x1000;
/* 1325 */       } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
/* 1326 */         runtimeInvisibleAnnotationsOffset = currentOffset;
/* 1327 */       } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
/* 1328 */         runtimeInvisibleTypeAnnotationsOffset = currentOffset;
/* 1329 */       } else if ("RuntimeVisibleParameterAnnotations".equals(attributeName)) {
/* 1330 */         runtimeVisibleParameterAnnotationsOffset = currentOffset;
/* 1331 */       } else if ("RuntimeInvisibleParameterAnnotations".equals(attributeName)) {
/* 1332 */         runtimeInvisibleParameterAnnotationsOffset = currentOffset;
/* 1333 */       } else if ("MethodParameters".equals(attributeName)) {
/* 1334 */         methodParametersOffset = currentOffset;
/*      */       } else {
/*      */         
/* 1337 */         Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset, attributeLength, charBuffer, -1, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1345 */         attribute.nextAttribute = attributes;
/* 1346 */         attributes = attribute;
/*      */       } 
/* 1348 */       currentOffset += attributeLength;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1353 */     MethodVisitor methodVisitor = classVisitor.visitMethod(context.currentMethodAccessFlags, context.currentMethodName, context.currentMethodDescriptor, 
/*      */ 
/*      */ 
/*      */         
/* 1357 */         (signatureIndex == 0) ? null : readUtf(signatureIndex, charBuffer), exceptions);
/*      */     
/* 1359 */     if (methodVisitor == null) {
/* 1360 */       return currentOffset;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1367 */     if (methodVisitor instanceof MethodWriter) {
/* 1368 */       MethodWriter methodWriter = (MethodWriter)methodVisitor;
/* 1369 */       if (methodWriter.canCopyMethodAttributes(this, synthetic, ((context.currentMethodAccessFlags & 0x20000) != 0), 
/*      */ 
/*      */ 
/*      */           
/* 1373 */           readUnsignedShort(methodInfoOffset + 4), signatureIndex, exceptionsOffset)) {
/*      */ 
/*      */         
/* 1376 */         methodWriter.setMethodAttributesSource(methodInfoOffset, currentOffset - methodInfoOffset);
/* 1377 */         return currentOffset;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1382 */     if (methodParametersOffset != 0 && (context.parsingOptions & 0x2) == 0) {
/* 1383 */       int parametersCount = readByte(methodParametersOffset);
/* 1384 */       int currentParameterOffset = methodParametersOffset + 1;
/* 1385 */       while (parametersCount-- > 0) {
/*      */         
/* 1387 */         methodVisitor.visitParameter(
/* 1388 */             readUTF8(currentParameterOffset, charBuffer), 
/* 1389 */             readUnsignedShort(currentParameterOffset + 2));
/* 1390 */         currentParameterOffset += 4;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1395 */     if (annotationDefaultOffset != 0) {
/* 1396 */       AnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
/* 1397 */       readElementValue(annotationVisitor, annotationDefaultOffset, null, charBuffer);
/* 1398 */       if (annotationVisitor != null) {
/* 1399 */         annotationVisitor.visitEnd();
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1404 */     if (runtimeVisibleAnnotationsOffset != 0) {
/* 1405 */       int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
/* 1406 */       int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
/* 1407 */       while (numAnnotations-- > 0) {
/*      */         
/* 1409 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1410 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1413 */         currentAnnotationOffset = readElementValues(methodVisitor
/* 1414 */             .visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1422 */     if (runtimeInvisibleAnnotationsOffset != 0) {
/* 1423 */       int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
/* 1424 */       int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
/* 1425 */       while (numAnnotations-- > 0) {
/*      */         
/* 1427 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1428 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1431 */         currentAnnotationOffset = readElementValues(methodVisitor
/* 1432 */             .visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1440 */     if (runtimeVisibleTypeAnnotationsOffset != 0) {
/* 1441 */       int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
/* 1442 */       int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
/* 1443 */       while (numAnnotations-- > 0) {
/*      */         
/* 1445 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/* 1447 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1448 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1451 */         currentAnnotationOffset = readElementValues(methodVisitor
/* 1452 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1464 */     if (runtimeInvisibleTypeAnnotationsOffset != 0) {
/* 1465 */       int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
/* 1466 */       int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
/* 1467 */       while (numAnnotations-- > 0) {
/*      */         
/* 1469 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/* 1471 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1472 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1475 */         currentAnnotationOffset = readElementValues(methodVisitor
/* 1476 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1488 */     if (runtimeVisibleParameterAnnotationsOffset != 0) {
/* 1489 */       readParameterAnnotations(methodVisitor, context, runtimeVisibleParameterAnnotationsOffset, true);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1494 */     if (runtimeInvisibleParameterAnnotationsOffset != 0) {
/* 1495 */       readParameterAnnotations(methodVisitor, context, runtimeInvisibleParameterAnnotationsOffset, false);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1503 */     while (attributes != null) {
/*      */       
/* 1505 */       Attribute nextAttribute = attributes.nextAttribute;
/* 1506 */       attributes.nextAttribute = null;
/* 1507 */       methodVisitor.visitAttribute(attributes);
/* 1508 */       attributes = nextAttribute;
/*      */     } 
/*      */ 
/*      */     
/* 1512 */     if (codeOffset != 0) {
/* 1513 */       methodVisitor.visitCode();
/* 1514 */       readCode(methodVisitor, context, codeOffset);
/*      */     } 
/*      */ 
/*      */     
/* 1518 */     methodVisitor.visitEnd();
/* 1519 */     return currentOffset;
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
/*      */   private void readCode(MethodVisitor methodVisitor, Context context, int codeOffset) {
/* 1536 */     int currentOffset = codeOffset;
/*      */ 
/*      */     
/* 1539 */     byte[] classBuffer = this.classFileBuffer;
/* 1540 */     char[] charBuffer = context.charBuffer;
/* 1541 */     int maxStack = readUnsignedShort(currentOffset);
/* 1542 */     int maxLocals = readUnsignedShort(currentOffset + 2);
/* 1543 */     int codeLength = readInt(currentOffset + 4);
/* 1544 */     currentOffset += 8;
/* 1545 */     if (codeLength > this.classFileBuffer.length - currentOffset) {
/* 1546 */       throw new IllegalArgumentException();
/*      */     }
/*      */ 
/*      */     
/* 1550 */     int bytecodeStartOffset = currentOffset;
/* 1551 */     int bytecodeEndOffset = currentOffset + codeLength;
/* 1552 */     Label[] labels = context.currentMethodLabels = new Label[codeLength + 1];
/* 1553 */     while (currentOffset < bytecodeEndOffset) {
/* 1554 */       int numTableEntries, numSwitchCases, bytecodeOffset = currentOffset - bytecodeStartOffset;
/* 1555 */       int opcode = classBuffer[currentOffset] & 0xFF;
/* 1556 */       switch (opcode) {
/*      */         case 0:
/*      */         case 1:
/*      */         case 2:
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 8:
/*      */         case 9:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*      */         case 13:
/*      */         case 14:
/*      */         case 15:
/*      */         case 26:
/*      */         case 27:
/*      */         case 28:
/*      */         case 29:
/*      */         case 30:
/*      */         case 31:
/*      */         case 32:
/*      */         case 33:
/*      */         case 34:
/*      */         case 35:
/*      */         case 36:
/*      */         case 37:
/*      */         case 38:
/*      */         case 39:
/*      */         case 40:
/*      */         case 41:
/*      */         case 42:
/*      */         case 43:
/*      */         case 44:
/*      */         case 45:
/*      */         case 46:
/*      */         case 47:
/*      */         case 48:
/*      */         case 49:
/*      */         case 50:
/*      */         case 51:
/*      */         case 52:
/*      */         case 53:
/*      */         case 59:
/*      */         case 60:
/*      */         case 61:
/*      */         case 62:
/*      */         case 63:
/*      */         case 64:
/*      */         case 65:
/*      */         case 66:
/*      */         case 67:
/*      */         case 68:
/*      */         case 69:
/*      */         case 70:
/*      */         case 71:
/*      */         case 72:
/*      */         case 73:
/*      */         case 74:
/*      */         case 75:
/*      */         case 76:
/*      */         case 77:
/*      */         case 78:
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/*      */         case 82:
/*      */         case 83:
/*      */         case 84:
/*      */         case 85:
/*      */         case 86:
/*      */         case 87:
/*      */         case 88:
/*      */         case 89:
/*      */         case 90:
/*      */         case 91:
/*      */         case 92:
/*      */         case 93:
/*      */         case 94:
/*      */         case 95:
/*      */         case 96:
/*      */         case 97:
/*      */         case 98:
/*      */         case 99:
/*      */         case 100:
/*      */         case 101:
/*      */         case 102:
/*      */         case 103:
/*      */         case 104:
/*      */         case 105:
/*      */         case 106:
/*      */         case 107:
/*      */         case 108:
/*      */         case 109:
/*      */         case 110:
/*      */         case 111:
/*      */         case 112:
/*      */         case 113:
/*      */         case 114:
/*      */         case 115:
/*      */         case 116:
/*      */         case 117:
/*      */         case 118:
/*      */         case 119:
/*      */         case 120:
/*      */         case 121:
/*      */         case 122:
/*      */         case 123:
/*      */         case 124:
/*      */         case 125:
/*      */         case 126:
/*      */         case 127:
/*      */         case 128:
/*      */         case 129:
/*      */         case 130:
/*      */         case 131:
/*      */         case 133:
/*      */         case 134:
/*      */         case 135:
/*      */         case 136:
/*      */         case 137:
/*      */         case 138:
/*      */         case 139:
/*      */         case 140:
/*      */         case 141:
/*      */         case 142:
/*      */         case 143:
/*      */         case 144:
/*      */         case 145:
/*      */         case 146:
/*      */         case 147:
/*      */         case 148:
/*      */         case 149:
/*      */         case 150:
/*      */         case 151:
/*      */         case 152:
/*      */         case 172:
/*      */         case 173:
/*      */         case 174:
/*      */         case 175:
/*      */         case 176:
/*      */         case 177:
/*      */         case 190:
/*      */         case 191:
/*      */         case 194:
/*      */         case 195:
/* 1704 */           currentOffset++;
/*      */           continue;
/*      */         case 153:
/*      */         case 154:
/*      */         case 155:
/*      */         case 156:
/*      */         case 157:
/*      */         case 158:
/*      */         case 159:
/*      */         case 160:
/*      */         case 161:
/*      */         case 162:
/*      */         case 163:
/*      */         case 164:
/*      */         case 165:
/*      */         case 166:
/*      */         case 167:
/*      */         case 168:
/*      */         case 198:
/*      */         case 199:
/* 1724 */           createLabel(bytecodeOffset + readShort(currentOffset + 1), labels);
/* 1725 */           currentOffset += 3;
/*      */           continue;
/*      */         case 202:
/*      */         case 203:
/*      */         case 204:
/*      */         case 205:
/*      */         case 206:
/*      */         case 207:
/*      */         case 208:
/*      */         case 209:
/*      */         case 210:
/*      */         case 211:
/*      */         case 212:
/*      */         case 213:
/*      */         case 214:
/*      */         case 215:
/*      */         case 216:
/*      */         case 217:
/*      */         case 218:
/*      */         case 219:
/* 1745 */           createLabel(bytecodeOffset + readUnsignedShort(currentOffset + 1), labels);
/* 1746 */           currentOffset += 3;
/*      */           continue;
/*      */         case 200:
/*      */         case 201:
/*      */         case 220:
/* 1751 */           createLabel(bytecodeOffset + readInt(currentOffset + 1), labels);
/* 1752 */           currentOffset += 5;
/*      */           continue;
/*      */         case 196:
/* 1755 */           switch (classBuffer[currentOffset + 1] & 0xFF) {
/*      */             case 21:
/*      */             case 22:
/*      */             case 23:
/*      */             case 24:
/*      */             case 25:
/*      */             case 54:
/*      */             case 55:
/*      */             case 56:
/*      */             case 57:
/*      */             case 58:
/*      */             case 169:
/* 1767 */               currentOffset += 4;
/*      */               continue;
/*      */             case 132:
/* 1770 */               currentOffset += 6;
/*      */               continue;
/*      */           } 
/* 1773 */           throw new IllegalArgumentException();
/*      */ 
/*      */ 
/*      */         
/*      */         case 170:
/* 1778 */           currentOffset += 4 - (bytecodeOffset & 0x3);
/*      */           
/* 1780 */           createLabel(bytecodeOffset + readInt(currentOffset), labels);
/* 1781 */           numTableEntries = readInt(currentOffset + 8) - readInt(currentOffset + 4) + 1;
/* 1782 */           currentOffset += 12;
/*      */           
/* 1784 */           while (numTableEntries-- > 0) {
/* 1785 */             createLabel(bytecodeOffset + readInt(currentOffset), labels);
/* 1786 */             currentOffset += 4;
/*      */           } 
/*      */           continue;
/*      */         
/*      */         case 171:
/* 1791 */           currentOffset += 4 - (bytecodeOffset & 0x3);
/*      */           
/* 1793 */           createLabel(bytecodeOffset + readInt(currentOffset), labels);
/* 1794 */           numSwitchCases = readInt(currentOffset + 4);
/* 1795 */           currentOffset += 8;
/*      */           
/* 1797 */           while (numSwitchCases-- > 0) {
/* 1798 */             createLabel(bytecodeOffset + readInt(currentOffset + 4), labels);
/* 1799 */             currentOffset += 8;
/*      */           } 
/*      */           continue;
/*      */         case 16:
/*      */         case 18:
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 54:
/*      */         case 55:
/*      */         case 56:
/*      */         case 57:
/*      */         case 58:
/*      */         case 169:
/*      */         case 188:
/* 1816 */           currentOffset += 2;
/*      */           continue;
/*      */         case 17:
/*      */         case 19:
/*      */         case 20:
/*      */         case 132:
/*      */         case 178:
/*      */         case 179:
/*      */         case 180:
/*      */         case 181:
/*      */         case 182:
/*      */         case 183:
/*      */         case 184:
/*      */         case 187:
/*      */         case 189:
/*      */         case 192:
/*      */         case 193:
/* 1833 */           currentOffset += 3;
/*      */           continue;
/*      */         case 185:
/*      */         case 186:
/* 1837 */           currentOffset += 5;
/*      */           continue;
/*      */         case 197:
/* 1840 */           currentOffset += 4;
/*      */           continue;
/*      */       } 
/* 1843 */       throw new IllegalArgumentException();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1849 */     int exceptionTableLength = readUnsignedShort(currentOffset);
/* 1850 */     currentOffset += 2;
/* 1851 */     while (exceptionTableLength-- > 0) {
/* 1852 */       Label start = createLabel(readUnsignedShort(currentOffset), labels);
/* 1853 */       Label end = createLabel(readUnsignedShort(currentOffset + 2), labels);
/* 1854 */       Label handler = createLabel(readUnsignedShort(currentOffset + 4), labels);
/* 1855 */       String catchType = readUTF8(this.cpInfoOffsets[readUnsignedShort(currentOffset + 6)], charBuffer);
/* 1856 */       currentOffset += 8;
/* 1857 */       methodVisitor.visitTryCatchBlock(start, end, handler, catchType);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1866 */     int stackMapFrameOffset = 0;
/*      */     
/* 1868 */     int stackMapTableEndOffset = 0;
/*      */     
/* 1870 */     boolean compressedFrames = true;
/*      */     
/* 1872 */     int localVariableTableOffset = 0;
/*      */     
/* 1874 */     int localVariableTypeTableOffset = 0;
/*      */ 
/*      */     
/* 1877 */     int[] visibleTypeAnnotationOffsets = null;
/*      */ 
/*      */     
/* 1880 */     int[] invisibleTypeAnnotationOffsets = null;
/*      */ 
/*      */     
/* 1883 */     Attribute attributes = null;
/*      */     
/* 1885 */     int attributesCount = readUnsignedShort(currentOffset);
/* 1886 */     currentOffset += 2;
/* 1887 */     while (attributesCount-- > 0) {
/*      */       
/* 1889 */       String attributeName = readUTF8(currentOffset, charBuffer);
/* 1890 */       int attributeLength = readInt(currentOffset + 2);
/* 1891 */       currentOffset += 6;
/* 1892 */       if ("LocalVariableTable".equals(attributeName)) {
/* 1893 */         if ((context.parsingOptions & 0x2) == 0) {
/* 1894 */           localVariableTableOffset = currentOffset;
/*      */           
/* 1896 */           int currentLocalVariableTableOffset = currentOffset;
/* 1897 */           int localVariableTableLength = readUnsignedShort(currentLocalVariableTableOffset);
/* 1898 */           currentLocalVariableTableOffset += 2;
/* 1899 */           while (localVariableTableLength-- > 0) {
/* 1900 */             int startPc = readUnsignedShort(currentLocalVariableTableOffset);
/* 1901 */             createDebugLabel(startPc, labels);
/* 1902 */             int length = readUnsignedShort(currentLocalVariableTableOffset + 2);
/* 1903 */             createDebugLabel(startPc + length, labels);
/*      */             
/* 1905 */             currentLocalVariableTableOffset += 10;
/*      */           } 
/*      */         } 
/* 1908 */       } else if ("LocalVariableTypeTable".equals(attributeName)) {
/* 1909 */         localVariableTypeTableOffset = currentOffset;
/*      */       
/*      */       }
/* 1912 */       else if ("LineNumberTable".equals(attributeName)) {
/* 1913 */         if ((context.parsingOptions & 0x2) == 0) {
/*      */           
/* 1915 */           int currentLineNumberTableOffset = currentOffset;
/* 1916 */           int lineNumberTableLength = readUnsignedShort(currentLineNumberTableOffset);
/* 1917 */           currentLineNumberTableOffset += 2;
/* 1918 */           while (lineNumberTableLength-- > 0) {
/* 1919 */             int startPc = readUnsignedShort(currentLineNumberTableOffset);
/* 1920 */             int lineNumber = readUnsignedShort(currentLineNumberTableOffset + 2);
/* 1921 */             currentLineNumberTableOffset += 4;
/* 1922 */             createDebugLabel(startPc, labels);
/* 1923 */             labels[startPc].addLineNumber(lineNumber);
/*      */           } 
/*      */         } 
/* 1926 */       } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
/*      */         
/* 1928 */         visibleTypeAnnotationOffsets = readTypeAnnotations(methodVisitor, context, currentOffset, true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/* 1935 */       else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
/*      */         
/* 1937 */         invisibleTypeAnnotationOffsets = readTypeAnnotations(methodVisitor, context, currentOffset, false);
/*      */       }
/* 1939 */       else if ("StackMapTable".equals(attributeName)) {
/* 1940 */         if ((context.parsingOptions & 0x4) == 0) {
/* 1941 */           stackMapFrameOffset = currentOffset + 2;
/* 1942 */           stackMapTableEndOffset = currentOffset + attributeLength;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/* 1953 */       else if ("StackMap".equals(attributeName)) {
/* 1954 */         if ((context.parsingOptions & 0x4) == 0) {
/* 1955 */           stackMapFrameOffset = currentOffset + 2;
/* 1956 */           stackMapTableEndOffset = currentOffset + attributeLength;
/* 1957 */           compressedFrames = false;
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 1965 */         Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset, attributeLength, charBuffer, codeOffset, labels);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1973 */         attribute.nextAttribute = attributes;
/* 1974 */         attributes = attribute;
/*      */       } 
/* 1976 */       currentOffset += attributeLength;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1981 */     boolean expandFrames = ((context.parsingOptions & 0x8) != 0);
/* 1982 */     if (stackMapFrameOffset != 0) {
/*      */ 
/*      */ 
/*      */       
/* 1986 */       context.currentFrameOffset = -1;
/* 1987 */       context.currentFrameType = 0;
/* 1988 */       context.currentFrameLocalCount = 0;
/* 1989 */       context.currentFrameLocalCountDelta = 0;
/* 1990 */       context.currentFrameLocalTypes = new Object[maxLocals];
/* 1991 */       context.currentFrameStackCount = 0;
/* 1992 */       context.currentFrameStackTypes = new Object[maxStack];
/* 1993 */       if (expandFrames) {
/* 1994 */         computeImplicitFrame(context);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2003 */       for (int offset = stackMapFrameOffset; offset < stackMapTableEndOffset - 2; offset++) {
/* 2004 */         if (classBuffer[offset] == 8) {
/* 2005 */           int potentialBytecodeOffset = readUnsignedShort(offset + 1);
/* 2006 */           if (potentialBytecodeOffset >= 0 && potentialBytecodeOffset < codeLength && (classBuffer[bytecodeStartOffset + potentialBytecodeOffset] & 0xFF) == 187)
/*      */           {
/*      */ 
/*      */             
/* 2010 */             createLabel(potentialBytecodeOffset, labels);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/* 2015 */     if (expandFrames && (context.parsingOptions & 0x100) != 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2022 */       methodVisitor.visitFrame(-1, maxLocals, null, 0, null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2030 */     int currentVisibleTypeAnnotationIndex = 0;
/*      */ 
/*      */     
/* 2033 */     int currentVisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, 0);
/*      */ 
/*      */     
/* 2036 */     int currentInvisibleTypeAnnotationIndex = 0;
/*      */ 
/*      */     
/* 2039 */     int currentInvisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, 0);
/*      */ 
/*      */     
/* 2042 */     boolean insertFrame = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2048 */     int wideJumpOpcodeDelta = ((context.parsingOptions & 0x100) == 0) ? 33 : 0;
/*      */     
/* 2050 */     currentOffset = bytecodeStartOffset;
/* 2051 */     while (currentOffset < bytecodeEndOffset) {
/* 2052 */       Label target, defaultLabel; int cpInfoOffset, low, numPairs, nameAndTypeCpInfoOffset, high, keys[]; String owner, name; Label[] table, values; String str1, descriptor; int i; String str2; int bootstrapMethodOffset; Handle handle; Object[] bootstrapMethodArguments; int j, currentBytecodeOffset = currentOffset - bytecodeStartOffset;
/*      */ 
/*      */       
/* 2055 */       Label currentLabel = labels[currentBytecodeOffset];
/* 2056 */       if (currentLabel != null) {
/* 2057 */         currentLabel.accept(methodVisitor, ((context.parsingOptions & 0x2) == 0));
/*      */       }
/*      */ 
/*      */       
/* 2061 */       while (stackMapFrameOffset != 0 && (context.currentFrameOffset == currentBytecodeOffset || context.currentFrameOffset == -1)) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2066 */         if (context.currentFrameOffset != -1) {
/* 2067 */           if (!compressedFrames || expandFrames) {
/* 2068 */             methodVisitor.visitFrame(-1, context.currentFrameLocalCount, context.currentFrameLocalTypes, context.currentFrameStackCount, context.currentFrameStackTypes);
/*      */ 
/*      */           
/*      */           }
/*      */           else {
/*      */ 
/*      */             
/* 2075 */             methodVisitor.visitFrame(context.currentFrameType, context.currentFrameLocalCountDelta, context.currentFrameLocalTypes, context.currentFrameStackCount, context.currentFrameStackTypes);
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2084 */           insertFrame = false;
/*      */         } 
/* 2086 */         if (stackMapFrameOffset < stackMapTableEndOffset) {
/*      */           
/* 2088 */           stackMapFrameOffset = readStackMapFrame(stackMapFrameOffset, compressedFrames, expandFrames, context); continue;
/*      */         } 
/* 2090 */         stackMapFrameOffset = 0;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2096 */       if (insertFrame) {
/* 2097 */         if ((context.parsingOptions & 0x8) != 0) {
/* 2098 */           methodVisitor.visitFrame(256, 0, null, 0, null);
/*      */         }
/* 2100 */         insertFrame = false;
/*      */       } 
/*      */ 
/*      */       
/* 2104 */       int opcode = classBuffer[currentOffset] & 0xFF;
/* 2105 */       switch (opcode) {
/*      */         case 0:
/*      */         case 1:
/*      */         case 2:
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 8:
/*      */         case 9:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*      */         case 13:
/*      */         case 14:
/*      */         case 15:
/*      */         case 46:
/*      */         case 47:
/*      */         case 48:
/*      */         case 49:
/*      */         case 50:
/*      */         case 51:
/*      */         case 52:
/*      */         case 53:
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/*      */         case 82:
/*      */         case 83:
/*      */         case 84:
/*      */         case 85:
/*      */         case 86:
/*      */         case 87:
/*      */         case 88:
/*      */         case 89:
/*      */         case 90:
/*      */         case 91:
/*      */         case 92:
/*      */         case 93:
/*      */         case 94:
/*      */         case 95:
/*      */         case 96:
/*      */         case 97:
/*      */         case 98:
/*      */         case 99:
/*      */         case 100:
/*      */         case 101:
/*      */         case 102:
/*      */         case 103:
/*      */         case 104:
/*      */         case 105:
/*      */         case 106:
/*      */         case 107:
/*      */         case 108:
/*      */         case 109:
/*      */         case 110:
/*      */         case 111:
/*      */         case 112:
/*      */         case 113:
/*      */         case 114:
/*      */         case 115:
/*      */         case 116:
/*      */         case 117:
/*      */         case 118:
/*      */         case 119:
/*      */         case 120:
/*      */         case 121:
/*      */         case 122:
/*      */         case 123:
/*      */         case 124:
/*      */         case 125:
/*      */         case 126:
/*      */         case 127:
/*      */         case 128:
/*      */         case 129:
/*      */         case 130:
/*      */         case 131:
/*      */         case 133:
/*      */         case 134:
/*      */         case 135:
/*      */         case 136:
/*      */         case 137:
/*      */         case 138:
/*      */         case 139:
/*      */         case 140:
/*      */         case 141:
/*      */         case 142:
/*      */         case 143:
/*      */         case 144:
/*      */         case 145:
/*      */         case 146:
/*      */         case 147:
/*      */         case 148:
/*      */         case 149:
/*      */         case 150:
/*      */         case 151:
/*      */         case 152:
/*      */         case 172:
/*      */         case 173:
/*      */         case 174:
/*      */         case 175:
/*      */         case 176:
/*      */         case 177:
/*      */         case 190:
/*      */         case 191:
/*      */         case 194:
/*      */         case 195:
/* 2213 */           methodVisitor.visitInsn(opcode);
/* 2214 */           currentOffset++;
/*      */           break;
/*      */         case 26:
/*      */         case 27:
/*      */         case 28:
/*      */         case 29:
/*      */         case 30:
/*      */         case 31:
/*      */         case 32:
/*      */         case 33:
/*      */         case 34:
/*      */         case 35:
/*      */         case 36:
/*      */         case 37:
/*      */         case 38:
/*      */         case 39:
/*      */         case 40:
/*      */         case 41:
/*      */         case 42:
/*      */         case 43:
/*      */         case 44:
/*      */         case 45:
/* 2236 */           opcode -= 26;
/* 2237 */           methodVisitor.visitVarInsn(21 + (opcode >> 2), opcode & 0x3);
/* 2238 */           currentOffset++;
/*      */           break;
/*      */         case 59:
/*      */         case 60:
/*      */         case 61:
/*      */         case 62:
/*      */         case 63:
/*      */         case 64:
/*      */         case 65:
/*      */         case 66:
/*      */         case 67:
/*      */         case 68:
/*      */         case 69:
/*      */         case 70:
/*      */         case 71:
/*      */         case 72:
/*      */         case 73:
/*      */         case 74:
/*      */         case 75:
/*      */         case 76:
/*      */         case 77:
/*      */         case 78:
/* 2260 */           opcode -= 59;
/* 2261 */           methodVisitor.visitVarInsn(54 + (opcode >> 2), opcode & 0x3);
/* 2262 */           currentOffset++;
/*      */           break;
/*      */         case 153:
/*      */         case 154:
/*      */         case 155:
/*      */         case 156:
/*      */         case 157:
/*      */         case 158:
/*      */         case 159:
/*      */         case 160:
/*      */         case 161:
/*      */         case 162:
/*      */         case 163:
/*      */         case 164:
/*      */         case 165:
/*      */         case 166:
/*      */         case 167:
/*      */         case 168:
/*      */         case 198:
/*      */         case 199:
/* 2282 */           methodVisitor.visitJumpInsn(opcode, labels[currentBytecodeOffset + 
/* 2283 */                 readShort(currentOffset + 1)]);
/* 2284 */           currentOffset += 3;
/*      */           break;
/*      */         case 200:
/*      */         case 201:
/* 2288 */           methodVisitor.visitJumpInsn(opcode - wideJumpOpcodeDelta, labels[currentBytecodeOffset + 
/*      */                 
/* 2290 */                 readInt(currentOffset + 1)]);
/* 2291 */           currentOffset += 5;
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 202:
/*      */         case 203:
/*      */         case 204:
/*      */         case 205:
/*      */         case 206:
/*      */         case 207:
/*      */         case 208:
/*      */         case 209:
/*      */         case 210:
/*      */         case 211:
/*      */         case 212:
/*      */         case 213:
/*      */         case 214:
/*      */         case 215:
/*      */         case 216:
/*      */         case 217:
/*      */         case 218:
/*      */         case 219:
/* 2321 */           opcode = (opcode < 218) ? (opcode - 49) : (opcode - 20);
/* 2322 */           target = labels[currentBytecodeOffset + readUnsignedShort(currentOffset + 1)];
/* 2323 */           if (opcode == 167 || opcode == 168) {
/*      */             
/* 2325 */             methodVisitor.visitJumpInsn(opcode + 33, target);
/*      */           
/*      */           }
/*      */           else {
/*      */             
/* 2330 */             opcode = (opcode < 167) ? ((opcode + 1 ^ 0x1) - 1) : (opcode ^ 0x1);
/* 2331 */             Label endif = createLabel(currentBytecodeOffset + 3, labels);
/* 2332 */             methodVisitor.visitJumpInsn(opcode, endif);
/* 2333 */             methodVisitor.visitJumpInsn(200, target);
/*      */ 
/*      */             
/* 2336 */             insertFrame = true;
/*      */           } 
/* 2338 */           currentOffset += 3;
/*      */           break;
/*      */ 
/*      */         
/*      */         case 220:
/* 2343 */           methodVisitor.visitJumpInsn(200, labels[currentBytecodeOffset + 
/* 2344 */                 readInt(currentOffset + 1)]);
/*      */ 
/*      */ 
/*      */           
/* 2348 */           insertFrame = true;
/* 2349 */           currentOffset += 5;
/*      */           break;
/*      */         case 196:
/* 2352 */           opcode = classBuffer[currentOffset + 1] & 0xFF;
/* 2353 */           if (opcode == 132) {
/* 2354 */             methodVisitor.visitIincInsn(
/* 2355 */                 readUnsignedShort(currentOffset + 2), readShort(currentOffset + 4));
/* 2356 */             currentOffset += 6; break;
/*      */           } 
/* 2358 */           methodVisitor.visitVarInsn(opcode, readUnsignedShort(currentOffset + 2));
/* 2359 */           currentOffset += 4;
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 170:
/* 2365 */           currentOffset += 4 - (currentBytecodeOffset & 0x3);
/*      */           
/* 2367 */           defaultLabel = labels[currentBytecodeOffset + readInt(currentOffset)];
/* 2368 */           low = readInt(currentOffset + 4);
/* 2369 */           high = readInt(currentOffset + 8);
/* 2370 */           currentOffset += 12;
/* 2371 */           table = new Label[high - low + 1];
/* 2372 */           for (i = 0; i < table.length; i++) {
/* 2373 */             table[i] = labels[currentBytecodeOffset + readInt(currentOffset)];
/* 2374 */             currentOffset += 4;
/*      */           } 
/* 2376 */           methodVisitor.visitTableSwitchInsn(low, high, defaultLabel, table);
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 171:
/* 2382 */           currentOffset += 4 - (currentBytecodeOffset & 0x3);
/*      */           
/* 2384 */           defaultLabel = labels[currentBytecodeOffset + readInt(currentOffset)];
/* 2385 */           numPairs = readInt(currentOffset + 4);
/* 2386 */           currentOffset += 8;
/* 2387 */           keys = new int[numPairs];
/* 2388 */           values = new Label[numPairs];
/* 2389 */           for (i = 0; i < numPairs; i++) {
/* 2390 */             keys[i] = readInt(currentOffset);
/* 2391 */             values[i] = labels[currentBytecodeOffset + readInt(currentOffset + 4)];
/* 2392 */             currentOffset += 8;
/*      */           } 
/* 2394 */           methodVisitor.visitLookupSwitchInsn(defaultLabel, keys, values);
/*      */           break;
/*      */         
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 54:
/*      */         case 55:
/*      */         case 56:
/*      */         case 57:
/*      */         case 58:
/*      */         case 169:
/* 2408 */           methodVisitor.visitVarInsn(opcode, classBuffer[currentOffset + 1] & 0xFF);
/* 2409 */           currentOffset += 2;
/*      */           break;
/*      */         case 16:
/*      */         case 188:
/* 2413 */           methodVisitor.visitIntInsn(opcode, classBuffer[currentOffset + 1]);
/* 2414 */           currentOffset += 2;
/*      */           break;
/*      */         case 17:
/* 2417 */           methodVisitor.visitIntInsn(opcode, readShort(currentOffset + 1));
/* 2418 */           currentOffset += 3;
/*      */           break;
/*      */         case 18:
/* 2421 */           methodVisitor.visitLdcInsn(readConst(classBuffer[currentOffset + 1] & 0xFF, charBuffer));
/* 2422 */           currentOffset += 2;
/*      */           break;
/*      */         case 19:
/*      */         case 20:
/* 2426 */           methodVisitor.visitLdcInsn(readConst(readUnsignedShort(currentOffset + 1), charBuffer));
/* 2427 */           currentOffset += 3;
/*      */           break;
/*      */         
/*      */         case 178:
/*      */         case 179:
/*      */         case 180:
/*      */         case 181:
/*      */         case 182:
/*      */         case 183:
/*      */         case 184:
/*      */         case 185:
/* 2438 */           cpInfoOffset = this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)];
/* 2439 */           nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
/* 2440 */           owner = readClass(cpInfoOffset, charBuffer);
/* 2441 */           str1 = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
/* 2442 */           str2 = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
/* 2443 */           if (opcode < 182) {
/* 2444 */             methodVisitor.visitFieldInsn(opcode, owner, str1, str2);
/*      */           } else {
/* 2446 */             boolean isInterface = (classBuffer[cpInfoOffset - 1] == 11);
/*      */             
/* 2448 */             methodVisitor.visitMethodInsn(opcode, owner, str1, str2, isInterface);
/*      */           } 
/* 2450 */           if (opcode == 185) {
/* 2451 */             currentOffset += 5; break;
/*      */           } 
/* 2453 */           currentOffset += 3;
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 186:
/* 2459 */           cpInfoOffset = this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)];
/* 2460 */           nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
/* 2461 */           name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
/* 2462 */           descriptor = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
/* 2463 */           bootstrapMethodOffset = this.bootstrapMethodOffsets[readUnsignedShort(cpInfoOffset)];
/*      */           
/* 2465 */           handle = (Handle)readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
/*      */           
/* 2467 */           bootstrapMethodArguments = new Object[readUnsignedShort(bootstrapMethodOffset + 2)];
/* 2468 */           bootstrapMethodOffset += 4;
/* 2469 */           for (j = 0; j < bootstrapMethodArguments.length; j++) {
/* 2470 */             bootstrapMethodArguments[j] = 
/* 2471 */               readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
/* 2472 */             bootstrapMethodOffset += 2;
/*      */           } 
/* 2474 */           methodVisitor.visitInvokeDynamicInsn(name, descriptor, handle, bootstrapMethodArguments);
/*      */           
/* 2476 */           currentOffset += 5;
/*      */           break;
/*      */         
/*      */         case 187:
/*      */         case 189:
/*      */         case 192:
/*      */         case 193:
/* 2483 */           methodVisitor.visitTypeInsn(opcode, readClass(currentOffset + 1, charBuffer));
/* 2484 */           currentOffset += 3;
/*      */           break;
/*      */         case 132:
/* 2487 */           methodVisitor.visitIincInsn(classBuffer[currentOffset + 1] & 0xFF, classBuffer[currentOffset + 2]);
/*      */           
/* 2489 */           currentOffset += 3;
/*      */           break;
/*      */         case 197:
/* 2492 */           methodVisitor.visitMultiANewArrayInsn(
/* 2493 */               readClass(currentOffset + 1, charBuffer), classBuffer[currentOffset + 3] & 0xFF);
/* 2494 */           currentOffset += 4;
/*      */           break;
/*      */         default:
/* 2497 */           throw new AssertionError();
/*      */       } 
/*      */ 
/*      */       
/* 2501 */       while (visibleTypeAnnotationOffsets != null && currentVisibleTypeAnnotationIndex < visibleTypeAnnotationOffsets.length && currentVisibleTypeAnnotationBytecodeOffset <= currentBytecodeOffset) {
/*      */ 
/*      */         
/* 2504 */         if (currentVisibleTypeAnnotationBytecodeOffset == currentBytecodeOffset) {
/*      */ 
/*      */           
/* 2507 */           int currentAnnotationOffset = readTypeAnnotationTarget(context, visibleTypeAnnotationOffsets[currentVisibleTypeAnnotationIndex]);
/*      */ 
/*      */           
/* 2510 */           String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 2511 */           currentAnnotationOffset += 2;
/*      */           
/* 2513 */           readElementValues(methodVisitor
/* 2514 */               .visitInsnAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2524 */         currentVisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, ++currentVisibleTypeAnnotationIndex);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2529 */       while (invisibleTypeAnnotationOffsets != null && currentInvisibleTypeAnnotationIndex < invisibleTypeAnnotationOffsets.length && currentInvisibleTypeAnnotationBytecodeOffset <= currentBytecodeOffset) {
/*      */ 
/*      */         
/* 2532 */         if (currentInvisibleTypeAnnotationBytecodeOffset == currentBytecodeOffset) {
/*      */ 
/*      */           
/* 2535 */           int currentAnnotationOffset = readTypeAnnotationTarget(context, invisibleTypeAnnotationOffsets[currentInvisibleTypeAnnotationIndex]);
/*      */ 
/*      */           
/* 2538 */           String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 2539 */           currentAnnotationOffset += 2;
/*      */           
/* 2541 */           readElementValues(methodVisitor
/* 2542 */               .visitInsnAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2552 */         currentInvisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, ++currentInvisibleTypeAnnotationIndex);
/*      */       } 
/*      */     } 
/*      */     
/* 2556 */     if (labels[codeLength] != null) {
/* 2557 */       methodVisitor.visitLabel(labels[codeLength]);
/*      */     }
/*      */ 
/*      */     
/* 2561 */     if (localVariableTableOffset != 0 && (context.parsingOptions & 0x2) == 0) {
/*      */       
/* 2563 */       int[] typeTable = null;
/* 2564 */       if (localVariableTypeTableOffset != 0) {
/* 2565 */         typeTable = new int[readUnsignedShort(localVariableTypeTableOffset) * 3];
/* 2566 */         currentOffset = localVariableTypeTableOffset + 2;
/* 2567 */         int typeTableIndex = typeTable.length;
/* 2568 */         while (typeTableIndex > 0) {
/*      */           
/* 2570 */           typeTable[--typeTableIndex] = currentOffset + 6;
/* 2571 */           typeTable[--typeTableIndex] = readUnsignedShort(currentOffset + 8);
/* 2572 */           typeTable[--typeTableIndex] = readUnsignedShort(currentOffset);
/* 2573 */           currentOffset += 10;
/*      */         } 
/*      */       } 
/* 2576 */       int localVariableTableLength = readUnsignedShort(localVariableTableOffset);
/* 2577 */       currentOffset = localVariableTableOffset + 2;
/* 2578 */       while (localVariableTableLength-- > 0) {
/* 2579 */         int startPc = readUnsignedShort(currentOffset);
/* 2580 */         int length = readUnsignedShort(currentOffset + 2);
/* 2581 */         String name = readUTF8(currentOffset + 4, charBuffer);
/* 2582 */         String descriptor = readUTF8(currentOffset + 6, charBuffer);
/* 2583 */         int index = readUnsignedShort(currentOffset + 8);
/* 2584 */         currentOffset += 10;
/* 2585 */         String signature = null;
/* 2586 */         if (typeTable != null) {
/* 2587 */           for (int i = 0; i < typeTable.length; i += 3) {
/* 2588 */             if (typeTable[i] == startPc && typeTable[i + 1] == index) {
/* 2589 */               signature = readUTF8(typeTable[i + 2], charBuffer);
/*      */               break;
/*      */             } 
/*      */           } 
/*      */         }
/* 2594 */         methodVisitor.visitLocalVariable(name, descriptor, signature, labels[startPc], labels[startPc + length], index);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2600 */     if (visibleTypeAnnotationOffsets != null) {
/* 2601 */       for (int typeAnnotationOffset : visibleTypeAnnotationOffsets) {
/* 2602 */         int targetType = readByte(typeAnnotationOffset);
/* 2603 */         if (targetType == 64 || targetType == 65) {
/*      */ 
/*      */           
/* 2606 */           currentOffset = readTypeAnnotationTarget(context, typeAnnotationOffset);
/*      */           
/* 2608 */           String annotationDescriptor = readUTF8(currentOffset, charBuffer);
/* 2609 */           currentOffset += 2;
/*      */           
/* 2611 */           readElementValues(methodVisitor
/* 2612 */               .visitLocalVariableAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, context.currentLocalVariableAnnotationRangeStarts, context.currentLocalVariableAnnotationRangeEnds, context.currentLocalVariableAnnotationRangeIndices, annotationDescriptor, true), currentOffset, true, charBuffer);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2628 */     if (invisibleTypeAnnotationOffsets != null) {
/* 2629 */       for (int typeAnnotationOffset : invisibleTypeAnnotationOffsets) {
/* 2630 */         int targetType = readByte(typeAnnotationOffset);
/* 2631 */         if (targetType == 64 || targetType == 65) {
/*      */ 
/*      */           
/* 2634 */           currentOffset = readTypeAnnotationTarget(context, typeAnnotationOffset);
/*      */           
/* 2636 */           String annotationDescriptor = readUTF8(currentOffset, charBuffer);
/* 2637 */           currentOffset += 2;
/*      */           
/* 2639 */           readElementValues(methodVisitor
/* 2640 */               .visitLocalVariableAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, context.currentLocalVariableAnnotationRangeStarts, context.currentLocalVariableAnnotationRangeEnds, context.currentLocalVariableAnnotationRangeIndices, annotationDescriptor, false), currentOffset, true, charBuffer);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2656 */     while (attributes != null) {
/*      */       
/* 2658 */       Attribute nextAttribute = attributes.nextAttribute;
/* 2659 */       attributes.nextAttribute = null;
/* 2660 */       methodVisitor.visitAttribute(attributes);
/* 2661 */       attributes = nextAttribute;
/*      */     } 
/*      */ 
/*      */     
/* 2665 */     methodVisitor.visitMaxs(maxStack, maxLocals);
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
/*      */   protected Label readLabel(int bytecodeOffset, Label[] labels) {
/* 2679 */     if (labels[bytecodeOffset] == null) {
/* 2680 */       labels[bytecodeOffset] = new Label();
/*      */     }
/* 2682 */     return labels[bytecodeOffset];
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
/*      */   private Label createLabel(int bytecodeOffset, Label[] labels) {
/* 2695 */     Label label = readLabel(bytecodeOffset, labels);
/* 2696 */     label.flags = (short)(label.flags & 0xFFFFFFFE);
/* 2697 */     return label;
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
/*      */   private void createDebugLabel(int bytecodeOffset, Label[] labels) {
/* 2709 */     if (labels[bytecodeOffset] == null) {
/* 2710 */       (readLabel(bytecodeOffset, labels)).flags = (short)((readLabel(bytecodeOffset, labels)).flags | 0x1);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] readTypeAnnotations(MethodVisitor methodVisitor, Context context, int runtimeTypeAnnotationsOffset, boolean visible) {
/* 2737 */     char[] charBuffer = context.charBuffer;
/* 2738 */     int currentOffset = runtimeTypeAnnotationsOffset;
/*      */     
/* 2740 */     int[] typeAnnotationsOffsets = new int[readUnsignedShort(currentOffset)];
/* 2741 */     currentOffset += 2;
/*      */     
/* 2743 */     for (int i = 0; i < typeAnnotationsOffsets.length; i++) {
/* 2744 */       int tableLength; typeAnnotationsOffsets[i] = currentOffset;
/*      */ 
/*      */       
/* 2747 */       int targetType = readInt(currentOffset);
/* 2748 */       switch (targetType >>> 24) {
/*      */ 
/*      */         
/*      */         case 64:
/*      */         case 65:
/* 2753 */           tableLength = readUnsignedShort(currentOffset + 1);
/* 2754 */           currentOffset += 3;
/* 2755 */           while (tableLength-- > 0) {
/* 2756 */             int startPc = readUnsignedShort(currentOffset);
/* 2757 */             int length = readUnsignedShort(currentOffset + 2);
/*      */             
/* 2759 */             currentOffset += 6;
/* 2760 */             createLabel(startPc, context.currentMethodLabels);
/* 2761 */             createLabel(startPc + length, context.currentMethodLabels);
/*      */           } 
/*      */           break;
/*      */         case 71:
/*      */         case 72:
/*      */         case 73:
/*      */         case 74:
/*      */         case 75:
/* 2769 */           currentOffset += 4;
/*      */           break;
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         case 23:
/*      */         case 66:
/*      */         case 67:
/*      */         case 68:
/*      */         case 69:
/*      */         case 70:
/* 2780 */           currentOffset += 3;
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         default:
/* 2790 */           throw new IllegalArgumentException();
/*      */       } 
/*      */ 
/*      */       
/* 2794 */       int pathLength = readByte(currentOffset);
/* 2795 */       if (targetType >>> 24 == 66) {
/*      */         
/* 2797 */         TypePath path = (pathLength == 0) ? null : new TypePath(this.classFileBuffer, currentOffset);
/* 2798 */         currentOffset += 1 + 2 * pathLength;
/*      */         
/* 2800 */         String annotationDescriptor = readUTF8(currentOffset, charBuffer);
/* 2801 */         currentOffset += 2;
/*      */ 
/*      */         
/* 2804 */         currentOffset = readElementValues(methodVisitor
/* 2805 */             .visitTryCatchAnnotation(targetType & 0xFFFFFF00, path, annotationDescriptor, visible), currentOffset, true, charBuffer);
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */ 
/*      */         
/* 2814 */         currentOffset += 3 + 2 * pathLength;
/*      */ 
/*      */ 
/*      */         
/* 2818 */         currentOffset = readElementValues(null, currentOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */     
/* 2822 */     return typeAnnotationsOffsets;
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
/*      */   private int getTypeAnnotationBytecodeOffset(int[] typeAnnotationOffsets, int typeAnnotationIndex) {
/* 2837 */     if (typeAnnotationOffsets == null || typeAnnotationIndex >= typeAnnotationOffsets.length || 
/*      */       
/* 2839 */       readByte(typeAnnotationOffsets[typeAnnotationIndex]) < 67) {
/* 2840 */       return -1;
/*      */     }
/* 2842 */     return readUnsignedShort(typeAnnotationOffsets[typeAnnotationIndex] + 1);
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
/*      */   private int readTypeAnnotationTarget(Context context, int typeAnnotationOffset) {
/* 2856 */     int tableLength, i, currentOffset = typeAnnotationOffset;
/*      */     
/* 2858 */     int targetType = readInt(typeAnnotationOffset);
/* 2859 */     switch (targetType >>> 24) {
/*      */       case 0:
/*      */       case 1:
/*      */       case 22:
/* 2863 */         targetType &= 0xFFFF0000;
/* 2864 */         currentOffset += 2;
/*      */         break;
/*      */       case 19:
/*      */       case 20:
/*      */       case 21:
/* 2869 */         targetType &= 0xFF000000;
/* 2870 */         currentOffset++;
/*      */         break;
/*      */       case 64:
/*      */       case 65:
/* 2874 */         targetType &= 0xFF000000;
/* 2875 */         tableLength = readUnsignedShort(currentOffset + 1);
/* 2876 */         currentOffset += 3;
/* 2877 */         context.currentLocalVariableAnnotationRangeStarts = new Label[tableLength];
/* 2878 */         context.currentLocalVariableAnnotationRangeEnds = new Label[tableLength];
/* 2879 */         context.currentLocalVariableAnnotationRangeIndices = new int[tableLength];
/* 2880 */         for (i = 0; i < tableLength; i++) {
/* 2881 */           int startPc = readUnsignedShort(currentOffset);
/* 2882 */           int length = readUnsignedShort(currentOffset + 2);
/* 2883 */           int index = readUnsignedShort(currentOffset + 4);
/* 2884 */           currentOffset += 6;
/* 2885 */           context.currentLocalVariableAnnotationRangeStarts[i] = 
/* 2886 */             createLabel(startPc, context.currentMethodLabels);
/* 2887 */           context.currentLocalVariableAnnotationRangeEnds[i] = 
/* 2888 */             createLabel(startPc + length, context.currentMethodLabels);
/* 2889 */           context.currentLocalVariableAnnotationRangeIndices[i] = index;
/*      */         } 
/*      */         break;
/*      */       case 71:
/*      */       case 72:
/*      */       case 73:
/*      */       case 74:
/*      */       case 75:
/* 2897 */         targetType &= 0xFF0000FF;
/* 2898 */         currentOffset += 4;
/*      */         break;
/*      */       case 16:
/*      */       case 17:
/*      */       case 18:
/*      */       case 23:
/*      */       case 66:
/* 2905 */         targetType &= 0xFFFFFF00;
/* 2906 */         currentOffset += 3;
/*      */         break;
/*      */       case 67:
/*      */       case 68:
/*      */       case 69:
/*      */       case 70:
/* 2912 */         targetType &= 0xFF000000;
/* 2913 */         currentOffset += 3;
/*      */         break;
/*      */       default:
/* 2916 */         throw new IllegalArgumentException();
/*      */     } 
/* 2918 */     context.currentTypeAnnotationTarget = targetType;
/*      */     
/* 2920 */     int pathLength = readByte(currentOffset);
/* 2921 */     context
/* 2922 */       .currentTypeAnnotationTargetPath = (pathLength == 0) ? null : new TypePath(this.classFileBuffer, currentOffset);
/*      */     
/* 2924 */     return currentOffset + 1 + 2 * pathLength;
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
/*      */   private void readParameterAnnotations(MethodVisitor methodVisitor, Context context, int runtimeParameterAnnotationsOffset, boolean visible) {
/* 2943 */     int currentOffset = runtimeParameterAnnotationsOffset;
/* 2944 */     int numParameters = this.classFileBuffer[currentOffset++] & 0xFF;
/* 2945 */     methodVisitor.visitAnnotableParameterCount(numParameters, visible);
/* 2946 */     char[] charBuffer = context.charBuffer;
/* 2947 */     for (int i = 0; i < numParameters; i++) {
/* 2948 */       int numAnnotations = readUnsignedShort(currentOffset);
/* 2949 */       currentOffset += 2;
/* 2950 */       while (numAnnotations-- > 0) {
/*      */         
/* 2952 */         String annotationDescriptor = readUTF8(currentOffset, charBuffer);
/* 2953 */         currentOffset += 2;
/*      */ 
/*      */         
/* 2956 */         currentOffset = readElementValues(methodVisitor
/* 2957 */             .visitParameterAnnotation(i, annotationDescriptor, visible), currentOffset, true, charBuffer);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int readElementValues(AnnotationVisitor annotationVisitor, int annotationOffset, boolean named, char[] charBuffer) {
/* 2984 */     int currentOffset = annotationOffset;
/*      */     
/* 2986 */     int numElementValuePairs = readUnsignedShort(currentOffset);
/* 2987 */     currentOffset += 2;
/* 2988 */     if (named) {
/*      */       
/* 2990 */       while (numElementValuePairs-- > 0) {
/* 2991 */         String elementName = readUTF8(currentOffset, charBuffer);
/*      */         
/* 2993 */         currentOffset = readElementValue(annotationVisitor, currentOffset + 2, elementName, charBuffer);
/*      */       } 
/*      */     } else {
/*      */       
/* 2997 */       while (numElementValuePairs-- > 0)
/*      */       {
/* 2999 */         currentOffset = readElementValue(annotationVisitor, currentOffset, null, charBuffer);
/*      */       }
/*      */     } 
/* 3002 */     if (annotationVisitor != null) {
/* 3003 */       annotationVisitor.visitEnd();
/*      */     }
/* 3005 */     return currentOffset;
/*      */   }
/*      */   
/*      */   private int readElementValue(AnnotationVisitor annotationVisitor, int elementValueOffset, String elementName, char[] charBuffer) {
/*      */     int numValues;
/*      */     byte[] byteValues;
/*      */     int i;
/*      */     boolean[] booleanValues;
/*      */     int j;
/*      */     short[] shortValues;
/*      */     int k;
/*      */     char[] charValues;
/*      */     int m, intValues[], n;
/*      */     long[] longValues;
/*      */     int i1;
/*      */     float[] floatValues;
/*      */     int i2;
/*      */     double[] doubleValues;
/* 3023 */     int i3, currentOffset = elementValueOffset;
/* 3024 */     if (annotationVisitor == null) {
/* 3025 */       switch (this.classFileBuffer[currentOffset] & 0xFF) {
/*      */         case 101:
/* 3027 */           return currentOffset + 5;
/*      */         case 64:
/* 3029 */           return readElementValues(null, currentOffset + 3, true, charBuffer);
/*      */         case 91:
/* 3031 */           return readElementValues(null, currentOffset + 1, false, charBuffer);
/*      */       } 
/* 3033 */       return currentOffset + 3;
/*      */     } 
/*      */     
/* 3036 */     switch (this.classFileBuffer[currentOffset++] & 0xFF) {
/*      */       case 66:
/* 3038 */         annotationVisitor.visit(elementName, 
/* 3039 */             Byte.valueOf((byte)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset)])));
/* 3040 */         currentOffset += 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3184 */         return currentOffset;case 67: annotationVisitor.visit(elementName, Character.valueOf((char)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset)]))); currentOffset += 2; return currentOffset;case 68: case 70: case 73: case 74: annotationVisitor.visit(elementName, readConst(readUnsignedShort(currentOffset), charBuffer)); currentOffset += 2; return currentOffset;case 83: annotationVisitor.visit(elementName, Short.valueOf((short)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset)]))); currentOffset += 2; return currentOffset;case 90: annotationVisitor.visit(elementName, (readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset)]) == 0) ? Boolean.FALSE : Boolean.TRUE); currentOffset += 2; return currentOffset;case 115: annotationVisitor.visit(elementName, readUTF8(currentOffset, charBuffer)); currentOffset += 2; return currentOffset;case 101: annotationVisitor.visitEnum(elementName, readUTF8(currentOffset, charBuffer), readUTF8(currentOffset + 2, charBuffer)); currentOffset += 4; return currentOffset;case 99: annotationVisitor.visit(elementName, Type.getType(readUTF8(currentOffset, charBuffer))); currentOffset += 2; return currentOffset;case 64: currentOffset = readElementValues(annotationVisitor.visitAnnotation(elementName, readUTF8(currentOffset, charBuffer)), currentOffset + 2, true, charBuffer); return currentOffset;case 91: numValues = readUnsignedShort(currentOffset); currentOffset += 2; if (numValues == 0) return readElementValues(annotationVisitor.visitArray(elementName), currentOffset - 2, false, charBuffer);  switch (this.classFileBuffer[currentOffset] & 0xFF) { case 66: byteValues = new byte[numValues]; for (i = 0; i < numValues; i++) { byteValues[i] = (byte)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, byteValues); return currentOffset;case 90: booleanValues = new boolean[numValues]; for (j = 0; j < numValues; j++) { booleanValues[j] = (readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]) != 0); currentOffset += 3; }  annotationVisitor.visit(elementName, booleanValues); return currentOffset;case 83: shortValues = new short[numValues]; for (k = 0; k < numValues; k++) { shortValues[k] = (short)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, shortValues); return currentOffset;case 67: charValues = new char[numValues]; for (m = 0; m < numValues; m++) { charValues[m] = (char)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, charValues); return currentOffset;case 73: intValues = new int[numValues]; for (n = 0; n < numValues; n++) { intValues[n] = readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, intValues); return currentOffset;case 74: longValues = new long[numValues]; for (i1 = 0; i1 < numValues; i1++) { longValues[i1] = readLong(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, longValues); return currentOffset;case 70: floatValues = new float[numValues]; for (i2 = 0; i2 < numValues; i2++) { floatValues[i2] = Float.intBitsToFloat(readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)])); currentOffset += 3; }  annotationVisitor.visit(elementName, floatValues); return currentOffset;case 68: doubleValues = new double[numValues]; for (i3 = 0; i3 < numValues; i3++) { doubleValues[i3] = Double.longBitsToDouble(readLong(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)])); currentOffset += 3; }  annotationVisitor.visit(elementName, doubleValues); return currentOffset; }  currentOffset = readElementValues(annotationVisitor.visitArray(elementName), currentOffset - 2, false, charBuffer); return currentOffset;
/*      */     } 
/*      */     throw new IllegalArgumentException();
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
/*      */   private void computeImplicitFrame(Context context) {
/* 3198 */     String methodDescriptor = context.currentMethodDescriptor;
/* 3199 */     Object[] locals = context.currentFrameLocalTypes;
/* 3200 */     int numLocal = 0;
/* 3201 */     if ((context.currentMethodAccessFlags & 0x8) == 0) {
/* 3202 */       if ("<init>".equals(context.currentMethodName)) {
/* 3203 */         locals[numLocal++] = Opcodes.UNINITIALIZED_THIS;
/*      */       } else {
/* 3205 */         locals[numLocal++] = readClass(this.header + 2, context.charBuffer);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 3210 */     int currentMethodDescritorOffset = 1;
/*      */     while (true) {
/* 3212 */       int currentArgumentDescriptorStartOffset = currentMethodDescritorOffset;
/* 3213 */       switch (methodDescriptor.charAt(currentMethodDescritorOffset++)) {
/*      */         case 'B':
/*      */         case 'C':
/*      */         case 'I':
/*      */         case 'S':
/*      */         case 'Z':
/* 3219 */           locals[numLocal++] = Opcodes.INTEGER;
/*      */           continue;
/*      */         case 'F':
/* 3222 */           locals[numLocal++] = Opcodes.FLOAT;
/*      */           continue;
/*      */         case 'J':
/* 3225 */           locals[numLocal++] = Opcodes.LONG;
/*      */           continue;
/*      */         case 'D':
/* 3228 */           locals[numLocal++] = Opcodes.DOUBLE;
/*      */           continue;
/*      */         case '[':
/* 3231 */           while (methodDescriptor.charAt(currentMethodDescritorOffset) == '[') {
/* 3232 */             currentMethodDescritorOffset++;
/*      */           }
/* 3234 */           if (methodDescriptor.charAt(currentMethodDescritorOffset) == 'L') {
/* 3235 */             currentMethodDescritorOffset++;
/* 3236 */             while (methodDescriptor.charAt(currentMethodDescritorOffset) != ';') {
/* 3237 */               currentMethodDescritorOffset++;
/*      */             }
/*      */           } 
/* 3240 */           locals[numLocal++] = methodDescriptor
/* 3241 */             .substring(currentArgumentDescriptorStartOffset, ++currentMethodDescritorOffset);
/*      */           continue;
/*      */         
/*      */         case 'L':
/* 3245 */           while (methodDescriptor.charAt(currentMethodDescritorOffset) != ';') {
/* 3246 */             currentMethodDescritorOffset++;
/*      */           }
/* 3248 */           locals[numLocal++] = methodDescriptor
/* 3249 */             .substring(currentArgumentDescriptorStartOffset + 1, currentMethodDescritorOffset++); continue;
/*      */       } 
/*      */       break;
/*      */     } 
/* 3253 */     context.currentFrameLocalCount = numLocal;
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
/*      */   private int readStackMapFrame(int stackMapFrameOffset, boolean compressed, boolean expand, Context context) {
/* 3278 */     int frameType, offsetDelta, currentOffset = stackMapFrameOffset;
/* 3279 */     char[] charBuffer = context.charBuffer;
/* 3280 */     Label[] labels = context.currentMethodLabels;
/*      */     
/* 3282 */     if (compressed) {
/*      */       
/* 3284 */       frameType = this.classFileBuffer[currentOffset++] & 0xFF;
/*      */     } else {
/* 3286 */       frameType = 255;
/* 3287 */       context.currentFrameOffset = -1;
/*      */     } 
/*      */     
/* 3290 */     context.currentFrameLocalCountDelta = 0;
/* 3291 */     if (frameType < 64) {
/* 3292 */       offsetDelta = frameType;
/* 3293 */       context.currentFrameType = 3;
/* 3294 */       context.currentFrameStackCount = 0;
/* 3295 */     } else if (frameType < 128) {
/* 3296 */       offsetDelta = frameType - 64;
/*      */       
/* 3298 */       currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
/*      */       
/* 3300 */       context.currentFrameType = 4;
/* 3301 */       context.currentFrameStackCount = 1;
/* 3302 */     } else if (frameType >= 247) {
/* 3303 */       offsetDelta = readUnsignedShort(currentOffset);
/* 3304 */       currentOffset += 2;
/* 3305 */       if (frameType == 247) {
/*      */         
/* 3307 */         currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
/*      */         
/* 3309 */         context.currentFrameType = 4;
/* 3310 */         context.currentFrameStackCount = 1;
/* 3311 */       } else if (frameType >= 248 && frameType < 251) {
/* 3312 */         context.currentFrameType = 2;
/* 3313 */         context.currentFrameLocalCountDelta = 251 - frameType;
/* 3314 */         context.currentFrameLocalCount -= context.currentFrameLocalCountDelta;
/* 3315 */         context.currentFrameStackCount = 0;
/* 3316 */       } else if (frameType == 251) {
/* 3317 */         context.currentFrameType = 3;
/* 3318 */         context.currentFrameStackCount = 0;
/* 3319 */       } else if (frameType < 255) {
/* 3320 */         int local = expand ? context.currentFrameLocalCount : 0;
/* 3321 */         for (int k = frameType - 251; k > 0; k--)
/*      */         {
/* 3323 */           currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameLocalTypes, local++, charBuffer, labels);
/*      */         }
/*      */         
/* 3326 */         context.currentFrameType = 1;
/* 3327 */         context.currentFrameLocalCountDelta = frameType - 251;
/* 3328 */         context.currentFrameLocalCount += context.currentFrameLocalCountDelta;
/* 3329 */         context.currentFrameStackCount = 0;
/*      */       } else {
/* 3331 */         int numberOfLocals = readUnsignedShort(currentOffset);
/* 3332 */         currentOffset += 2;
/* 3333 */         context.currentFrameType = 0;
/* 3334 */         context.currentFrameLocalCountDelta = numberOfLocals;
/* 3335 */         context.currentFrameLocalCount = numberOfLocals;
/* 3336 */         for (int local = 0; local < numberOfLocals; local++)
/*      */         {
/* 3338 */           currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameLocalTypes, local, charBuffer, labels);
/*      */         }
/*      */         
/* 3341 */         int numberOfStackItems = readUnsignedShort(currentOffset);
/* 3342 */         currentOffset += 2;
/* 3343 */         context.currentFrameStackCount = numberOfStackItems;
/* 3344 */         for (int stack = 0; stack < numberOfStackItems; stack++)
/*      */         {
/* 3346 */           currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, stack, charBuffer, labels);
/*      */         }
/*      */       } 
/*      */     } else {
/*      */       
/* 3351 */       throw new IllegalArgumentException();
/*      */     } 
/* 3353 */     context.currentFrameOffset += offsetDelta + 1;
/* 3354 */     createLabel(context.currentFrameOffset, labels);
/* 3355 */     return currentOffset;
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
/*      */   private int readVerificationTypeInfo(int verificationTypeInfoOffset, Object[] frame, int index, char[] charBuffer, Label[] labels) {
/* 3378 */     int currentOffset = verificationTypeInfoOffset;
/* 3379 */     int tag = this.classFileBuffer[currentOffset++] & 0xFF;
/* 3380 */     switch (tag) {
/*      */       case 0:
/* 3382 */         frame[index] = Opcodes.TOP;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3413 */         return currentOffset;case 1: frame[index] = Opcodes.INTEGER; return currentOffset;case 2: frame[index] = Opcodes.FLOAT; return currentOffset;case 3: frame[index] = Opcodes.DOUBLE; return currentOffset;case 4: frame[index] = Opcodes.LONG; return currentOffset;case 5: frame[index] = Opcodes.NULL; return currentOffset;case 6: frame[index] = Opcodes.UNINITIALIZED_THIS; return currentOffset;case 7: frame[index] = readClass(currentOffset, charBuffer); currentOffset += 2; return currentOffset;case 8: frame[index] = createLabel(readUnsignedShort(currentOffset), labels); currentOffset += 2; return currentOffset;
/*      */     } 
/*      */     throw new IllegalArgumentException();
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
/*      */   final int getFirstAttributeOffset() {
/* 3430 */     int currentOffset = this.header + 8 + readUnsignedShort(this.header + 6) * 2;
/*      */ 
/*      */     
/* 3433 */     int fieldsCount = readUnsignedShort(currentOffset);
/* 3434 */     currentOffset += 2;
/*      */     
/* 3436 */     while (fieldsCount-- > 0) {
/*      */ 
/*      */ 
/*      */       
/* 3440 */       int attributesCount = readUnsignedShort(currentOffset + 6);
/* 3441 */       currentOffset += 8;
/*      */       
/* 3443 */       while (attributesCount-- > 0)
/*      */       {
/*      */ 
/*      */ 
/*      */         
/* 3448 */         currentOffset += 6 + readInt(currentOffset + 2);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 3453 */     int methodsCount = readUnsignedShort(currentOffset);
/* 3454 */     currentOffset += 2;
/* 3455 */     while (methodsCount-- > 0) {
/* 3456 */       int attributesCount = readUnsignedShort(currentOffset + 6);
/* 3457 */       currentOffset += 8;
/* 3458 */       while (attributesCount-- > 0) {
/* 3459 */         currentOffset += 6 + readInt(currentOffset + 2);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 3464 */     return currentOffset + 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] readBootstrapMethodsAttribute(int maxStringLength) {
/* 3475 */     char[] charBuffer = new char[maxStringLength];
/* 3476 */     int currentAttributeOffset = getFirstAttributeOffset();
/* 3477 */     for (int i = readUnsignedShort(currentAttributeOffset - 2); i > 0; i--) {
/*      */       
/* 3479 */       String attributeName = readUTF8(currentAttributeOffset, charBuffer);
/* 3480 */       int attributeLength = readInt(currentAttributeOffset + 2);
/* 3481 */       currentAttributeOffset += 6;
/* 3482 */       if ("BootstrapMethods".equals(attributeName)) {
/*      */         
/* 3484 */         int[] result = new int[readUnsignedShort(currentAttributeOffset)];
/*      */         
/* 3486 */         int currentBootstrapMethodOffset = currentAttributeOffset + 2;
/* 3487 */         for (int j = 0; j < result.length; j++) {
/* 3488 */           result[j] = currentBootstrapMethodOffset;
/*      */ 
/*      */           
/* 3491 */           currentBootstrapMethodOffset += 4 + 
/* 3492 */             readUnsignedShort(currentBootstrapMethodOffset + 2) * 2;
/*      */         } 
/* 3494 */         return result;
/*      */       } 
/* 3496 */       currentAttributeOffset += attributeLength;
/*      */     } 
/* 3498 */     throw new IllegalArgumentException();
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
/*      */ 
/*      */ 
/*      */   
/*      */   private Attribute readAttribute(Attribute[] attributePrototypes, String type, int offset, int length, char[] charBuffer, int codeAttributeOffset, Label[] labels) {
/* 3529 */     for (Attribute attributePrototype : attributePrototypes) {
/* 3530 */       if (attributePrototype.type.equals(type)) {
/* 3531 */         return attributePrototype.read(this, offset, length, charBuffer, codeAttributeOffset, labels);
/*      */       }
/*      */     } 
/*      */     
/* 3535 */     return (new Attribute(type)).read(this, offset, length, null, -1, null);
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
/*      */   public int getItemCount() {
/* 3548 */     return this.cpInfoOffsets.length;
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
/*      */   public int getItem(int constantPoolEntryIndex) {
/* 3562 */     return this.cpInfoOffsets[constantPoolEntryIndex];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxStringLength() {
/* 3573 */     return this.maxStringLength;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readByte(int offset) {
/* 3584 */     return this.classFileBuffer[offset] & 0xFF;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readUnsignedShort(int offset) {
/* 3595 */     byte[] classBuffer = this.classFileBuffer;
/* 3596 */     return (classBuffer[offset] & 0xFF) << 8 | classBuffer[offset + 1] & 0xFF;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public short readShort(int offset) {
/* 3607 */     byte[] classBuffer = this.classFileBuffer;
/* 3608 */     return (short)((classBuffer[offset] & 0xFF) << 8 | classBuffer[offset + 1] & 0xFF);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readInt(int offset) {
/* 3619 */     byte[] classBuffer = this.classFileBuffer;
/* 3620 */     return (classBuffer[offset] & 0xFF) << 24 | (classBuffer[offset + 1] & 0xFF) << 16 | (classBuffer[offset + 2] & 0xFF) << 8 | classBuffer[offset + 3] & 0xFF;
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
/*      */   public long readLong(int offset) {
/* 3634 */     long l1 = readInt(offset);
/* 3635 */     long l0 = readInt(offset + 4) & 0xFFFFFFFFL;
/* 3636 */     return l1 << 32L | l0;
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
/*      */   public String readUTF8(int offset, char[] charBuffer) {
/* 3652 */     int constantPoolEntryIndex = readUnsignedShort(offset);
/* 3653 */     if (offset == 0 || constantPoolEntryIndex == 0) {
/* 3654 */       return null;
/*      */     }
/* 3656 */     return readUtf(constantPoolEntryIndex, charBuffer);
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
/*      */   final String readUtf(int constantPoolEntryIndex, char[] charBuffer) {
/* 3669 */     String value = this.constantUtf8Values[constantPoolEntryIndex];
/* 3670 */     if (value != null) {
/* 3671 */       return value;
/*      */     }
/* 3673 */     int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
/* 3674 */     this.constantUtf8Values[constantPoolEntryIndex] = 
/* 3675 */       readUtf(cpInfoOffset + 2, readUnsignedShort(cpInfoOffset), charBuffer); return readUtf(cpInfoOffset + 2, readUnsignedShort(cpInfoOffset), charBuffer);
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
/*      */   private String readUtf(int utfOffset, int utfLength, char[] charBuffer) {
/* 3688 */     int currentOffset = utfOffset;
/* 3689 */     int endOffset = currentOffset + utfLength;
/* 3690 */     int strLength = 0;
/* 3691 */     byte[] classBuffer = this.classFileBuffer;
/* 3692 */     while (currentOffset < endOffset) {
/* 3693 */       int currentByte = classBuffer[currentOffset++];
/* 3694 */       if ((currentByte & 0x80) == 0) {
/* 3695 */         charBuffer[strLength++] = (char)(currentByte & 0x7F); continue;
/* 3696 */       }  if ((currentByte & 0xE0) == 192) {
/* 3697 */         charBuffer[strLength++] = (char)(((currentByte & 0x1F) << 6) + (classBuffer[currentOffset++] & 0x3F));
/*      */         continue;
/*      */       } 
/* 3700 */       charBuffer[strLength++] = (char)(((currentByte & 0xF) << 12) + ((classBuffer[currentOffset++] & 0x3F) << 6) + (classBuffer[currentOffset++] & 0x3F));
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3707 */     return new String(charBuffer, 0, strLength);
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
/*      */   private String readStringish(int offset, char[] charBuffer) {
/* 3726 */     return readUTF8(this.cpInfoOffsets[readUnsignedShort(offset)], charBuffer);
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
/*      */   public String readClass(int offset, char[] charBuffer) {
/* 3741 */     return readStringish(offset, charBuffer);
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
/*      */   public String readModule(int offset, char[] charBuffer) {
/* 3756 */     return readStringish(offset, charBuffer);
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
/*      */   public String readPackage(int offset, char[] charBuffer) {
/* 3771 */     return readStringish(offset, charBuffer);
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
/*      */   private ConstantDynamic readConstantDynamic(int constantPoolEntryIndex, char[] charBuffer) {
/* 3785 */     ConstantDynamic constantDynamic = this.constantDynamicValues[constantPoolEntryIndex];
/* 3786 */     if (constantDynamic != null) {
/* 3787 */       return constantDynamic;
/*      */     }
/* 3789 */     int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
/* 3790 */     int nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
/* 3791 */     String name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
/* 3792 */     String descriptor = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
/* 3793 */     int bootstrapMethodOffset = this.bootstrapMethodOffsets[readUnsignedShort(cpInfoOffset)];
/* 3794 */     Handle handle = (Handle)readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
/* 3795 */     Object[] bootstrapMethodArguments = new Object[readUnsignedShort(bootstrapMethodOffset + 2)];
/* 3796 */     bootstrapMethodOffset += 4;
/* 3797 */     for (int i = 0; i < bootstrapMethodArguments.length; i++) {
/* 3798 */       bootstrapMethodArguments[i] = readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
/* 3799 */       bootstrapMethodOffset += 2;
/*      */     } 
/* 3801 */     this.constantDynamicValues[constantPoolEntryIndex] = new ConstantDynamic(name, descriptor, handle, bootstrapMethodArguments); return new ConstantDynamic(name, descriptor, handle, bootstrapMethodArguments);
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
/*      */   public Object readConst(int constantPoolEntryIndex, char[] charBuffer) {
/*      */     int referenceKind, referenceCpInfoOffset, nameAndTypeCpInfoOffset;
/*      */     String owner, name, descriptor;
/*      */     boolean isInterface;
/* 3820 */     int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
/* 3821 */     switch (this.classFileBuffer[cpInfoOffset - 1]) {
/*      */       case 3:
/* 3823 */         return Integer.valueOf(readInt(cpInfoOffset));
/*      */       case 4:
/* 3825 */         return Float.valueOf(Float.intBitsToFloat(readInt(cpInfoOffset)));
/*      */       case 5:
/* 3827 */         return Long.valueOf(readLong(cpInfoOffset));
/*      */       case 6:
/* 3829 */         return Double.valueOf(Double.longBitsToDouble(readLong(cpInfoOffset)));
/*      */       case 7:
/* 3831 */         return Type.getObjectType(readUTF8(cpInfoOffset, charBuffer));
/*      */       case 8:
/* 3833 */         return readUTF8(cpInfoOffset, charBuffer);
/*      */       case 16:
/* 3835 */         return Type.getMethodType(readUTF8(cpInfoOffset, charBuffer));
/*      */       case 15:
/* 3837 */         referenceKind = readByte(cpInfoOffset);
/* 3838 */         referenceCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 1)];
/* 3839 */         nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(referenceCpInfoOffset + 2)];
/* 3840 */         owner = readClass(referenceCpInfoOffset, charBuffer);
/* 3841 */         name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
/* 3842 */         descriptor = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
/* 3843 */         isInterface = (this.classFileBuffer[referenceCpInfoOffset - 1] == 11);
/*      */         
/* 3845 */         return new Handle(referenceKind, owner, name, descriptor, isInterface);
/*      */       case 17:
/* 3847 */         return readConstantDynamic(constantPoolEntryIndex, charBuffer);
/*      */     } 
/* 3849 */     throw new IllegalArgumentException();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\ClassReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */