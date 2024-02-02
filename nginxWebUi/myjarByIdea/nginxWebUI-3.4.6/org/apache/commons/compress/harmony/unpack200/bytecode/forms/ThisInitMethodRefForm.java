package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class ThisInitMethodRefForm extends InitMethodReferenceForm {
   public ThisInitMethodRefForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   protected String context(OperandManager operandManager) {
      return operandManager.getCurrentClass();
   }
}
