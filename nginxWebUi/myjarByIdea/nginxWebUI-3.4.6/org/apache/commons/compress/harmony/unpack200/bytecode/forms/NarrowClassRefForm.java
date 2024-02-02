package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class NarrowClassRefForm extends ClassRefForm {
   public NarrowClassRefForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   public NarrowClassRefForm(int opcode, String name, int[] rewrite, boolean widened) {
      super(opcode, name, rewrite, widened);
   }

   protected void setNestedEntries(ByteCode byteCode, OperandManager operandManager, int offset) throws Pack200Exception {
      super.setNestedEntries(byteCode, operandManager, offset);
      if (!this.widened) {
         byteCode.setNestedPositions(new int[][]{{0, 1}});
      }

   }

   public boolean nestedMustStartClassPool() {
      return !this.widened;
   }
}
