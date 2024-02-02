package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.SegmentConstantPool;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public abstract class InitMethodReferenceForm extends ClassSpecificReferenceForm {
   public InitMethodReferenceForm(int opcode, String name, int[] rewrite) {
      super(opcode, name, rewrite);
   }

   protected abstract String context(OperandManager var1);

   protected int getPoolID() {
      return 11;
   }

   protected int getOffset(OperandManager operandManager) {
      return operandManager.nextInitRef();
   }

   protected void setNestedEntries(ByteCode byteCode, OperandManager operandManager, int offset) throws Pack200Exception {
      SegmentConstantPool globalPool = operandManager.globalConstantPool();
      ClassFileEntry[] nested = null;
      nested = new ClassFileEntry[]{globalPool.getInitMethodPoolEntry(11, (long)offset, this.context(operandManager))};
      byteCode.setNested(nested);
      byteCode.setNestedPositions(new int[][]{{0, 2}});
   }
}
