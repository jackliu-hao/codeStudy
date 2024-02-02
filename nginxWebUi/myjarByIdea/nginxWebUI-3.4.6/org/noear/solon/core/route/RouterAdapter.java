package org.noear.solon.core.route;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Endpoint;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterEntity;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.HandlerLoader;
import org.noear.solon.core.handle.HandlerSlots;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.ListenerPipeline;

public abstract class RouterAdapter implements HandlerSlots {
   private Router _router;
   private RouterHandler _routerHandler;
   private List<FilterEntity> _filterList = new ArrayList();
   private final ListenerPipeline _listenerPipeline = new ListenerPipeline();

   protected void initRouter(Filter appFilter) {
      this._router = new RouterDefault();
      this._routerHandler = new RouterHandler(this._router);
      this._filterList.add(new FilterEntity(Integer.MAX_VALUE, appFilter));
   }

   protected RouterHandler routerHandler() {
      return this._routerHandler;
   }

   protected List<FilterEntity> filterList() {
      return this._filterList;
   }

   public Router router() {
      return this._router;
   }

   public void routerSet(Router router) {
      if (router != null) {
         this._router = router;
         this._routerHandler.bind(router);
      }

   }

   public void filter(Filter filter) {
      this.filter(0, filter);
   }

   public void filter(int index, Filter filter) {
      this._filterList.add(new FilterEntity(index, filter));
      this._filterList.sort(Comparator.comparingInt((f) -> {
         return f.index;
      }));
   }

   public void before(Handler handler) {
      this.before("**", MethodType.ALL, handler);
   }

   public void before(int index, Handler handler) {
      this.before("**", MethodType.ALL, index, handler);
   }

   public void before(MethodType method, Handler handler) {
      this.before("**", method, handler);
   }

   public void before(MethodType method, int index, Handler handler) {
      this.before("**", method, index, handler);
   }

   public void before(String expr, Handler handler) {
      this.before(expr, MethodType.ALL, handler);
   }

   public void before(String expr, MethodType method, Handler handler) {
      this._router.add(expr, Endpoint.before, method, handler);
   }

   public void before(String expr, MethodType method, int index, Handler handler) {
      this._router.add(expr, Endpoint.before, method, index, handler);
   }

   public void after(Handler handler) {
      this.after("**", MethodType.ALL, handler);
   }

   public void after(MethodType method, Handler handler) {
      this.after("**", method, handler);
   }

   public void after(String expr, Handler handler) {
      this.after(expr, MethodType.ALL, handler);
   }

   public void after(String expr, MethodType method, Handler handler) {
      this._router.add(expr, Endpoint.after, method, handler);
   }

   public void after(String expr, MethodType method, int index, Handler handler) {
      this._router.add(expr, Endpoint.after, method, index, handler);
   }

   public void add(String expr, MethodType method, Handler handler) {
      this._router.add(expr, Endpoint.main, method, handler);
   }

   public void add(String expr, Class<?> clz) {
      BeanWrap bw = Aop.wrapAndPut(clz);
      if (bw != null) {
         (new HandlerLoader(bw, expr)).load(this);
      }

   }

   public void add(String expr, Class<?> clz, boolean remoting) {
      BeanWrap bw = Aop.wrapAndPut(clz);
      if (bw != null) {
         (new HandlerLoader(bw, expr, remoting)).load(this);
      }

   }

   public void all(String path, Handler handler) {
      this.add(path, MethodType.ALL, handler);
   }

   public void http(String path, Handler handler) {
      this.add(path, MethodType.HTTP, handler);
   }

   public void head(String path, Handler handler) {
      this.add(path, MethodType.HEAD, handler);
   }

   public void get(String path, Handler handler) {
      this.add(path, MethodType.GET, handler);
   }

   public void post(String path, Handler handler) {
      this.add(path, MethodType.POST, handler);
   }

   public void put(String path, Handler handler) {
      this.add(path, MethodType.PUT, handler);
   }

   public void patch(String path, Handler handler) {
      this.add(path, MethodType.PATCH, handler);
   }

   public void delete(String path, Handler handler) {
      this.add(path, MethodType.DELETE, handler);
   }

   public void ws(String path, Handler handler) {
      this.add(path, MethodType.WEBSOCKET, handler);
   }

   public void ws(String path, Listener listener) {
      this._router.add(path, MethodType.WEBSOCKET, listener);
   }

   public void socket(String path, Handler handler) {
      this.add(path, MethodType.SOCKET, handler);
   }

   public void socket(String path, Listener listener) {
      this._router.add(path, MethodType.SOCKET, listener);
   }

   public void listen(String path, Listener listener) {
      this._router.add(path, MethodType.ALL, listener);
   }

   public void listenBefore(Listener listener) {
      this._listenerPipeline.prev(listener);
   }

   public void listenAfter(Listener listener) {
      this._listenerPipeline.next(listener);
   }

   public Listener listener() {
      return this._listenerPipeline;
   }
}
