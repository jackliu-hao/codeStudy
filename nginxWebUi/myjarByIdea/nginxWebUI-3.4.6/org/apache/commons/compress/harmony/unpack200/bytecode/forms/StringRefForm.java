package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.SegmentConstantPool;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPString;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class StringRefForm extends SingleByteReferenceForm {
   public StringRefForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   public StringRefForm(int opcode, String name, int[] rewrite, boolean widened) {
      this(opcode, name, rewrite);
      this.widened = widened;
   }

   protected int getOffset(OperandManager operandManager) {
      return operandManager.nextStringRef();
   }

   protected int getPoolID() {
      return 6;
   }

   protected void setNestedEntries(ByteCode byteCode, OperandManager operandManager, int offset) throws Pack200Exception {
      SegmentConstantPool globalPool = operandManager.globalConstantPool();
      ClassFileEntry[] nested = null;
      nested = new ClassFileEntry[]{(CPString)globalPool.getValue(this.getPoolID(), (long)offset)};
      byteCode.setNested(nested);
      if (this.widened) {
         byteCode.setNestedPositions(new int[][]{{0, 2}});
      } else {
         byteCode.setNestedPositions(new int[][]{{0, 1}});
      }

   }
}
