package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class MultiANewArrayForm extends ClassRefForm {
   public MultiANewArrayForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   public void setByteCodeOperands(ByteCode byteCode, OperandManager operandManager, int codeLength) {
      super.setByteCodeOperands(byteCode, operandManager, codeLength);
      int dimension = operandManager.nextByte();
      byteCode.setOperandByte(dimension, 2);
   }
}
