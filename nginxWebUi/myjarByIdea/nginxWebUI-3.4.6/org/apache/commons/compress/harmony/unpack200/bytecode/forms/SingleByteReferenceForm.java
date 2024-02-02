package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public abstract class SingleByteReferenceForm extends ReferenceForm {
   protected boolean widened;

   public SingleByteReferenceForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   protected abstract int getOffset(OperandManager var1);

   protected abstract int getPoolID();

   protected void setNestedEntries(ByteCode byteCode, OperandManager operandManager, int offset) throws Pack200Exception {
      super.setNestedEntries(byteCode, operandManager, offset);
      if (this.widened) {
         byteCode.setNestedPositions(new int[][]{{0, 2}});
      } else {
         byteCode.setNestedPositions(new int[][]{{0, 1}});
      }

   }

   public boolean nestedMustStartClassPool() {
      return !this.widened;
   }
}
