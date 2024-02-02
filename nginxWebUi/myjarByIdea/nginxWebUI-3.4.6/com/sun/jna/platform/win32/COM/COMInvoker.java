package com.sun.jna.platform.win32.COM;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public abstract class COMInvoker extends PointerType {
   protected int _invokeNativeInt(int vtableId, Object[] args) {
      Pointer vptr = this.getPointer().getPointer(0L);
      Function func = Function.getFunction(vptr.getPointer((long)(vtableId * Native.POINTER_SIZE)));
      return func.invokeInt(args);
   }

   protected Object _invokeNativeObject(int vtableId, Object[] args, Class<?> returnType) {
      Pointer vptr = this.getPointer().getPointer(0L);
      Function func = Function.getFunction(vptr.getPointer((long)(vtableId * Native.POINTER_SIZE)));
      return func.invoke(returnType, args);
   }

   protected void _invokeNativeVoid(int vtableId, Object[] args) {
      Pointer vptr = this.getPointer().getPointer(0L);
      Function func = Function.getFunction(vptr.getPointer((long)(vtableId * Native.POINTER_SIZE)));
      func.invokeVoid(args);
   }
}
