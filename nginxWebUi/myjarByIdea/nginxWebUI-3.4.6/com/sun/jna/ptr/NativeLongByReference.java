package com.sun.jna.ptr;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class NativeLongByReference extends ByReference {
   public NativeLongByReference() {
      this(new NativeLong(0L));
   }

   public NativeLongByReference(NativeLong value) {
      super(NativeLong.SIZE);
      this.setValue(value);
   }

   public void setValue(NativeLong value) {
      this.getPointer().setNativeLong(0L, value);
   }

   public NativeLong getValue() {
      return this.getPointer().getNativeLong(0L);
   }

   public String toString() {
      return NativeLong.SIZE > 4 ? String.format("NativeLong@0x1$%x=0x%2$x (%2$d)", Pointer.nativeValue(this.getPointer()), this.getValue().longValue()) : String.format("NativeLong@0x1$%x=0x%2$x (%2$d)", Pointer.nativeValue(this.getPointer()), this.getValue().intValue());
   }
}
