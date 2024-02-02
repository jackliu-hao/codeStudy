package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;

@ComInterface(
   iid = "{00000000-0000-0000-C000-000000000046}"
)
public interface IUnknown {
   <T> T queryInterface(Class<T> var1) throws COMException;
}
