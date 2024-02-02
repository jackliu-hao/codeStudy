package com.mysql.cj.protocol;

import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.log.Log;
import java.io.Closeable;
import java.io.IOException;

public interface SocketFactory extends SocketMetadata {
   <T extends Closeable> T connect(String var1, int var2, PropertySet var3, int var4) throws IOException;

   default void beforeHandshake() throws IOException {
   }

   <T extends Closeable> T performTlsHandshake(SocketConnection var1, ServerSession var2) throws IOException;

   default <T extends Closeable> T performTlsHandshake(SocketConnection socketConnection, ServerSession serverSession, Log log) throws IOException {
      return this.performTlsHandshake(socketConnection, serverSession);
   }

   default void afterHandshake() throws IOException {
   }
}
