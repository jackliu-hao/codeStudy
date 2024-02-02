package org.h2.server;

import java.sql.SQLException;

public interface Service {
   void init(String... var1) throws Exception;

   String getURL();

   void start() throws SQLException;

   void listen();

   void stop();

   boolean isRunning(boolean var1);

   boolean getAllowOthers();

   String getName();

   String getType();

   int getPort();

   boolean isDaemon();
}
