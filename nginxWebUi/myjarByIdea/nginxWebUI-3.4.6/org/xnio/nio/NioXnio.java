package org.xnio.nio;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.xnio.FileSystemWatcher;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;
import org.xnio.management.XnioProviderMXBean;
import org.xnio.management.XnioServerMXBean;
import org.xnio.management.XnioWorkerMXBean;

final class NioXnio extends Xnio {
   static final boolean IS_HP_UX;
   static final boolean HAS_BUGGY_EVENT_PORT;
   final SelectorCreator tempSelectorCreator;
   final SelectorCreator mainSelectorCreator;
   private final ThreadLocal<FinalizableSelectorHolder> selectorThreadLocal = new ThreadLocal<FinalizableSelectorHolder>() {
      public void remove() {
         FinalizableSelectorHolder holder = (FinalizableSelectorHolder)this.get();
         if (holder != null) {
            IoUtils.safeClose(holder.selector);
         }

         super.remove();
      }
   };

   NioXnio() {
      super("nio");
      Object[] objects = (Object[])AccessController.doPrivileged(new PrivilegedAction<Object[]>() {
         public Object[] run() {
            String jdkVersion = System.getProperty("java.specification.version", "1.8");
            boolean jdk9 = !jdkVersion.equals("1.8") && !jdkVersion.equals("8");
            SelectorProvider defaultProvider = SelectorProvider.provider();
            String chosenProvider = System.getProperty("xnio.nio.selector.provider");
            SelectorProvider provider = null;
            if (chosenProvider != null) {
               try {
                  provider = (SelectorProvider)Class.forName(chosenProvider, true, NioXnio.class.getClassLoader()).asSubclass(SelectorProvider.class).getConstructor().newInstance();
                  provider.openSelector().close();
               } catch (Throwable var23) {
                  provider = null;
               }
            }

            if (!jdk9) {
               if (provider == null) {
                  try {
                     provider = (SelectorProvider)Class.forName("sun.nio.ch.KQueueSelectorProvider", true, NioXnio.class.getClassLoader()).asSubclass(SelectorProvider.class).getConstructor().newInstance();
                     provider.openSelector().close();
                  } catch (Throwable var22) {
                     provider = null;
                  }
               }

               if (provider == null) {
                  try {
                     provider = (SelectorProvider)Class.forName("sun.nio.ch.EPollSelectorProvider", true, NioXnio.class.getClassLoader()).asSubclass(SelectorProvider.class).getConstructor().newInstance();
                     provider.openSelector().close();
                  } catch (Throwable var21) {
                     provider = null;
                  }
               }

               if (provider == null && !NioXnio.HAS_BUGGY_EVENT_PORT) {
                  try {
                     provider = (SelectorProvider)Class.forName("sun.nio.ch.EventPortSelectorProvider", true, NioXnio.class.getClassLoader()).asSubclass(SelectorProvider.class).getConstructor().newInstance();
                     provider.openSelector().close();
                  } catch (Throwable var20) {
                     provider = null;
                  }
               }

               if (provider == null) {
                  try {
                     provider = (SelectorProvider)Class.forName("sun.nio.ch.DevPollSelectorProvider", true, NioXnio.class.getClassLoader()).asSubclass(SelectorProvider.class).getConstructor().newInstance();
                     provider.openSelector().close();
                  } catch (Throwable var19) {
                     provider = null;
                  }
               }

               if (provider == null) {
                  try {
                     provider = (SelectorProvider)Class.forName("sun.nio.ch.EventPortSelectorProvider", true, NioXnio.class.getClassLoader()).asSubclass(SelectorProvider.class).getConstructor().newInstance();
                     provider.openSelector().close();
                  } catch (Throwable var18) {
                     provider = null;
                  }
               }

               if (provider == null) {
                  try {
                     provider = (SelectorProvider)Class.forName("sun.nio.ch.PollsetSelectorProvider", true, NioXnio.class.getClassLoader()).asSubclass(SelectorProvider.class).getConstructor().newInstance();
                     provider.openSelector().close();
                  } catch (Throwable var17) {
                     provider = null;
                  }
               }
            }

            if (provider == null) {
               try {
                  defaultProvider.openSelector().close();
                  provider = defaultProvider;
               } catch (Throwable var16) {
               }
            }

            if (provider == null) {
               try {
                  provider = (SelectorProvider)Class.forName("sun.nio.ch.PollSelectorProvider", true, NioXnio.class.getClassLoader()).asSubclass(SelectorProvider.class).getConstructor().newInstance();
                  provider.openSelector().close();
               } catch (Throwable var15) {
                  provider = null;
               }
            }

            if (provider == null) {
               throw Log.log.noSelectorProvider();
            } else {
               Log.log.selectorProvider(provider);
               boolean defaultIsPoll = "sun.nio.ch.PollSelectorProvider".equals(provider.getClass().getName());
               String chosenMainSelector = System.getProperty("xnio.nio.selector.main");
               String chosenTempSelector = System.getProperty("xnio.nio.selector.temp");
               SelectorCreator defaultSelectorCreator = new DefaultSelectorCreator(provider);
               Object[] objects = new Object[]{provider, null, null};
               ConstructorSelectorCreator creator;
               if (chosenTempSelector != null) {
                  try {
                     creator = new ConstructorSelectorCreator(chosenTempSelector, provider);
                     IoUtils.safeClose(creator.open());
                     objects[1] = creator;
                  } catch (Exception var14) {
                  }
               }

               if (chosenMainSelector != null) {
                  try {
                     creator = new ConstructorSelectorCreator(chosenMainSelector, provider);
                     IoUtils.safeClose(creator.open());
                     objects[2] = creator;
                  } catch (Exception var13) {
                  }
               }

               if (!defaultIsPoll && !jdk9 && objects[1] == null) {
                  try {
                     SelectorProvider pollSelectorProvider = (SelectorProvider)Class.forName("sun.nio.ch.PollSelectorProvider", true, NioXnio.class.getClassLoader()).asSubclass(SelectorProvider.class).getConstructor().newInstance();
                     pollSelectorProvider.openSelector().close();
                     objects[1] = new DefaultSelectorCreator(provider);
                  } catch (Exception var12) {
                  }
               }

               if (objects[1] == null) {
                  objects[1] = defaultSelectorCreator;
               }

               if (objects[2] == null) {
                  objects[2] = defaultSelectorCreator;
               }

               return objects;
            }
         }
      });
      this.tempSelectorCreator = (SelectorCreator)objects[1];
      this.mainSelectorCreator = (SelectorCreator)objects[2];
      Log.log.selectors(this.mainSelectorCreator, this.tempSelectorCreator);
      register((XnioProviderMXBean)(new XnioProviderMXBean() {
         public String getName() {
            return "nio";
         }

         public String getVersion() {
            return Version.getVersionString();
         }
      }));
   }

