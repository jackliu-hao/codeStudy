package freemarker.debug.impl;

import freemarker.debug.DebuggerListener;
import freemarker.debug.EnvironmentSuspendedEvent;
import freemarker.log.Logger;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

public class RmiDebuggerListenerImpl extends UnicastRemoteObject implements DebuggerListener, Unreferenced {
   private static final Logger LOG = Logger.getLogger("freemarker.debug.client");
   private static final long serialVersionUID = 1L;
   private final DebuggerListener listener;

   public void unreferenced() {
      try {
         UnicastRemoteObject.unexportObject(this, false);
      } catch (NoSuchObjectException var2) {
         LOG.warn("Failed to unexport RMI debugger listener", var2);
      }

   }

   public RmiDebuggerListenerImpl(DebuggerListener listener) throws RemoteException {
      this.listener = listener;
   }

   public void environmentSuspended(EnvironmentSuspendedEvent e) throws RemoteException {
      this.listener.environmentSuspended(e);
   }
}
