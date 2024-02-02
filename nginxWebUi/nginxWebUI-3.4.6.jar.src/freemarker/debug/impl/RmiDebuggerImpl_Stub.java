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
  
  static {
    try {
      $method_addBreakpoint_0 = Debugger.class.getMethod("addBreakpoint", new Class[] { Breakpoint.class });
      $method_addDebuggerListener_1 = Debugger.class.getMethod("addDebuggerListener", new Class[] { DebuggerListener.class });
      $method_getBreakpoints_2 = Debugger.class.getMethod("getBreakpoints", new Class[0]);
      $method_getBreakpoints_3 = Debugger.class.getMethod("getBreakpoints", new Class[] { String.class });
      $method_getSuspendedEnvironments_4 = Debugger.class.getMethod("getSuspendedEnvironments", new Class[0]);
      $method_removeBreakpoint_5 = Debugger.class.getMethod("removeBreakpoint", new Class[] { Breakpoint.class });
      $method_removeBreakpoints_6 = Debugger.class.getMethod("removeBreakpoints", new Class[0]);
      $method_removeBreakpoints_7 = Debugger.class.getMethod("removeBreakpoints", new Class[] { String.class });
      $method_removeDebuggerListener_8 = Debugger.class.getMethod("removeDebuggerListener", new Class[] { Object.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError("stub class initialization failed");
    } 
  }
  
  public RmiDebuggerImpl_Stub(RemoteRef paramRemoteRef) {
    super(paramRemoteRef);
  }
  
  public void addBreakpoint(Breakpoint paramBreakpoint) throws RemoteException {
    try {
      this.ref.invoke(this, $method_addBreakpoint_0, new Object[] { paramBreakpoint }, -7089035859976030762L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public Object addDebuggerListener(DebuggerListener paramDebuggerListener) throws RemoteException {
    try {
      return this.ref.invoke(this, $method_addDebuggerListener_1, new Object[] { paramDebuggerListener }, 3973888913376431645L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public List getBreakpoints() throws RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_getBreakpoints_2, null, 2717170791450965365L);
      return (List)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public List getBreakpoints(String paramString) throws RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_getBreakpoints_3, new Object[] { paramString }, 2245868106496574916L);
      return (List)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public Collection getSuspendedEnvironments() throws RemoteException {
    try {
      Object object = this.ref.invoke(this, $method_getSuspendedEnvironments_4, null, 6416507983008154583L);
      return (Collection)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void removeBreakpoint(Breakpoint paramBreakpoint) throws RemoteException {
    try {
      this.ref.invoke(this, $method_removeBreakpoint_5, new Object[] { paramBreakpoint }, -6894101526753771883L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void removeBreakpoints() throws RemoteException {
    try {
      this.ref.invoke(this, $method_removeBreakpoints_6, null, -431815962995809519L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void removeBreakpoints(String paramString) throws RemoteException {
    try {
      this.ref.invoke(this, $method_removeBreakpoints_7, new Object[] { paramString }, -4131389507095882284L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void removeDebuggerListener(Object paramObject) throws RemoteException {
    try {
      this.ref.invoke(this, $method_removeDebuggerListener_8, new Object[] { paramObject }, 8368105080961049709L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\impl\RmiDebuggerImpl_Stub.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */