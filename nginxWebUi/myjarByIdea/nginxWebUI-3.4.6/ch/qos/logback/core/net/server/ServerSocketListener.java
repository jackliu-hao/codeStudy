package ch.qos.logback.core.net.server;

import ch.qos.logback.core.util.CloseUtil;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public abstract class ServerSocketListener<T extends Client> implements ServerListener<T> {
   private final ServerSocket serverSocket;

   public ServerSocketListener(ServerSocket serverSocket) {
      this.serverSocket = serverSocket;
   }

   public T acceptClient() throws IOException {
      Socket socket = this.serverSocket.accept();
      return this.createClient(this.socketAddressToString(socket.getRemoteSocketAddress()), socket);
   }

   protected abstract T createClient(String var1, Socket var2) throws IOException;

   public void close() {
      CloseUtil.closeQuietly(this.serverSocket);
   }

   public String toString() {
      return this.socketAddressToString(this.serverSocket.getLocalSocketAddress());
   }

   private String socketAddressToString(SocketAddress address) {
      String addr = address.toString();
      int i = addr.indexOf("/");
      if (i >= 0) {
         addr = addr.substring(i + 1);
      }

      return addr;
   }
}
