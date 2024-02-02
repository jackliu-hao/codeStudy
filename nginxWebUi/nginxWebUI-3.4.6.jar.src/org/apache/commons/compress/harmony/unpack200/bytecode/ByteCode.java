/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.compress.harmony.unpack200.Segment;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.forms.ByteCodeForm;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ByteCode
/*     */   extends ClassFileEntry
/*     */ {
/*     */   public static ByteCode getByteCode(int opcode) {
/*  31 */     int byteOpcode = 0xFF & opcode;
/*  32 */     if (ByteCodeForm.get(byteOpcode).hasNoOperand()) {
/*  33 */       if (null == noArgByteCodes[byteOpcode]) {
/*  34 */         noArgByteCodes[byteOpcode] = new ByteCode(byteOpcode);
/*     */       }
/*  36 */       return noArgByteCodes[byteOpcode];
/*     */     } 
/*  38 */     return new ByteCode(byteOpcode);
/*     */   }
/*     */   
/*  41 */   private static ByteCode[] noArgByteCodes = new ByteCode[255];
/*     */   
/*     */   private final ByteCodeForm byteCodeForm;
/*     */   
/*     */   private ClassFileEntry[] nested;
/*     */   
/*     */   private int[][] nestedPositions;
/*     */   private int[] rewrite;
/*  49 */   private int byteCodeOffset = -1;
/*     */   private int[] byteCodeTargets;
/*     */   
/*     */   protected ByteCode(int opcode) {
/*  53 */     this(opcode, ClassFileEntry.NONE);
/*     */   }
/*     */   
/*     */   protected ByteCode(int opcode, ClassFileEntry[] nested) {
/*  57 */     this.byteCodeForm = ByteCodeForm.get(opcode);
/*  58 */     this.rewrite = this.byteCodeForm.getRewriteCopy();
/*  59 */     this.nested = nested;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doWrite(DataOutputStream dos) throws IOException {
/*  64 */     for (int i = 0; i < this.rewrite.length; i++) {
/*  65 */       dos.writeByte(this.rewrite[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  71 */     return (this == obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void extractOperands(OperandManager operandManager, Segment segment, int codeLength) {
/*  79 */     ByteCodeForm currentByteCodeForm = getByteCodeForm();
/*  80 */     currentByteCodeForm.setByteCodeOperands(this, operandManager, codeLength);
/*     */   }
/*     */   
/*     */   protected ByteCodeForm getByteCodeForm() {
/*  84 */     return this.byteCodeForm;
/*     */   }
/*     */   
/*     */   public int getLength() {
/*  88 */     return this.rewrite.length;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  92 */     return getByteCodeForm().getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public ClassFileEntry[] getNestedClassFileEntries() {
/*  97 */     return this.nested;
/*     */   }
/*     */   
/*     */   public int getOpcode() {
/* 101 */     return getByteCodeForm().getOpcode();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 106 */     return objectHashCode();
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
/*     */   protected void resolve(ClassConstantPool pool) {
/* 118 */     super.resolve(pool);
/* 119 */     if (this.nested.length > 0)
/*     */     {
/*     */       
/* 122 */       for (int index = 0; index < this.nested.length; index++) {
/* 123 */         int argLength = getNestedPosition(index)[1];
/* 124 */         switch (argLength) {
/*     */           
/*     */           case 1:
/* 127 */             setOperandByte(pool.indexOf(this.nested[index]), getNestedPosition(index)[0]);
/*     */             break;
/*     */           
/*     */           case 2:
/* 131 */             setOperand2Bytes(pool.indexOf(this.nested[index]), getNestedPosition(index)[0]);
/*     */             break;
/*     */           
/*     */           default:
/* 135 */             throw new Error("Unhandled resolve " + this);
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
/*     */   public void setOperandBytes(int[] operands) {
/* 148 */     int firstOperandIndex = getByteCodeForm().firstOperandIndex();
/* 149 */     int byteCodeFormLength = getByteCodeForm().operandLength();
/* 150 */     if (firstOperandIndex < 1)
/*     */     {
/* 152 */       throw new Error("Trying to rewrite " + this + " that has no rewrite");
/*     */     }
/*     */     
/* 155 */     if (byteCodeFormLength != operands.length) {
/* 156 */       throw new Error("Trying to rewrite " + this + " with " + operands.length + " but bytecode has length " + this.byteCodeForm
/* 157 */           .operandLength());
/*     */     }
/*     */     
/* 160 */     for (int index = 0; index < byteCodeFormLength; index++) {
/* 161 */       this.rewrite[index + firstOperandIndex] = operands[index] & 0xFF;
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
/*     */   public void setOperand2Bytes(int operand, int position) {
/* 174 */     int firstOperandIndex = getByteCodeForm().firstOperandIndex();
/* 175 */     int byteCodeFormLength = (getByteCodeForm().getRewrite()).length;
/* 176 */     if (firstOperandIndex < 1)
/*     */     {
/* 178 */       throw new Error("Trying to rewrite " + this + " that has no rewrite");
/*     */     }
/*     */     
/* 181 */     if (firstOperandIndex + position + 1 > byteCodeFormLength) {
/* 182 */       throw new Error("Trying to rewrite " + this + " with an int at position " + position + " but this won't fit in the rewrite array");
/*     */     }
/*     */ 
/*     */     
/* 186 */     this.rewrite[firstOperandIndex + position] = (operand & 0xFF00) >> 8;
/* 187 */     this.rewrite[firstOperandIndex + position + 1] = operand & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOperandSigned2Bytes(int operand, int position) {
/* 198 */     if (operand >= 0) {
/* 199 */       setOperand2Bytes(operand, position);
/*     */     } else {
/* 201 */       int twosComplementOperand = 65536 + operand;
/* 202 */       setOperand2Bytes(twosComplementOperand, position);
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
/*     */   public void setOperandByte(int operand, int position) {
/* 215 */     int firstOperandIndex = getByteCodeForm().firstOperandIndex();
/* 216 */     int byteCodeFormLength = getByteCodeForm().operandLength();
/* 217 */     if (firstOperandIndex < 1)
/*     */     {
/* 219 */       throw new Error("Trying to rewrite " + this + " that has no rewrite");
/*     */     }
/*     */     
/* 222 */     if (firstOperandIndex + position > byteCodeFormLength) {
/* 223 */       throw new Error("Trying to rewrite " + this + " with an byte at position " + position + " but this won't fit in the rewrite array");
/*     */     }
/*     */ 
/*     */     
/* 227 */     this.rewrite[firstOperandIndex + position] = operand & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 232 */     return getByteCodeForm().getName();
/*     */   }
/*     */   
/*     */   public void setNested(ClassFileEntry[] nested) {
/* 236 */     this.nested = nested;
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
/*     */   public void setNestedPositions(int[][] nestedPositions) {
/* 252 */     this.nestedPositions = nestedPositions;
/*     */   }
/*     */   
/*     */   public int[][] getNestedPositions() {
/* 256 */     return this.nestedPositions;
/*     */   }
/*     */   
/*     */   public int[] getNestedPosition(int index) {
/* 260 */     return getNestedPositions()[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasMultipleByteCodes() {
/* 270 */     return getByteCodeForm().hasMultipleByteCodes();
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
/*     */   public void setByteCodeIndex(int byteCodeOffset) {
/* 282 */     this.byteCodeOffset = byteCodeOffset;
/*     */   }
/*     */   
/*     */   public int getByteCodeIndex() {
/* 286 */     return this.byteCodeOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setByteCodeTargets(int[] byteCodeTargets) {
/* 297 */     this.byteCodeTargets = byteCodeTargets;
/*     */   }
/*     */   
/*     */   public int[] getByteCodeTargets() {
/* 301 */     return this.byteCodeTargets;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void applyByteCodeTargetFixup(CodeAttribute codeAttribute) {
/* 312 */     getByteCodeForm().fixUpByteCodeTargets(this, codeAttribute);
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
/*     */   public void setRewrite(int[] rewrite) {
/* 325 */     this.rewrite = rewrite;
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
/*     */   public int[] getRewrite() {
/* 338 */     return this.rewrite;
/*     */   }
/*     */   
/*     */   public boolean nestedMustStartClassPool() {
/* 342 */     return this.byteCodeForm.nestedMustStartClassPool();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\ByteCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */