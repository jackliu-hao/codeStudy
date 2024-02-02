package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class FieldRefForm extends ReferenceForm {
   public FieldRefForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   protected int getOffset(OperandManager operandManager) {
      return operandManager.nextFieldRef();
   }

   protected int getPoolID() {
      return 10;
   }
}
