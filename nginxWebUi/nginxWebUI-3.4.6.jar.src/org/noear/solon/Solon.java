/*     */ package org.noear.solon;
/*     */ 
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.RuntimeMXBean;
/*     */ import org.noear.solon.core.JarClassLoader;
/*     */ import org.noear.solon.core.NvMap;
/*     */ import org.noear.solon.core.PluginEntity;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.core.util.PrintUtil;
/*     */ import org.noear.solon.ext.ConsumerEx;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Solon
/*     */ {
/*  28 */   private static int stopDelay = 10;
/*     */   
/*     */   private static SolonApp app;
/*     */   
/*  32 */   private static String encoding = "utf-8";
/*     */   
/*     */   @Deprecated
/*     */   public static SolonApp global() {
/*  36 */     return app;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SolonApp app() {
/*  43 */     return app;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SolonProps cfg() {
/*  50 */     return app().cfg();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encoding() {
/*  57 */     return encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void encodingSet(String charset) {
/*  65 */     if (app == null && Utils.isNotEmpty(charset)) {
/*  66 */       encoding = charset;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SolonApp start(Class<?> source, String[] args) {
/*  77 */     return start(source, args, (ConsumerEx<SolonApp>)null);
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
/*     */   public static SolonApp start(Class<?> source, String[] args, ConsumerEx<SolonApp> initialize) {
/*  89 */     NvMap argx = NvMap.from(args);
/*  90 */     return start(source, argx, initialize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SolonApp start(Class<?> source, NvMap argx, ConsumerEx<SolonApp> initialize) {
/* 101 */     if (app != null) {
/* 102 */       return app;
/*     */     }
/*     */ 
/*     */     
/* 106 */     if (Utils.isNotEmpty(encoding)) {
/* 107 */       System.setProperty("file.encoding", encoding);
/*     */     }
/*     */ 
/*     */     
/* 111 */     System.getProperties().putIfAbsent("java.awt.headless", "true");
/*     */ 
/*     */     
/* 114 */     RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
/* 115 */     String pid = rb.getName().split("@")[0];
/* 116 */     System.setProperty("PID", pid);
/*     */ 
/*     */     
/* 119 */     JarClassLoader.bindingThread();
/*     */     
/* 121 */     PrintUtil.info("App", "Start loading");
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 126 */       app = new SolonApp(source, argx);
/*     */ 
/*     */       
/* 129 */       app.initAwait();
/*     */ 
/*     */       
/* 132 */       app.init();
/*     */ 
/*     */       
/* 135 */       if (initialize != null) {
/* 136 */         initialize.accept(app);
/*     */       }
/*     */ 
/*     */       
/* 140 */       app.run();
/*     */     }
/* 142 */     catch (Throwable ex) {
/*     */       
/* 144 */       ex = Utils.throwableUnwrap(ex);
/* 145 */       EventBus.push(ex);
/*     */       
/* 147 */       if (!app.enableErrorAutoprint()) {
/* 148 */         ex.printStackTrace();
/*     */       }
/*     */ 
/*     */       
/* 152 */       stop0(true, 0);
/* 153 */       return null;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 158 */     stopDelay = cfg().getInt("solon.stop.delay", 10);
/*     */     
/* 160 */     if (app.enableSafeStop()) {
/*     */       
/* 162 */       Runtime.getRuntime().addShutdownHook(new Thread(() -> stop0(false, stopDelay)));
/*     */     } else {
/* 164 */       Runtime.getRuntime().addShutdownHook(new Thread(() -> stop0(false, 0)));
/*     */     } 
/*     */ 
/*     */     
/* 168 */     PrintUtil.info("App", "End loading elapsed=" + app.elapsedTimes() + "ms pid=" + pid);
/*     */     
/* 170 */     return app;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void stopDelaySet(int delay) {
/* 179 */     stopDelay = delay;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void stop() {
/* 186 */     stop(stopDelay);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void stop(int delay) {
/* 195 */     (new Thread(() -> stop0(true, delay))).start();
/*     */   }
/*     */   
/*     */   private static void stop0(boolean exit, int delay) {
/* 199 */     if (app() == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 204 */     if (delay > 0) {
/*     */       
/* 206 */       String hint = "(1.prestop 2.delay 3.stop)";
/*     */       
/* 208 */       PrintUtil.info("App", "Security to stop: begin..." + hint);
/*     */ 
/*     */       
/* 211 */       cfg().plugs().forEach(p -> p.prestop());
/* 212 */       PrintUtil.info("App", "Security to stop: 1 completed " + hint);
/*     */ 
/*     */ 
/*     */       
/* 216 */       int delay1 = (int)(delay * 0.2D);
/* 217 */       int delay2 = delay - delay1;
/*     */ 
/*     */       
/* 220 */       if (delay1 > 0) {
/* 221 */         sleep0(delay1);
/*     */       }
/*     */       
/* 224 */       (app()).stopped = true;
/*     */ 
/*     */       
/* 227 */       if (delay2 > 0) {
/* 228 */         sleep0(delay2);
/*     */       }
/*     */       
/* 231 */       PrintUtil.info("App", "Security to stop: 2 completed " + hint);
/*     */ 
/*     */       
/* 234 */       cfg().plugs().forEach(p -> p.stop());
/* 235 */       PrintUtil.info("App", "Security to stop: 3 completed " + hint);
/*     */     } else {
/*     */       
/* 238 */       cfg().plugs().forEach(p -> p.prestop());
/*     */ 
/*     */       
/* 241 */       (app()).stopped = true;
/*     */       
/* 243 */       cfg().plugs().forEach(p -> p.stop());
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 248 */     if (exit) {
/* 249 */       System.exit(1);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void sleep0(int seconds) {
/*     */     try {
/* 255 */       Thread.sleep((seconds * 1000));
/* 256 */     } catch (InterruptedException interruptedException) {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\Solon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */