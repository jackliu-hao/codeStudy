package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.SegmentConstantPool;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public abstract class ClassSpecificReferenceForm extends ReferenceForm {
   public ClassSpecificReferenceForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   protected abstract int getOffset(OperandManager var1);

   protected abstract int getPoolID();

   protected abstract String context(OperandManager var1);

   protected void setNestedEntries(ByteCode byteCode, OperandManager operandManager, int offset) throws Pack200Exception {
      SegmentConstantPool globalPool = operandManager.globalConstantPool();
      ClassFileEntry[] nested = null;
      nested = new ClassFileEntry[]{globalPool.getClassSpecificPoolEntry(this.getPoolID(), (long)offset, this.context(operandManager))};
      byteCode.setNested(nested);
      byteCode.setNestedPositions(new int[][]{{0, 2}});
   }
}
