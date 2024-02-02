package com.sun.jna.ptr;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class PointerByReference extends ByReference {
   public PointerByReference() {
      this((Pointer)null);
   }

   public PointerByReference(Pointer value) {
      super(Native.POINTER_SIZE);
      this.setValue(value);
   }

   public void setValue(Pointer value) {
      this.getPointer().setPointer(0L, value);
   }

   public Pointer getValue() {
      return this.getPointer().getPointer(0L);
   }
}
