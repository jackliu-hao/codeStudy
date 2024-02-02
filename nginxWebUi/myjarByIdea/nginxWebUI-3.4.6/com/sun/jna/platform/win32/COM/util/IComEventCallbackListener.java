package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.IDispatchCallback;

public interface IComEventCallbackListener {
   void setDispatchCallbackListener(IDispatchCallback var1);

   void errorReceivingCallbackEvent(String var1, Exception var2);
}