   protected XnioWorker build(XnioWorker.Builder builder) {
      NioXnioWorker worker = new NioXnioWorker(builder);
      worker.start();
      return worker;
   }

   public FileSystemWatcher createFileSystemWatcher(String name, OptionMap options) {
      try {
         boolean daemonThread = options.get(Options.THREAD_DAEMON, true);
         return new WatchServiceFileSystemWatcher(name, daemonThread);
      } catch (LinkageError var4) {
         return super.createFileSystemWatcher(name, options);
      }
   }

   protected void handleThreadExit() {
      Log.log.tracef("Invoke selectorThreadLocal.remove() on Thread [%s] exits", Thread.currentThread().getName());
      this.selectorThreadLocal.remove();
      super.handleThreadExit();
   }

   Selector getSelector() throws IOException {
      ThreadLocal<FinalizableSelectorHolder> threadLocal = this.selectorThreadLocal;
      FinalizableSelectorHolder holder = (FinalizableSelectorHolder)threadLocal.get();
      if (holder == null) {
         holder = new FinalizableSelectorHolder(this.tempSelectorCreator.open());
         threadLocal.set(holder);
      }

      return holder.selector;
   }

   protected static Closeable register(XnioWorkerMXBean workerMXBean) {
      return Xnio.register(workerMXBean);
   }

   protected static Closeable register(XnioServerMXBean serverMXBean) {
      return Xnio.register(serverMXBean);
   }

   static {
      Log.log.greeting(Version.getVersionString());
      IS_HP_UX = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            String bugLevel = System.getProperty("sun.nio.ch.bugLevel");
            if (bugLevel == null) {
               System.setProperty("sun.nio.ch.bugLevel", "");
            }

            return System.getProperty("os.name", "unknown").equalsIgnoreCase("hp-ux");
         }
      });
      HAS_BUGGY_EVENT_PORT = true;
   }

   private static final class FinalizableSelectorHolder {
      final Selector selector;

      private FinalizableSelectorHolder(Selector selector) {
         this.selector = selector;
      }

      protected void finalize() throws Throwable {
         IoUtils.safeClose(this.selector);
      }

      // $FF: synthetic method
      FinalizableSelectorHolder(Selector x0, Object x1) {
         this(x0);
      }
   }

   private static class ConstructorSelectorCreator implements SelectorCreator {
      private final Constructor<? extends Selector> constructor;
      private final SelectorProvider provider;

      public ConstructorSelectorCreator(String name, SelectorProvider provider) throws ClassNotFoundException, NoSuchMethodException {
         this.provider = provider;
         Class<? extends Selector> selectorImplClass = Class.forName(name, true, (ClassLoader)null).asSubclass(Selector.class);
         Constructor<? extends Selector> constructor = selectorImplClass.getDeclaredConstructor(SelectorProvider.class);
         constructor.setAccessible(true);
         this.constructor = constructor;
      }

      public Selector open() throws IOException {
         try {
            return (Selector)this.constructor.newInstance(this.provider);
         } catch (InstantiationException var5) {
            return Selector.open();
         } catch (IllegalAccessException var6) {
            return Selector.open();
         } catch (InvocationTargetException var7) {
            InvocationTargetException e = var7;

            try {
               throw e.getTargetException();
            } catch (Error | RuntimeException | IOException var3) {
               throw var3;
            } catch (Throwable var4) {
               throw Log.log.unexpectedSelectorOpenProblem(var4);
            }
         }
      }

      public String toString() {
         return String.format("Selector creator %s for provider %s", this.constructor.getDeclaringClass(), this.provider.getClass());
      }
   }

   private static class DefaultSelectorCreator implements SelectorCreator {
      private final SelectorProvider provider;

      private DefaultSelectorCreator(SelectorProvider provider) {
         this.provider = provider;
      }

      public Selector open() throws IOException {
         return this.provider.openSelector();
      }

      public String toString() {
         return "Default system selector creator for provider " + this.provider.getClass();
      }

      // $FF: synthetic method
      DefaultSelectorCreator(SelectorProvider x0, Object x1) {
         this(x0);
      }
   }

   interface SelectorCreator {
      Selector open() throws IOException;
   }
}
