package ch.qos.logback.core.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CloseUtil {
   public static void closeQuietly(Closeable closeable) {
      if (closeable != null) {
         try {
            closeable.close();
         } catch (IOException var2) {
         }

      }
   }

   public static void closeQuietly(Socket socket) {
      if (socket != null) {
         try {
            socket.close();
         } catch (IOException var2) {
         }

      }
   }

   public static void closeQuietly(ServerSocket serverSocket) {
      if (serverSocket != null) {
         try {
            serverSocket.close();
         } catch (IOException var2) {
         }

      }
   }
}
