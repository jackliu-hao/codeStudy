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
  
  static {
    try {
      $method_environmentSuspended_0 = DebuggerListener.class.getMethod("environmentSuspended", new Class[] { EnvironmentSuspendedEvent.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError("stub class initialization failed");
    } 
  }
  
  public RmiDebuggerListenerImpl_Stub(RemoteRef paramRemoteRef) {
    super(paramRemoteRef);
  }
  
  public void environmentSuspended(EnvironmentSuspendedEvent paramEnvironmentSuspendedEvent) throws RemoteException {
    try {
      this.ref.invoke(this, $method_environmentSuspended_0, new Object[] { paramEnvironmentSuspendedEvent }, -2541155567719209082L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\impl\RmiDebuggerListenerImpl_Stub.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */