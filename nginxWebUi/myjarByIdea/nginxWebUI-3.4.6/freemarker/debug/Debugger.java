package freemarker.debug;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

public interface Debugger extends Remote {
   int DEFAULT_PORT = 7011;

   void addBreakpoint(Breakpoint var1) throws RemoteException;

   void removeBreakpoint(Breakpoint var1) throws RemoteException;

   void removeBreakpoints(String var1) throws RemoteException;

   void removeBreakpoints() throws RemoteException;

   List getBreakpoints() throws RemoteException;

   List getBreakpoints(String var1) throws RemoteException;

   Collection getSuspendedEnvironments() throws RemoteException;

   Object addDebuggerListener(DebuggerListener var1) throws RemoteException;

   void removeDebuggerListener(Object var1) throws RemoteException;
}
