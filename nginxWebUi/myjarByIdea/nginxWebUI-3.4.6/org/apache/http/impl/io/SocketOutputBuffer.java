package org.apache.http.impl.io;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
public class SocketOutputBuffer extends AbstractSessionOutputBuffer {
   public SocketOutputBuffer(Socket socket, int bufferSize, HttpParams params) throws IOException {
      Args.notNull(socket, "Socket");
      int n = bufferSize;
      if (bufferSize < 0) {
         n = socket.getSendBufferSize();
      }

      if (n < 1024) {
         n = 1024;
      }

      this.init(socket.getOutputStream(), n, params);
   }
}
