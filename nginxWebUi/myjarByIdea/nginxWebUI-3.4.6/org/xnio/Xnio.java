package org.xnio;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.xnio._private.Messages;
import org.xnio.management.XnioProviderMXBean;
import org.xnio.management.XnioServerMXBean;
import org.xnio.management.XnioWorkerMXBean;
import org.xnio.ssl.JsseSslUtils;
import org.xnio.ssl.JsseXnioSsl;
import org.xnio.ssl.XnioSsl;

public abstract class Xnio {
   static final InetSocketAddress ANY_INET_ADDRESS = new InetSocketAddress(0);
   static final LocalSocketAddress ANY_LOCAL_ADDRESS = new LocalSocketAddress("");
   private static final EnumMap<FileAccess, OptionMap> FILE_ACCESS_OPTION_MAPS;
   private static final RuntimePermission ALLOW_BLOCKING_SETTING = new RuntimePermission("changeThreadBlockingSetting");
   public static final boolean NIO2 = true;
   private final String name;
   private static final ThreadLocal<Boolean> BLOCKING;

   protected Xnio(String name) {
      if (name == null) {
         throw Messages.msg.nullParameter("name");
      } else {
         this.name = name;
      }
   }

   public static boolean allowBlocking(boolean newSetting) throws SecurityException {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(ALLOW_BLOCKING_SETTING);
      }

      ThreadLocal<Boolean> threadLocal = BLOCKING;

      boolean var3;
      try {
         var3 = (Boolean)threadLocal.get();
      } finally {
         threadLocal.set(newSetting);
      }

