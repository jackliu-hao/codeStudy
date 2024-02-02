package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class LocalForm extends ByteCodeForm {
   public LocalForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   public void setByteCodeOperands(ByteCode byteCode, OperandManager operandManager, int codeLength) {
      byteCode.setOperandBytes(new int[]{operandManager.nextLocal()});
   }
}
