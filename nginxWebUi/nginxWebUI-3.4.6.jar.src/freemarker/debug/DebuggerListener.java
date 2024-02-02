package freemarker.debug;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

public interface DebuggerListener extends Remote, EventListener {
  void environmentSuspended(EnvironmentSuspendedEvent paramEnvironmentSuspendedEvent) throws RemoteException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\DebuggerListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */