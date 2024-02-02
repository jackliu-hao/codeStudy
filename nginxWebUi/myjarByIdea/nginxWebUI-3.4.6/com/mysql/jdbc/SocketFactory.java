package com.mysql.jdbc;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;

/** @deprecated */
@Deprecated
public interface SocketFactory {
   Socket afterHandshake() throws SocketException, IOException;

   Socket beforeHandshake() throws SocketException, IOException;

   Socket connect(String var1, int var2, Properties var3) throws SocketException, IOException;
}
