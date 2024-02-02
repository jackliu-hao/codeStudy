package io.undertow.servlet.websockets;

import io.undertow.servlet.handlers.ServletRequestContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

class SecurityActions {
   static ServletRequestContext requireCurrentServletRequestContext() {
      return System.getSecurityManager() == null ? ServletRequestContext.requireCurrent() : (ServletRequestContext)AccessController.doPrivileged(new PrivilegedAction<ServletRequestContext>() {
         public ServletRequestContext run() {
            return ServletRequestContext.requireCurrent();
         }
      });
   }
}
