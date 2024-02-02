package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.OaIdl;

public interface IDispatch extends IUnknown {
   <T> void setProperty(String var1, T var2);

   <T> T getProperty(Class<T> var1, String var2, Object... var3);

   <T> T invokeMethod(Class<T> var1, String var2, Object... var3);

   <T> void setProperty(OaIdl.DISPID var1, T var2);

   <T> T getProperty(Class<T> var1, OaIdl.DISPID var2, Object... var3);

   <T> T invokeMethod(Class<T> var1, OaIdl.DISPID var2, Object... var3);
}
