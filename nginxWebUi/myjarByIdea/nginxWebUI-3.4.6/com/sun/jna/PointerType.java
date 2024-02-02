package com.sun.jna;

public abstract class PointerType implements NativeMapped {
   private Pointer pointer;

   protected PointerType() {
      this.pointer = Pointer.NULL;
   }

   protected PointerType(Pointer p) {
      this.pointer = p;
   }

   public Class<?> nativeType() {
      return Pointer.class;
   }

   public Object toNative() {
      return this.getPointer();
   }

   public Pointer getPointer() {
      return this.pointer;
   }

   public void setPointer(Pointer p) {
      this.pointer = p;
   }

   public Object fromNative(Object nativeValue, FromNativeContext context) {
      if (nativeValue == null) {
         return null;
      } else {
         PointerType pt = (PointerType)Klass.newInstance(this.getClass());
         pt.pointer = (Pointer)nativeValue;
         return pt;
      }
   }

   public int hashCode() {
      return this.pointer != null ? this.pointer.hashCode() : 0;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o instanceof PointerType) {
         Pointer p = ((PointerType)o).getPointer();
         if (this.pointer == null) {
            return p == null;
         } else {
            return this.pointer.equals(p);
         }
      } else {
         return false;
      }
   }

   public String toString() {
      return this.pointer == null ? "NULL" : this.pointer.toString() + " (" + super.toString() + ")";
   }
}
