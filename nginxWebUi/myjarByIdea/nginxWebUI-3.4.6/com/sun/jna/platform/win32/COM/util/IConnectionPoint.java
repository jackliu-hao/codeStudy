package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;

@ComInterface(
   iid = "{B196B284-BAB4-101A-B69C-00AA00341D07}"
)
public interface IConnectionPoint {
   IComEventCallbackCookie advise(Class<?> var1, IComEventCallbackListener var2) throws COMException;

   void unadvise(Class<?> var1, IComEventCallbackCookie var2) throws COMException;
}
