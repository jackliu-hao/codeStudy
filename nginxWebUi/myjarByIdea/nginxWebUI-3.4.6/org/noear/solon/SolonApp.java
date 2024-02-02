package org.noear.solon;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.noear.solon.annotation.Import;
import org.noear.solon.core.Aop;
import org.noear.solon.core.ExtendLoader;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.PluginEntity;
import org.noear.solon.core.Signal;
import org.noear.solon.core.event.AppInitEndEvent;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.BeanLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.event.PluginLoadEndEvent;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextUtil;
import org.noear.solon.core.handle.FilterChain;
import org.noear.solon.core.handle.FilterChainNode;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.route.RouterAdapter;
import org.noear.solon.core.util.PrintUtil;

public class SolonApp extends RouterAdapter {
   private final SolonProps _prop;
   private final Class<?> _source;
   private final long _startupTime = System.currentTimeMillis();
   protected boolean stopped = false;
   private final Map<Integer, Signal> signals = new LinkedHashMap();
   private final Set<BiConsumer<String, Object>> _onSharedAdd_event = new HashSet();
   private final Map<String, Object> _shared = new HashMap();
   private Map<String, Object> _shared_unmod;
   private Handler _handler = null;
   private Map<Integer, Handler> _statusHandlers = new HashMap();
   private boolean _enableHttp = true;
   private boolean _enableWebSocket = false;
   private boolean _enableWebSocketD = false;
   private boolean _enableSocketD = false;
   private boolean _enableTransaction = true;
   private boolean _enableCaching = true;
   private boolean _enableStaticfiles = true;
   private boolean _enableErrorAutoprint = true;
   private boolean _enableSessionState = true;
   private boolean _enableJarIsolation = false;
   private boolean _enableSafeStop = false;

   protected SolonApp(Class<?> source, NvMap args) throws Exception {
      this._source = source;
      this._prop = (new SolonProps()).load(source, args);
      this.initRouter(this::doFilter);
      this._handler = this.routerHandler();
      this.enableJarIsolation(this._prop.getBool("solon.extend.isolation", false));
   }

   protected void initAwait() {
      String addr = this.cfg().get("solon.start.ping");
      if (Utils.isNotEmpty(addr)) {
         try {
            while(true) {
               if (Utils.ping(addr)) {
                  PrintUtil.info("App", "Start ping succeed: " + addr);
                  Thread.sleep(1000L);
                  break;
               }

               PrintUtil.info("App", "Start ping failure: " + addr);
               Thread.sleep(2000L);
            }
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      }

   }

   protected void init() {
      String filterStr = this.cfg().extendFilter();
      List loaderList;
      if (Utils.isEmpty(filterStr)) {
         loaderList = ExtendLoader.load(this.cfg().extend(), false);
      } else {
         String[] filterS = filterStr.split(",");
         loaderList = ExtendLoader.load(this.cfg().extend(), false, (path) -> {
            String[] var2 = filterS;
            int var3 = filterS.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String f = var2[var4];
               if (path.contains(f)) {
                  return true;
               }
            }

            return false;
         });
      }

      this.cfg().plugsScan(loaderList);
   }

   protected void run() {
      EventBus.push(AppInitEndEvent.instance);
      List<PluginEntity> plugs = this.cfg().plugs();
      int i = 0;

      for(int len = plugs.size(); i < len; ++i) {
         ((PluginEntity)plugs.get(i)).start(Aop.context());
      }

      EventBus.push(PluginLoadEndEvent.instance);
      this.importTry();
      if (this.source() != null) {
         Aop.context().beanScan(this.source());
      }

      EventBus.push(BeanLoadEndEvent.instance);
      NvMap map = this.cfg().getXmap("solon.view.mapping");
      map.forEach((k, v) -> {
         RenderManager.mapping("." + k, v);
      });
      Aop.context().beanLoaded();
      EventBus.push(AppLoadEndEvent.instance);
   }

