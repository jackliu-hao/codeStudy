package org.noear.solon.core.route;

import java.util.Collection;
import java.util.List;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Endpoint;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.ListenerHolder;
import org.noear.solon.core.message.Session;

public class RouterDefault implements Router {
   private final RoutingTable<Handler>[] routesH = new RoutingTableDefault[3];
   private final RoutingTable<Listener> routesL;

   public RouterDefault() {
      this.routesH[0] = new RoutingTableDefault();
      this.routesH[1] = new RoutingTableDefault();
      this.routesH[2] = new RoutingTableDefault();
      this.routesL = new RoutingTableDefault();
   }

   public void add(String path, Endpoint endpoint, MethodType method, int index, Handler handler) {
      RoutingDefault routing = new RoutingDefault(path, method, index, handler);
      if (!path.contains("*") && !path.contains("{")) {
         this.routesH[endpoint.code].add(0, routing);
      } else {
         this.routesH[endpoint.code].add(routing);
      }

   }

   public void remove(String pathPrefix) {
      this.routesH[Endpoint.before.code].remove(pathPrefix);
      this.routesH[Endpoint.main.code].remove(pathPrefix);
      this.routesH[Endpoint.after.code].remove(pathPrefix);
   }

   public Collection<Routing<Handler>> getAll(Endpoint endpoint) {
      return this.routesH[endpoint.code].getAll();
   }

   public Handler matchOne(Context ctx, Endpoint endpoint) {
      String pathNew = ctx.pathNew();
      MethodType method = MethodType.valueOf(ctx.method());
      return (Handler)this.routesH[endpoint.code].matchOne(pathNew, method);
   }

   public List<Handler> matchAll(Context ctx, Endpoint endpoint) {
      String pathNew = ctx.pathNew();
      MethodType method = MethodType.valueOf(ctx.method());
      return this.routesH[endpoint.code].matchAll(pathNew, method);
   }

   public void add(String path, MethodType method, int index, Listener listener) {
      Listener lh = new ListenerHolder(path, listener);
      this.routesL.add(new RoutingDefault(path, method, index, lh));
   }

   public Listener matchOne(Session session) {
      String path = session.pathNew();
      return path == null ? null : (Listener)this.routesL.matchOne(path, session.method());
   }

   public List<Listener> matchAll(Session session) {
      String path = session.pathNew();
      return path == null ? null : this.routesL.matchAll(path, session.method());
   }

   public void clear() {
      this.routesH[0].clear();
      this.routesH[1].clear();
      this.routesH[2].clear();
      this.routesL.clear();
   }
}