      return var3;
   }

   public static boolean isBlockingAllowed() {
      return (Boolean)BLOCKING.get();
   }

   public static void checkBlockingAllowed() throws IllegalStateException {
      if (!(Boolean)BLOCKING.get()) {
         throw Messages.msg.blockingNotAllowed();
      }
   }

   public static Xnio getInstance(final ClassLoader classLoader) {
      return doGetInstance((String)null, (ServiceLoader)AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<XnioProvider>>() {
         public ServiceLoader<XnioProvider> run() {
            return ServiceLoader.load(XnioProvider.class, classLoader);
         }
      }));
   }

   public static Xnio getInstance() {
      return doGetInstance((String)null, (ServiceLoader)AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<XnioProvider>>() {
         public ServiceLoader<XnioProvider> run() {
            return ServiceLoader.load(XnioProvider.class, Xnio.class.getClassLoader());
         }
      }));
   }

   public static Xnio getInstance(String provider, final ClassLoader classLoader) {
      return doGetInstance(provider, (ServiceLoader)AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<XnioProvider>>() {
         public ServiceLoader<XnioProvider> run() {
            return ServiceLoader.load(XnioProvider.class, classLoader);
         }
      }));
   }

   public static Xnio getInstance(String provider) {
      return doGetInstance(provider, (ServiceLoader)AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<XnioProvider>>() {
         public ServiceLoader<XnioProvider> run() {
            return ServiceLoader.load(XnioProvider.class, Xnio.class.getClassLoader());
         }
      }));
   }

   private static synchronized Xnio doGetInstance(String provider, ServiceLoader<XnioProvider> serviceLoader) {
      Iterator<XnioProvider> iterator = serviceLoader.iterator();

      while(true) {
         try {
            if (!iterator.hasNext()) {
               break;
            }

            XnioProvider xnioProvider = (XnioProvider)iterator.next();

            try {
               if (provider == null || provider.equals(xnioProvider.getName())) {
                  return xnioProvider.getInstance();
               }
            } catch (Throwable var7) {
               Messages.msg.debugf(var7, "Not loading provider %s", xnioProvider.getName());
            }
         } catch (Throwable var8) {
            Messages.msg.debugf(var8, "Skipping non-loadable provider", new Object[0]);
         }
      }

      try {
         Xnio xnio = Xnio.OsgiSupport.doGetOsgiService();
         if (xnio != null) {
            return xnio;
         }
      } catch (NoClassDefFoundError var5) {
      } catch (Throwable var6) {
         Messages.msg.debugf(var6, "Not using OSGi service", new Object[0]);
      }

      throw Messages.msg.noProviderFound();
   }

   public XnioSsl getSslProvider(OptionMap optionMap) throws GeneralSecurityException {
      return new JsseXnioSsl(this, optionMap);
   }

   public XnioSsl getSslProvider(KeyManager[] keyManagers, TrustManager[] trustManagers, OptionMap optionMap) throws GeneralSecurityException {
      return new JsseXnioSsl(this, optionMap, JsseSslUtils.createSSLContext(keyManagers, trustManagers, (SecureRandom)null, optionMap));
   }

   public FileChannel openFile(File file, OptionMap options) throws IOException {
      if (file == null) {
         throw Messages.msg.nullParameter("file");
      } else if (options == null) {
         throw Messages.msg.nullParameter("options");
      } else {
         try {
            FileAccess fileAccess = (FileAccess)options.get(Options.FILE_ACCESS, FileAccess.READ_WRITE);
            boolean append = options.get(Options.FILE_APPEND, false);
            boolean create = options.get(Options.FILE_CREATE, fileAccess != FileAccess.READ_ONLY);
            EnumSet<StandardOpenOption> openOptions = EnumSet.noneOf(StandardOpenOption.class);
            if (create) {
               openOptions.add(StandardOpenOption.CREATE);
            }

            if (fileAccess.isRead()) {
               openOptions.add(StandardOpenOption.READ);
            }

            if (fileAccess.isWrite()) {
               openOptions.add(StandardOpenOption.WRITE);
            }

            if (append) {
               openOptions.add(StandardOpenOption.APPEND);
            }

            Path path = file.toPath();
            return new XnioFileChannel(path.getFileSystem().provider().newFileChannel(path, openOptions));
         } catch (NoSuchFileException var8) {
            throw new FileNotFoundException(var8.getMessage());
         }
      }
   }

   public FileChannel openFile(String fileName, OptionMap options) throws IOException {
      if (fileName == null) {
         throw Messages.msg.nullParameter("fileName");
      } else {
         return this.openFile(new File(fileName), options);
      }
   }

   public FileChannel openFile(File file, FileAccess access) throws IOException {
      if (access == null) {
         throw Messages.msg.nullParameter("access");
      } else {
         return this.openFile(file, (OptionMap)FILE_ACCESS_OPTION_MAPS.get(access));
      }
   }

   public FileChannel openFile(String fileName, FileAccess access) throws IOException {
      if (access == null) {
         throw Messages.msg.nullParameter("access");
      } else if (fileName == null) {
         throw Messages.msg.nullParameter("fileName");
      } else {
         return this.openFile(new File(fileName), (OptionMap)FILE_ACCESS_OPTION_MAPS.get(access));
      }
   }

   protected FileChannel unwrapFileChannel(FileChannel src) {
      return src instanceof XnioFileChannel ? ((XnioFileChannel)src).getDelegate() : src;
   }

   public XnioWorker.Builder createWorkerBuilder() {
      return new XnioWorker.Builder(this);
   }

   protected abstract XnioWorker build(XnioWorker.Builder var1);

   public XnioWorker createWorker(OptionMap optionMap) throws IOException, IllegalArgumentException {
      return this.createWorker((ThreadGroup)null, optionMap);
   }

   public XnioWorker createWorker(ThreadGroup threadGroup, OptionMap optionMap) throws IOException, IllegalArgumentException {
      return this.createWorker(threadGroup, optionMap, (Runnable)null);
   }

   public XnioWorker createWorker(ThreadGroup threadGroup, OptionMap optionMap, Runnable terminationTask) throws IOException, IllegalArgumentException {
      XnioWorker.Builder workerBuilder = this.createWorkerBuilder();
      workerBuilder.populateFromOptions(optionMap);
      workerBuilder.setThreadGroup(threadGroup);
      workerBuilder.setTerminationTask(terminationTask);
      return workerBuilder.build();
   }

   public FileSystemWatcher createFileSystemWatcher(String name, OptionMap options) {
      int pollInterval = options.get(Options.WATCHER_POLL_INTERVAL, 5000);
      boolean daemonThread = options.get(Options.THREAD_DAEMON, true);
      return new PollingFileSystemWatcher(name, pollInterval, daemonThread);
   }

   protected void handleThreadExit() {
   }

   public final String getName() {
      return this.name;
   }

   public final String toString() {
      return String.format("XNIO provider \"%s\" <%s@%s>", this.getName(), this.getClass().getName(), Integer.toHexString(this.hashCode()));
   }

   protected static String getProperty(String name) {
      if (!name.startsWith("xnio.")) {
         throw Messages.msg.propReadForbidden();
      } else {
         SecurityManager sm = System.getSecurityManager();
         return sm != null ? (String)AccessController.doPrivileged(new ReadPropertyAction(name, (String)null)) : System.getProperty(name);
      }
   }

   protected static String getProperty(String name, String defaultValue) {
      if (!name.startsWith("xnio.")) {
         throw Messages.msg.propReadForbidden();
      } else {
         SecurityManager sm = System.getSecurityManager();
         return sm != null ? (String)AccessController.doPrivileged(new ReadPropertyAction(name, defaultValue)) : System.getProperty(name, defaultValue);
      }
   }

   protected static Closeable register(XnioProviderMXBean providerMXBean) {
      try {
         ObjectName objectName = new ObjectName("org.xnio", ObjectProperties.properties(ObjectProperties.property("type", "Xnio"), ObjectProperties.property("provider", ObjectName.quote(providerMXBean.getName()))));
         Xnio.MBeanHolder.MBEAN_SERVER.registerMBean(providerMXBean, objectName);
         return new MBeanCloseable(objectName);
      } catch (Throwable var2) {
         return IoUtils.nullCloseable();
      }
   }

   protected static Closeable register(XnioWorkerMXBean workerMXBean) {
      try {
         ObjectName objectName = new ObjectName("org.xnio", ObjectProperties.properties(ObjectProperties.property("type", "Xnio"), ObjectProperties.property("provider", ObjectName.quote(workerMXBean.getProviderName())), ObjectProperties.property("worker", ObjectName.quote(workerMXBean.getName()))));
         Xnio.MBeanHolder.MBEAN_SERVER.registerMBean(workerMXBean, objectName);
         return new MBeanCloseable(objectName);
      } catch (Throwable var2) {
         return IoUtils.nullCloseable();
      }
   }

   protected static Closeable register(XnioServerMXBean serverMXBean) {
      try {
         ObjectName objectName = new ObjectName("org.xnio", ObjectProperties.properties(ObjectProperties.property("type", "Xnio"), ObjectProperties.property("provider", ObjectName.quote(serverMXBean.getProviderName())), ObjectProperties.property("worker", ObjectName.quote(serverMXBean.getWorkerName())), ObjectProperties.property("address", ObjectName.quote(serverMXBean.getBindAddress()))));
         Xnio.MBeanHolder.MBEAN_SERVER.registerMBean(serverMXBean, objectName);
         return new MBeanCloseable(objectName);
      } catch (Throwable var2) {
         return IoUtils.nullCloseable();
      }
   }

   static {
      Messages.msg.greeting(Version.VERSION);
      EnumMap<FileAccess, OptionMap> map = new EnumMap(FileAccess.class);
      FileAccess[] var1 = FileAccess.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         FileAccess access = var1[var3];
         map.put(access, OptionMap.create(Options.FILE_ACCESS, access));
      }

      FILE_ACCESS_OPTION_MAPS = map;
      BLOCKING = new ThreadLocal<Boolean>() {
         protected Boolean initialValue() {
            return Boolean.TRUE;
         }
      };
   }

   static class MBeanCloseable extends AtomicBoolean implements Closeable {
      private final ObjectName objectName;

      MBeanCloseable(ObjectName objectName) {
         this.objectName = objectName;
      }

      public void close() {
         if (!this.getAndSet(true)) {
            try {
               Xnio.MBeanHolder.MBEAN_SERVER.unregisterMBean(this.objectName);
            } catch (Throwable var2) {
            }
         }

      }
   }

   static class OsgiSupport {
      static Xnio doGetOsgiService() {
         Bundle bundle = FrameworkUtil.getBundle(Xnio.class);
         BundleContext context = bundle.getBundleContext();
         if (context == null) {
            throw new IllegalStateException("Bundle not started");
         } else {
            ServiceReference<Xnio> sr = context.getServiceReference(Xnio.class);
            return sr == null ? null : (Xnio)context.getService(sr);
         }
      }
   }

   static final class MBeanHolder {
      private static final MBeanServer MBEAN_SERVER = (MBeanServer)AccessController.doPrivileged(new PrivilegedAction<MBeanServer>() {
         public MBeanServer run() {
            return ManagementFactory.getPlatformMBeanServer();
         }
      });
   }
}
