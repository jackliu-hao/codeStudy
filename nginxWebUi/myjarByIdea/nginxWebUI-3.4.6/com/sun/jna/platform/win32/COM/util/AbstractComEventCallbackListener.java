package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.IDispatchCallback;

public abstract class AbstractComEventCallbackListener implements IComEventCallbackListener {
   IDispatchCallback dispatchCallback = null;

   public void setDispatchCallbackListener(IDispatchCallback dispatchCallback) {
      this.dispatchCallback = dispatchCallback;
   }
}
