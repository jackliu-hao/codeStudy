/*     */ package org.noear.solon;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Consumer;
/*     */ import org.noear.solon.annotation.Import;
/*     */ import org.noear.solon.core.Aop;
/*     */ import org.noear.solon.core.ExtendLoader;
/*     */ import org.noear.solon.core.JarClassLoader;
/*     */ import org.noear.solon.core.NvMap;
/*     */ import org.noear.solon.core.Plugin;
/*     */ import org.noear.solon.core.PluginEntity;
/*     */ import org.noear.solon.core.Signal;
/*     */ import org.noear.solon.core.event.AppInitEndEvent;
/*     */ import org.noear.solon.core.event.AppLoadEndEvent;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.core.event.EventListener;
/*     */ import org.noear.solon.core.event.PluginLoadEndEvent;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.ContextUtil;
/*     */ import org.noear.solon.core.handle.FilterChain;
/*     */ import org.noear.solon.core.handle.FilterChainNode;
/*     */ import org.noear.solon.core.handle.Handler;
/*     */ import org.noear.solon.core.handle.RenderManager;
/*     */ import org.noear.solon.core.route.RouterAdapter;
/*     */ import org.noear.solon.core.util.PrintUtil;
/*     */ 
/*     */ public class SolonApp extends RouterAdapter {
/*     */   private final SolonProps _prop;
/*     */   private final Class<?> _source;
/*     */   private final long _startupTime;
/*     */   protected boolean stopped = false;
/*     */   private final Map<Integer, Signal> signals;
/*     */   private final Set<BiConsumer<String, Object>> _onSharedAdd_event;
/*     */   private final Map<String, Object> _shared;
/*     */   private Map<String, Object> _shared_unmod;
/*     */   private Handler _handler;
/*     */   private Map<Integer, Handler> _statusHandlers;
/*     */   private boolean _enableHttp;
/*     */   private boolean _enableWebSocket;
/*     */   private boolean _enableWebSocketD;
/*     */   private boolean _enableSocketD;
/*     */   private boolean _enableTransaction;
/*     */   private boolean _enableCaching;
/*     */   private boolean _enableStaticfiles;
/*     */   private boolean _enableErrorAutoprint;
/*     */   private boolean _enableSessionState;
/*     */   private boolean _enableJarIsolation;
/*     */   private boolean _enableSafeStop;
/*     */   
/*     */   protected void initAwait() {
/*  59 */     String addr = cfg().get("solon.start.ping");
/*  60 */     if (Utils.isNotEmpty(addr)) {
/*     */       try {
/*     */         while (true) {
/*  63 */           if (Utils.ping(addr)) {
/*  64 */             PrintUtil.info("App", "Start ping succeed: " + addr);
/*  65 */             Thread.sleep(1000L);
/*     */             break;
/*     */           } 
/*  68 */           PrintUtil.info("App", "Start ping failure: " + addr);
/*  69 */           Thread.sleep(2000L);
/*     */         }
/*     */       
/*  72 */       } catch (Exception e) {
/*  73 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void init() {
/*     */     List<ClassLoader> loaderList;
/*  85 */     String filterStr = cfg().extendFilter();
/*  86 */     if (Utils.isEmpty(filterStr)) {
/*     */       
/*  88 */       loaderList = ExtendLoader.load(cfg().extend(), false);
/*     */     } else {
/*     */       
/*  91 */       String[] filterS = filterStr.split(",");
/*  92 */       loaderList = ExtendLoader.load(cfg().extend(), false, path -> {
/*     */             for (String f : filterS) {
/*     */               if (path.contains(f)) {
/*     */                 return true;
/*     */               }
/*     */             } 
/*     */ 
/*     */             
/*     */             return false;
/*     */           });
/*     */     } 
/*     */ 
/*     */     
/* 105 */     cfg().plugsScan(loaderList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void run() {
/* 113 */     EventBus.push(AppInitEndEvent.instance);
/*     */ 
/*     */     
/* 116 */     List<PluginEntity> plugs = cfg().plugs();
/* 117 */     for (int i = 0, len = plugs.size(); i < len; i++) {
/* 118 */       ((PluginEntity)plugs.get(i)).start(Aop.context());
/*     */     }
/*     */ 
/*     */     
/* 122 */     EventBus.push(PluginLoadEndEvent.instance);
/*     */ 
/*     */ 
/*     */     
/* 126 */     importTry();
/*     */ 
/*     */     
/* 129 */     if (source() != null) {
/* 130 */       Aop.context().beanScan(source());
/*     */     }
/*     */ 
/*     */     
/* 134 */     EventBus.push(BeanLoadEndEvent.instance);
/*     */ 
/*     */ 
/*     */     
/* 138 */     NvMap map = cfg().getXmap("solon.view.mapping");
/* 139 */     map.forEach((k, v) -> RenderManager.mapping("." + k, v));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 144 */     Aop.context().beanLoaded();
/*     */ 
/*     */     
/* 147 */     EventBus.push(AppLoadEndEvent.instance);
/*     */   }
/*     */   
/*     */   protected void importTry()
/*     */   {
/* 152 */     if (this._source == null) {
/*     */       return;
/*     */     }
/*     */     
/* 156 */     for (Annotation a1 : this._source.getAnnotations())
/* 157 */     { if (a1 instanceof Import)
/* 158 */       { Aop.context().beanImport((Import)a1); }
/*     */       else
/* 160 */       { Aop.context().beanImport(a1.annotationType().<Import>getAnnotation(Import.class)); }  }  }
/*     */   public void signalAdd(Signal instance) { this.signals.putIfAbsent(Integer.valueOf(instance.port()), instance); }
/*     */   public Signal signalGet(int port) { return this.signals.get(Integer.valueOf(port)); }
/*     */   public Collection<Signal> signals() { return Collections.unmodifiableCollection(this.signals.values()); }
/*     */   public ClassLoader classLoader() { return (ClassLoader)JarClassLoader.global(); }
/*     */   public void sharedAdd(String key, Object obj) { this._shared.put(key, obj); this._onSharedAdd_event.forEach(fun -> fun.accept(key, obj)); }
/*     */   public <T> void sharedGet(String key, Consumer<T> event) { Object tmp = this._shared.get(key); if (tmp != null) { event.accept((T)tmp); } else { onSharedAdd((k, v) -> { if (k.equals(key)) event.accept(v);  }); }  }
/*     */   public void onSharedAdd(BiConsumer<String, Object> event) { this._onSharedAdd_event.add(event); }
/* 168 */   public Map<String, Object> shared() { if (this._shared_unmod == null) this._shared_unmod = Collections.unmodifiableMap(this._shared);  return this._shared_unmod; } protected long elapsedTimes() { return System.currentTimeMillis() - this._startupTime; } public Class<?> source() { return this._source; } @Deprecated public int port() { return this._prop.serverPort(); } public SolonProps cfg() { return this._prop; } public void plug(Plugin plugin) { PluginEntity p = new PluginEntity(plugin); p.start(Aop.context()); cfg().plugs().add(p); } public void pluginAdd(int priority, Plugin plugin) { PluginEntity p = new PluginEntity(plugin, priority); cfg().plugs().add(p); cfg().plugsSort(); } public PluginEntity pluginPop(Class<?> pluginClz) { PluginEntity tmp = null; for (PluginEntity pe : cfg().plugs()) { if (pluginClz.isInstance(pe.getPlugin())) { tmp = pe; break; }  }  if (tmp != null) cfg().plugs().remove(tmp);  return tmp; } public Handler handlerGet() { return this._handler; } public void handlerSet(Handler handler) { if (handler != null) this._handler = handler;  } protected SolonApp(Class<?> source, NvMap args) throws Exception { this.signals = new LinkedHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 199 */     this._onSharedAdd_event = new HashSet<>();
/* 200 */     this._shared = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 331 */     this._handler = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 438 */     this._statusHandlers = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 457 */     this._enableHttp = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 474 */     this._enableWebSocket = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 488 */     this._enableWebSocketD = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 508 */     this._enableSocketD = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 525 */     this._enableTransaction = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 542 */     this._enableCaching = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 559 */     this._enableStaticfiles = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 576 */     this._enableErrorAutoprint = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 593 */     this._enableSessionState = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 611 */     this._enableJarIsolation = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 628 */     this._enableSafeStop = false; this._startupTime = System.currentTimeMillis(); this._source = source; this._prop = (new SolonProps()).load(source, args); initRouter(this::doFilter); this._handler = (Handler)routerHandler(); enableJarIsolation(this._prop.getBool("solon.extend.isolation", false)); }
/*     */   public void tryHandle(Context x) { try { ContextUtil.currentSet(x); if (this.stopped) { x.status(403); } else { (new FilterChainNode(filterList())).doFilter(x); if (!x.getHandled() && x.status() < 400) x.status(404);  }  doStatus(x); } catch (Throwable ex) { ex = Utils.throwableUnwrap(ex); if (!ex.equals(x.errors)) EventBus.push(ex);  if (!x.getHandled()) { if (x.status() < 400) x.status(500);  x.setHandled(true); }  if (!x.getRendered()) try { if (!doStatus(x) && Solon.cfg().isDebugMode())
/*     */             x.output(ex);  } catch (RuntimeException e) { throw e; } catch (Throwable e) { throw new RuntimeException(e); }   } finally { ContextUtil.currentRemove(); }  }
/*     */   protected void doFilter(Context x, FilterChain chain) throws Throwable { this._handler.handle(x); }
/*     */   protected boolean doStatus(Context x) throws Throwable { if (x.status() >= 400 && this._statusHandlers.size() > 0) { Handler h = this._statusHandlers.get(Integer.valueOf(x.status())); if (h != null) { x.status(200); x.setHandled(true); h.handle(x); return true; }  }  return false; }
/*     */   public <T> SolonApp onEvent(Class<T> type, EventListener<T> handler) { EventBus.subscribe(type, handler); return this; }
/* 634 */   public SolonApp onError(EventListener<Throwable> handler) { return onEvent(Throwable.class, handler); } public boolean enableSafeStop() { return this._enableSafeStop; } public SolonApp onStatus(Integer code, Handler handler) { this._statusHandlers.put(code, handler); return this; } public void block() throws InterruptedException { Thread.currentThread().join(); } public boolean enableHttp() { return this._enableHttp; } public SolonApp enableHttp(boolean enable) { this._enableHttp = enable; return this; } public boolean enableWebSocket() { return this._enableWebSocket; } public SolonApp enableWebSocket(boolean enable) { this._enableWebSocket = enable; return this; } public boolean enableWebSocketD() { return this._enableWebSocketD; } public SolonApp enableWebSocketD(boolean enable) { this._enableWebSocketD = enable; if (enable) this._enableWebSocket = enable;  return this; } public boolean enableSocketD() { return this._enableSocketD; } public SolonApp enableSocketD(boolean enable) { this._enableSocketD = enable; return this; } public boolean enableTransaction() { return this._enableTransaction; } public SolonApp enableTransaction(boolean enable) { this._enableTransaction = enable; return this; } public boolean enableCaching() { return this._enableCaching; } public SolonApp enableCaching(boolean enable) { this._enableCaching = enable; return this; } public boolean enableStaticfiles() { return this._enableStaticfiles; } public SolonApp enableStaticfiles(boolean enable) { this._enableStaticfiles = enable; return this; }
/*     */   public boolean enableErrorAutoprint() { return this._enableErrorAutoprint; }
/*     */   public void enableErrorAutoprint(boolean enable) { this._enableErrorAutoprint = enable; }
/*     */   public boolean enableSessionState() { return this._enableSessionState; }
/*     */   public SolonApp enableSessionState(boolean enable) { this._enableSessionState = enable; return this; }
/*     */   public boolean enableJarIsolation() { return this._enableJarIsolation; }
/*     */   private SolonApp enableJarIsolation(boolean enable) { this._enableJarIsolation = enable; return this; }
/* 641 */   public void enableSafeStop(boolean enable) { this._enableSafeStop = enable; }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\SolonApp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */