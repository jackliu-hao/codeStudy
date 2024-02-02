package org.noear.solon.core.route;

import java.util.Collection;
import java.util.List;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Endpoint;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Session;

public interface Router {
   default void add(String path, Handler handler) {
      this.add(path, Endpoint.main, MethodType.HTTP, handler);
   }

   default void add(String path, Endpoint endpoint, MethodType method, Handler handler) {
      this.add(path, endpoint, method, 0, handler);
   }

   void add(String path, Endpoint endpoint, MethodType method, int index, Handler handler);

   void remove(String pathPrefix);

   Collection<Routing<Handler>> getAll(Endpoint endpoint);

   Handler matchOne(Context ctx, Endpoint endpoint);

   List<Handler> matchAll(Context ctx, Endpoint endpoint);

   default void add(String path, Listener listener) {
      this.add(path, MethodType.ALL, listener);
   }

   default void add(String path, MethodType method, Listener listener) {
      this.add(path, method, 0, listener);
   }

   void add(String path, MethodType method, int index, Listener listener);

   Listener matchOne(Session session);

   List<Listener> matchAll(Session session);

   void clear();
}
