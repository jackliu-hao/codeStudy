package freemarker.debug.impl;

import freemarker.debug.Breakpoint;
import freemarker.debug.Debugger;
import freemarker.debug.DebuggerListener;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.util.Collection;
import java.util.List;

public final class RmiDebuggerImpl_Stub extends RemoteStub implements Debugger, Remote {
   private static final long serialVersionUID = 2L;
   private static Method $method_addBreakpoint_0;
   private static Method $method_addDebuggerListener_1;
   private static Method $method_getBreakpoints_2;
   private static Method $method_getBreakpoints_3;
   private static Method $method_getSuspendedEnvironments_4;
   private static Method $method_removeBreakpoint_5;
   private static Method $method_removeBreakpoints_6;
   private static Method $method_removeBreakpoints_7;
   private static Method $method_removeDebuggerListener_8;
   // $FF: synthetic field
   static Class class$freemarker$debug$Debugger;
   // $FF: synthetic field
   static Class class$freemarker$debug$Breakpoint;
   // $FF: synthetic field
   static Class class$freemarker$debug$DebuggerListener;
   // $FF: synthetic field
   static Class class$java$lang$String;
   // $FF: synthetic field
   static Class class$java$lang$Object;

   static {
      try {
         $method_addBreakpoint_0 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = class$("freemarker.debug.Debugger"))).getMethod("addBreakpoint", class$freemarker$debug$Breakpoint != null ? class$freemarker$debug$Breakpoint : (class$freemarker$debug$Breakpoint = class$("freemarker.debug.Breakpoint")));
         $method_addDebuggerListener_1 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = class$("freemarker.debug.Debugger"))).getMethod("addDebuggerListener", class$freemarker$debug$DebuggerListener != null ? class$freemarker$debug$DebuggerListener : (class$freemarker$debug$DebuggerListener = class$("freemarker.debug.DebuggerListener")));
         $method_getBreakpoints_2 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = class$("freemarker.debug.Debugger"))).getMethod("getBreakpoints");
         $method_getBreakpoints_3 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = class$("freemarker.debug.Debugger"))).getMethod("getBreakpoints", class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
         $method_getSuspendedEnvironments_4 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = class$("freemarker.debug.Debugger"))).getMethod("getSuspendedEnvironments");
         $method_removeBreakpoint_5 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = class$("freemarker.debug.Debugger"))).getMethod("removeBreakpoint", class$freemarker$debug$Breakpoint != null ? class$freemarker$debug$Breakpoint : (class$freemarker$debug$Breakpoint = class$("freemarker.debug.Breakpoint")));
         $method_removeBreakpoints_6 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = class$("freemarker.debug.Debugger"))).getMethod("removeBreakpoints");
         $method_removeBreakpoints_7 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = class$("freemarker.debug.Debugger"))).getMethod("removeBreakpoints", class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
         $method_removeDebuggerListener_8 = (class$freemarker$debug$Debugger != null ? class$freemarker$debug$Debugger : (class$freemarker$debug$Debugger = class$("freemarker.debug.Debugger"))).getMethod("removeDebuggerListener", class$java$lang$Object != null ? class$java$lang$Object : (class$java$lang$Object = class$("java.lang.Object")));
      } catch (NoSuchMethodException var0) {
         throw new NoSuchMethodError("stub class initialization failed");
      }
   }

   public RmiDebuggerImpl_Stub(RemoteRef var1) {
      super(var1);
   }

   public void addBreakpoint(Breakpoint var1) throws RemoteException {
      try {
         super.ref.invoke(this, $method_addBreakpoint_0, new Object[]{var1}, -7089035859976030762L);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public Object addDebuggerListener(DebuggerListener var1) throws RemoteException {
      try {
         Object var2 = super.ref.invoke(this, $method_addDebuggerListener_1, new Object[]{var1}, 3973888913376431645L);
         return var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   // $FF: synthetic method
   static Class class$(String var0) {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   public List getBreakpoints() throws RemoteException {
      try {
         Object var1 = super.ref.invoke(this, $method_getBreakpoints_2, (Object[])null, 2717170791450965365L);
         return (List)var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new UnexpectedException("undeclared checked exception", var4);
      }
   }

   public List getBreakpoints(String var1) throws RemoteException {
      try {
         Object var2 = super.ref.invoke(this, $method_getBreakpoints_3, new Object[]{var1}, 2245868106496574916L);
         return (List)var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public Collection getSuspendedEnvironments() throws RemoteException {
      try {
         Object var1 = super.ref.invoke(this, $method_getSuspendedEnvironments_4, (Object[])null, 6416507983008154583L);
         return (Collection)var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new UnexpectedException("undeclared checked exception", var4);
      }
   }

   public void removeBreakpoint(Breakpoint var1) throws RemoteException {
      try {
         super.ref.invoke(this, $method_removeBreakpoint_5, new Object[]{var1}, -6894101526753771883L);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public void removeBreakpoints() throws RemoteException {
      try {
         super.ref.invoke(this, $method_removeBreakpoints_6, (Object[])null, -431815962995809519L);
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new UnexpectedException("undeclared checked exception", var4);
      }
   }

   public void removeBreakpoints(String var1) throws RemoteException {
      try {
         super.ref.invoke(this, $method_removeBreakpoints_7, new Object[]{var1}, -4131389507095882284L);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public void removeDebuggerListener(Object var1) throws RemoteException {
      try {
         super.ref.invoke(this, $method_removeDebuggerListener_8, new Object[]{var1}, 8368105080961049709L);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }
}
