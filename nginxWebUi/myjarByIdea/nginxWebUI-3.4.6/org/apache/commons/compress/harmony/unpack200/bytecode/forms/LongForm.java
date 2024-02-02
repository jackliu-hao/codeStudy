package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class LongForm extends ReferenceForm {
   public LongForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   protected int getOffset(OperandManager operandManager) {
      return operandManager.nextLongRef();
   }

   protected int getPoolID() {
      return 4;
   }
}
