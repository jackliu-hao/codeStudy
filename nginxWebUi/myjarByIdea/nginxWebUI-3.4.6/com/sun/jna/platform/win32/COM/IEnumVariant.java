package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Variant;

public interface IEnumVariant extends IUnknown {
   IEnumVariant Clone();

   Variant.VARIANT[] Next(int var1);

   void Reset();

   void Skip(int var1);
}
