package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.commons.compress.harmony.unpack200.Segment;
import org.apache.commons.compress.harmony.unpack200.bytecode.forms.ByteCodeForm;

public class ByteCode extends ClassFileEntry {
   private static ByteCode[] noArgByteCodes = new ByteCode[255];
   private final ByteCodeForm byteCodeForm;
   private ClassFileEntry[] nested;
   private int[][] nestedPositions;
   private int[] rewrite;
   private int byteCodeOffset;
   private int[] byteCodeTargets;

   public static ByteCode getByteCode(int opcode) {
      int byteOpcode = 255 & opcode;
      if (ByteCodeForm.get(byteOpcode).hasNoOperand()) {
         if (null == noArgByteCodes[byteOpcode]) {
            noArgByteCodes[byteOpcode] = new ByteCode(byteOpcode);
         }

         return noArgByteCodes[byteOpcode];
      } else {
         return new ByteCode(byteOpcode);
      }
   }

   protected ByteCode(int opcode) {
      this(opcode, ClassFileEntry.NONE);
   }

   protected ByteCode(int opcode, ClassFileEntry[] nested) {
      this.byteCodeOffset = -1;
      this.byteCodeForm = ByteCodeForm.get(opcode);
      this.rewrite = this.byteCodeForm.getRewriteCopy();
      this.nested = nested;
   }

   protected void doWrite(DataOutputStream dos) throws IOException {
      for(int i = 0; i < this.rewrite.length; ++i) {
         dos.writeByte(this.rewrite[i]);
      }

   }

   public boolean equals(Object obj) {
      return this == obj;
   }

   public void extractOperands(OperandManager operandManager, Segment segment, int codeLength) {
      ByteCodeForm currentByteCodeForm = this.getByteCodeForm();
      currentByteCodeForm.setByteCodeOperands(this, operandManager, codeLength);
   }

   protected ByteCodeForm getByteCodeForm() {
      return this.byteCodeForm;
   }

   public int getLength() {
      return this.rewrite.length;
   }

   public String getName() {
      return this.getByteCodeForm().getName();
   }

   public ClassFileEntry[] getNestedClassFileEntries() {
      return this.nested;
   }

   public int getOpcode() {
      return this.getByteCodeForm().getOpcode();
   }

   public int hashCode() {
      return this.objectHashCode();
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      if (this.nested.length > 0) {
         for(int index = 0; index < this.nested.length; ++index) {
            int argLength = this.getNestedPosition(index)[1];
            switch (argLength) {
               case 1:
                  this.setOperandByte(pool.indexOf(this.nested[index]), this.getNestedPosition(index)[0]);
                  break;
               case 2:
                  this.setOperand2Bytes(pool.indexOf(this.nested[index]), this.getNestedPosition(index)[0]);
                  break;
               default:
                  throw new Error("Unhandled resolve " + this);
            }
         }
      }

   }

   public void setOperandBytes(int[] operands) {
      int firstOperandIndex = this.getByteCodeForm().firstOperandIndex();
      int byteCodeFormLength = this.getByteCodeForm().operandLength();
      if (firstOperandIndex < 1) {
         throw new Error("Trying to rewrite " + this + " that has no rewrite");
      } else if (byteCodeFormLength != operands.length) {
         throw new Error("Trying to rewrite " + this + " with " + operands.length + " but bytecode has length " + this.byteCodeForm.operandLength());
      } else {
         for(int index = 0; index < byteCodeFormLength; ++index) {
            this.rewrite[index + firstOperandIndex] = operands[index] & 255;
         }

      }
   }

   public void setOperand2Bytes(int operand, int position) {
      int firstOperandIndex = this.getByteCodeForm().firstOperandIndex();
      int byteCodeFormLength = this.getByteCodeForm().getRewrite().length;
      if (firstOperandIndex < 1) {
         throw new Error("Trying to rewrite " + this + " that has no rewrite");
      } else if (firstOperandIndex + position + 1 > byteCodeFormLength) {
         throw new Error("Trying to rewrite " + this + " with an int at position " + position + " but this won't fit in the rewrite array");
      } else {
         this.rewrite[firstOperandIndex + position] = (operand & '\uff00') >> 8;
         this.rewrite[firstOperandIndex + position + 1] = operand & 255;
      }
   }

   public void setOperandSigned2Bytes(int operand, int position) {
      if (operand >= 0) {
         this.setOperand2Bytes(operand, position);
      } else {
         int twosComplementOperand = 65536 + operand;
         this.setOperand2Bytes(twosComplementOperand, position);
      }

   }

   public void setOperandByte(int operand, int position) {
      int firstOperandIndex = this.getByteCodeForm().firstOperandIndex();
      int byteCodeFormLength = this.getByteCodeForm().operandLength();
      if (firstOperandIndex < 1) {
         throw new Error("Trying to rewrite " + this + " that has no rewrite");
      } else if (firstOperandIndex + position > byteCodeFormLength) {
         throw new Error("Trying to rewrite " + this + " with an byte at position " + position + " but this won't fit in the rewrite array");
      } else {
         this.rewrite[firstOperandIndex + position] = operand & 255;
      }
   }

   public String toString() {
      return this.getByteCodeForm().getName();
   }

   public void setNested(ClassFileEntry[] nested) {
      this.nested = nested;
   }

   public void setNestedPositions(int[][] nestedPositions) {
      this.nestedPositions = nestedPositions;
   }

   public int[][] getNestedPositions() {
      return this.nestedPositions;
   }

   public int[] getNestedPosition(int index) {
      return this.getNestedPositions()[index];
   }

   public boolean hasMultipleByteCodes() {
      return this.getByteCodeForm().hasMultipleByteCodes();
   }

   public void setByteCodeIndex(int byteCodeOffset) {
      this.byteCodeOffset = byteCodeOffset;
   }

   public int getByteCodeIndex() {
      return this.byteCodeOffset;
   }

   public void setByteCodeTargets(int[] byteCodeTargets) {
      this.byteCodeTargets = byteCodeTargets;
   }

   public int[] getByteCodeTargets() {
      return this.byteCodeTargets;
   }

   public void applyByteCodeTargetFixup(CodeAttribute codeAttribute) {
      this.getByteCodeForm().fixUpByteCodeTargets(this, codeAttribute);
   }

   public void setRewrite(int[] rewrite) {
      this.rewrite = rewrite;
   }

   public int[] getRewrite() {
      return this.rewrite;
   }

   public boolean nestedMustStartClassPool() {
      return this.byteCodeForm.nestedMustStartClassPool();
   }
}
