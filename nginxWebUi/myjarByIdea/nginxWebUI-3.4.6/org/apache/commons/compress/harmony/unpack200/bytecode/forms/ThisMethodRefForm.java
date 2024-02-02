package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class ThisMethodRefForm extends ClassSpecificReferenceForm {
   public ThisMethodRefForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   protected int getOffset(OperandManager operandManager) {
      return operandManager.nextThisMethodRef();
   }

   protected int getPoolID() {
      return 11;
   }

   protected String context(OperandManager operandManager) {
      return operandManager.getCurrentClass();
   }
}
