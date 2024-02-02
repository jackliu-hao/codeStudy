package ch.qos.logback.core.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class RemoteReceiverServerListener extends ServerSocketListener<RemoteReceiverClient> {
   public RemoteReceiverServerListener(ServerSocket serverSocket) {
      super(serverSocket);
   }

   protected RemoteReceiverClient createClient(String id, Socket socket) throws IOException {
      return new RemoteReceiverStreamClient(id, socket);
   }
}
