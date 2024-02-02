package org.noear.solon;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.ext.ConsumerEx;

public class Solon {
   private static int stopDelay = 10;
   private static SolonApp app;
   private static String encoding = "utf-8";

   /** @deprecated */
   @Deprecated
   public static SolonApp global() {
      return app;
   }

   public static SolonApp app() {
      return app;
   }

   public static SolonProps cfg() {
      return app().cfg();
   }

   public static String encoding() {
      return encoding;
   }

   public static void encodingSet(String charset) {
      if (app == null && Utils.isNotEmpty(charset)) {
         encoding = charset;
      }

   }

   public static SolonApp start(Class<?> source, String[] args) {
      return start(source, (String[])args, (ConsumerEx)null);
   }

   public static SolonApp start(Class<?> source, String[] args, ConsumerEx<SolonApp> initialize) {
      NvMap argx = NvMap.from(args);
      return start(source, argx, initialize);
   }

   public static SolonApp start(Class<?> source, NvMap argx, ConsumerEx<SolonApp> initialize) {
      if (app != null) {
         return app;
      } else {
         if (Utils.isNotEmpty(encoding)) {
            System.setProperty("file.encoding", encoding);
         }

         System.getProperties().putIfAbsent("java.awt.headless", "true");
         RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
         String pid = rb.getName().split("@")[0];
         System.setProperty("PID", pid);
         JarClassLoader.bindingThread();
         PrintUtil.info("App", "Start loading");

         try {
            app = new SolonApp(source, argx);
            app.initAwait();
            app.init();
            if (initialize != null) {
               initialize.accept(app);
            }

            app.run();
         } catch (Throwable var6) {
            Throwable ex = Utils.throwableUnwrap(var6);
            EventBus.push(ex);
            if (!app.enableErrorAutoprint()) {
               ex.printStackTrace();
            }

            stop0(true, 0);
            return null;
         }

         stopDelay = cfg().getInt("solon.stop.delay", 10);
         if (app.enableSafeStop()) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
               stop0(false, stopDelay);
            }));
         } else {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
               stop0(false, 0);
            }));
         }

         PrintUtil.info("App", "End loading elapsed=" + app.elapsedTimes() + "ms pid=" + pid);
         return app;
      }
   }

   public static void stopDelaySet(int delay) {
      stopDelay = delay;
   }

   public static void stop() {
      stop(stopDelay);
   }

   public static void stop(int delay) {
      (new Thread(() -> {
         stop0(true, delay);
      })).start();
   }

   private static void stop0(boolean exit, int delay) {
      if (app() != null) {
         if (delay > 0) {
            String hint = "(1.prestop 2.delay 3.stop)";
            PrintUtil.info("App", "Security to stop: begin..." + hint);
            cfg().plugs().forEach((p) -> {
               p.prestop();
            });
            PrintUtil.info("App", "Security to stop: 1 completed " + hint);
            int delay1 = (int)((double)delay * 0.2);
            int delay2 = delay - delay1;
            if (delay1 > 0) {
               sleep0(delay1);
            }

            app().stopped = true;
            if (delay2 > 0) {
               sleep0(delay2);
            }

            PrintUtil.info("App", "Security to stop: 2 completed " + hint);
            cfg().plugs().forEach((p) -> {
               p.stop();
            });
            PrintUtil.info("App", "Security to stop: 3 completed " + hint);
         } else {
            cfg().plugs().forEach((p) -> {
               p.prestop();
            });
            app().stopped = true;
            cfg().plugs().forEach((p) -> {
               p.stop();
            });
         }

         if (exit) {
            System.exit(1);
         }

      }
   }

   private static void sleep0(int seconds) {
      try {
         Thread.sleep((long)(seconds * 1000));
      } catch (InterruptedException var2) {
      }

   }
}
