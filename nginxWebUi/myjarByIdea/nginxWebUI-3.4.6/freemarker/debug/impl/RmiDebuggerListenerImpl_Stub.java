package freemarker.debug.impl;

import freemarker.debug.DebuggerListener;
import freemarker.debug.EnvironmentSuspendedEvent;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public final class RmiDebuggerListenerImpl_Stub extends RemoteStub implements DebuggerListener, Remote {
   private static final long serialVersionUID = 2L;
   private static Method $method_environmentSuspended_0;
   // $FF: synthetic field
   static Class class$freemarker$debug$DebuggerListener;
   // $FF: synthetic field
   static Class class$freemarker$debug$EnvironmentSuspendedEvent;

   static {
      try {
         $method_environmentSuspended_0 = (class$freemarker$debug$DebuggerListener != null ? class$freemarker$debug$DebuggerListener : (class$freemarker$debug$DebuggerListener = class$("freemarker.debug.DebuggerListener"))).getMethod("environmentSuspended", class$freemarker$debug$EnvironmentSuspendedEvent != null ? class$freemarker$debug$EnvironmentSuspendedEvent : (class$freemarker$debug$EnvironmentSuspendedEvent = class$("freemarker.debug.EnvironmentSuspendedEvent")));
      } catch (NoSuchMethodException var0) {
         throw new NoSuchMethodError("stub class initialization failed");
      }
   }

   public RmiDebuggerListenerImpl_Stub(RemoteRef var1) {
      super(var1);
   }

   // $FF: synthetic method
   static Class class$(String var0) {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   public void environmentSuspended(EnvironmentSuspendedEvent var1) throws RemoteException {
      try {
         super.ref.invoke(this, $method_environmentSuspended_0, new Object[]{var1}, -2541155567719209082L);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }
}
