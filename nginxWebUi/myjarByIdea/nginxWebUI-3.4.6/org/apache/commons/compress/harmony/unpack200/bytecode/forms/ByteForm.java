package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class ByteForm extends ByteCodeForm {
   public ByteForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   public void setByteCodeOperands(ByteCode byteCode, OperandManager operandManager, int codeLength) {
      byteCode.setOperandByte(operandManager.nextByte() & 255, 0);
   }
}