   protected void importTry() {
      if (this._source != null) {
         Annotation[] var1 = this._source.getAnnotations();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Annotation a1 = var1[var3];
            if (a1 instanceof Import) {
               Aop.context().beanImport((Import)a1);
            } else {
               Aop.context().beanImport((Import)a1.annotationType().getAnnotation(Import.class));
            }
         }

      }
   }

   public void signalAdd(Signal instance) {
      this.signals.putIfAbsent(instance.port(), instance);
   }

   public Signal signalGet(int port) {
      return (Signal)this.signals.get(port);
   }

   public Collection<Signal> signals() {
      return Collections.unmodifiableCollection(this.signals.values());
   }

   public ClassLoader classLoader() {
      return JarClassLoader.global();
   }

   public void sharedAdd(String key, Object obj) {
      this._shared.put(key, obj);
      this._onSharedAdd_event.forEach((fun) -> {
         fun.accept(key, obj);
      });
   }

   public <T> void sharedGet(String key, Consumer<T> event) {
      Object tmp = this._shared.get(key);
      if (tmp != null) {
         event.accept(tmp);
      } else {
         this.onSharedAdd((k, v) -> {
            if (k.equals(key)) {
               event.accept(v);
            }

         });
      }

   }

   public void onSharedAdd(BiConsumer<String, Object> event) {
      this._onSharedAdd_event.add(event);
   }

   public Map<String, Object> shared() {
      if (this._shared_unmod == null) {
         this._shared_unmod = Collections.unmodifiableMap(this._shared);
      }

      return this._shared_unmod;
   }

   protected long elapsedTimes() {
      return System.currentTimeMillis() - this._startupTime;
   }

   public Class<?> source() {
      return this._source;
   }

   /** @deprecated */
   @Deprecated
   public int port() {
      return this._prop.serverPort();
   }

   public SolonProps cfg() {
      return this._prop;
   }

   public void plug(Plugin plugin) {
      PluginEntity p = new PluginEntity(plugin);
      p.start(Aop.context());
      this.cfg().plugs().add(p);
   }

   public void pluginAdd(int priority, Plugin plugin) {
      PluginEntity p = new PluginEntity(plugin, priority);
      this.cfg().plugs().add(p);
      this.cfg().plugsSort();
   }

   public PluginEntity pluginPop(Class<?> pluginClz) {
      PluginEntity tmp = null;
      Iterator var3 = this.cfg().plugs().iterator();

      while(var3.hasNext()) {
         PluginEntity pe = (PluginEntity)var3.next();
         if (pluginClz.isInstance(pe.getPlugin())) {
            tmp = pe;
            break;
         }
      }

      if (tmp != null) {
         this.cfg().plugs().remove(tmp);
      }

      return tmp;
   }

   public Handler handlerGet() {
      return this._handler;
   }

   public void handlerSet(Handler handler) {
      if (handler != null) {
         this._handler = handler;
      }

   }

   public void tryHandle(Context x) {
      try {
         ContextUtil.currentSet(x);
         if (this.stopped) {
            x.status(403);
         } else {
            (new FilterChainNode(this.filterList())).doFilter(x);
            if (!x.getHandled() && x.status() < 400) {
               x.status(404);
            }
         }

         this.doStatus(x);
      } catch (Throwable var11) {
         Throwable ex = Utils.throwableUnwrap(var11);
         if (!ex.equals(x.errors)) {
            EventBus.push(ex);
         }

         if (!x.getHandled()) {
            if (x.status() < 400) {
               x.status(500);
            }

            x.setHandled(true);
         }

         if (!x.getRendered()) {
            try {
               if (!this.doStatus(x) && Solon.cfg().isDebugMode()) {
                  x.output(ex);
               }
            } catch (RuntimeException var9) {
               throw var9;
            } catch (Throwable var10) {
               throw new RuntimeException(var10);
            }
         }
      } finally {
         ContextUtil.currentRemove();
      }

   }

   protected void doFilter(Context x, FilterChain chain) throws Throwable {
      this._handler.handle(x);
   }

   protected boolean doStatus(Context x) throws Throwable {
      if (x.status() >= 400 && this._statusHandlers.size() > 0) {
         Handler h = (Handler)this._statusHandlers.get(x.status());
         if (h != null) {
            x.status(200);
            x.setHandled(true);
            h.handle(x);
            return true;
         }
      }

      return false;
   }

   public <T> SolonApp onEvent(Class<T> type, EventListener<T> handler) {
      EventBus.subscribe(type, handler);
      return this;
   }

   public SolonApp onError(EventListener<Throwable> handler) {
      return this.onEvent(Throwable.class, handler);
   }

   public SolonApp onStatus(Integer code, Handler handler) {
      this._statusHandlers.put(code, handler);
      return this;
   }

   public void block() throws InterruptedException {
      Thread.currentThread().join();
   }

   public boolean enableHttp() {
      return this._enableHttp;
   }

   public SolonApp enableHttp(boolean enable) {
      this._enableHttp = enable;
      return this;
   }

   public boolean enableWebSocket() {
      return this._enableWebSocket;
   }

   public SolonApp enableWebSocket(boolean enable) {
      this._enableWebSocket = enable;
      return this;
   }

   public boolean enableWebSocketD() {
      return this._enableWebSocketD;
   }

   public SolonApp enableWebSocketD(boolean enable) {
      this._enableWebSocketD = enable;
      if (enable) {
         this._enableWebSocket = enable;
      }

      return this;
   }

   public boolean enableSocketD() {
      return this._enableSocketD;
   }

   public SolonApp enableSocketD(boolean enable) {
      this._enableSocketD = enable;
      return this;
   }

   public boolean enableTransaction() {
      return this._enableTransaction;
   }

   public SolonApp enableTransaction(boolean enable) {
      this._enableTransaction = enable;
      return this;
   }

   public boolean enableCaching() {
      return this._enableCaching;
   }

   public SolonApp enableCaching(boolean enable) {
      this._enableCaching = enable;
      return this;
   }

   public boolean enableStaticfiles() {
      return this._enableStaticfiles;
   }

   public SolonApp enableStaticfiles(boolean enable) {
      this._enableStaticfiles = enable;
      return this;
   }

   public boolean enableErrorAutoprint() {
      return this._enableErrorAutoprint;
   }

   public void enableErrorAutoprint(boolean enable) {
      this._enableErrorAutoprint = enable;
   }

   public boolean enableSessionState() {
      return this._enableSessionState;
   }

   public SolonApp enableSessionState(boolean enable) {
      this._enableSessionState = enable;
      return this;
   }

   public boolean enableJarIsolation() {
      return this._enableJarIsolation;
   }

   private SolonApp enableJarIsolation(boolean enable) {
      this._enableJarIsolation = enable;
      return this;
   }

   public boolean enableSafeStop() {
      return this._enableSafeStop;
   }

   public void enableSafeStop(boolean enable) {
      this._enableSafeStop = enable;
   }
}
