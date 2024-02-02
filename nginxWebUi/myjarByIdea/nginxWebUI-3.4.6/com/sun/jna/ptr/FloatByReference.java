package com.sun.jna.ptr;

import com.sun.jna.Pointer;

public class FloatByReference extends ByReference {
   public FloatByReference() {
      this(0.0F);
   }

   public FloatByReference(float value) {
      super(4);
      this.setValue(value);
   }

   public void setValue(float value) {
      this.getPointer().setFloat(0L, value);
   }

   public float getValue() {
      return this.getPointer().getFloat(0L);
   }

   public String toString() {
      return String.format("float@0x%x=%s", Pointer.nativeValue(this.getPointer()), this.getValue());
   }
}
