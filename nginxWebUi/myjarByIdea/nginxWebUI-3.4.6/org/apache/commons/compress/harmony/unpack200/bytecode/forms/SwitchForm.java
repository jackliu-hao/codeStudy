package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.CodeAttribute;

public abstract class SwitchForm extends VariableInstructionForm {
   public SwitchForm(int opcode, String name) {
      super(opcode, name);
   }

   public void fixUpByteCodeTargets(ByteCode byteCode, CodeAttribute codeAttribute) {
      int[] originalTargets = byteCode.getByteCodeTargets();
      int numberOfLabels = originalTargets.length;
      int[] replacementTargets = new int[numberOfLabels];
      int sourceIndex = byteCode.getByteCodeIndex();
      int sourceValue = (Integer)codeAttribute.byteCodeOffsets.get(sourceIndex);

      int index;
      for(int index = 0; index < numberOfLabels; ++index) {
         index = sourceIndex + originalTargets[index];
         int targetValue = (Integer)codeAttribute.byteCodeOffsets.get(index);
         replacementTargets[index] = targetValue - sourceValue;
      }

      int[] rewriteArray = byteCode.getRewrite();

      for(index = 0; index < numberOfLabels; ++index) {
         this.setRewrite4Bytes(replacementTargets[index], rewriteArray);
      }

   }
}
