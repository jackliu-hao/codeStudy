/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.file.NoSuchFileException;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.security.AccessController;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.EnumMap;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import org.osgi.framework.Bundle;
/*     */ import org.osgi.framework.BundleContext;
/*     */ import org.osgi.framework.FrameworkUtil;
/*     */ import org.osgi.framework.ServiceReference;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.management.XnioProviderMXBean;
/*     */ import org.xnio.management.XnioServerMXBean;
/*     */ import org.xnio.management.XnioWorkerMXBean;
/*     */ import org.xnio.ssl.JsseSslUtils;
/*     */ import org.xnio.ssl.JsseXnioSsl;
/*     */ import org.xnio.ssl.XnioSsl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Xnio
/*     */ {
/*  68 */   static final InetSocketAddress ANY_INET_ADDRESS = new InetSocketAddress(0);
/*  69 */   static final LocalSocketAddress ANY_LOCAL_ADDRESS = new LocalSocketAddress("");
/*     */   
/*     */   private static final EnumMap<FileAccess, OptionMap> FILE_ACCESS_OPTION_MAPS;
/*     */   
/*  73 */   private static final RuntimePermission ALLOW_BLOCKING_SETTING = new RuntimePermission("changeThreadBlockingSetting");
/*     */   public static final boolean NIO2 = true;
/*     */   private final String name;
/*     */   private static final ThreadLocal<Boolean> BLOCKING;
/*     */   
/*     */   static final class MBeanHolder
/*     */   {
/*  80 */     private static final MBeanServer MBEAN_SERVER = AccessController.<MBeanServer>doPrivileged(new PrivilegedAction<MBeanServer>() {
/*     */           public MBeanServer run() {
/*  82 */             return ManagementFactory.getPlatformMBeanServer();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  95 */     Messages.msg.greeting(Version.VERSION);
/*  96 */     EnumMap<FileAccess, OptionMap> map = new EnumMap<>(FileAccess.class);
/*  97 */     for (FileAccess access : FileAccess.values()) {
/*  98 */       map.put(access, OptionMap.create(Options.FILE_ACCESS, access));
/*     */     }
/* 100 */     FILE_ACCESS_OPTION_MAPS = map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     BLOCKING = new ThreadLocal<Boolean>() {
/*     */         protected Boolean initialValue() {
/* 123 */           return Boolean.TRUE;
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   protected Xnio(String name) {
/*     */     if (name == null) {
/*     */       throw Messages.msg.nullParameter("name");
/*     */     }
/*     */     this.name = name;
/*     */   }
/*     */   
/*     */   public static boolean allowBlocking(boolean newSetting) throws SecurityException {
/* 136 */     SecurityManager sm = System.getSecurityManager();
/* 137 */     if (sm != null) {
/* 138 */       sm.checkPermission(ALLOW_BLOCKING_SETTING);
/*     */     }
/* 140 */     ThreadLocal<Boolean> threadLocal = BLOCKING;
/*     */     try {
/* 142 */       return ((Boolean)threadLocal.get()).booleanValue();
/*     */     } finally {
/* 144 */       threadLocal.set(Boolean.valueOf(newSetting));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBlockingAllowed() {
/* 154 */     return ((Boolean)BLOCKING.get()).booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkBlockingAllowed() throws IllegalStateException {
/* 163 */     if (!((Boolean)BLOCKING.get()).booleanValue()) {
/* 164 */       throw Messages.msg.blockingNotAllowed();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Xnio getInstance(final ClassLoader classLoader) {
/* 178 */     return doGetInstance(null, AccessController.<ServiceLoader<XnioProvider>>doPrivileged(new PrivilegedAction<ServiceLoader<XnioProvider>>() {
/*     */             public ServiceLoader<XnioProvider> run() {
/* 180 */               return ServiceLoader.load(XnioProvider.class, classLoader);
/*     */             }
/*     */           }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Xnio getInstance() {
/* 194 */     return doGetInstance(null, AccessController.<ServiceLoader<XnioProvider>>doPrivileged(new PrivilegedAction<ServiceLoader<XnioProvider>>() {
/*     */             public ServiceLoader<XnioProvider> run() {
/* 196 */               return ServiceLoader.load(XnioProvider.class, Xnio.class.getClassLoader());
/*     */             }
/*     */           }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Xnio getInstance(String provider, final ClassLoader classLoader) {
/* 211 */     return doGetInstance(provider, AccessController.<ServiceLoader<XnioProvider>>doPrivileged(new PrivilegedAction<ServiceLoader<XnioProvider>>() {
/*     */             public ServiceLoader<XnioProvider> run() {
/* 213 */               return ServiceLoader.load(XnioProvider.class, classLoader);
/*     */             }
/*     */           }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Xnio getInstance(String provider) {
/* 227 */     return doGetInstance(provider, AccessController.<ServiceLoader<XnioProvider>>doPrivileged(new PrivilegedAction<ServiceLoader<XnioProvider>>() {
/*     */             public ServiceLoader<XnioProvider> run() {
/* 229 */               return ServiceLoader.load(XnioProvider.class, Xnio.class.getClassLoader());
/*     */             }
/*     */           }));
/*     */   }
/*     */   
/*     */   private static synchronized Xnio doGetInstance(String provider, ServiceLoader<XnioProvider> serviceLoader) {
/* 235 */     Iterator<XnioProvider> iterator = serviceLoader.iterator();
/*     */     while (true) {
/*     */       try {
/* 238 */         if (!iterator.hasNext())
/* 239 */           break;  XnioProvider xnioProvider = iterator.next();
/*     */         try {
/* 241 */           if (provider == null || provider.equals(xnioProvider.getName())) {
/* 242 */             return xnioProvider.getInstance();
/*     */           }
/* 244 */         } catch (Throwable t) {
/* 245 */           Messages.msg.debugf(t, "Not loading provider %s", xnioProvider.getName());
/*     */         } 
/* 247 */       } catch (Throwable t) {
/* 248 */         Messages.msg.debugf(t, "Skipping non-loadable provider", new Object[0]);
/*     */       } 
/*     */     } 
/*     */     try {
/* 252 */       Xnio xnio = OsgiSupport.doGetOsgiService();
/* 253 */       if (xnio != null) {
/* 254 */         return xnio;
/*     */       }
/* 256 */     } catch (NoClassDefFoundError noClassDefFoundError) {
/*     */     
/* 258 */     } catch (Throwable t) {
/* 259 */       Messages.msg.debugf(t, "Not using OSGi service", new Object[0]);
/*     */     } 
/* 261 */     throw Messages.msg.noProviderFound();
/*     */   }
/*     */   
/*     */   static class OsgiSupport
/*     */   {
/*     */     static Xnio doGetOsgiService() {
/* 267 */       Bundle bundle = FrameworkUtil.getBundle(Xnio.class);
/* 268 */       BundleContext context = bundle.getBundleContext();
/* 269 */       if (context == null) {
/* 270 */         throw new IllegalStateException("Bundle not started");
/*     */       }
/* 272 */       ServiceReference<Xnio> sr = context.getServiceReference(Xnio.class);
/* 273 */       if (sr == null) {
/* 274 */         return null;
/*     */       }
/* 276 */       return (Xnio)context.getService(sr);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XnioSsl getSslProvider(OptionMap optionMap) throws GeneralSecurityException {
/* 295 */     return (XnioSsl)new JsseXnioSsl(this, optionMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XnioSsl getSslProvider(KeyManager[] keyManagers, TrustManager[] trustManagers, OptionMap optionMap) throws GeneralSecurityException {
/* 308 */     return (XnioSsl)new JsseXnioSsl(this, optionMap, JsseSslUtils.createSSLContext(keyManagers, trustManagers, null, optionMap));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileChannel openFile(File file, OptionMap options) throws IOException {
/* 326 */     if (file == null) {
/* 327 */       throw Messages.msg.nullParameter("file");
/*     */     }
/* 329 */     if (options == null) {
/* 330 */       throw Messages.msg.nullParameter("options");
/*     */     }
/*     */     try {
/* 333 */       FileAccess fileAccess = options.<FileAccess>get(Options.FILE_ACCESS, FileAccess.READ_WRITE);
/* 334 */       boolean append = options.get(Options.FILE_APPEND, false);
/* 335 */       boolean create = options.get(Options.FILE_CREATE, (fileAccess != FileAccess.READ_ONLY));
/* 336 */       EnumSet<StandardOpenOption> openOptions = EnumSet.noneOf(StandardOpenOption.class);
/* 337 */       if (create) {
/* 338 */         openOptions.add(StandardOpenOption.CREATE);
/*     */       }
/* 340 */       if (fileAccess.isRead()) {
/* 341 */         openOptions.add(StandardOpenOption.READ);
/*     */       }
/* 343 */       if (fileAccess.isWrite()) {
/* 344 */         openOptions.add(StandardOpenOption.WRITE);
/*     */       }
/* 346 */       if (append) {
/* 347 */         openOptions.add(StandardOpenOption.APPEND);
/*     */       }
/* 349 */       Path path = file.toPath();
/* 350 */       return new XnioFileChannel(path.getFileSystem().provider().newFileChannel(path, (Set)openOptions, (FileAttribute<?>[])new FileAttribute[0]));
/* 351 */     } catch (NoSuchFileException e) {
/* 352 */       throw new FileNotFoundException(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileChannel openFile(String fileName, OptionMap options) throws IOException {
/* 365 */     if (fileName == null) {
/* 366 */       throw Messages.msg.nullParameter("fileName");
/*     */     }
/* 368 */     return openFile(new File(fileName), options);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileChannel openFile(File file, FileAccess access) throws IOException {
/* 380 */     if (access == null) {
/* 381 */       throw Messages.msg.nullParameter("access");
/*     */     }
/* 383 */     return openFile(file, FILE_ACCESS_OPTION_MAPS.get(access));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileChannel openFile(String fileName, FileAccess access) throws IOException {
/* 395 */     if (access == null) {
/* 396 */       throw Messages.msg.nullParameter("access");
/*     */     }
/* 398 */     if (fileName == null) {
/* 399 */       throw Messages.msg.nullParameter("fileName");
/*     */     }
/* 401 */     return openFile(new File(fileName), FILE_ACCESS_OPTION_MAPS.get(access));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected FileChannel unwrapFileChannel(FileChannel src) {
/* 411 */     if (src instanceof XnioFileChannel) {
/* 412 */       return ((XnioFileChannel)src).getDelegate();
/*     */     }
/* 414 */     return src;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XnioWorker.Builder createWorkerBuilder() {
/* 430 */     return new XnioWorker.Builder(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XnioWorker createWorker(OptionMap optionMap) throws IOException, IllegalArgumentException {
/* 450 */     return createWorker(null, optionMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XnioWorker createWorker(ThreadGroup threadGroup, OptionMap optionMap) throws IOException, IllegalArgumentException {
/* 463 */     return createWorker(threadGroup, optionMap, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XnioWorker createWorker(ThreadGroup threadGroup, OptionMap optionMap, Runnable terminationTask) throws IOException, IllegalArgumentException {
/* 477 */     XnioWorker.Builder workerBuilder = createWorkerBuilder();
/* 478 */     workerBuilder.populateFromOptions(optionMap);
/* 479 */     workerBuilder.setThreadGroup(threadGroup);
/* 480 */     workerBuilder.setTerminationTask(terminationTask);
/* 481 */     return workerBuilder.build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileSystemWatcher createFileSystemWatcher(String name, OptionMap options) {
/* 492 */     int pollInterval = options.get(Options.WATCHER_POLL_INTERVAL, 5000);
/* 493 */     boolean daemonThread = options.get(Options.THREAD_DAEMON, true);
/* 494 */     return new PollingFileSystemWatcher(name, pollInterval, daemonThread);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleThreadExit() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getName() {
/* 515 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 524 */     return String.format("XNIO provider \"%s\" <%s@%s>", new Object[] { getName(), getClass().getName(), Integer.toHexString(hashCode()) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String getProperty(String name) {
/* 535 */     if (!name.startsWith("xnio.")) {
/* 536 */       throw Messages.msg.propReadForbidden();
/*     */     }
/* 538 */     SecurityManager sm = System.getSecurityManager();
/* 539 */     if (sm != null) {
/* 540 */       return AccessController.<String>doPrivileged(new ReadPropertyAction(name, null));
/*     */     }
/* 542 */     return System.getProperty(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String getProperty(String name, String defaultValue) {
/* 555 */     if (!name.startsWith("xnio.")) {
/* 556 */       throw Messages.msg.propReadForbidden();
/*     */     }
/* 558 */     SecurityManager sm = System.getSecurityManager();
/* 559 */     if (sm != null) {
/* 560 */       return AccessController.<String>doPrivileged(new ReadPropertyAction(name, defaultValue));
/*     */     }
/* 562 */     return System.getProperty(name, defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static Closeable register(XnioProviderMXBean providerMXBean) {
/*     */     try {
/* 574 */       ObjectName objectName = new ObjectName("org.xnio", ObjectProperties.properties(new ObjectProperties.Property[] { ObjectProperties.property("type", "Xnio"), ObjectProperties.property("provider", ObjectName.quote(providerMXBean.getName())) }));
/* 575 */       MBeanHolder.MBEAN_SERVER.registerMBean(providerMXBean, objectName);
/* 576 */       return new MBeanCloseable(objectName);
/* 577 */     } catch (Throwable ignored) {
/* 578 */       return IoUtils.nullCloseable();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static Closeable register(XnioWorkerMXBean workerMXBean) {
/*     */     try {
/* 590 */       ObjectName objectName = new ObjectName("org.xnio", ObjectProperties.properties(new ObjectProperties.Property[] { ObjectProperties.property("type", "Xnio"), ObjectProperties.property("provider", ObjectName.quote(workerMXBean.getProviderName())), ObjectProperties.property("worker", ObjectName.quote(workerMXBean.getName())) }));
/* 591 */       MBeanHolder.MBEAN_SERVER.registerMBean(workerMXBean, objectName);
/* 592 */       return new MBeanCloseable(objectName);
/* 593 */     } catch (Throwable ignored) {
/* 594 */       return IoUtils.nullCloseable();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static Closeable register(XnioServerMXBean serverMXBean) {
/*     */     try {
/* 606 */       ObjectName objectName = new ObjectName("org.xnio", ObjectProperties.properties(new ObjectProperties.Property[] { ObjectProperties.property("type", "Xnio"), ObjectProperties.property("provider", ObjectName.quote(serverMXBean.getProviderName())), ObjectProperties.property("worker", ObjectName.quote(serverMXBean.getWorkerName())), ObjectProperties.property("address", ObjectName.quote(serverMXBean.getBindAddress())) }));
/* 607 */       MBeanHolder.MBEAN_SERVER.registerMBean(serverMXBean, objectName);
/* 608 */       return new MBeanCloseable(objectName);
/* 609 */     } catch (Throwable ignored) {
/* 610 */       return IoUtils.nullCloseable();
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract XnioWorker build(XnioWorker.Builder paramBuilder);
/*     */   
/*     */   static class MBeanCloseable extends AtomicBoolean implements Closeable { private final ObjectName objectName;
/*     */     
/*     */     MBeanCloseable(ObjectName objectName) {
/* 619 */       this.objectName = objectName;
/*     */     }
/*     */     
/*     */     public void close() {
/* 623 */       if (!getAndSet(true))
/* 624 */         try { Xnio.MBeanHolder.MBEAN_SERVER.unregisterMBean(this.objectName); }
/* 625 */         catch (Throwable throwable) {} 
/*     */     } }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Xnio.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */