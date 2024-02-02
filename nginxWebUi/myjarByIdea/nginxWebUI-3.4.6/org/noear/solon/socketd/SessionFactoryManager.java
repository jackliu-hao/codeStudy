package org.noear.solon.socketd;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.noear.solon.core.message.Session;

public class SessionFactoryManager {
   public static Map<String, SessionFactory> uriCached = new HashMap();
   public static Map<Class<?>, SessionFactory> clzCached = new HashMap();

   public static void register(SessionFactory factory) {
      String[] var1 = factory.schemes();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String p = var1[var3];
         uriCached.putIfAbsent(p, factory);
      }

      clzCached.putIfAbsent(factory.driveType(), factory);
   }

   public static Session create(Connector connector) {
      SessionFactory factory = (SessionFactory)clzCached.get(connector.driveType());
      if (factory == null) {
         throw new IllegalArgumentException("The connector is not supported");
      } else {
         Session session = factory.createSession(connector);
         session.setHandshaked(true);
         return session;
      }
   }

   public static Session create(URI uri, boolean autoReconnect) {
      SessionFactory factory = (SessionFactory)uriCached.get(uri.getScheme());
      if (factory == null) {
         throw new IllegalArgumentException("The " + uri.getScheme() + " protocol is not supported");
      } else {
         Session session = factory.createSession(uri, autoReconnect);
         session.setHandshaked(true);
         return session;
      }
   }
}
