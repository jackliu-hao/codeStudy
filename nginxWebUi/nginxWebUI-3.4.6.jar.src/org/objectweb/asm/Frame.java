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
/*      */ class Frame
/*      */ {
/*      */   static final int SAME_FRAME = 0;
/*      */   static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
/*      */   static final int RESERVED = 128;
/*      */   static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
/*      */   static final int CHOP_FRAME = 248;
/*      */   static final int SAME_FRAME_EXTENDED = 251;
/*      */   static final int APPEND_FRAME = 252;
/*      */   static final int FULL_FRAME = 255;
/*      */   static final int ITEM_TOP = 0;
/*      */   static final int ITEM_INTEGER = 1;
/*      */   static final int ITEM_FLOAT = 2;
/*      */   static final int ITEM_DOUBLE = 3;
/*      */   static final int ITEM_LONG = 4;
/*      */   static final int ITEM_NULL = 5;
/*      */   static final int ITEM_UNINITIALIZED_THIS = 6;
/*      */   static final int ITEM_OBJECT = 7;
/*      */   static final int ITEM_UNINITIALIZED = 8;
/*      */   private static final int ITEM_ASM_BOOLEAN = 9;
/*      */   private static final int ITEM_ASM_BYTE = 10;
/*      */   private static final int ITEM_ASM_CHAR = 11;
/*      */   private static final int ITEM_ASM_SHORT = 12;
/*      */   private static final int DIM_SIZE = 6;
/*      */   private static final int KIND_SIZE = 4;
/*      */   private static final int FLAGS_SIZE = 2;
/*      */   private static final int VALUE_SIZE = 20;
/*      */   private static final int DIM_SHIFT = 26;
/*      */   private static final int KIND_SHIFT = 22;
/*      */   private static final int FLAGS_SHIFT = 20;
/*      */   private static final int DIM_MASK = -67108864;
/*      */   private static final int KIND_MASK = 62914560;
/*      */   private static final int VALUE_MASK = 1048575;
/*      */   private static final int ARRAY_OF = 67108864;
/*      */   private static final int ELEMENT_OF = -67108864;
/*      */   private static final int CONSTANT_KIND = 4194304;
/*      */   private static final int REFERENCE_KIND = 8388608;
/*      */   private static final int UNINITIALIZED_KIND = 12582912;
/*      */   private static final int LOCAL_KIND = 16777216;
/*      */   private static final int STACK_KIND = 20971520;
/*      */   private static final int TOP_IF_LONG_OR_DOUBLE_FLAG = 1048576;
/*      */   private static final int TOP = 4194304;
/*      */   private static final int BOOLEAN = 4194313;
/*      */   private static final int BYTE = 4194314;
/*      */   private static final int CHAR = 4194315;
/*      */   private static final int SHORT = 4194316;
/*      */   private static final int INTEGER = 4194305;
/*      */   private static final int FLOAT = 4194306;
/*      */   private static final int LONG = 4194308;
/*      */   private static final int DOUBLE = 4194307;
/*      */   private static final int NULL = 4194309;
/*      */   private static final int UNINITIALIZED_THIS = 4194310;
/*      */   Label owner;
/*      */   private int[] inputLocals;
/*      */   private int[] inputStack;
/*      */   private int[] outputLocals;
/*      */   private int[] outputStack;
/*      */   private short outputStackStart;
/*      */   private short outputStackTop;
/*      */   private int initializationCount;
/*      */   private int[] initializations;
/*      */   
/*      */   Frame(Label owner) {
/*  243 */     this.owner = owner;
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
/*      */   final void copyFrom(Frame frame) {
/*  255 */     this.inputLocals = frame.inputLocals;
/*  256 */     this.inputStack = frame.inputStack;
/*  257 */     this.outputStackStart = 0;
/*  258 */     this.outputLocals = frame.outputLocals;
/*  259 */     this.outputStack = frame.outputStack;
/*  260 */     this.outputStackTop = frame.outputStackTop;
/*  261 */     this.initializationCount = frame.initializationCount;
/*  262 */     this.initializations = frame.initializations;
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
/*      */   static int getAbstractTypeFromApiFormat(SymbolTable symbolTable, Object type) {
/*  281 */     if (type instanceof Integer)
/*  282 */       return 0x400000 | ((Integer)type).intValue(); 
/*  283 */     if (type instanceof String) {
/*  284 */       String descriptor = Type.getObjectType((String)type).getDescriptor();
/*  285 */       return getAbstractTypeFromDescriptor(symbolTable, descriptor, 0);
/*      */     } 
/*  287 */     return 0xC00000 | symbolTable
/*  288 */       .addUninitializedType("", ((Label)type).bytecodeOffset);
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
/*      */   static int getAbstractTypeFromInternalName(SymbolTable symbolTable, String internalName) {
/*  302 */     return 0x800000 | symbolTable.addType(internalName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int getAbstractTypeFromDescriptor(SymbolTable symbolTable, String buffer, int offset) {
/*      */     String internalName;
/*      */     int elementDescriptorOffset;
/*      */     int typeValue;
/*  316 */     switch (buffer.charAt(offset)) {
/*      */       case 'V':
/*  318 */         return 0;
/*      */       case 'B':
/*      */       case 'C':
/*      */       case 'I':
/*      */       case 'S':
/*      */       case 'Z':
/*  324 */         return 4194305;
/*      */       case 'F':
/*  326 */         return 4194306;
/*      */       case 'J':
/*  328 */         return 4194308;
/*      */       case 'D':
/*  330 */         return 4194307;
/*      */       case 'L':
/*  332 */         internalName = buffer.substring(offset + 1, buffer.length() - 1);
/*  333 */         return 0x800000 | symbolTable.addType(internalName);
/*      */       case '[':
/*  335 */         elementDescriptorOffset = offset + 1;
/*  336 */         while (buffer.charAt(elementDescriptorOffset) == '[') {
/*  337 */           elementDescriptorOffset++;
/*      */         }
/*      */         
/*  340 */         switch (buffer.charAt(elementDescriptorOffset)) {
/*      */           case 'Z':
/*  342 */             typeValue = 4194313;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  372 */             return elementDescriptorOffset - offset << 26 | typeValue;case 'C': typeValue = 4194315; return elementDescriptorOffset - offset << 26 | typeValue;case 'B': typeValue = 4194314; return elementDescriptorOffset - offset << 26 | typeValue;case 'S': typeValue = 4194316; return elementDescriptorOffset - offset << 26 | typeValue;case 'I': typeValue = 4194305; return elementDescriptorOffset - offset << 26 | typeValue;case 'F': typeValue = 4194306; return elementDescriptorOffset - offset << 26 | typeValue;case 'J': typeValue = 4194308; return elementDescriptorOffset - offset << 26 | typeValue;case 'D': typeValue = 4194307; return elementDescriptorOffset - offset << 26 | typeValue;case 'L': internalName = buffer.substring(elementDescriptorOffset + 1, buffer.length() - 1); typeValue = 0x800000 | symbolTable.addType(internalName); return elementDescriptorOffset - offset << 26 | typeValue;
/*      */         }  throw new IllegalArgumentException();
/*  374 */     }  throw new IllegalArgumentException();
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
/*      */   final void setInputFrameFromDescriptor(SymbolTable symbolTable, int access, String descriptor, int maxLocals) {
/*  397 */     this.inputLocals = new int[maxLocals];
/*  398 */     this.inputStack = new int[0];
/*  399 */     int inputLocalIndex = 0;
/*  400 */     if ((access & 0x8) == 0) {
/*  401 */       if ((access & 0x40000) == 0) {
/*  402 */         this.inputLocals[inputLocalIndex++] = 0x800000 | symbolTable
/*  403 */           .addType(symbolTable.getClassName());
/*      */       } else {
/*  405 */         this.inputLocals[inputLocalIndex++] = 4194310;
/*      */       } 
/*      */     }
/*  408 */     for (Type argumentType : Type.getArgumentTypes(descriptor)) {
/*      */       
/*  410 */       int abstractType = getAbstractTypeFromDescriptor(symbolTable, argumentType.getDescriptor(), 0);
/*  411 */       this.inputLocals[inputLocalIndex++] = abstractType;
/*  412 */       if (abstractType == 4194308 || abstractType == 4194307) {
/*  413 */         this.inputLocals[inputLocalIndex++] = 4194304;
/*      */       }
/*      */     } 
/*  416 */     while (inputLocalIndex < maxLocals) {
/*  417 */       this.inputLocals[inputLocalIndex++] = 4194304;
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
/*      */   final void setInputFrameFromApiFormat(SymbolTable symbolTable, int numLocal, Object[] local, int numStack, Object[] stack) {
/*  438 */     int inputLocalIndex = 0;
/*  439 */     for (int i = 0; i < numLocal; i++) {
/*  440 */       this.inputLocals[inputLocalIndex++] = getAbstractTypeFromApiFormat(symbolTable, local[i]);
/*  441 */       if (local[i] == Opcodes.LONG || local[i] == Opcodes.DOUBLE) {
/*  442 */         this.inputLocals[inputLocalIndex++] = 4194304;
/*      */       }
/*      */     } 
/*  445 */     while (inputLocalIndex < this.inputLocals.length) {
/*  446 */       this.inputLocals[inputLocalIndex++] = 4194304;
/*      */     }
/*  448 */     int numStackTop = 0;
/*  449 */     for (int j = 0; j < numStack; j++) {
/*  450 */       if (stack[j] == Opcodes.LONG || stack[j] == Opcodes.DOUBLE) {
/*  451 */         numStackTop++;
/*      */       }
/*      */     } 
/*  454 */     this.inputStack = new int[numStack + numStackTop];
/*  455 */     int inputStackIndex = 0;
/*  456 */     for (int k = 0; k < numStack; k++) {
/*  457 */       this.inputStack[inputStackIndex++] = getAbstractTypeFromApiFormat(symbolTable, stack[k]);
/*  458 */       if (stack[k] == Opcodes.LONG || stack[k] == Opcodes.DOUBLE) {
/*  459 */         this.inputStack[inputStackIndex++] = 4194304;
/*      */       }
/*      */     } 
/*  462 */     this.outputStackTop = 0;
/*  463 */     this.initializationCount = 0;
/*      */   }
/*      */   
/*      */   final int getInputStackSize() {
/*  467 */     return this.inputStack.length;
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
/*      */   private int getLocal(int localIndex) {
/*  481 */     if (this.outputLocals == null || localIndex >= this.outputLocals.length)
/*      */     {
/*      */       
/*  484 */       return 0x1000000 | localIndex;
/*      */     }
/*  486 */     int abstractType = this.outputLocals[localIndex];
/*  487 */     if (abstractType == 0)
/*      */     {
/*      */       
/*  490 */       abstractType = this.outputLocals[localIndex] = 0x1000000 | localIndex;
/*      */     }
/*  492 */     return abstractType;
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
/*      */   private void setLocal(int localIndex, int abstractType) {
/*  504 */     if (this.outputLocals == null) {
/*  505 */       this.outputLocals = new int[10];
/*      */     }
/*  507 */     int outputLocalsLength = this.outputLocals.length;
/*  508 */     if (localIndex >= outputLocalsLength) {
/*  509 */       int[] newOutputLocals = new int[Math.max(localIndex + 1, 2 * outputLocalsLength)];
/*  510 */       System.arraycopy(this.outputLocals, 0, newOutputLocals, 0, outputLocalsLength);
/*  511 */       this.outputLocals = newOutputLocals;
/*      */     } 
/*      */     
/*  514 */     this.outputLocals[localIndex] = abstractType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void push(int abstractType) {
/*  524 */     if (this.outputStack == null) {
/*  525 */       this.outputStack = new int[10];
/*      */     }
/*  527 */     int outputStackLength = this.outputStack.length;
/*  528 */     if (this.outputStackTop >= outputStackLength) {
/*  529 */       int[] newOutputStack = new int[Math.max(this.outputStackTop + 1, 2 * outputStackLength)];
/*  530 */       System.arraycopy(this.outputStack, 0, newOutputStack, 0, outputStackLength);
/*  531 */       this.outputStack = newOutputStack;
/*      */     } 
/*      */     
/*  534 */     this.outputStackTop = (short)(this.outputStackTop + 1); this.outputStack[this.outputStackTop] = abstractType;
/*      */ 
/*      */     
/*  537 */     short outputStackSize = (short)(this.outputStackStart + this.outputStackTop);
/*  538 */     if (outputStackSize > this.owner.outputStackMax) {
/*  539 */       this.owner.outputStackMax = outputStackSize;
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
/*      */   private void push(SymbolTable symbolTable, String descriptor) {
/*  551 */     int typeDescriptorOffset = (descriptor.charAt(0) == '(') ? Type.getReturnTypeOffset(descriptor) : 0;
/*  552 */     int abstractType = getAbstractTypeFromDescriptor(symbolTable, descriptor, typeDescriptorOffset);
/*  553 */     if (abstractType != 0) {
/*  554 */       push(abstractType);
/*  555 */       if (abstractType == 4194308 || abstractType == 4194307) {
/*  556 */         push(4194304);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int pop() {
/*  567 */     if (this.outputStackTop > 0) {
/*  568 */       return this.outputStack[this.outputStackTop = (short)(this.outputStackTop - 1)];
/*      */     }
/*      */     
/*  571 */     return 0x1400000 | -(this.outputStackStart = (short)(this.outputStackStart - 1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void pop(int elements) {
/*  581 */     if (this.outputStackTop >= elements) {
/*  582 */       this.outputStackTop = (short)(this.outputStackTop - elements);
/*      */     }
/*      */     else {
/*      */       
/*  586 */       this.outputStackStart = (short)(this.outputStackStart - elements - this.outputStackTop);
/*  587 */       this.outputStackTop = 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void pop(String descriptor) {
/*  597 */     char firstDescriptorChar = descriptor.charAt(0);
/*  598 */     if (firstDescriptorChar == '(') {
/*  599 */       pop((Type.getArgumentsAndReturnSizes(descriptor) >> 2) - 1);
/*  600 */     } else if (firstDescriptorChar == 'J' || firstDescriptorChar == 'D') {
/*  601 */       pop(2);
/*      */     } else {
/*  603 */       pop(1);
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
/*      */   private void addInitializedType(int abstractType) {
/*  619 */     if (this.initializations == null) {
/*  620 */       this.initializations = new int[2];
/*      */     }
/*  622 */     int initializationsLength = this.initializations.length;
/*  623 */     if (this.initializationCount >= initializationsLength) {
/*      */       
/*  625 */       int[] newInitializations = new int[Math.max(this.initializationCount + 1, 2 * initializationsLength)];
/*  626 */       System.arraycopy(this.initializations, 0, newInitializations, 0, initializationsLength);
/*  627 */       this.initializations = newInitializations;
/*      */     } 
/*      */     
/*  630 */     this.initializations[this.initializationCount++] = abstractType;
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
/*      */   private int getInitializedType(SymbolTable symbolTable, int abstractType) {
/*  643 */     if (abstractType == 4194310 || (abstractType & 0xFFC00000) == 12582912)
/*      */     {
/*  645 */       for (int i = 0; i < this.initializationCount; i++) {
/*  646 */         int initializedType = this.initializations[i];
/*  647 */         int dim = initializedType & 0xFC000000;
/*  648 */         int kind = initializedType & 0x3C00000;
/*  649 */         int value = initializedType & 0xFFFFF;
/*  650 */         if (kind == 16777216) {
/*  651 */           initializedType = dim + this.inputLocals[value];
/*  652 */         } else if (kind == 20971520) {
/*  653 */           initializedType = dim + this.inputStack[this.inputStack.length - value];
/*      */         } 
/*  655 */         if (abstractType == initializedType) {
/*  656 */           if (abstractType == 4194310) {
/*  657 */             return 0x800000 | symbolTable.addType(symbolTable.getClassName());
/*      */           }
/*  659 */           return 0x800000 | symbolTable
/*  660 */             .addType((symbolTable.getType(abstractType & 0xFFFFF)).value);
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*  665 */     return abstractType;
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
/*      */   void execute(int opcode, int arg, Symbol argSymbol, SymbolTable symbolTable) {
/*      */     int abstractType1;
/*      */     int abstractType2;
/*      */     int abstractType3;
/*      */     int abstractType4;
/*      */     String arrayElementType;
/*      */     String castType;
/*  687 */     switch (opcode) {
/*      */       case 0:
/*      */       case 116:
/*      */       case 117:
/*      */       case 118:
/*      */       case 119:
/*      */       case 145:
/*      */       case 146:
/*      */       case 147:
/*      */       case 167:
/*      */       case 177:
/*      */         return;
/*      */       case 1:
/*  700 */         push(4194309);
/*      */       
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 16:
/*      */       case 17:
/*      */       case 21:
/*  712 */         push(4194305);
/*      */       
/*      */       case 9:
/*      */       case 10:
/*      */       case 22:
/*  717 */         push(4194308);
/*  718 */         push(4194304);
/*      */       
/*      */       case 11:
/*      */       case 12:
/*      */       case 13:
/*      */       case 23:
/*  724 */         push(4194306);
/*      */       
/*      */       case 14:
/*      */       case 15:
/*      */       case 24:
/*  729 */         push(4194307);
/*  730 */         push(4194304);
/*      */       
/*      */       case 18:
/*  733 */         switch (argSymbol.tag) {
/*      */           case 3:
/*  735 */             push(4194305);
/*      */           
/*      */           case 5:
/*  738 */             push(4194308);
/*  739 */             push(4194304);
/*      */           
/*      */           case 4:
/*  742 */             push(4194306);
/*      */           
/*      */           case 6:
/*  745 */             push(4194307);
/*  746 */             push(4194304);
/*      */           
/*      */           case 7:
/*  749 */             push(0x800000 | symbolTable.addType("java/lang/Class"));
/*      */           
/*      */           case 8:
/*  752 */             push(0x800000 | symbolTable.addType("java/lang/String"));
/*      */           
/*      */           case 16:
/*  755 */             push(0x800000 | symbolTable.addType("java/lang/invoke/MethodType"));
/*      */           
/*      */           case 15:
/*  758 */             push(0x800000 | symbolTable.addType("java/lang/invoke/MethodHandle"));
/*      */           
/*      */           case 17:
/*  761 */             push(symbolTable, argSymbol.value);
/*      */         } 
/*      */         
/*  764 */         throw new AssertionError();
/*      */ 
/*      */       
/*      */       case 25:
/*  768 */         push(getLocal(arg));
/*      */       
/*      */       case 47:
/*      */       case 143:
/*  772 */         pop(2);
/*  773 */         push(4194308);
/*  774 */         push(4194304);
/*      */       
/*      */       case 49:
/*      */       case 138:
/*  778 */         pop(2);
/*  779 */         push(4194307);
/*  780 */         push(4194304);
/*      */       
/*      */       case 50:
/*  783 */         pop(1);
/*  784 */         abstractType1 = pop();
/*  785 */         push((abstractType1 == 4194309) ? abstractType1 : (-67108864 + abstractType1));
/*      */       
/*      */       case 54:
/*      */       case 56:
/*      */       case 58:
/*  790 */         abstractType1 = pop();
/*  791 */         setLocal(arg, abstractType1);
/*  792 */         if (arg > 0) {
/*  793 */           int previousLocalType = getLocal(arg - 1);
/*  794 */           if (previousLocalType == 4194308 || previousLocalType == 4194307) {
/*  795 */             setLocal(arg - 1, 4194304);
/*  796 */           } else if ((previousLocalType & 0x3C00000) == 16777216 || (previousLocalType & 0x3C00000) == 20971520) {
/*      */ 
/*      */ 
/*      */             
/*  800 */             setLocal(arg - 1, previousLocalType | 0x100000);
/*      */           } 
/*      */         } 
/*      */       
/*      */       case 55:
/*      */       case 57:
/*  806 */         pop(1);
/*  807 */         abstractType1 = pop();
/*  808 */         setLocal(arg, abstractType1);
/*  809 */         setLocal(arg + 1, 4194304);
/*  810 */         if (arg > 0) {
/*  811 */           int previousLocalType = getLocal(arg - 1);
/*  812 */           if (previousLocalType == 4194308 || previousLocalType == 4194307) {
/*  813 */             setLocal(arg - 1, 4194304);
/*  814 */           } else if ((previousLocalType & 0x3C00000) == 16777216 || (previousLocalType & 0x3C00000) == 20971520) {
/*      */ 
/*      */ 
/*      */             
/*  818 */             setLocal(arg - 1, previousLocalType | 0x100000);
/*      */           } 
/*      */         } 
/*      */       
/*      */       case 79:
/*      */       case 81:
/*      */       case 83:
/*      */       case 84:
/*      */       case 85:
/*      */       case 86:
/*  828 */         pop(3);
/*      */       
/*      */       case 80:
/*      */       case 82:
/*  832 */         pop(4);
/*      */       
/*      */       case 87:
/*      */       case 153:
/*      */       case 154:
/*      */       case 155:
/*      */       case 156:
/*      */       case 157:
/*      */       case 158:
/*      */       case 170:
/*      */       case 171:
/*      */       case 172:
/*      */       case 174:
/*      */       case 176:
/*      */       case 191:
/*      */       case 194:
/*      */       case 195:
/*      */       case 198:
/*      */       case 199:
/*  851 */         pop(1);
/*      */       
/*      */       case 88:
/*      */       case 159:
/*      */       case 160:
/*      */       case 161:
/*      */       case 162:
/*      */       case 163:
/*      */       case 164:
/*      */       case 165:
/*      */       case 166:
/*      */       case 173:
/*      */       case 175:
/*  864 */         pop(2);
/*      */       
/*      */       case 89:
/*  867 */         abstractType1 = pop();
/*  868 */         push(abstractType1);
/*  869 */         push(abstractType1);
/*      */       
/*      */       case 90:
/*  872 */         abstractType1 = pop();
/*  873 */         abstractType2 = pop();
/*  874 */         push(abstractType1);
/*  875 */         push(abstractType2);
/*  876 */         push(abstractType1);
/*      */       
/*      */       case 91:
/*  879 */         abstractType1 = pop();
/*  880 */         abstractType2 = pop();
/*  881 */         abstractType3 = pop();
/*  882 */         push(abstractType1);
/*  883 */         push(abstractType3);
/*  884 */         push(abstractType2);
/*  885 */         push(abstractType1);
/*      */       
/*      */       case 92:
/*  888 */         abstractType1 = pop();
/*  889 */         abstractType2 = pop();
/*  890 */         push(abstractType2);
/*  891 */         push(abstractType1);
/*  892 */         push(abstractType2);
/*  893 */         push(abstractType1);
/*      */       
/*      */       case 93:
/*  896 */         abstractType1 = pop();
/*  897 */         abstractType2 = pop();
/*  898 */         abstractType3 = pop();
/*  899 */         push(abstractType2);
/*  900 */         push(abstractType1);
/*  901 */         push(abstractType3);
/*  902 */         push(abstractType2);
/*  903 */         push(abstractType1);
/*      */       
/*      */       case 94:
/*  906 */         abstractType1 = pop();
/*  907 */         abstractType2 = pop();
/*  908 */         abstractType3 = pop();
/*  909 */         abstractType4 = pop();
/*  910 */         push(abstractType2);
/*  911 */         push(abstractType1);
/*  912 */         push(abstractType4);
/*  913 */         push(abstractType3);
/*  914 */         push(abstractType2);
/*  915 */         push(abstractType1);
/*      */       
/*      */       case 95:
/*  918 */         abstractType1 = pop();
/*  919 */         abstractType2 = pop();
/*  920 */         push(abstractType1);
/*  921 */         push(abstractType2);
/*      */       
/*      */       case 46:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 96:
/*      */       case 100:
/*      */       case 104:
/*      */       case 108:
/*      */       case 112:
/*      */       case 120:
/*      */       case 122:
/*      */       case 124:
/*      */       case 126:
/*      */       case 128:
/*      */       case 130:
/*      */       case 136:
/*      */       case 142:
/*      */       case 149:
/*      */       case 150:
/*  942 */         pop(2);
/*  943 */         push(4194305);
/*      */       
/*      */       case 97:
/*      */       case 101:
/*      */       case 105:
/*      */       case 109:
/*      */       case 113:
/*      */       case 127:
/*      */       case 129:
/*      */       case 131:
/*  953 */         pop(4);
/*  954 */         push(4194308);
/*  955 */         push(4194304);
/*      */       
/*      */       case 48:
/*      */       case 98:
/*      */       case 102:
/*      */       case 106:
/*      */       case 110:
/*      */       case 114:
/*      */       case 137:
/*      */       case 144:
/*  965 */         pop(2);
/*  966 */         push(4194306);
/*      */       
/*      */       case 99:
/*      */       case 103:
/*      */       case 107:
/*      */       case 111:
/*      */       case 115:
/*  973 */         pop(4);
/*  974 */         push(4194307);
/*  975 */         push(4194304);
/*      */       
/*      */       case 121:
/*      */       case 123:
/*      */       case 125:
/*  980 */         pop(3);
/*  981 */         push(4194308);
/*  982 */         push(4194304);
/*      */       
/*      */       case 132:
/*  985 */         setLocal(arg, 4194305);
/*      */       
/*      */       case 133:
/*      */       case 140:
/*  989 */         pop(1);
/*  990 */         push(4194308);
/*  991 */         push(4194304);
/*      */       
/*      */       case 134:
/*  994 */         pop(1);
/*  995 */         push(4194306);
/*      */       
/*      */       case 135:
/*      */       case 141:
/*  999 */         pop(1);
/* 1000 */         push(4194307);
/* 1001 */         push(4194304);
/*      */       
/*      */       case 139:
/*      */       case 190:
/*      */       case 193:
/* 1006 */         pop(1);
/* 1007 */         push(4194305);
/*      */       
/*      */       case 148:
/*      */       case 151:
/*      */       case 152:
/* 1012 */         pop(4);
/* 1013 */         push(4194305);
/*      */       
/*      */       case 168:
/*      */       case 169:
/* 1017 */         throw new IllegalArgumentException("JSR/RET are not supported with computeFrames option");
/*      */       case 178:
/* 1019 */         push(symbolTable, argSymbol.value);
/*      */       
/*      */       case 179:
/* 1022 */         pop(argSymbol.value);
/*      */       
/*      */       case 180:
/* 1025 */         pop(1);
/* 1026 */         push(symbolTable, argSymbol.value);
/*      */       
/*      */       case 181:
/* 1029 */         pop(argSymbol.value);
/* 1030 */         pop();
/*      */       
/*      */       case 182:
/*      */       case 183:
/*      */       case 184:
/*      */       case 185:
/* 1036 */         pop(argSymbol.value);
/* 1037 */         if (opcode != 184) {
/* 1038 */           abstractType1 = pop();
/* 1039 */           if (opcode == 183 && argSymbol.name.charAt(0) == '<') {
/* 1040 */             addInitializedType(abstractType1);
/*      */           }
/*      */         } 
/* 1043 */         push(symbolTable, argSymbol.value);
/*      */       
/*      */       case 186:
/* 1046 */         pop(argSymbol.value);
/* 1047 */         push(symbolTable, argSymbol.value);
/*      */       
/*      */       case 187:
/* 1050 */         push(0xC00000 | symbolTable.addUninitializedType(argSymbol.value, arg));
/*      */       
/*      */       case 188:
/* 1053 */         pop();
/* 1054 */         switch (arg) {
/*      */           case 4:
/* 1056 */             push(71303177);
/*      */           
/*      */           case 5:
/* 1059 */             push(71303179);
/*      */           
/*      */           case 8:
/* 1062 */             push(71303178);
/*      */           
/*      */           case 9:
/* 1065 */             push(71303180);
/*      */           
/*      */           case 10:
/* 1068 */             push(71303169);
/*      */           
/*      */           case 6:
/* 1071 */             push(71303170);
/*      */           
/*      */           case 7:
/* 1074 */             push(71303171);
/*      */           
/*      */           case 11:
/* 1077 */             push(71303172);
/*      */         } 
/*      */         
/* 1080 */         throw new IllegalArgumentException();
/*      */ 
/*      */       
/*      */       case 189:
/* 1084 */         arrayElementType = argSymbol.value;
/* 1085 */         pop();
/* 1086 */         if (arrayElementType.charAt(0) == '[') {
/* 1087 */           push(symbolTable, '[' + arrayElementType);
/*      */         } else {
/* 1089 */           push(0x4800000 | symbolTable.addType(arrayElementType));
/*      */         } 
/*      */       
/*      */       case 192:
/* 1093 */         castType = argSymbol.value;
/* 1094 */         pop();
/* 1095 */         if (castType.charAt(0) == '[') {
/* 1096 */           push(symbolTable, castType);
/*      */         } else {
/* 1098 */           push(0x800000 | symbolTable.addType(castType));
/*      */         } 
/*      */       
/*      */       case 197:
/* 1102 */         pop(arg);
/* 1103 */         push(symbolTable, argSymbol.value);
/*      */     } 
/*      */     
/* 1106 */     throw new IllegalArgumentException();
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
/*      */   private int getConcreteOutputType(int abstractOutputType, int numStack) {
/* 1123 */     int dim = abstractOutputType & 0xFC000000;
/* 1124 */     int kind = abstractOutputType & 0x3C00000;
/* 1125 */     if (kind == 16777216) {
/*      */ 
/*      */ 
/*      */       
/* 1129 */       int concreteOutputType = dim + this.inputLocals[abstractOutputType & 0xFFFFF];
/* 1130 */       if ((abstractOutputType & 0x100000) != 0 && (concreteOutputType == 4194308 || concreteOutputType == 4194307))
/*      */       {
/* 1132 */         concreteOutputType = 4194304;
/*      */       }
/* 1134 */       return concreteOutputType;
/* 1135 */     }  if (kind == 20971520) {
/*      */ 
/*      */ 
/*      */       
/* 1139 */       int concreteOutputType = dim + this.inputStack[numStack - (abstractOutputType & 0xFFFFF)];
/* 1140 */       if ((abstractOutputType & 0x100000) != 0 && (concreteOutputType == 4194308 || concreteOutputType == 4194307))
/*      */       {
/* 1142 */         concreteOutputType = 4194304;
/*      */       }
/* 1144 */       return concreteOutputType;
/*      */     } 
/* 1146 */     return abstractOutputType;
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
/*      */   final boolean merge(SymbolTable symbolTable, Frame dstFrame, int catchTypeIndex) {
/* 1164 */     boolean frameChanged = false;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1169 */     int numLocal = this.inputLocals.length;
/* 1170 */     int numStack = this.inputStack.length;
/* 1171 */     if (dstFrame.inputLocals == null) {
/* 1172 */       dstFrame.inputLocals = new int[numLocal];
/* 1173 */       frameChanged = true;
/*      */     }  int i;
/* 1175 */     for (i = 0; i < numLocal; i++) {
/*      */       int concreteOutputType;
/* 1177 */       if (this.outputLocals != null && i < this.outputLocals.length) {
/* 1178 */         int abstractOutputType = this.outputLocals[i];
/* 1179 */         if (abstractOutputType == 0) {
/*      */ 
/*      */           
/* 1182 */           concreteOutputType = this.inputLocals[i];
/*      */         } else {
/* 1184 */           concreteOutputType = getConcreteOutputType(abstractOutputType, numStack);
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1189 */         concreteOutputType = this.inputLocals[i];
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1194 */       if (this.initializations != null) {
/* 1195 */         concreteOutputType = getInitializedType(symbolTable, concreteOutputType);
/*      */       }
/* 1197 */       frameChanged |= merge(symbolTable, concreteOutputType, dstFrame.inputLocals, i);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1205 */     if (catchTypeIndex > 0) {
/* 1206 */       for (i = 0; i < numLocal; i++) {
/* 1207 */         frameChanged |= merge(symbolTable, this.inputLocals[i], dstFrame.inputLocals, i);
/*      */       }
/* 1209 */       if (dstFrame.inputStack == null) {
/* 1210 */         dstFrame.inputStack = new int[1];
/* 1211 */         frameChanged = true;
/*      */       } 
/* 1213 */       frameChanged |= merge(symbolTable, catchTypeIndex, dstFrame.inputStack, 0);
/* 1214 */       return frameChanged;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1220 */     int numInputStack = this.inputStack.length + this.outputStackStart;
/* 1221 */     if (dstFrame.inputStack == null) {
/* 1222 */       dstFrame.inputStack = new int[numInputStack + this.outputStackTop];
/* 1223 */       frameChanged = true;
/*      */     } 
/*      */     
/*      */     int j;
/*      */     
/* 1228 */     for (j = 0; j < numInputStack; j++) {
/* 1229 */       int concreteOutputType = this.inputStack[j];
/* 1230 */       if (this.initializations != null) {
/* 1231 */         concreteOutputType = getInitializedType(symbolTable, concreteOutputType);
/*      */       }
/* 1233 */       frameChanged |= merge(symbolTable, concreteOutputType, dstFrame.inputStack, j);
/*      */     } 
/*      */ 
/*      */     
/* 1237 */     for (j = 0; j < this.outputStackTop; j++) {
/* 1238 */       int abstractOutputType = this.outputStack[j];
/* 1239 */       int concreteOutputType = getConcreteOutputType(abstractOutputType, numStack);
/* 1240 */       if (this.initializations != null) {
/* 1241 */         concreteOutputType = getInitializedType(symbolTable, concreteOutputType);
/*      */       }
/* 1243 */       frameChanged |= 
/* 1244 */         merge(symbolTable, concreteOutputType, dstFrame.inputStack, numInputStack + j);
/*      */     } 
/* 1246 */     return frameChanged;
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
/*      */   private static boolean merge(SymbolTable symbolTable, int sourceType, int[] dstTypes, int dstIndex) {
/* 1268 */     int mergedType, dstType = dstTypes[dstIndex];
/* 1269 */     if (dstType == sourceType)
/*      */     {
/* 1271 */       return false;
/*      */     }
/* 1273 */     int srcType = sourceType;
/* 1274 */     if ((sourceType & 0x3FFFFFF) == 4194309) {
/* 1275 */       if (dstType == 4194309) {
/* 1276 */         return false;
/*      */       }
/* 1278 */       srcType = 4194309;
/*      */     } 
/* 1280 */     if (dstType == 0) {
/*      */       
/* 1282 */       dstTypes[dstIndex] = srcType;
/* 1283 */       return true;
/*      */     } 
/*      */     
/* 1286 */     if ((dstType & 0xFC000000) != 0 || (dstType & 0x3C00000) == 8388608) {
/*      */       
/* 1288 */       if (srcType == 4194309)
/*      */       {
/* 1290 */         return false; } 
/* 1291 */       if ((srcType & 0xFFC00000) == (dstType & 0xFFC00000)) {
/*      */         
/* 1293 */         if ((dstType & 0x3C00000) == 8388608) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1299 */           mergedType = srcType & 0xFC000000 | 0x800000 | symbolTable.addMergedType(srcType & 0xFFFFF, dstType & 0xFFFFF);
/*      */         }
/*      */         else {
/*      */           
/* 1303 */           int mergedDim = -67108864 + (srcType & 0xFC000000);
/* 1304 */           mergedType = mergedDim | 0x800000 | symbolTable.addType("java/lang/Object");
/*      */         } 
/* 1306 */       } else if ((srcType & 0xFC000000) != 0 || (srcType & 0x3C00000) == 8388608) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1311 */         int srcDim = srcType & 0xFC000000;
/* 1312 */         if (srcDim != 0 && (srcType & 0x3C00000) != 8388608) {
/* 1313 */           srcDim = -67108864 + srcDim;
/*      */         }
/* 1315 */         int dstDim = dstType & 0xFC000000;
/* 1316 */         if (dstDim != 0 && (dstType & 0x3C00000) != 8388608) {
/* 1317 */           dstDim = -67108864 + dstDim;
/*      */         }
/*      */         
/* 1320 */         mergedType = Math.min(srcDim, dstDim) | 0x800000 | symbolTable.addType("java/lang/Object");
/*      */       } else {
/*      */         
/* 1323 */         mergedType = 4194304;
/*      */       } 
/* 1325 */     } else if (dstType == 4194309) {
/*      */ 
/*      */ 
/*      */       
/* 1329 */       mergedType = ((srcType & 0xFC000000) != 0 || (srcType & 0x3C00000) == 8388608) ? srcType : 4194304;
/*      */     } else {
/*      */       
/* 1332 */       mergedType = 4194304;
/*      */     } 
/* 1334 */     if (mergedType != dstType) {
/* 1335 */       dstTypes[dstIndex] = mergedType;
/* 1336 */       return true;
/*      */     } 
/* 1338 */     return false;
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
/*      */   final void accept(MethodWriter methodWriter) {
/* 1356 */     int[] localTypes = this.inputLocals;
/* 1357 */     int numLocal = 0;
/* 1358 */     int numTrailingTop = 0;
/* 1359 */     int i = 0;
/* 1360 */     while (i < localTypes.length) {
/* 1361 */       int localType = localTypes[i];
/* 1362 */       i += (localType == 4194308 || localType == 4194307) ? 2 : 1;
/* 1363 */       if (localType == 4194304) {
/* 1364 */         numTrailingTop++; continue;
/*      */       } 
/* 1366 */       numLocal += numTrailingTop + 1;
/* 1367 */       numTrailingTop = 0;
/*      */     } 
/*      */ 
/*      */     
/* 1371 */     int[] stackTypes = this.inputStack;
/* 1372 */     int numStack = 0;
/* 1373 */     i = 0;
/* 1374 */     while (i < stackTypes.length) {
/* 1375 */       int stackType = stackTypes[i];
/* 1376 */       i += (stackType == 4194308 || stackType == 4194307) ? 2 : 1;
/* 1377 */       numStack++;
/*      */     } 
/*      */     
/* 1380 */     int frameIndex = methodWriter.visitFrameStart(this.owner.bytecodeOffset, numLocal, numStack);
/* 1381 */     i = 0;
/* 1382 */     while (numLocal-- > 0) {
/* 1383 */       int localType = localTypes[i];
/* 1384 */       i += (localType == 4194308 || localType == 4194307) ? 2 : 1;
/* 1385 */       methodWriter.visitAbstractType(frameIndex++, localType);
/*      */     } 
/* 1387 */     i = 0;
/* 1388 */     while (numStack-- > 0) {
/* 1389 */       int stackType = stackTypes[i];
/* 1390 */       i += (stackType == 4194308 || stackType == 4194307) ? 2 : 1;
/* 1391 */       methodWriter.visitAbstractType(frameIndex++, stackType);
/*      */     } 
/* 1393 */     methodWriter.visitFrameEnd();
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
/*      */   static void putAbstractType(SymbolTable symbolTable, int abstractType, ByteVector output) {
/* 1409 */     int arrayDimensions = (abstractType & 0xFC000000) >> 26;
/* 1410 */     if (arrayDimensions == 0) {
/* 1411 */       int typeValue = abstractType & 0xFFFFF;
/* 1412 */       switch (abstractType & 0x3C00000) {
/*      */         case 4194304:
/* 1414 */           output.putByte(typeValue);
/*      */           return;
/*      */         case 8388608:
/* 1417 */           output
/* 1418 */             .putByte(7)
/* 1419 */             .putShort((symbolTable.addConstantClass((symbolTable.getType(typeValue)).value)).index);
/*      */           return;
/*      */         case 12582912:
/* 1422 */           output.putByte(8).putShort((int)(symbolTable.getType(typeValue)).data);
/*      */           return;
/*      */       } 
/* 1425 */       throw new AssertionError();
/*      */     } 
/*      */ 
/*      */     
/* 1429 */     StringBuilder typeDescriptor = new StringBuilder();
/* 1430 */     while (arrayDimensions-- > 0) {
/* 1431 */       typeDescriptor.append('[');
/*      */     }
/* 1433 */     if ((abstractType & 0x3C00000) == 8388608) {
/* 1434 */       typeDescriptor
/* 1435 */         .append('L')
/* 1436 */         .append((symbolTable.getType(abstractType & 0xFFFFF)).value)
/* 1437 */         .append(';');
/*      */     } else {
/* 1439 */       switch (abstractType & 0xFFFFF) {
/*      */         case 9:
/* 1441 */           typeDescriptor.append('Z');
/*      */           break;
/*      */         case 10:
/* 1444 */           typeDescriptor.append('B');
/*      */           break;
/*      */         case 11:
/* 1447 */           typeDescriptor.append('C');
/*      */           break;
/*      */         case 12:
/* 1450 */           typeDescriptor.append('S');
/*      */           break;
/*      */         case 1:
/* 1453 */           typeDescriptor.append('I');
/*      */           break;
/*      */         case 2:
/* 1456 */           typeDescriptor.append('F');
/*      */           break;
/*      */         case 4:
/* 1459 */           typeDescriptor.append('J');
/*      */           break;
/*      */         case 3:
/* 1462 */           typeDescriptor.append('D');
/*      */           break;
/*      */         default:
/* 1465 */           throw new AssertionError();
/*      */       } 
/*      */     } 
/* 1468 */     output
/* 1469 */       .putByte(7)
/* 1470 */       .putShort((symbolTable.addConstantClass(typeDescriptor.toString())).index);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\Frame.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */