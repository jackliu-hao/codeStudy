/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import org.xnio.FileSystemWatcher;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.Xnio;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.management.XnioProviderMXBean;
/*     */ import org.xnio.management.XnioServerMXBean;
/*     */ import org.xnio.management.XnioWorkerMXBean;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class NioXnio
/*     */   extends Xnio
/*     */ {
/*     */   static {
/*  58 */     Log.log.greeting(Version.getVersionString());
/*  59 */   } static final boolean IS_HP_UX = ((Boolean)AccessController.<Boolean>doPrivileged(new PrivilegedAction<Boolean>() {
/*     */         public Boolean run() {
/*  61 */           String bugLevel = System.getProperty("sun.nio.ch.bugLevel");
/*  62 */           if (bugLevel == null) System.setProperty("sun.nio.ch.bugLevel", ""); 
/*  63 */           return Boolean.valueOf(System.getProperty("os.name", "unknown").equalsIgnoreCase("hp-ux"));
/*     */         }
/*  65 */       })).booleanValue();
/*     */   
/*     */   static final boolean HAS_BUGGY_EVENT_PORT = true;
/*     */   final SelectorCreator tempSelectorCreator;
/*     */   final SelectorCreator mainSelectorCreator;
/*     */   private final ThreadLocal<FinalizableSelectorHolder> selectorThreadLocal;
/*     */   
/*     */   NioXnio()
/*     */   {
/*  74 */     super("nio");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 255 */     this.selectorThreadLocal = new ThreadLocal<FinalizableSelectorHolder>() { public Object[] run() { String jdkVersion = System.getProperty("java.specification.version", "1.8"); boolean jdk9 = (!jdkVersion.equals("1.8") && !jdkVersion.equals("8")); SelectorProvider defaultProvider = SelectorProvider.provider(); String chosenProvider = System.getProperty("xnio.nio.selector.provider"); SelectorProvider provider = null; if (chosenProvider != null) try { provider = Class.forName(chosenProvider, true, NioXnio.class.getClassLoader()).<SelectorProvider>asSubclass(SelectorProvider.class).getConstructor(new Class[0]).newInstance(new Object[0]); provider.openSelector().close(); } catch (Throwable e) { provider = null; }   if (!jdk9) { if (provider == null) try { provider = Class.forName("sun.nio.ch.KQueueSelectorProvider", true, NioXnio.class.getClassLoader()).<SelectorProvider>asSubclass(SelectorProvider.class).getConstructor(new Class[0]).newInstance(new Object[0]); provider.openSelector().close(); } catch (Throwable e) { provider = null; }   if (provider == null) try { provider = Class.forName("sun.nio.ch.EPollSelectorProvider", true, NioXnio.class.getClassLoader()).<SelectorProvider>asSubclass(SelectorProvider.class).getConstructor(new Class[0]).newInstance(new Object[0]); provider.openSelector().close(); } catch (Throwable e) { provider = null; }   if (provider == null && !NioXnio.HAS_BUGGY_EVENT_PORT) try { provider = Class.forName("sun.nio.ch.EventPortSelectorProvider", true, NioXnio.class.getClassLoader()).<SelectorProvider>asSubclass(SelectorProvider.class).getConstructor(new Class[0]).newInstance(new Object[0]); provider.openSelector().close(); } catch (Throwable e) { provider = null; }   if (provider == null) try { provider = Class.forName("sun.nio.ch.DevPollSelectorProvider", true, NioXnio.class.getClassLoader()).<SelectorProvider>asSubclass(SelectorProvider.class).getConstructor(new Class[0]).newInstance(new Object[0]); provider.openSelector().close(); } catch (Throwable e) { provider = null; }   if (provider == null) try { provider = Class.forName("sun.nio.ch.EventPortSelectorProvider", true, NioXnio.class.getClassLoader()).<SelectorProvider>asSubclass(SelectorProvider.class).getConstructor(new Class[0]).newInstance(new Object[0]); provider.openSelector().close(); } catch (Throwable e) { provider = null; }   if (provider == null) try { provider = Class.forName("sun.nio.ch.PollsetSelectorProvider", true, NioXnio.class.getClassLoader()).<SelectorProvider>asSubclass(SelectorProvider.class).getConstructor(new Class[0]).newInstance(new Object[0]); provider.openSelector().close(); } catch (Throwable e) { provider = null; }   }  if (provider == null) try { defaultProvider.openSelector().close(); provider = defaultProvider; } catch (Throwable throwable) {}  if (provider == null) try { provider = Class.forName("sun.nio.ch.PollSelectorProvider", true, NioXnio.class.getClassLoader()).<SelectorProvider>asSubclass(SelectorProvider.class).getConstructor(new Class[0]).newInstance(new Object[0]); provider.openSelector().close(); } catch (Throwable e) { provider = null; }   if (provider == null) throw Log.log.noSelectorProvider();  Log.log.selectorProvider(provider); boolean defaultIsPoll = "sun.nio.ch.PollSelectorProvider".equals(provider.getClass().getName()); String chosenMainSelector = System.getProperty("xnio.nio.selector.main"); String chosenTempSelector = System.getProperty("xnio.nio.selector.temp"); NioXnio.SelectorCreator defaultSelectorCreator = new NioXnio.DefaultSelectorCreator(provider); Object[] objects = new Object[3]; objects[0] = provider; if (chosenTempSelector != null) try { NioXnio.ConstructorSelectorCreator creator = new NioXnio.ConstructorSelectorCreator(chosenTempSelector, provider); IoUtils.safeClose(creator.open()); objects[1] = creator; } catch (Exception exception) {}  if (chosenMainSelector != null) try { NioXnio.ConstructorSelectorCreator creator = new NioXnio.ConstructorSelectorCreator(chosenMainSelector, provider); IoUtils.safeClose(creator.open()); objects[2] = creator; } catch (Exception exception) {}  if (!defaultIsPoll && !jdk9) if (objects[1] == null) try { SelectorProvider pollSelectorProvider = Class.forName("sun.nio.ch.PollSelectorProvider", true, NioXnio.class.getClassLoader()).<SelectorProvider>asSubclass(SelectorProvider.class).getConstructor(new Class[0]).newInstance(new Object[0]); pollSelectorProvider.openSelector().close(); objects[1] = new NioXnio.DefaultSelectorCreator(provider); } catch (Exception exception) {}   if (objects[1] == null)
/*     */             objects[1] = defaultSelectorCreator;  if (objects[2] == null)
/*     */             objects[2] = defaultSelectorCreator;  return objects; }
/* 258 */       }); this.tempSelectorCreator = (SelectorCreator)objects[1]; this.mainSelectorCreator = (SelectorCreator)objects[2]; Log.log.selectors(this.mainSelectorCreator, this.tempSelectorCreator); register(new XnioProviderMXBean() { public void remove() { NioXnio.FinalizableSelectorHolder holder = get();
/* 259 */           if (holder != null) {
/* 260 */             IoUtils.safeClose(holder.selector);
/*     */           }
/* 262 */           super.remove(); } }
/*     */       ; Object[] objects = AccessController.<Object[]>doPrivileged(new PrivilegedAction<Object[]>()
/*     */         {
/*     */           public String getName() { return "nio"; }
/*     */           public String getVersion() { return Version.getVersionString(); } }); }
/* 267 */   protected XnioWorker build(XnioWorker.Builder builder) { NioXnioWorker worker = new NioXnioWorker(builder); worker.start(); return worker; } public FileSystemWatcher createFileSystemWatcher(String name, OptionMap options) { try { boolean daemonThread = options.get(Options.THREAD_DAEMON, true); return new WatchServiceFileSystemWatcher(name, daemonThread); } catch (LinkageError linkageError) { return super.createFileSystemWatcher(name, options); }  } protected void handleThreadExit() { Log.log.tracef("Invoke selectorThreadLocal.remove() on Thread [%s] exits", Thread.currentThread().getName()); this.selectorThreadLocal.remove(); super.handleThreadExit(); } Selector getSelector() throws IOException { ThreadLocal<FinalizableSelectorHolder> threadLocal = this.selectorThreadLocal;
/* 268 */     FinalizableSelectorHolder holder = threadLocal.get();
/* 269 */     if (holder == null) {
/* 270 */       holder = new FinalizableSelectorHolder(this.tempSelectorCreator.open());
/* 271 */       threadLocal.set(holder);
/*     */     } 
/* 273 */     return holder.selector; }
/*     */   
/*     */   static interface SelectorCreator {
/*     */     Selector open() throws IOException; }
/*     */   private static class DefaultSelectorCreator implements SelectorCreator { private final SelectorProvider provider;
/*     */     
/*     */     private DefaultSelectorCreator(SelectorProvider provider) {
/* 280 */       this.provider = provider;
/*     */     }
/*     */     
/*     */     public Selector open() throws IOException {
/* 284 */       return this.provider.openSelector();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 288 */       return "Default system selector creator for provider " + this.provider.getClass();
/*     */     } }
/*     */ 
/*     */   
/*     */   private static class ConstructorSelectorCreator
/*     */     implements SelectorCreator {
/*     */     private final Constructor<? extends Selector> constructor;
/*     */     private final SelectorProvider provider;
/*     */     
/*     */     public ConstructorSelectorCreator(String name, SelectorProvider provider) throws ClassNotFoundException, NoSuchMethodException {
/* 298 */       this.provider = provider;
/* 299 */       Class<? extends Selector> selectorImplClass = Class.forName(name, true, null).asSubclass(Selector.class);
/* 300 */       Constructor<? extends Selector> constructor = selectorImplClass.getDeclaredConstructor(new Class[] { SelectorProvider.class });
/* 301 */       constructor.setAccessible(true);
/* 302 */       this.constructor = constructor;
/*     */     }
/*     */     
/*     */     public Selector open() throws IOException {
/*     */       try {
/* 307 */         return this.constructor.newInstance(new Object[] { this.provider });
/* 308 */       } catch (InstantiationException e) {
/* 309 */         return Selector.open();
/* 310 */       } catch (IllegalAccessException e) {
/* 311 */         return Selector.open();
/* 312 */       } catch (InvocationTargetException e) {
/*     */         try {
/* 314 */           throw e.getTargetException();
/* 315 */         } catch (IOException|Error|RuntimeException e2) {
/* 316 */           throw e2;
/* 317 */         } catch (Throwable t) {
/* 318 */           throw Log.log.unexpectedSelectorOpenProblem(t);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     public String toString() {
/* 324 */       return String.format("Selector creator %s for provider %s", new Object[] { this.constructor.getDeclaringClass(), this.provider.getClass() });
/*     */     }
/*     */   }
/*     */   
/*     */   protected static Closeable register(XnioWorkerMXBean workerMXBean) {
/* 329 */     return Xnio.register(workerMXBean);
/*     */   }
/*     */   
/*     */   protected static Closeable register(XnioServerMXBean serverMXBean) {
/* 333 */     return Xnio.register(serverMXBean);
/*     */   }
/*     */   
/*     */   private static final class FinalizableSelectorHolder
/*     */   {
/*     */     final Selector selector;
/*     */     
/*     */     private FinalizableSelectorHolder(Selector selector) {
/* 341 */       this.selector = selector;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void finalize() throws Throwable {
/* 346 */       IoUtils.safeClose(this.selector);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioXnio.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */