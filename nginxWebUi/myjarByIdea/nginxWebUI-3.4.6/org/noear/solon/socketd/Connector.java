package org.noear.solon.socketd;

import java.io.IOException;
import java.net.URI;
import org.noear.solon.core.message.Session;

public interface Connector<T> {
   URI uri();

   boolean autoReconnect();

   Class<T> driveType();

   T open(Session session) throws IOException;
}
