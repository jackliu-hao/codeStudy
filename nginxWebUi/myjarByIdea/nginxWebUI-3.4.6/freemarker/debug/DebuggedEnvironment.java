package freemarker.debug;

import java.rmi.RemoteException;

public interface DebuggedEnvironment extends DebugModel {
   void resume() throws RemoteException;

   void stop() throws RemoteException;

   long getId() throws RemoteException;
}
