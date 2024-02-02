package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPInterfaceMethodRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class IMethodRefForm extends ReferenceForm {
   public IMethodRefForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   protected int getOffset(OperandManager operandManager) {
      return operandManager.nextIMethodRef();
   }

   protected int getPoolID() {
      return 12;
   }

   public void setByteCodeOperands(ByteCode byteCode, OperandManager operandManager, int codeLength) {
      super.setByteCodeOperands(byteCode, operandManager, codeLength);
      int count = ((CPInterfaceMethodRef)byteCode.getNestedClassFileEntries()[0]).invokeInterfaceCount();
      byteCode.getRewrite()[3] = count;
   }
}
