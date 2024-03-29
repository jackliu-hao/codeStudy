package com.sun.jna.ptr;

import com.sun.jna.Pointer;

public class ByteByReference extends ByReference {
   public ByteByReference() {
      this((byte)0);
   }

   public ByteByReference(byte value) {
      super(1);
      this.setValue(value);
   }

   public void setValue(byte value) {
      this.getPointer().setByte(0L, value);
   }

   public byte getValue() {
      return this.getPointer().getByte(0L);
   }

   public String toString() {
      return String.format("byte@0x%1$x=0x%2$x (%2$d)", Pointer.nativeValue(this.getPointer()), this.getValue());
   }
}
