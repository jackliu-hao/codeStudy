package freemarker.debug;

import java.rmi.RemoteException;

public interface DebuggedEnvironment extends DebugModel {
  void resume() throws RemoteException;
  
  void stop() throws RemoteException;
  
  long getId() throws RemoteException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\DebuggedEnvironment.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */