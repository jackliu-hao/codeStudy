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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Label
/*     */ {
/*     */   static final int FLAG_DEBUG_ONLY = 1;
/*     */   static final int FLAG_JUMP_TARGET = 2;
/*     */   static final int FLAG_RESOLVED = 4;
/*     */   static final int FLAG_REACHABLE = 8;
/*     */   static final int FLAG_SUBROUTINE_CALLER = 16;
/*     */   static final int FLAG_SUBROUTINE_START = 32;
/*     */   static final int FLAG_SUBROUTINE_END = 64;
/*     */   static final int LINE_NUMBERS_CAPACITY_INCREMENT = 4;
/*     */   static final int FORWARD_REFERENCES_CAPACITY_INCREMENT = 6;
/*     */   static final int FORWARD_REFERENCE_TYPE_MASK = -268435456;
/*     */   static final int FORWARD_REFERENCE_TYPE_SHORT = 268435456;
/*     */   static final int FORWARD_REFERENCE_TYPE_WIDE = 536870912;
/*     */   static final int FORWARD_REFERENCE_HANDLE_MASK = 268435455;
/* 130 */   static final Label EMPTY_LIST = new Label();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object info;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   short flags;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private short lineNumber;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] otherLineNumbers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int bytecodeOffset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] forwardReferences;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   short inputStackSize;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   short outputStackSize;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   short outputStackMax;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   short subroutineId;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Frame frame;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Label nextBasicBlock;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Edge outgoingEdges;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Label nextListElement;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getOffset() {
/* 302 */     if ((this.flags & 0x4) == 0) {
/* 303 */       throw new IllegalStateException("Label offset position has not been resolved yet");
/*     */     }
/* 305 */     return this.bytecodeOffset;
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
/*     */   final Label getCanonicalInstance() {
/* 322 */     return (this.frame == null) ? this : this.frame.owner;
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
/*     */   final void addLineNumber(int lineNumber) {
/* 335 */     if (this.lineNumber == 0) {
/* 336 */       this.lineNumber = (short)lineNumber;
/*     */     } else {
/* 338 */       if (this.otherLineNumbers == null) {
/* 339 */         this.otherLineNumbers = new int[4];
/*     */       }
/* 341 */       int otherLineNumberIndex = this.otherLineNumbers[0] = this.otherLineNumbers[0] + 1;
/* 342 */       if (otherLineNumberIndex >= this.otherLineNumbers.length) {
/* 343 */         int[] newLineNumbers = new int[this.otherLineNumbers.length + 4];
/* 344 */         System.arraycopy(this.otherLineNumbers, 0, newLineNumbers, 0, this.otherLineNumbers.length);
/* 345 */         this.otherLineNumbers = newLineNumbers;
/*     */       } 
/* 347 */       this.otherLineNumbers[otherLineNumberIndex] = lineNumber;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void accept(MethodVisitor methodVisitor, boolean visitLineNumbers) {
/* 358 */     methodVisitor.visitLabel(this);
/* 359 */     if (visitLineNumbers && this.lineNumber != 0) {
/* 360 */       methodVisitor.visitLineNumber(this.lineNumber & 0xFFFF, this);
/* 361 */       if (this.otherLineNumbers != null) {
/* 362 */         for (int i = 1; i <= this.otherLineNumbers[0]; i++) {
/* 363 */           methodVisitor.visitLineNumber(this.otherLineNumbers[i], this);
/*     */         }
/*     */       }
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
/*     */   final void put(ByteVector code, int sourceInsnBytecodeOffset, boolean wideReference) {
/* 386 */     if ((this.flags & 0x4) == 0) {
/* 387 */       if (wideReference) {
/* 388 */         addForwardReference(sourceInsnBytecodeOffset, 536870912, code.length);
/* 389 */         code.putInt(-1);
/*     */       } else {
/* 391 */         addForwardReference(sourceInsnBytecodeOffset, 268435456, code.length);
/* 392 */         code.putShort(-1);
/*     */       }
/*     */     
/* 395 */     } else if (wideReference) {
/* 396 */       code.putInt(this.bytecodeOffset - sourceInsnBytecodeOffset);
/*     */     } else {
/* 398 */       code.putShort(this.bytecodeOffset - sourceInsnBytecodeOffset);
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
/*     */   private void addForwardReference(int sourceInsnBytecodeOffset, int referenceType, int referenceHandle) {
/* 417 */     if (this.forwardReferences == null) {
/* 418 */       this.forwardReferences = new int[6];
/*     */     }
/* 420 */     int lastElementIndex = this.forwardReferences[0];
/* 421 */     if (lastElementIndex + 2 >= this.forwardReferences.length) {
/* 422 */       int[] newValues = new int[this.forwardReferences.length + 6];
/* 423 */       System.arraycopy(this.forwardReferences, 0, newValues, 0, this.forwardReferences.length);
/* 424 */       this.forwardReferences = newValues;
/*     */     } 
/* 426 */     this.forwardReferences[++lastElementIndex] = sourceInsnBytecodeOffset;
/* 427 */     this.forwardReferences[++lastElementIndex] = referenceType | referenceHandle;
/* 428 */     this.forwardReferences[0] = lastElementIndex;
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
/*     */   final boolean resolve(byte[] code, int bytecodeOffset) {
/* 446 */     this.flags = (short)(this.flags | 0x4);
/* 447 */     this.bytecodeOffset = bytecodeOffset;
/* 448 */     if (this.forwardReferences == null) {
/* 449 */       return false;
/*     */     }
/* 451 */     boolean hasAsmInstructions = false;
/* 452 */     for (int i = this.forwardReferences[0]; i > 0; i -= 2) {
/* 453 */       int sourceInsnBytecodeOffset = this.forwardReferences[i - 1];
/* 454 */       int reference = this.forwardReferences[i];
/* 455 */       int relativeOffset = bytecodeOffset - sourceInsnBytecodeOffset;
/* 456 */       int handle = reference & 0xFFFFFFF;
/* 457 */       if ((reference & 0xF0000000) == 268435456) {
/* 458 */         if (relativeOffset < -32768 || relativeOffset > 32767) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 463 */           int opcode = code[sourceInsnBytecodeOffset] & 0xFF;
/* 464 */           if (opcode < 198) {
/*     */             
/* 466 */             code[sourceInsnBytecodeOffset] = (byte)(opcode + 49);
/*     */           } else {
/*     */             
/* 469 */             code[sourceInsnBytecodeOffset] = (byte)(opcode + 20);
/*     */           } 
/* 471 */           hasAsmInstructions = true;
/*     */         } 
/* 473 */         code[handle++] = (byte)(relativeOffset >>> 8);
/* 474 */         code[handle] = (byte)relativeOffset;
/*     */       } else {
/* 476 */         code[handle++] = (byte)(relativeOffset >>> 24);
/* 477 */         code[handle++] = (byte)(relativeOffset >>> 16);
/* 478 */         code[handle++] = (byte)(relativeOffset >>> 8);
/* 479 */         code[handle] = (byte)relativeOffset;
/*     */       } 
/*     */     } 
/* 482 */     return hasAsmInstructions;
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
/*     */   final void markSubroutine(short subroutineId) {
/* 506 */     Label listOfBlocksToProcess = this;
/* 507 */     listOfBlocksToProcess.nextListElement = EMPTY_LIST;
/* 508 */     while (listOfBlocksToProcess != EMPTY_LIST) {
/*     */       
/* 510 */       Label basicBlock = listOfBlocksToProcess;
/* 511 */       listOfBlocksToProcess = listOfBlocksToProcess.nextListElement;
/* 512 */       basicBlock.nextListElement = null;
/*     */ 
/*     */ 
/*     */       
/* 516 */       if (basicBlock.subroutineId == 0) {
/* 517 */         basicBlock.subroutineId = subroutineId;
/* 518 */         listOfBlocksToProcess = basicBlock.pushSuccessors(listOfBlocksToProcess);
/*     */       } 
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
/*     */   final void addSubroutineRetSuccessors(Label subroutineCaller) {
/* 542 */     Label listOfProcessedBlocks = EMPTY_LIST;
/* 543 */     Label listOfBlocksToProcess = this;
/* 544 */     listOfBlocksToProcess.nextListElement = EMPTY_LIST;
/* 545 */     while (listOfBlocksToProcess != EMPTY_LIST) {
/*     */       
/* 547 */       Label basicBlock = listOfBlocksToProcess;
/* 548 */       listOfBlocksToProcess = basicBlock.nextListElement;
/* 549 */       basicBlock.nextListElement = listOfProcessedBlocks;
/* 550 */       listOfProcessedBlocks = basicBlock;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 555 */       if ((basicBlock.flags & 0x40) != 0 && basicBlock.subroutineId != subroutineCaller.subroutineId)
/*     */       {
/* 557 */         basicBlock.outgoingEdges = new Edge(basicBlock.outputStackSize, subroutineCaller.outgoingEdges.successor, basicBlock.outgoingEdges);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 570 */       listOfBlocksToProcess = basicBlock.pushSuccessors(listOfBlocksToProcess);
/*     */     } 
/*     */ 
/*     */     
/* 574 */     while (listOfProcessedBlocks != EMPTY_LIST) {
/* 575 */       Label newListOfProcessedBlocks = listOfProcessedBlocks.nextListElement;
/* 576 */       listOfProcessedBlocks.nextListElement = null;
/* 577 */       listOfProcessedBlocks = newListOfProcessedBlocks;
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
/*     */   private Label pushSuccessors(Label listOfLabelsToProcess) {
/* 591 */     Label newListOfLabelsToProcess = listOfLabelsToProcess;
/* 592 */     Edge outgoingEdge = this.outgoingEdges;
/* 593 */     while (outgoingEdge != null) {
/*     */ 
/*     */       
/* 596 */       boolean isJsrTarget = ((this.flags & 0x10) != 0 && outgoingEdge == this.outgoingEdges.nextEdge);
/*     */       
/* 598 */       if (!isJsrTarget && outgoingEdge.successor.nextListElement == null) {
/*     */ 
/*     */         
/* 601 */         outgoingEdge.successor.nextListElement = newListOfLabelsToProcess;
/* 602 */         newListOfLabelsToProcess = outgoingEdge.successor;
/*     */       } 
/* 604 */       outgoingEdge = outgoingEdge.nextEdge;
/*     */     } 
/* 606 */     return newListOfLabelsToProcess;
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
/*     */   public String toString() {
/* 620 */     return "L" + System.identityHashCode(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\Label.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */