package com.sun.jna.ptr;

import com.sun.jna.Pointer;

public class IntByReference extends ByReference {
   public IntByReference() {
      this(0);
   }

   public IntByReference(int value) {
      super(4);
      this.setValue(value);
   }

   public void setValue(int value) {
      this.getPointer().setInt(0L, value);
   }

   public int getValue() {
      return this.getPointer().getInt(0L);
   }

   public String toString() {
      return String.format("int@0x%1$x=0x%2$x (%2$d)", Pointer.nativeValue(this.getPointer()), this.getValue());
   }
}
