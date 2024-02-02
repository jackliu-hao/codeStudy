package freemarker.debug;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

public interface DebuggerListener extends Remote, EventListener {
   void environmentSuspended(EnvironmentSuspendedEvent var1) throws RemoteException;
}
