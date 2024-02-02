package org.apache.commons.compress.harmony.unpack200.bytecode;

public abstract class CPConstantNumber extends CPConstant {
   public CPConstantNumber(byte tag, Object value, int globalIndex) {
      super(tag, value, globalIndex);
   }

   protected Number getNumber() {
      return (Number)this.getValue();
   }
}
