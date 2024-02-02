package com.sun.jna.ptr;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import java.lang.reflect.Method;

public abstract class ByReference extends PointerType {
   protected ByReference(int dataSize) {
      this.setPointer(new Memory((long)dataSize));
   }

   public String toString() {
      try {
         Method getValue = this.getClass().getMethod("getValue");
         Object value = getValue.invoke(this);
         return value == null ? String.format("null@0x%x", Pointer.nativeValue(this.getPointer())) : String.format("%s@0x%x=%s", value.getClass().getSimpleName(), Pointer.nativeValue(this.getPointer()), value);
      } catch (Exception var3) {
         return String.format("ByReference Contract violated - %s#getValue raised exception: %s", this.getClass().getName(), var3.getMessage());
      }
   }
}
