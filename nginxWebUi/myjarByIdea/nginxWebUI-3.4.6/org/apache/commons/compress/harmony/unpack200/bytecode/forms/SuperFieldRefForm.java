package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class SuperFieldRefForm extends ClassSpecificReferenceForm {
   public SuperFieldRefForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   protected int getOffset(OperandManager operandManager) {
      return operandManager.nextSuperFieldRef();
   }

   protected int getPoolID() {
      return 10;
   }

   protected String context(OperandManager operandManager) {
      return operandManager.getSuperClass();
   }
}
