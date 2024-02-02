package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class FloatRefForm extends SingleByteReferenceForm {
   public FloatRefForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   public FloatRefForm(int opcode, String name, int[] rewrite, boolean widened) {
      this(opcode, name, rewrite);
      this.widened = widened;
   }

   protected int getOffset(OperandManager operandManager) {
      return operandManager.nextFloatRef();
   }

   protected int getPoolID() {
      return 3;
   }
}
