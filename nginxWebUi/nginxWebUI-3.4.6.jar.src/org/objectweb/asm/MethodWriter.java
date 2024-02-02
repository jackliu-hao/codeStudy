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
/*      */ final class MethodWriter
/*      */   extends MethodVisitor
/*      */ {
/*      */   static final int COMPUTE_NOTHING = 0;
/*      */   static final int COMPUTE_MAX_STACK_AND_LOCAL = 1;
/*      */   static final int COMPUTE_MAX_STACK_AND_LOCAL_FROM_FRAMES = 2;
/*      */   static final int COMPUTE_INSERTED_FRAMES = 3;
/*      */   static final int COMPUTE_ALL_FRAMES = 4;
/*      */   private static final int NA = 0;
/*   81 */   private static final int[] STACK_SIZE_DELTA = new int[] { 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 0, 0, 1, 2, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, -1, 0, -1, -1, -1, -1, -1, -2, -1, -2, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -3, -4, -3, -4, -3, -3, -3, -3, -1, -2, 1, 1, 1, 2, 2, 2, 0, -1, -2, -1, -2, -1, -2, -1, -2, -1, -2, -1, -2, -1, -2, -1, -2, -1, -2, -1, -2, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -2, -1, -2, -1, -2, 0, 1, 0, 1, -1, -1, 0, 0, 1, 1, -1, 0, -1, 0, 0, 0, -3, -1, -1, -3, -3, -1, -1, -1, -1, -1, -1, -2, -2, -2, -2, -2, -2, -2, -2, 0, 1, 0, -1, -1, -1, -2, -1, -2, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, -1, -1, 0, 0 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final SymbolTable symbolTable;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int accessFlags;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int nameIndex;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final String name;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int descriptorIndex;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final String descriptor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int maxStack;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int maxLocals;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  320 */   private final ByteVector code = new ByteVector();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Handler firstHandler;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Handler lastHandler;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int lineNumberTableLength;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteVector lineNumberTable;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int localVariableTableLength;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteVector localVariableTable;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int localVariableTypeTableLength;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteVector localVariableTypeTable;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int stackMapTableNumberOfEntries;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteVector stackMapTableEntries;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private AnnotationWriter lastCodeRuntimeVisibleTypeAnnotation;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private AnnotationWriter lastCodeRuntimeInvisibleTypeAnnotation;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Attribute firstCodeAttribute;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int numberOfExceptions;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int[] exceptionIndexTable;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int signatureIndex;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private AnnotationWriter lastRuntimeVisibleAnnotation;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private AnnotationWriter lastRuntimeInvisibleAnnotation;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int visibleAnnotableParameterCount;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private AnnotationWriter[] lastRuntimeVisibleParameterAnnotations;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int invisibleAnnotableParameterCount;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private AnnotationWriter[] lastRuntimeInvisibleParameterAnnotations;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private AnnotationWriter lastRuntimeVisibleTypeAnnotation;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private AnnotationWriter lastRuntimeInvisibleTypeAnnotation;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteVector defaultValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int parametersCount;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteVector parameters;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Attribute firstAttribute;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int compute;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Label firstBasicBlock;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Label lastBasicBlock;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Label currentBasicBlock;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int relativeStackSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int maxRelativeStackSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int currentLocals;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int previousFrameOffset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] previousFrame;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] currentFrame;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasSubroutines;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasAsmInstructions;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int lastBytecodeOffset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int sourceOffset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int sourceLength;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   MethodWriter(SymbolTable symbolTable, int access, String name, String descriptor, String signature, String[] exceptions, int compute) {
/*  595 */     super(589824);
/*  596 */     this.symbolTable = symbolTable;
/*  597 */     this.accessFlags = "<init>".equals(name) ? (access | 0x40000) : access;
/*  598 */     this.nameIndex = symbolTable.addConstantUtf8(name);
/*  599 */     this.name = name;
/*  600 */     this.descriptorIndex = symbolTable.addConstantUtf8(descriptor);
/*  601 */     this.descriptor = descriptor;
/*  602 */     this.signatureIndex = (signature == null) ? 0 : symbolTable.addConstantUtf8(signature);
/*  603 */     if (exceptions != null && exceptions.length > 0) {
/*  604 */       this.numberOfExceptions = exceptions.length;
/*  605 */       this.exceptionIndexTable = new int[this.numberOfExceptions];
/*  606 */       for (int i = 0; i < this.numberOfExceptions; i++) {
/*  607 */         this.exceptionIndexTable[i] = (symbolTable.addConstantClass(exceptions[i])).index;
/*      */       }
/*      */     } else {
/*  610 */       this.numberOfExceptions = 0;
/*  611 */       this.exceptionIndexTable = null;
/*      */     } 
/*  613 */     this.compute = compute;
/*  614 */     if (compute != 0) {
/*      */       
/*  616 */       int argumentsSize = Type.getArgumentsAndReturnSizes(descriptor) >> 2;
/*  617 */       if ((access & 0x8) != 0) {
/*  618 */         argumentsSize--;
/*      */       }
/*  620 */       this.maxLocals = argumentsSize;
/*  621 */       this.currentLocals = argumentsSize;
/*      */       
/*  623 */       this.firstBasicBlock = new Label();
/*  624 */       visitLabel(this.firstBasicBlock);
/*      */     } 
/*      */   }
/*      */   
/*      */   boolean hasFrames() {
/*  629 */     return (this.stackMapTableNumberOfEntries > 0);
/*      */   }
/*      */   
/*      */   boolean hasAsmInstructions() {
/*  633 */     return this.hasAsmInstructions;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void visitParameter(String name, int access) {
/*  642 */     if (this.parameters == null) {
/*  643 */       this.parameters = new ByteVector();
/*      */     }
/*  645 */     this.parametersCount++;
/*  646 */     this.parameters.putShort((name == null) ? 0 : this.symbolTable.addConstantUtf8(name)).putShort(access);
/*      */   }
/*      */ 
/*      */   
/*      */   public AnnotationVisitor visitAnnotationDefault() {
/*  651 */     this.defaultValue = new ByteVector();
/*  652 */     return new AnnotationWriter(this.symbolTable, false, this.defaultValue, null);
/*      */   }
/*      */ 
/*      */   
/*      */   public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
/*  657 */     if (visible) {
/*  658 */       return this
/*  659 */         .lastRuntimeVisibleAnnotation = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeVisibleAnnotation);
/*      */     }
/*  661 */     return this
/*  662 */       .lastRuntimeInvisibleAnnotation = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeInvisibleAnnotation);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
/*  669 */     if (visible) {
/*  670 */       return this
/*  671 */         .lastRuntimeVisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeVisibleTypeAnnotation);
/*      */     }
/*      */     
/*  674 */     return this
/*  675 */       .lastRuntimeInvisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeInvisibleTypeAnnotation);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
/*  682 */     if (visible) {
/*  683 */       this.visibleAnnotableParameterCount = parameterCount;
/*      */     } else {
/*  685 */       this.invisibleAnnotableParameterCount = parameterCount;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotationVisitor visitParameterAnnotation(int parameter, String annotationDescriptor, boolean visible) {
/*  692 */     if (visible) {
/*  693 */       if (this.lastRuntimeVisibleParameterAnnotations == null) {
/*  694 */         this
/*  695 */           .lastRuntimeVisibleParameterAnnotations = new AnnotationWriter[(Type.getArgumentTypes(this.descriptor)).length];
/*      */       }
/*  697 */       this.lastRuntimeVisibleParameterAnnotations[parameter] = 
/*  698 */         AnnotationWriter.create(this.symbolTable, annotationDescriptor, this.lastRuntimeVisibleParameterAnnotations[parameter]); return AnnotationWriter.create(this.symbolTable, annotationDescriptor, this.lastRuntimeVisibleParameterAnnotations[parameter]);
/*      */     } 
/*      */     
/*  701 */     if (this.lastRuntimeInvisibleParameterAnnotations == null) {
/*  702 */       this
/*  703 */         .lastRuntimeInvisibleParameterAnnotations = new AnnotationWriter[(Type.getArgumentTypes(this.descriptor)).length];
/*      */     }
/*  705 */     this.lastRuntimeInvisibleParameterAnnotations[parameter] = 
/*  706 */       AnnotationWriter.create(this.symbolTable, annotationDescriptor, this.lastRuntimeInvisibleParameterAnnotations[parameter]); return AnnotationWriter.create(this.symbolTable, annotationDescriptor, this.lastRuntimeInvisibleParameterAnnotations[parameter]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void visitAttribute(Attribute attribute) {
/*  716 */     if (attribute.isCodeAttribute()) {
/*  717 */       attribute.nextAttribute = this.firstCodeAttribute;
/*  718 */       this.firstCodeAttribute = attribute;
/*      */     } else {
/*  720 */       attribute.nextAttribute = this.firstAttribute;
/*  721 */       this.firstAttribute = attribute;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void visitCode() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
/*  737 */     if (this.compute == 4) {
/*      */       return;
/*      */     }
/*      */     
/*  741 */     if (this.compute == 3) {
/*  742 */       if (this.currentBasicBlock.frame == null) {
/*      */ 
/*      */ 
/*      */         
/*  746 */         this.currentBasicBlock.frame = new CurrentFrame(this.currentBasicBlock);
/*  747 */         this.currentBasicBlock.frame.setInputFrameFromDescriptor(this.symbolTable, this.accessFlags, this.descriptor, numLocal);
/*      */         
/*  749 */         this.currentBasicBlock.frame.accept(this);
/*      */       } else {
/*  751 */         if (type == -1) {
/*  752 */           this.currentBasicBlock.frame.setInputFrameFromApiFormat(this.symbolTable, numLocal, local, numStack, stack);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  758 */         this.currentBasicBlock.frame.accept(this);
/*      */       } 
/*  760 */     } else if (type == -1) {
/*  761 */       if (this.previousFrame == null) {
/*  762 */         int argumentsSize = Type.getArgumentsAndReturnSizes(this.descriptor) >> 2;
/*  763 */         Frame implicitFirstFrame = new Frame(new Label());
/*  764 */         implicitFirstFrame.setInputFrameFromDescriptor(this.symbolTable, this.accessFlags, this.descriptor, argumentsSize);
/*      */         
/*  766 */         implicitFirstFrame.accept(this);
/*      */       } 
/*  768 */       this.currentLocals = numLocal;
/*  769 */       int frameIndex = visitFrameStart(this.code.length, numLocal, numStack); int i;
/*  770 */       for (i = 0; i < numLocal; i++) {
/*  771 */         this.currentFrame[frameIndex++] = Frame.getAbstractTypeFromApiFormat(this.symbolTable, local[i]);
/*      */       }
/*  773 */       for (i = 0; i < numStack; i++) {
/*  774 */         this.currentFrame[frameIndex++] = Frame.getAbstractTypeFromApiFormat(this.symbolTable, stack[i]);
/*      */       }
/*  776 */       visitFrameEnd();
/*      */     } else {
/*  778 */       int offsetDelta, i; if (this.symbolTable.getMajorVersion() < 50) {
/*  779 */         throw new IllegalArgumentException("Class versions V1_5 or less must use F_NEW frames.");
/*      */       }
/*      */       
/*  782 */       if (this.stackMapTableEntries == null) {
/*  783 */         this.stackMapTableEntries = new ByteVector();
/*  784 */         offsetDelta = this.code.length;
/*      */       } else {
/*  786 */         offsetDelta = this.code.length - this.previousFrameOffset - 1;
/*  787 */         if (offsetDelta < 0) {
/*  788 */           if (type == 3) {
/*      */             return;
/*      */           }
/*  791 */           throw new IllegalStateException();
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  796 */       switch (type) {
/*      */         case 0:
/*  798 */           this.currentLocals = numLocal;
/*  799 */           this.stackMapTableEntries.putByte(255).putShort(offsetDelta).putShort(numLocal);
/*  800 */           for (i = 0; i < numLocal; i++) {
/*  801 */             putFrameType(local[i]);
/*      */           }
/*  803 */           this.stackMapTableEntries.putShort(numStack);
/*  804 */           for (i = 0; i < numStack; i++) {
/*  805 */             putFrameType(stack[i]);
/*      */           }
/*      */           break;
/*      */         case 1:
/*  809 */           this.currentLocals += numLocal;
/*  810 */           this.stackMapTableEntries.putByte(251 + numLocal).putShort(offsetDelta);
/*  811 */           for (i = 0; i < numLocal; i++) {
/*  812 */             putFrameType(local[i]);
/*      */           }
/*      */           break;
/*      */         case 2:
/*  816 */           this.currentLocals -= numLocal;
/*  817 */           this.stackMapTableEntries.putByte(251 - numLocal).putShort(offsetDelta);
/*      */           break;
/*      */         case 3:
/*  820 */           if (offsetDelta < 64) {
/*  821 */             this.stackMapTableEntries.putByte(offsetDelta); break;
/*      */           } 
/*  823 */           this.stackMapTableEntries.putByte(251).putShort(offsetDelta);
/*      */           break;
/*      */         
/*      */         case 4:
/*  827 */           if (offsetDelta < 64) {
/*  828 */             this.stackMapTableEntries.putByte(64 + offsetDelta);
/*      */           } else {
/*  830 */             this.stackMapTableEntries
/*  831 */               .putByte(247)
/*  832 */               .putShort(offsetDelta);
/*      */           } 
/*  834 */           putFrameType(stack[0]);
/*      */           break;
/*      */         default:
/*  837 */           throw new IllegalArgumentException();
/*      */       } 
/*      */       
/*  840 */       this.previousFrameOffset = this.code.length;
/*  841 */       this.stackMapTableNumberOfEntries++;
/*      */     } 
/*      */     
/*  844 */     if (this.compute == 2) {
/*  845 */       this.relativeStackSize = numStack;
/*  846 */       for (int i = 0; i < numStack; i++) {
/*  847 */         if (stack[i] == Opcodes.LONG || stack[i] == Opcodes.DOUBLE) {
/*  848 */           this.relativeStackSize++;
/*      */         }
/*      */       } 
/*  851 */       if (this.relativeStackSize > this.maxRelativeStackSize) {
/*  852 */         this.maxRelativeStackSize = this.relativeStackSize;
/*      */       }
/*      */     } 
/*      */     
/*  856 */     this.maxStack = Math.max(this.maxStack, numStack);
/*  857 */     this.maxLocals = Math.max(this.maxLocals, this.currentLocals);
/*      */   }
/*      */ 
/*      */   
/*      */   public void visitInsn(int opcode) {
/*  862 */     this.lastBytecodeOffset = this.code.length;
/*      */     
/*  864 */     this.code.putByte(opcode);
/*      */     
/*  866 */     if (this.currentBasicBlock != null) {
/*  867 */       if (this.compute == 4 || this.compute == 3) {
/*  868 */         this.currentBasicBlock.frame.execute(opcode, 0, null, null);
/*      */       } else {
/*  870 */         int size = this.relativeStackSize + STACK_SIZE_DELTA[opcode];
/*  871 */         if (size > this.maxRelativeStackSize) {
/*  872 */           this.maxRelativeStackSize = size;
/*      */         }
/*  874 */         this.relativeStackSize = size;
/*      */       } 
/*  876 */       if ((opcode >= 172 && opcode <= 177) || opcode == 191) {
/*  877 */         endCurrentBasicBlockWithNoSuccessor();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void visitIntInsn(int opcode, int operand) {
/*  884 */     this.lastBytecodeOffset = this.code.length;
/*      */     
/*  886 */     if (opcode == 17) {
/*  887 */       this.code.put12(opcode, operand);
/*      */     } else {
/*  889 */       this.code.put11(opcode, operand);
/*      */     } 
/*      */     
/*  892 */     if (this.currentBasicBlock != null) {
/*  893 */       if (this.compute == 4 || this.compute == 3) {
/*  894 */         this.currentBasicBlock.frame.execute(opcode, operand, null, null);
/*  895 */       } else if (opcode != 188) {
/*      */         
/*  897 */         int size = this.relativeStackSize + 1;
/*  898 */         if (size > this.maxRelativeStackSize) {
/*  899 */           this.maxRelativeStackSize = size;
/*      */         }
/*  901 */         this.relativeStackSize = size;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void visitVarInsn(int opcode, int var) {
/*  908 */     this.lastBytecodeOffset = this.code.length;
/*      */     
/*  910 */     if (var < 4 && opcode != 169) {
/*      */       int optimizedOpcode;
/*  912 */       if (opcode < 54) {
/*  913 */         optimizedOpcode = 26 + (opcode - 21 << 2) + var;
/*      */       } else {
/*  915 */         optimizedOpcode = 59 + (opcode - 54 << 2) + var;
/*      */       } 
/*  917 */       this.code.putByte(optimizedOpcode);
/*  918 */     } else if (var >= 256) {
/*  919 */       this.code.putByte(196).put12(opcode, var);
/*      */     } else {
/*  921 */       this.code.put11(opcode, var);
/*      */     } 
/*      */     
/*  924 */     if (this.currentBasicBlock != null) {
/*  925 */       if (this.compute == 4 || this.compute == 3) {
/*  926 */         this.currentBasicBlock.frame.execute(opcode, var, null, null);
/*      */       }
/*  928 */       else if (opcode == 169) {
/*      */         
/*  930 */         this.currentBasicBlock.flags = (short)(this.currentBasicBlock.flags | 0x40);
/*  931 */         this.currentBasicBlock.outputStackSize = (short)this.relativeStackSize;
/*  932 */         endCurrentBasicBlockWithNoSuccessor();
/*      */       } else {
/*  934 */         int size = this.relativeStackSize + STACK_SIZE_DELTA[opcode];
/*  935 */         if (size > this.maxRelativeStackSize) {
/*  936 */           this.maxRelativeStackSize = size;
/*      */         }
/*  938 */         this.relativeStackSize = size;
/*      */       } 
/*      */     }
/*      */     
/*  942 */     if (this.compute != 0) {
/*      */       int currentMaxLocals;
/*  944 */       if (opcode == 22 || opcode == 24 || opcode == 55 || opcode == 57) {
/*      */ 
/*      */ 
/*      */         
/*  948 */         currentMaxLocals = var + 2;
/*      */       } else {
/*  950 */         currentMaxLocals = var + 1;
/*      */       } 
/*  952 */       if (currentMaxLocals > this.maxLocals) {
/*  953 */         this.maxLocals = currentMaxLocals;
/*      */       }
/*      */     } 
/*  956 */     if (opcode >= 54 && this.compute == 4 && this.firstHandler != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  964 */       visitLabel(new Label());
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void visitTypeInsn(int opcode, String type) {
/*  970 */     this.lastBytecodeOffset = this.code.length;
/*      */     
/*  972 */     Symbol typeSymbol = this.symbolTable.addConstantClass(type);
/*  973 */     this.code.put12(opcode, typeSymbol.index);
/*      */     
/*  975 */     if (this.currentBasicBlock != null) {
/*  976 */       if (this.compute == 4 || this.compute == 3) {
/*  977 */         this.currentBasicBlock.frame.execute(opcode, this.lastBytecodeOffset, typeSymbol, this.symbolTable);
/*  978 */       } else if (opcode == 187) {
/*      */         
/*  980 */         int size = this.relativeStackSize + 1;
/*  981 */         if (size > this.maxRelativeStackSize) {
/*  982 */           this.maxRelativeStackSize = size;
/*      */         }
/*  984 */         this.relativeStackSize = size;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
/*  992 */     this.lastBytecodeOffset = this.code.length;
/*      */     
/*  994 */     Symbol fieldrefSymbol = this.symbolTable.addConstantFieldref(owner, name, descriptor);
/*  995 */     this.code.put12(opcode, fieldrefSymbol.index);
/*      */     
/*  997 */     if (this.currentBasicBlock != null) {
/*  998 */       if (this.compute == 4 || this.compute == 3) {
/*  999 */         this.currentBasicBlock.frame.execute(opcode, 0, fieldrefSymbol, this.symbolTable);
/*      */       } else {
/*      */         int size;
/* 1002 */         char firstDescChar = descriptor.charAt(0);
/* 1003 */         switch (opcode) {
/*      */           case 178:
/* 1005 */             size = this.relativeStackSize + ((firstDescChar == 'D' || firstDescChar == 'J') ? 2 : 1);
/*      */             break;
/*      */           case 179:
/* 1008 */             size = this.relativeStackSize + ((firstDescChar == 'D' || firstDescChar == 'J') ? -2 : -1);
/*      */             break;
/*      */           case 180:
/* 1011 */             size = this.relativeStackSize + ((firstDescChar == 'D' || firstDescChar == 'J') ? 1 : 0);
/*      */             break;
/*      */           
/*      */           default:
/* 1015 */             size = this.relativeStackSize + ((firstDescChar == 'D' || firstDescChar == 'J') ? -3 : -2);
/*      */             break;
/*      */         } 
/* 1018 */         if (size > this.maxRelativeStackSize) {
/* 1019 */           this.maxRelativeStackSize = size;
/*      */         }
/* 1021 */         this.relativeStackSize = size;
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
/*      */   public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
/* 1033 */     this.lastBytecodeOffset = this.code.length;
/*      */     
/* 1035 */     Symbol methodrefSymbol = this.symbolTable.addConstantMethodref(owner, name, descriptor, isInterface);
/* 1036 */     if (opcode == 185) {
/* 1037 */       this.code.put12(185, methodrefSymbol.index)
/* 1038 */         .put11(methodrefSymbol.getArgumentsAndReturnSizes() >> 2, 0);
/*      */     } else {
/* 1040 */       this.code.put12(opcode, methodrefSymbol.index);
/*      */     } 
/*      */     
/* 1043 */     if (this.currentBasicBlock != null) {
/* 1044 */       if (this.compute == 4 || this.compute == 3) {
/* 1045 */         this.currentBasicBlock.frame.execute(opcode, 0, methodrefSymbol, this.symbolTable);
/*      */       } else {
/* 1047 */         int size, argumentsAndReturnSize = methodrefSymbol.getArgumentsAndReturnSizes();
/* 1048 */         int stackSizeDelta = (argumentsAndReturnSize & 0x3) - (argumentsAndReturnSize >> 2);
/*      */         
/* 1050 */         if (opcode == 184) {
/* 1051 */           size = this.relativeStackSize + stackSizeDelta + 1;
/*      */         } else {
/* 1053 */           size = this.relativeStackSize + stackSizeDelta;
/*      */         } 
/* 1055 */         if (size > this.maxRelativeStackSize) {
/* 1056 */           this.maxRelativeStackSize = size;
/*      */         }
/* 1058 */         this.relativeStackSize = size;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
/* 1069 */     this.lastBytecodeOffset = this.code.length;
/*      */ 
/*      */     
/* 1072 */     Symbol invokeDynamicSymbol = this.symbolTable.addConstantInvokeDynamic(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
/*      */     
/* 1074 */     this.code.put12(186, invokeDynamicSymbol.index);
/* 1075 */     this.code.putShort(0);
/*      */     
/* 1077 */     if (this.currentBasicBlock != null) {
/* 1078 */       if (this.compute == 4 || this.compute == 3) {
/* 1079 */         this.currentBasicBlock.frame.execute(186, 0, invokeDynamicSymbol, this.symbolTable);
/*      */       } else {
/* 1081 */         int argumentsAndReturnSize = invokeDynamicSymbol.getArgumentsAndReturnSizes();
/* 1082 */         int stackSizeDelta = (argumentsAndReturnSize & 0x3) - (argumentsAndReturnSize >> 2) + 1;
/* 1083 */         int size = this.relativeStackSize + stackSizeDelta;
/* 1084 */         if (size > this.maxRelativeStackSize) {
/* 1085 */           this.maxRelativeStackSize = size;
/*      */         }
/* 1087 */         this.relativeStackSize = size;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void visitJumpInsn(int opcode, Label label) {
/* 1094 */     this.lastBytecodeOffset = this.code.length;
/*      */ 
/*      */ 
/*      */     
/* 1098 */     int baseOpcode = (opcode >= 200) ? (opcode - 33) : opcode;
/* 1099 */     boolean nextInsnIsJumpTarget = false;
/* 1100 */     if ((label.flags & 0x4) != 0 && label.bytecodeOffset - this.code.length < -32768) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1106 */       if (baseOpcode == 167) {
/* 1107 */         this.code.putByte(200);
/* 1108 */       } else if (baseOpcode == 168) {
/* 1109 */         this.code.putByte(201);
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 1114 */         this.code.putByte((baseOpcode >= 198) ? (baseOpcode ^ 0x1) : ((baseOpcode + 1 ^ 0x1) - 1));
/* 1115 */         this.code.putShort(8);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1122 */         this.code.putByte(220);
/* 1123 */         this.hasAsmInstructions = true;
/*      */         
/* 1125 */         nextInsnIsJumpTarget = true;
/*      */       } 
/* 1127 */       label.put(this.code, this.code.length - 1, true);
/* 1128 */     } else if (baseOpcode != opcode) {
/*      */ 
/*      */       
/* 1131 */       this.code.putByte(opcode);
/* 1132 */       label.put(this.code, this.code.length - 1, true);
/*      */     
/*      */     }
/*      */     else {
/*      */       
/* 1137 */       this.code.putByte(baseOpcode);
/* 1138 */       label.put(this.code, this.code.length - 1, false);
/*      */     } 
/*      */ 
/*      */     
/* 1142 */     if (this.currentBasicBlock != null) {
/* 1143 */       Label nextBasicBlock = null;
/* 1144 */       if (this.compute == 4) {
/* 1145 */         this.currentBasicBlock.frame.execute(baseOpcode, 0, null, null);
/*      */         
/* 1147 */         (label.getCanonicalInstance()).flags = (short)((label.getCanonicalInstance()).flags | 0x2);
/*      */         
/* 1149 */         addSuccessorToCurrentBasicBlock(0, label);
/* 1150 */         if (baseOpcode != 167)
/*      */         {
/*      */ 
/*      */           
/* 1154 */           nextBasicBlock = new Label();
/*      */         }
/* 1156 */       } else if (this.compute == 3) {
/* 1157 */         this.currentBasicBlock.frame.execute(baseOpcode, 0, null, null);
/* 1158 */       } else if (this.compute == 2) {
/*      */         
/* 1160 */         this.relativeStackSize += STACK_SIZE_DELTA[baseOpcode];
/*      */       }
/* 1162 */       else if (baseOpcode == 168) {
/*      */         
/* 1164 */         if ((label.flags & 0x20) == 0) {
/* 1165 */           label.flags = (short)(label.flags | 0x20);
/* 1166 */           this.hasSubroutines = true;
/*      */         } 
/* 1168 */         this.currentBasicBlock.flags = (short)(this.currentBasicBlock.flags | 0x10);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1175 */         addSuccessorToCurrentBasicBlock(this.relativeStackSize + 1, label);
/*      */         
/* 1177 */         nextBasicBlock = new Label();
/*      */       } else {
/*      */         
/* 1180 */         this.relativeStackSize += STACK_SIZE_DELTA[baseOpcode];
/* 1181 */         addSuccessorToCurrentBasicBlock(this.relativeStackSize, label);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1186 */       if (nextBasicBlock != null) {
/* 1187 */         if (nextInsnIsJumpTarget) {
/* 1188 */           nextBasicBlock.flags = (short)(nextBasicBlock.flags | 0x2);
/*      */         }
/* 1190 */         visitLabel(nextBasicBlock);
/*      */       } 
/* 1192 */       if (baseOpcode == 167) {
/* 1193 */         endCurrentBasicBlockWithNoSuccessor();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void visitLabel(Label label) {
/* 1201 */     this.hasAsmInstructions |= label.resolve(this.code.data, this.code.length);
/*      */ 
/*      */     
/* 1204 */     if ((label.flags & 0x1) != 0) {
/*      */       return;
/*      */     }
/* 1207 */     if (this.compute == 4) {
/* 1208 */       if (this.currentBasicBlock != null) {
/* 1209 */         if (label.bytecodeOffset == this.currentBasicBlock.bytecodeOffset) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1214 */           this.currentBasicBlock.flags = (short)(this.currentBasicBlock.flags | label.flags & 0x2);
/*      */ 
/*      */ 
/*      */           
/* 1218 */           label.frame = this.currentBasicBlock.frame;
/*      */ 
/*      */           
/*      */           return;
/*      */         } 
/*      */         
/* 1224 */         addSuccessorToCurrentBasicBlock(0, label);
/*      */       } 
/*      */       
/* 1227 */       if (this.lastBasicBlock != null) {
/* 1228 */         if (label.bytecodeOffset == this.lastBasicBlock.bytecodeOffset) {
/*      */           
/* 1230 */           this.lastBasicBlock.flags = (short)(this.lastBasicBlock.flags | label.flags & 0x2);
/*      */           
/* 1232 */           label.frame = this.lastBasicBlock.frame;
/* 1233 */           this.currentBasicBlock = this.lastBasicBlock;
/*      */           return;
/*      */         } 
/* 1236 */         this.lastBasicBlock.nextBasicBlock = label;
/*      */       } 
/* 1238 */       this.lastBasicBlock = label;
/*      */       
/* 1240 */       this.currentBasicBlock = label;
/*      */       
/* 1242 */       label.frame = new Frame(label);
/* 1243 */     } else if (this.compute == 3) {
/* 1244 */       if (this.currentBasicBlock == null) {
/*      */ 
/*      */         
/* 1247 */         this.currentBasicBlock = label;
/*      */       } else {
/*      */         
/* 1250 */         this.currentBasicBlock.frame.owner = label;
/*      */       } 
/* 1252 */     } else if (this.compute == 1) {
/* 1253 */       if (this.currentBasicBlock != null) {
/*      */         
/* 1255 */         this.currentBasicBlock.outputStackMax = (short)this.maxRelativeStackSize;
/* 1256 */         addSuccessorToCurrentBasicBlock(this.relativeStackSize, label);
/*      */       } 
/*      */       
/* 1259 */       this.currentBasicBlock = label;
/* 1260 */       this.relativeStackSize = 0;
/* 1261 */       this.maxRelativeStackSize = 0;
/*      */       
/* 1263 */       if (this.lastBasicBlock != null) {
/* 1264 */         this.lastBasicBlock.nextBasicBlock = label;
/*      */       }
/* 1266 */       this.lastBasicBlock = label;
/* 1267 */     } else if (this.compute == 2 && this.currentBasicBlock == null) {
/*      */ 
/*      */ 
/*      */       
/* 1271 */       this.currentBasicBlock = label;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void visitLdcInsn(Object value) {
/* 1277 */     this.lastBytecodeOffset = this.code.length;
/*      */     
/* 1279 */     Symbol constantSymbol = this.symbolTable.addConstant(value);
/* 1280 */     int constantIndex = constantSymbol.index;
/*      */ 
/*      */     
/*      */     char firstDescriptorChar;
/*      */ 
/*      */     
/* 1286 */     boolean isLongOrDouble = (constantSymbol.tag == 5 || constantSymbol.tag == 6 || (constantSymbol.tag == 17 && ((firstDescriptorChar = constantSymbol.value.charAt(0)) == 'J' || firstDescriptorChar == 'D')));
/*      */     
/* 1288 */     if (isLongOrDouble) {
/* 1289 */       this.code.put12(20, constantIndex);
/* 1290 */     } else if (constantIndex >= 256) {
/* 1291 */       this.code.put12(19, constantIndex);
/*      */     } else {
/* 1293 */       this.code.put11(18, constantIndex);
/*      */     } 
/*      */     
/* 1296 */     if (this.currentBasicBlock != null) {
/* 1297 */       if (this.compute == 4 || this.compute == 3) {
/* 1298 */         this.currentBasicBlock.frame.execute(18, 0, constantSymbol, this.symbolTable);
/*      */       } else {
/* 1300 */         int size = this.relativeStackSize + (isLongOrDouble ? 2 : 1);
/* 1301 */         if (size > this.maxRelativeStackSize) {
/* 1302 */           this.maxRelativeStackSize = size;
/*      */         }
/* 1304 */         this.relativeStackSize = size;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void visitIincInsn(int var, int increment) {
/* 1311 */     this.lastBytecodeOffset = this.code.length;
/*      */     
/* 1313 */     if (var > 255 || increment > 127 || increment < -128) {
/* 1314 */       this.code.putByte(196).put12(132, var).putShort(increment);
/*      */     } else {
/* 1316 */       this.code.putByte(132).put11(var, increment);
/*      */     } 
/*      */     
/* 1319 */     if (this.currentBasicBlock != null && (this.compute == 4 || this.compute == 3))
/*      */     {
/* 1321 */       this.currentBasicBlock.frame.execute(132, var, null, null);
/*      */     }
/* 1323 */     if (this.compute != 0) {
/* 1324 */       int currentMaxLocals = var + 1;
/* 1325 */       if (currentMaxLocals > this.maxLocals) {
/* 1326 */         this.maxLocals = currentMaxLocals;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
/* 1334 */     this.lastBytecodeOffset = this.code.length;
/*      */     
/* 1336 */     this.code.putByte(170).putByteArray(null, 0, (4 - this.code.length % 4) % 4);
/* 1337 */     dflt.put(this.code, this.lastBytecodeOffset, true);
/* 1338 */     this.code.putInt(min).putInt(max);
/* 1339 */     for (Label label : labels) {
/* 1340 */       label.put(this.code, this.lastBytecodeOffset, true);
/*      */     }
/*      */     
/* 1343 */     visitSwitchInsn(dflt, labels);
/*      */   }
/*      */ 
/*      */   
/*      */   public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
/* 1348 */     this.lastBytecodeOffset = this.code.length;
/*      */     
/* 1350 */     this.code.putByte(171).putByteArray(null, 0, (4 - this.code.length % 4) % 4);
/* 1351 */     dflt.put(this.code, this.lastBytecodeOffset, true);
/* 1352 */     this.code.putInt(labels.length);
/* 1353 */     for (int i = 0; i < labels.length; i++) {
/* 1354 */       this.code.putInt(keys[i]);
/* 1355 */       labels[i].put(this.code, this.lastBytecodeOffset, true);
/*      */     } 
/*      */     
/* 1358 */     visitSwitchInsn(dflt, labels);
/*      */   }
/*      */   
/*      */   private void visitSwitchInsn(Label dflt, Label[] labels) {
/* 1362 */     if (this.currentBasicBlock != null) {
/* 1363 */       if (this.compute == 4) {
/* 1364 */         this.currentBasicBlock.frame.execute(171, 0, null, null);
/*      */         
/* 1366 */         addSuccessorToCurrentBasicBlock(0, dflt);
/* 1367 */         (dflt.getCanonicalInstance()).flags = (short)((dflt.getCanonicalInstance()).flags | 0x2);
/* 1368 */         for (Label label : labels) {
/* 1369 */           addSuccessorToCurrentBasicBlock(0, label);
/* 1370 */           (label.getCanonicalInstance()).flags = (short)((label.getCanonicalInstance()).flags | 0x2);
/*      */         } 
/* 1372 */       } else if (this.compute == 1) {
/*      */         
/* 1374 */         this.relativeStackSize--;
/*      */         
/* 1376 */         addSuccessorToCurrentBasicBlock(this.relativeStackSize, dflt);
/* 1377 */         for (Label label : labels) {
/* 1378 */           addSuccessorToCurrentBasicBlock(this.relativeStackSize, label);
/*      */         }
/*      */       } 
/*      */       
/* 1382 */       endCurrentBasicBlockWithNoSuccessor();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
/* 1388 */     this.lastBytecodeOffset = this.code.length;
/*      */     
/* 1390 */     Symbol descSymbol = this.symbolTable.addConstantClass(descriptor);
/* 1391 */     this.code.put12(197, descSymbol.index).putByte(numDimensions);
/*      */     
/* 1393 */     if (this.currentBasicBlock != null) {
/* 1394 */       if (this.compute == 4 || this.compute == 3) {
/* 1395 */         this.currentBasicBlock.frame.execute(197, numDimensions, descSymbol, this.symbolTable);
/*      */       }
/*      */       else {
/*      */         
/* 1399 */         this.relativeStackSize += 1 - numDimensions;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
/* 1407 */     if (visible) {
/* 1408 */       return this
/* 1409 */         .lastCodeRuntimeVisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef & 0xFF0000FF | this.lastBytecodeOffset << 8, typePath, descriptor, this.lastCodeRuntimeVisibleTypeAnnotation);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1416 */     return this
/* 1417 */       .lastCodeRuntimeInvisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef & 0xFF0000FF | this.lastBytecodeOffset << 8, typePath, descriptor, this.lastCodeRuntimeInvisibleTypeAnnotation);
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
/*      */   public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
/* 1431 */     Handler newHandler = new Handler(start, end, handler, (type != null) ? (this.symbolTable.addConstantClass(type)).index : 0, type);
/* 1432 */     if (this.firstHandler == null) {
/* 1433 */       this.firstHandler = newHandler;
/*      */     } else {
/* 1435 */       this.lastHandler.nextHandler = newHandler;
/*      */     } 
/* 1437 */     this.lastHandler = newHandler;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
/* 1443 */     if (visible) {
/* 1444 */       return this
/* 1445 */         .lastCodeRuntimeVisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastCodeRuntimeVisibleTypeAnnotation);
/*      */     }
/*      */     
/* 1448 */     return this
/* 1449 */       .lastCodeRuntimeInvisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastCodeRuntimeInvisibleTypeAnnotation);
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
/*      */   public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
/* 1462 */     if (signature != null) {
/* 1463 */       if (this.localVariableTypeTable == null) {
/* 1464 */         this.localVariableTypeTable = new ByteVector();
/*      */       }
/* 1466 */       this.localVariableTypeTableLength++;
/* 1467 */       this.localVariableTypeTable
/* 1468 */         .putShort(start.bytecodeOffset)
/* 1469 */         .putShort(end.bytecodeOffset - start.bytecodeOffset)
/* 1470 */         .putShort(this.symbolTable.addConstantUtf8(name))
/* 1471 */         .putShort(this.symbolTable.addConstantUtf8(signature))
/* 1472 */         .putShort(index);
/*      */     } 
/* 1474 */     if (this.localVariableTable == null) {
/* 1475 */       this.localVariableTable = new ByteVector();
/*      */     }
/* 1477 */     this.localVariableTableLength++;
/* 1478 */     this.localVariableTable
/* 1479 */       .putShort(start.bytecodeOffset)
/* 1480 */       .putShort(end.bytecodeOffset - start.bytecodeOffset)
/* 1481 */       .putShort(this.symbolTable.addConstantUtf8(name))
/* 1482 */       .putShort(this.symbolTable.addConstantUtf8(descriptor))
/* 1483 */       .putShort(index);
/* 1484 */     if (this.compute != 0) {
/* 1485 */       char firstDescChar = descriptor.charAt(0);
/* 1486 */       int currentMaxLocals = index + ((firstDescChar == 'J' || firstDescChar == 'D') ? 2 : 1);
/* 1487 */       if (currentMaxLocals > this.maxLocals) {
/* 1488 */         this.maxLocals = currentMaxLocals;
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
/*      */   public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
/* 1504 */     ByteVector typeAnnotation = new ByteVector();
/*      */     
/* 1506 */     typeAnnotation.putByte(typeRef >>> 24).putShort(start.length);
/* 1507 */     for (int i = 0; i < start.length; i++) {
/* 1508 */       typeAnnotation
/* 1509 */         .putShort((start[i]).bytecodeOffset)
/* 1510 */         .putShort((end[i]).bytecodeOffset - (start[i]).bytecodeOffset)
/* 1511 */         .putShort(index[i]);
/*      */     }
/* 1513 */     TypePath.put(typePath, typeAnnotation);
/*      */     
/* 1515 */     typeAnnotation.putShort(this.symbolTable.addConstantUtf8(descriptor)).putShort(0);
/* 1516 */     if (visible) {
/* 1517 */       return this.lastCodeRuntimeVisibleTypeAnnotation = new AnnotationWriter(this.symbolTable, true, typeAnnotation, this.lastCodeRuntimeVisibleTypeAnnotation);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1524 */     return this.lastCodeRuntimeInvisibleTypeAnnotation = new AnnotationWriter(this.symbolTable, true, typeAnnotation, this.lastCodeRuntimeInvisibleTypeAnnotation);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void visitLineNumber(int line, Label start) {
/* 1535 */     if (this.lineNumberTable == null) {
/* 1536 */       this.lineNumberTable = new ByteVector();
/*      */     }
/* 1538 */     this.lineNumberTableLength++;
/* 1539 */     this.lineNumberTable.putShort(start.bytecodeOffset);
/* 1540 */     this.lineNumberTable.putShort(line);
/*      */   }
/*      */ 
/*      */   
/*      */   public void visitMaxs(int maxStack, int maxLocals) {
/* 1545 */     if (this.compute == 4) {
/* 1546 */       computeAllFrames();
/* 1547 */     } else if (this.compute == 1) {
/* 1548 */       computeMaxStackAndLocal();
/* 1549 */     } else if (this.compute == 2) {
/* 1550 */       this.maxStack = this.maxRelativeStackSize;
/*      */     } else {
/* 1552 */       this.maxStack = maxStack;
/* 1553 */       this.maxLocals = maxLocals;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void computeAllFrames() {
/* 1560 */     Handler handler = this.firstHandler;
/* 1561 */     while (handler != null) {
/*      */       
/* 1563 */       String catchTypeDescriptor = (handler.catchTypeDescriptor == null) ? "java/lang/Throwable" : handler.catchTypeDescriptor;
/* 1564 */       int catchType = Frame.getAbstractTypeFromInternalName(this.symbolTable, catchTypeDescriptor);
/*      */       
/* 1566 */       Label handlerBlock = handler.handlerPc.getCanonicalInstance();
/* 1567 */       handlerBlock.flags = (short)(handlerBlock.flags | 0x2);
/*      */       
/* 1569 */       Label handlerRangeBlock = handler.startPc.getCanonicalInstance();
/* 1570 */       Label handlerRangeEnd = handler.endPc.getCanonicalInstance();
/* 1571 */       while (handlerRangeBlock != handlerRangeEnd) {
/* 1572 */         handlerRangeBlock.outgoingEdges = new Edge(catchType, handlerBlock, handlerRangeBlock.outgoingEdges);
/*      */         
/* 1574 */         handlerRangeBlock = handlerRangeBlock.nextBasicBlock;
/*      */       } 
/* 1576 */       handler = handler.nextHandler;
/*      */     } 
/*      */ 
/*      */     
/* 1580 */     Frame firstFrame = this.firstBasicBlock.frame;
/* 1581 */     firstFrame.setInputFrameFromDescriptor(this.symbolTable, this.accessFlags, this.descriptor, this.maxLocals);
/* 1582 */     firstFrame.accept(this);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1590 */     Label listOfBlocksToProcess = this.firstBasicBlock;
/* 1591 */     listOfBlocksToProcess.nextListElement = Label.EMPTY_LIST;
/* 1592 */     int maxStackSize = 0;
/* 1593 */     while (listOfBlocksToProcess != Label.EMPTY_LIST) {
/*      */       
/* 1595 */       Label label = listOfBlocksToProcess;
/* 1596 */       listOfBlocksToProcess = listOfBlocksToProcess.nextListElement;
/* 1597 */       label.nextListElement = null;
/*      */       
/* 1599 */       label.flags = (short)(label.flags | 0x8);
/*      */       
/* 1601 */       int maxBlockStackSize = label.frame.getInputStackSize() + label.outputStackMax;
/* 1602 */       if (maxBlockStackSize > maxStackSize) {
/* 1603 */         maxStackSize = maxBlockStackSize;
/*      */       }
/*      */       
/* 1606 */       Edge outgoingEdge = label.outgoingEdges;
/* 1607 */       while (outgoingEdge != null) {
/* 1608 */         Label successorBlock = outgoingEdge.successor.getCanonicalInstance();
/*      */         
/* 1610 */         boolean successorBlockChanged = label.frame.merge(this.symbolTable, successorBlock.frame, outgoingEdge.info);
/* 1611 */         if (successorBlockChanged && successorBlock.nextListElement == null) {
/*      */ 
/*      */           
/* 1614 */           successorBlock.nextListElement = listOfBlocksToProcess;
/* 1615 */           listOfBlocksToProcess = successorBlock;
/*      */         } 
/* 1617 */         outgoingEdge = outgoingEdge.nextEdge;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1624 */     Label basicBlock = this.firstBasicBlock;
/* 1625 */     while (basicBlock != null) {
/* 1626 */       if ((basicBlock.flags & 0xA) == 10)
/*      */       {
/* 1628 */         basicBlock.frame.accept(this);
/*      */       }
/* 1630 */       if ((basicBlock.flags & 0x8) == 0) {
/*      */         
/* 1632 */         Label nextBasicBlock = basicBlock.nextBasicBlock;
/* 1633 */         int startOffset = basicBlock.bytecodeOffset;
/* 1634 */         int endOffset = ((nextBasicBlock == null) ? this.code.length : nextBasicBlock.bytecodeOffset) - 1;
/* 1635 */         if (endOffset >= startOffset) {
/*      */           
/* 1637 */           for (int i = startOffset; i < endOffset; i++) {
/* 1638 */             this.code.data[i] = 0;
/*      */           }
/* 1640 */           this.code.data[endOffset] = -65;
/*      */ 
/*      */           
/* 1643 */           int frameIndex = visitFrameStart(startOffset, 0, 1);
/* 1644 */           this.currentFrame[frameIndex] = 
/* 1645 */             Frame.getAbstractTypeFromInternalName(this.symbolTable, "java/lang/Throwable");
/* 1646 */           visitFrameEnd();
/*      */           
/* 1648 */           this.firstHandler = Handler.removeRange(this.firstHandler, basicBlock, nextBasicBlock);
/*      */           
/* 1650 */           maxStackSize = Math.max(maxStackSize, 1);
/*      */         } 
/*      */       } 
/* 1653 */       basicBlock = basicBlock.nextBasicBlock;
/*      */     } 
/*      */     
/* 1656 */     this.maxStack = maxStackSize;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void computeMaxStackAndLocal() {
/* 1662 */     Handler handler = this.firstHandler;
/* 1663 */     while (handler != null) {
/* 1664 */       Label handlerBlock = handler.handlerPc;
/* 1665 */       Label handlerRangeBlock = handler.startPc;
/* 1666 */       Label handlerRangeEnd = handler.endPc;
/*      */       
/* 1668 */       while (handlerRangeBlock != handlerRangeEnd) {
/* 1669 */         if ((handlerRangeBlock.flags & 0x10) == 0) {
/* 1670 */           handlerRangeBlock.outgoingEdges = new Edge(2147483647, handlerBlock, handlerRangeBlock.outgoingEdges);
/*      */         
/*      */         }
/*      */         else {
/*      */ 
/*      */           
/* 1676 */           handlerRangeBlock.outgoingEdges.nextEdge.nextEdge = new Edge(2147483647, handlerBlock, handlerRangeBlock.outgoingEdges.nextEdge.nextEdge);
/*      */         } 
/*      */ 
/*      */         
/* 1680 */         handlerRangeBlock = handlerRangeBlock.nextBasicBlock;
/*      */       } 
/* 1682 */       handler = handler.nextHandler;
/*      */     } 
/*      */ 
/*      */     
/* 1686 */     if (this.hasSubroutines) {
/*      */ 
/*      */       
/* 1689 */       short numSubroutines = 1;
/* 1690 */       this.firstBasicBlock.markSubroutine(numSubroutines);
/*      */ 
/*      */       
/* 1693 */       for (short currentSubroutine = 1; currentSubroutine <= numSubroutines; currentSubroutine = (short)(currentSubroutine + 1)) {
/* 1694 */         Label label = this.firstBasicBlock;
/* 1695 */         while (label != null) {
/* 1696 */           if ((label.flags & 0x10) != 0 && label.subroutineId == currentSubroutine) {
/*      */             
/* 1698 */             Label jsrTarget = label.outgoingEdges.nextEdge.successor;
/* 1699 */             if (jsrTarget.subroutineId == 0) {
/*      */               
/* 1701 */               numSubroutines = (short)(numSubroutines + 1); jsrTarget.markSubroutine(numSubroutines);
/*      */             } 
/*      */           } 
/* 1704 */           label = label.nextBasicBlock;
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1710 */       Label basicBlock = this.firstBasicBlock;
/* 1711 */       while (basicBlock != null) {
/* 1712 */         if ((basicBlock.flags & 0x10) != 0) {
/*      */ 
/*      */           
/* 1715 */           Label subroutine = basicBlock.outgoingEdges.nextEdge.successor;
/* 1716 */           subroutine.addSubroutineRetSuccessors(basicBlock);
/*      */         } 
/* 1718 */         basicBlock = basicBlock.nextBasicBlock;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1726 */     Label listOfBlocksToProcess = this.firstBasicBlock;
/* 1727 */     listOfBlocksToProcess.nextListElement = Label.EMPTY_LIST;
/* 1728 */     int maxStackSize = this.maxStack;
/* 1729 */     while (listOfBlocksToProcess != Label.EMPTY_LIST) {
/*      */ 
/*      */ 
/*      */       
/* 1733 */       Label basicBlock = listOfBlocksToProcess;
/* 1734 */       listOfBlocksToProcess = listOfBlocksToProcess.nextListElement;
/*      */       
/* 1736 */       int inputStackTop = basicBlock.inputStackSize;
/* 1737 */       int maxBlockStackSize = inputStackTop + basicBlock.outputStackMax;
/*      */       
/* 1739 */       if (maxBlockStackSize > maxStackSize) {
/* 1740 */         maxStackSize = maxBlockStackSize;
/*      */       }
/*      */ 
/*      */       
/* 1744 */       Edge outgoingEdge = basicBlock.outgoingEdges;
/* 1745 */       if ((basicBlock.flags & 0x10) != 0)
/*      */       {
/*      */ 
/*      */ 
/*      */         
/* 1750 */         outgoingEdge = outgoingEdge.nextEdge;
/*      */       }
/* 1752 */       while (outgoingEdge != null) {
/* 1753 */         Label successorBlock = outgoingEdge.successor;
/* 1754 */         if (successorBlock.nextListElement == null) {
/* 1755 */           successorBlock
/* 1756 */             .inputStackSize = (short)((outgoingEdge.info == Integer.MAX_VALUE) ? 1 : (inputStackTop + outgoingEdge.info));
/* 1757 */           successorBlock.nextListElement = listOfBlocksToProcess;
/* 1758 */           listOfBlocksToProcess = successorBlock;
/*      */         } 
/* 1760 */         outgoingEdge = outgoingEdge.nextEdge;
/*      */       } 
/*      */     } 
/* 1763 */     this.maxStack = maxStackSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void visitEnd() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addSuccessorToCurrentBasicBlock(int info, Label successor) {
/* 1782 */     this.currentBasicBlock.outgoingEdges = new Edge(info, successor, this.currentBasicBlock.outgoingEdges);
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
/*      */   private void endCurrentBasicBlockWithNoSuccessor() {
/* 1794 */     if (this.compute == 4) {
/* 1795 */       Label nextBasicBlock = new Label();
/* 1796 */       nextBasicBlock.frame = new Frame(nextBasicBlock);
/* 1797 */       nextBasicBlock.resolve(this.code.data, this.code.length);
/* 1798 */       this.lastBasicBlock.nextBasicBlock = nextBasicBlock;
/* 1799 */       this.lastBasicBlock = nextBasicBlock;
/* 1800 */       this.currentBasicBlock = null;
/* 1801 */     } else if (this.compute == 1) {
/* 1802 */       this.currentBasicBlock.outputStackMax = (short)this.maxRelativeStackSize;
/* 1803 */       this.currentBasicBlock = null;
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
/*      */   int visitFrameStart(int offset, int numLocal, int numStack) {
/* 1820 */     int frameLength = 3 + numLocal + numStack;
/* 1821 */     if (this.currentFrame == null || this.currentFrame.length < frameLength) {
/* 1822 */       this.currentFrame = new int[frameLength];
/*      */     }
/* 1824 */     this.currentFrame[0] = offset;
/* 1825 */     this.currentFrame[1] = numLocal;
/* 1826 */     this.currentFrame[2] = numStack;
/* 1827 */     return 3;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void visitAbstractType(int frameIndex, int abstractType) {
/* 1837 */     this.currentFrame[frameIndex] = abstractType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void visitFrameEnd() {
/* 1846 */     if (this.previousFrame != null) {
/* 1847 */       if (this.stackMapTableEntries == null) {
/* 1848 */         this.stackMapTableEntries = new ByteVector();
/*      */       }
/* 1850 */       putFrame();
/* 1851 */       this.stackMapTableNumberOfEntries++;
/*      */     } 
/* 1853 */     this.previousFrame = this.currentFrame;
/* 1854 */     this.currentFrame = null;
/*      */   }
/*      */ 
/*      */   
/*      */   private void putFrame() {
/* 1859 */     int numLocal = this.currentFrame[1];
/* 1860 */     int numStack = this.currentFrame[2];
/* 1861 */     if (this.symbolTable.getMajorVersion() < 50) {
/*      */       
/* 1863 */       this.stackMapTableEntries.putShort(this.currentFrame[0]).putShort(numLocal);
/* 1864 */       putAbstractTypes(3, 3 + numLocal);
/* 1865 */       this.stackMapTableEntries.putShort(numStack);
/* 1866 */       putAbstractTypes(3 + numLocal, 3 + numLocal + numStack);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 1872 */     int offsetDelta = (this.stackMapTableNumberOfEntries == 0) ? this.currentFrame[0] : (this.currentFrame[0] - this.previousFrame[0] - 1);
/* 1873 */     int previousNumlocal = this.previousFrame[1];
/* 1874 */     int numLocalDelta = numLocal - previousNumlocal;
/* 1875 */     int type = 255;
/* 1876 */     if (numStack == 0) {
/* 1877 */       switch (numLocalDelta) {
/*      */         case -3:
/*      */         case -2:
/*      */         case -1:
/* 1881 */           type = 248;
/*      */           break;
/*      */         case 0:
/* 1884 */           type = (offsetDelta < 64) ? 0 : 251;
/*      */           break;
/*      */         case 1:
/*      */         case 2:
/*      */         case 3:
/* 1889 */           type = 252;
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */     
/* 1895 */     } else if (numLocalDelta == 0 && numStack == 1) {
/*      */ 
/*      */ 
/*      */       
/* 1899 */       type = (offsetDelta < 63) ? 64 : 247;
/*      */     } 
/* 1901 */     if (type != 255) {
/*      */       
/* 1903 */       int frameIndex = 3;
/* 1904 */       for (int i = 0; i < previousNumlocal && i < numLocal; i++) {
/* 1905 */         if (this.currentFrame[frameIndex] != this.previousFrame[frameIndex]) {
/* 1906 */           type = 255;
/*      */           break;
/*      */         } 
/* 1909 */         frameIndex++;
/*      */       } 
/*      */     } 
/* 1912 */     switch (type) {
/*      */       case 0:
/* 1914 */         this.stackMapTableEntries.putByte(offsetDelta);
/*      */         return;
/*      */       case 64:
/* 1917 */         this.stackMapTableEntries.putByte(64 + offsetDelta);
/* 1918 */         putAbstractTypes(3 + numLocal, 4 + numLocal);
/*      */         return;
/*      */       case 247:
/* 1921 */         this.stackMapTableEntries
/* 1922 */           .putByte(247)
/* 1923 */           .putShort(offsetDelta);
/* 1924 */         putAbstractTypes(3 + numLocal, 4 + numLocal);
/*      */         return;
/*      */       case 251:
/* 1927 */         this.stackMapTableEntries.putByte(251).putShort(offsetDelta);
/*      */         return;
/*      */       case 248:
/* 1930 */         this.stackMapTableEntries
/* 1931 */           .putByte(251 + numLocalDelta)
/* 1932 */           .putShort(offsetDelta);
/*      */         return;
/*      */       case 252:
/* 1935 */         this.stackMapTableEntries
/* 1936 */           .putByte(251 + numLocalDelta)
/* 1937 */           .putShort(offsetDelta);
/* 1938 */         putAbstractTypes(3 + previousNumlocal, 3 + numLocal);
/*      */         return;
/*      */     } 
/*      */     
/* 1942 */     this.stackMapTableEntries.putByte(255).putShort(offsetDelta).putShort(numLocal);
/* 1943 */     putAbstractTypes(3, 3 + numLocal);
/* 1944 */     this.stackMapTableEntries.putShort(numStack);
/* 1945 */     putAbstractTypes(3 + numLocal, 3 + numLocal + numStack);
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
/*      */   private void putAbstractTypes(int start, int end) {
/* 1958 */     for (int i = start; i < end; i++) {
/* 1959 */       Frame.putAbstractType(this.symbolTable, this.currentFrame[i], this.stackMapTableEntries);
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
/*      */   private void putFrameType(Object type) {
/* 1974 */     if (type instanceof Integer) {
/* 1975 */       this.stackMapTableEntries.putByte(((Integer)type).intValue());
/* 1976 */     } else if (type instanceof String) {
/* 1977 */       this.stackMapTableEntries
/* 1978 */         .putByte(7)
/* 1979 */         .putShort((this.symbolTable.addConstantClass((String)type)).index);
/*      */     } else {
/* 1981 */       this.stackMapTableEntries
/* 1982 */         .putByte(8)
/* 1983 */         .putShort(((Label)type).bytecodeOffset);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean canCopyMethodAttributes(ClassReader source, boolean hasSyntheticAttribute, boolean hasDeprecatedAttribute, int descriptorIndex, int signatureIndex, int exceptionsOffset) {
/* 2026 */     if (source != this.symbolTable.getSource() || descriptorIndex != this.descriptorIndex || signatureIndex != this.signatureIndex || hasDeprecatedAttribute != (((this.accessFlags & 0x20000) != 0)))
/*      */     {
/*      */ 
/*      */       
/* 2030 */       return false;
/*      */     }
/*      */     
/* 2033 */     boolean needSyntheticAttribute = (this.symbolTable.getMajorVersion() < 49 && (this.accessFlags & 0x1000) != 0);
/* 2034 */     if (hasSyntheticAttribute != needSyntheticAttribute) {
/* 2035 */       return false;
/*      */     }
/* 2037 */     if (exceptionsOffset == 0) {
/* 2038 */       if (this.numberOfExceptions != 0) {
/* 2039 */         return false;
/*      */       }
/* 2041 */     } else if (source.readUnsignedShort(exceptionsOffset) == this.numberOfExceptions) {
/* 2042 */       int currentExceptionOffset = exceptionsOffset + 2;
/* 2043 */       for (int i = 0; i < this.numberOfExceptions; i++) {
/* 2044 */         if (source.readUnsignedShort(currentExceptionOffset) != this.exceptionIndexTable[i]) {
/* 2045 */           return false;
/*      */         }
/* 2047 */         currentExceptionOffset += 2;
/*      */       } 
/*      */     } 
/* 2050 */     return true;
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
/*      */   void setMethodAttributesSource(int methodInfoOffset, int methodInfoLength) {
/* 2065 */     this.sourceOffset = methodInfoOffset + 6;
/* 2066 */     this.sourceLength = methodInfoLength - 6;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int computeMethodInfoSize() {
/* 2077 */     if (this.sourceOffset != 0)
/*      */     {
/* 2079 */       return 6 + this.sourceLength;
/*      */     }
/*      */     
/* 2082 */     int size = 8;
/*      */     
/* 2084 */     if (this.code.length > 0) {
/* 2085 */       if (this.code.length > 65535) {
/* 2086 */         throw new MethodTooLargeException(this.symbolTable
/* 2087 */             .getClassName(), this.name, this.descriptor, this.code.length);
/*      */       }
/* 2089 */       this.symbolTable.addConstantUtf8("Code");
/*      */ 
/*      */       
/* 2092 */       size += 16 + this.code.length + Handler.getExceptionTableSize(this.firstHandler);
/* 2093 */       if (this.stackMapTableEntries != null) {
/* 2094 */         boolean useStackMapTable = (this.symbolTable.getMajorVersion() >= 50);
/* 2095 */         this.symbolTable.addConstantUtf8(useStackMapTable ? "StackMapTable" : "StackMap");
/*      */         
/* 2097 */         size += 8 + this.stackMapTableEntries.length;
/*      */       } 
/* 2099 */       if (this.lineNumberTable != null) {
/* 2100 */         this.symbolTable.addConstantUtf8("LineNumberTable");
/*      */         
/* 2102 */         size += 8 + this.lineNumberTable.length;
/*      */       } 
/* 2104 */       if (this.localVariableTable != null) {
/* 2105 */         this.symbolTable.addConstantUtf8("LocalVariableTable");
/*      */         
/* 2107 */         size += 8 + this.localVariableTable.length;
/*      */       } 
/* 2109 */       if (this.localVariableTypeTable != null) {
/* 2110 */         this.symbolTable.addConstantUtf8("LocalVariableTypeTable");
/*      */         
/* 2112 */         size += 8 + this.localVariableTypeTable.length;
/*      */       } 
/* 2114 */       if (this.lastCodeRuntimeVisibleTypeAnnotation != null) {
/* 2115 */         size += this.lastCodeRuntimeVisibleTypeAnnotation
/* 2116 */           .computeAnnotationsSize("RuntimeVisibleTypeAnnotations");
/*      */       }
/*      */       
/* 2119 */       if (this.lastCodeRuntimeInvisibleTypeAnnotation != null) {
/* 2120 */         size += this.lastCodeRuntimeInvisibleTypeAnnotation
/* 2121 */           .computeAnnotationsSize("RuntimeInvisibleTypeAnnotations");
/*      */       }
/*      */       
/* 2124 */       if (this.firstCodeAttribute != null) {
/* 2125 */         size += this.firstCodeAttribute
/* 2126 */           .computeAttributesSize(this.symbolTable, this.code.data, this.code.length, this.maxStack, this.maxLocals);
/*      */       }
/*      */     } 
/*      */     
/* 2130 */     if (this.numberOfExceptions > 0) {
/* 2131 */       this.symbolTable.addConstantUtf8("Exceptions");
/* 2132 */       size += 8 + 2 * this.numberOfExceptions;
/*      */     } 
/* 2134 */     size += Attribute.computeAttributesSize(this.symbolTable, this.accessFlags, this.signatureIndex);
/* 2135 */     size += 
/* 2136 */       AnnotationWriter.computeAnnotationsSize(this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2141 */     if (this.lastRuntimeVisibleParameterAnnotations != null) {
/* 2142 */       size += 
/* 2143 */         AnnotationWriter.computeParameterAnnotationsSize("RuntimeVisibleParameterAnnotations", this.lastRuntimeVisibleParameterAnnotations, 
/*      */ 
/*      */           
/* 2146 */           (this.visibleAnnotableParameterCount == 0) ? 
/* 2147 */           this.lastRuntimeVisibleParameterAnnotations.length : 
/* 2148 */           this.visibleAnnotableParameterCount);
/*      */     }
/* 2150 */     if (this.lastRuntimeInvisibleParameterAnnotations != null) {
/* 2151 */       size += 
/* 2152 */         AnnotationWriter.computeParameterAnnotationsSize("RuntimeInvisibleParameterAnnotations", this.lastRuntimeInvisibleParameterAnnotations, 
/*      */ 
/*      */           
/* 2155 */           (this.invisibleAnnotableParameterCount == 0) ? 
/* 2156 */           this.lastRuntimeInvisibleParameterAnnotations.length : 
/* 2157 */           this.invisibleAnnotableParameterCount);
/*      */     }
/* 2159 */     if (this.defaultValue != null) {
/* 2160 */       this.symbolTable.addConstantUtf8("AnnotationDefault");
/* 2161 */       size += 6 + this.defaultValue.length;
/*      */     } 
/* 2163 */     if (this.parameters != null) {
/* 2164 */       this.symbolTable.addConstantUtf8("MethodParameters");
/*      */       
/* 2166 */       size += 7 + this.parameters.length;
/*      */     } 
/* 2168 */     if (this.firstAttribute != null) {
/* 2169 */       size += this.firstAttribute.computeAttributesSize(this.symbolTable);
/*      */     }
/* 2171 */     return size;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void putMethodInfo(ByteVector output) {
/* 2181 */     boolean useSyntheticAttribute = (this.symbolTable.getMajorVersion() < 49);
/* 2182 */     int mask = useSyntheticAttribute ? 4096 : 0;
/* 2183 */     output.putShort(this.accessFlags & (mask ^ 0xFFFFFFFF)).putShort(this.nameIndex).putShort(this.descriptorIndex);
/*      */     
/* 2185 */     if (this.sourceOffset != 0) {
/* 2186 */       output.putByteArray((this.symbolTable.getSource()).classFileBuffer, this.sourceOffset, this.sourceLength);
/*      */       
/*      */       return;
/*      */     } 
/* 2190 */     int attributeCount = 0;
/* 2191 */     if (this.code.length > 0) {
/* 2192 */       attributeCount++;
/*      */     }
/* 2194 */     if (this.numberOfExceptions > 0) {
/* 2195 */       attributeCount++;
/*      */     }
/* 2197 */     if ((this.accessFlags & 0x1000) != 0 && useSyntheticAttribute) {
/* 2198 */       attributeCount++;
/*      */     }
/* 2200 */     if (this.signatureIndex != 0) {
/* 2201 */       attributeCount++;
/*      */     }
/* 2203 */     if ((this.accessFlags & 0x20000) != 0) {
/* 2204 */       attributeCount++;
/*      */     }
/* 2206 */     if (this.lastRuntimeVisibleAnnotation != null) {
/* 2207 */       attributeCount++;
/*      */     }
/* 2209 */     if (this.lastRuntimeInvisibleAnnotation != null) {
/* 2210 */       attributeCount++;
/*      */     }
/* 2212 */     if (this.lastRuntimeVisibleParameterAnnotations != null) {
/* 2213 */       attributeCount++;
/*      */     }
/* 2215 */     if (this.lastRuntimeInvisibleParameterAnnotations != null) {
/* 2216 */       attributeCount++;
/*      */     }
/* 2218 */     if (this.lastRuntimeVisibleTypeAnnotation != null) {
/* 2219 */       attributeCount++;
/*      */     }
/* 2221 */     if (this.lastRuntimeInvisibleTypeAnnotation != null) {
/* 2222 */       attributeCount++;
/*      */     }
/* 2224 */     if (this.defaultValue != null) {
/* 2225 */       attributeCount++;
/*      */     }
/* 2227 */     if (this.parameters != null) {
/* 2228 */       attributeCount++;
/*      */     }
/* 2230 */     if (this.firstAttribute != null) {
/* 2231 */       attributeCount += this.firstAttribute.getAttributeCount();
/*      */     }
/*      */     
/* 2234 */     output.putShort(attributeCount);
/* 2235 */     if (this.code.length > 0) {
/*      */ 
/*      */       
/* 2238 */       int size = 10 + this.code.length + Handler.getExceptionTableSize(this.firstHandler);
/* 2239 */       int codeAttributeCount = 0;
/* 2240 */       if (this.stackMapTableEntries != null) {
/*      */         
/* 2242 */         size += 8 + this.stackMapTableEntries.length;
/* 2243 */         codeAttributeCount++;
/*      */       } 
/* 2245 */       if (this.lineNumberTable != null) {
/*      */         
/* 2247 */         size += 8 + this.lineNumberTable.length;
/* 2248 */         codeAttributeCount++;
/*      */       } 
/* 2250 */       if (this.localVariableTable != null) {
/*      */         
/* 2252 */         size += 8 + this.localVariableTable.length;
/* 2253 */         codeAttributeCount++;
/*      */       } 
/* 2255 */       if (this.localVariableTypeTable != null) {
/*      */         
/* 2257 */         size += 8 + this.localVariableTypeTable.length;
/* 2258 */         codeAttributeCount++;
/*      */       } 
/* 2260 */       if (this.lastCodeRuntimeVisibleTypeAnnotation != null) {
/* 2261 */         size += this.lastCodeRuntimeVisibleTypeAnnotation
/* 2262 */           .computeAnnotationsSize("RuntimeVisibleTypeAnnotations");
/*      */         
/* 2264 */         codeAttributeCount++;
/*      */       } 
/* 2266 */       if (this.lastCodeRuntimeInvisibleTypeAnnotation != null) {
/* 2267 */         size += this.lastCodeRuntimeInvisibleTypeAnnotation
/* 2268 */           .computeAnnotationsSize("RuntimeInvisibleTypeAnnotations");
/*      */         
/* 2270 */         codeAttributeCount++;
/*      */       } 
/* 2272 */       if (this.firstCodeAttribute != null) {
/* 2273 */         size += this.firstCodeAttribute
/* 2274 */           .computeAttributesSize(this.symbolTable, this.code.data, this.code.length, this.maxStack, this.maxLocals);
/*      */         
/* 2276 */         codeAttributeCount += this.firstCodeAttribute.getAttributeCount();
/*      */       } 
/* 2278 */       output
/* 2279 */         .putShort(this.symbolTable.addConstantUtf8("Code"))
/* 2280 */         .putInt(size)
/* 2281 */         .putShort(this.maxStack)
/* 2282 */         .putShort(this.maxLocals)
/* 2283 */         .putInt(this.code.length)
/* 2284 */         .putByteArray(this.code.data, 0, this.code.length);
/* 2285 */       Handler.putExceptionTable(this.firstHandler, output);
/* 2286 */       output.putShort(codeAttributeCount);
/* 2287 */       if (this.stackMapTableEntries != null) {
/* 2288 */         boolean useStackMapTable = (this.symbolTable.getMajorVersion() >= 50);
/* 2289 */         output
/* 2290 */           .putShort(this.symbolTable
/* 2291 */             .addConstantUtf8(
/* 2292 */               useStackMapTable ? "StackMapTable" : "StackMap"))
/* 2293 */           .putInt(2 + this.stackMapTableEntries.length)
/* 2294 */           .putShort(this.stackMapTableNumberOfEntries)
/* 2295 */           .putByteArray(this.stackMapTableEntries.data, 0, this.stackMapTableEntries.length);
/*      */       } 
/* 2297 */       if (this.lineNumberTable != null) {
/* 2298 */         output
/* 2299 */           .putShort(this.symbolTable.addConstantUtf8("LineNumberTable"))
/* 2300 */           .putInt(2 + this.lineNumberTable.length)
/* 2301 */           .putShort(this.lineNumberTableLength)
/* 2302 */           .putByteArray(this.lineNumberTable.data, 0, this.lineNumberTable.length);
/*      */       }
/* 2304 */       if (this.localVariableTable != null) {
/* 2305 */         output
/* 2306 */           .putShort(this.symbolTable.addConstantUtf8("LocalVariableTable"))
/* 2307 */           .putInt(2 + this.localVariableTable.length)
/* 2308 */           .putShort(this.localVariableTableLength)
/* 2309 */           .putByteArray(this.localVariableTable.data, 0, this.localVariableTable.length);
/*      */       }
/* 2311 */       if (this.localVariableTypeTable != null) {
/* 2312 */         output
/* 2313 */           .putShort(this.symbolTable.addConstantUtf8("LocalVariableTypeTable"))
/* 2314 */           .putInt(2 + this.localVariableTypeTable.length)
/* 2315 */           .putShort(this.localVariableTypeTableLength)
/* 2316 */           .putByteArray(this.localVariableTypeTable.data, 0, this.localVariableTypeTable.length);
/*      */       }
/* 2318 */       if (this.lastCodeRuntimeVisibleTypeAnnotation != null) {
/* 2319 */         this.lastCodeRuntimeVisibleTypeAnnotation.putAnnotations(this.symbolTable
/* 2320 */             .addConstantUtf8("RuntimeVisibleTypeAnnotations"), output);
/*      */       }
/* 2322 */       if (this.lastCodeRuntimeInvisibleTypeAnnotation != null) {
/* 2323 */         this.lastCodeRuntimeInvisibleTypeAnnotation.putAnnotations(this.symbolTable
/* 2324 */             .addConstantUtf8("RuntimeInvisibleTypeAnnotations"), output);
/*      */       }
/* 2326 */       if (this.firstCodeAttribute != null) {
/* 2327 */         this.firstCodeAttribute.putAttributes(this.symbolTable, this.code.data, this.code.length, this.maxStack, this.maxLocals, output);
/*      */       }
/*      */     } 
/*      */     
/* 2331 */     if (this.numberOfExceptions > 0) {
/* 2332 */       output
/* 2333 */         .putShort(this.symbolTable.addConstantUtf8("Exceptions"))
/* 2334 */         .putInt(2 + 2 * this.numberOfExceptions)
/* 2335 */         .putShort(this.numberOfExceptions);
/* 2336 */       for (int exceptionIndex : this.exceptionIndexTable) {
/* 2337 */         output.putShort(exceptionIndex);
/*      */       }
/*      */     } 
/* 2340 */     Attribute.putAttributes(this.symbolTable, this.accessFlags, this.signatureIndex, output);
/* 2341 */     AnnotationWriter.putAnnotations(this.symbolTable, this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation, output);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2348 */     if (this.lastRuntimeVisibleParameterAnnotations != null) {
/* 2349 */       AnnotationWriter.putParameterAnnotations(this.symbolTable
/* 2350 */           .addConstantUtf8("RuntimeVisibleParameterAnnotations"), this.lastRuntimeVisibleParameterAnnotations, 
/*      */           
/* 2352 */           (this.visibleAnnotableParameterCount == 0) ? 
/* 2353 */           this.lastRuntimeVisibleParameterAnnotations.length : 
/* 2354 */           this.visibleAnnotableParameterCount, output);
/*      */     }
/*      */     
/* 2357 */     if (this.lastRuntimeInvisibleParameterAnnotations != null) {
/* 2358 */       AnnotationWriter.putParameterAnnotations(this.symbolTable
/* 2359 */           .addConstantUtf8("RuntimeInvisibleParameterAnnotations"), this.lastRuntimeInvisibleParameterAnnotations, 
/*      */           
/* 2361 */           (this.invisibleAnnotableParameterCount == 0) ? 
/* 2362 */           this.lastRuntimeInvisibleParameterAnnotations.length : 
/* 2363 */           this.invisibleAnnotableParameterCount, output);
/*      */     }
/*      */     
/* 2366 */     if (this.defaultValue != null) {
/* 2367 */       output
/* 2368 */         .putShort(this.symbolTable.addConstantUtf8("AnnotationDefault"))
/* 2369 */         .putInt(this.defaultValue.length)
/* 2370 */         .putByteArray(this.defaultValue.data, 0, this.defaultValue.length);
/*      */     }
/* 2372 */     if (this.parameters != null) {
/* 2373 */       output
/* 2374 */         .putShort(this.symbolTable.addConstantUtf8("MethodParameters"))
/* 2375 */         .putInt(1 + this.parameters.length)
/* 2376 */         .putByte(this.parametersCount)
/* 2377 */         .putByteArray(this.parameters.data, 0, this.parameters.length);
/*      */     }
/* 2379 */     if (this.firstAttribute != null) {
/* 2380 */       this.firstAttribute.putAttributes(this.symbolTable, output);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void collectAttributePrototypes(Attribute.Set attributePrototypes) {
/* 2390 */     attributePrototypes.addAttributes(this.firstAttribute);
/* 2391 */     attributePrototypes.addAttributes(this.firstCodeAttribute);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\MethodWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */