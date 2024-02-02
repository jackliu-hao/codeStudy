package freemarker.debug;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

public interface Debugger extends Remote {
  public static final int DEFAULT_PORT = 7011;
  
  void addBreakpoint(Breakpoint paramBreakpoint) throws RemoteException;
  
  void removeBreakpoint(Breakpoint paramBreakpoint) throws RemoteException;
  
  void removeBreakpoints(String paramString) throws RemoteException;
  
  void removeBreakpoints() throws RemoteException;
  
  List getBreakpoints() throws RemoteException;
  
  List getBreakpoints(String paramString) throws RemoteException;
  
  Collection getSuspendedEnvironments() throws RemoteException;
  
  Object addDebuggerListener(DebuggerListener paramDebuggerListener) throws RemoteException;
  
  void removeDebuggerListener(Object paramObject) throws RemoteException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\Debugger.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */