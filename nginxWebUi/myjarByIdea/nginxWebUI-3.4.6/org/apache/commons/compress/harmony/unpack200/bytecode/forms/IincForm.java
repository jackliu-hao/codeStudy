package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class IincForm extends ByteCodeForm {
   public IincForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   public void setByteCodeOperands(ByteCode byteCode, OperandManager operandManager, int codeLength) {
      int local = operandManager.nextLocal();
      int constant = operandManager.nextByte();
      byteCode.setOperandBytes(new int[]{local, constant});
   }
}
