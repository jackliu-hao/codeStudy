package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class SuperMethodRefForm extends ClassSpecificReferenceForm {
   public SuperMethodRefForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   protected int getOffset(OperandManager operandManager) {
      return operandManager.nextSuperMethodRef();
   }

   protected int getPoolID() {
      return 11;
   }

   protected String context(OperandManager operandManager) {
      return operandManager.getSuperClass();
   }
}
