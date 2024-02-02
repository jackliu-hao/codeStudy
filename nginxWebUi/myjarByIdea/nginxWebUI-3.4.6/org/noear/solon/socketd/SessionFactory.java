package org.noear.solon.socketd;

import java.net.URI;
import org.noear.solon.core.message.Session;

public interface SessionFactory {
   String[] schemes();

   Class<?> driveType();

   Session createSession(Connector connector);

   Session createSession(URI uri, boolean autoReconnect);
}
