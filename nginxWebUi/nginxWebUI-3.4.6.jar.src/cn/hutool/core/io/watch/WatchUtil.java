/*     */ package cn.hutool.core.io.watch;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.WatchEvent;
/*     */ import java.nio.file.WatchKey;
/*     */ import java.nio.file.WatchService;
/*     */ import java.nio.file.Watchable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WatchUtil
/*     */ {
/*     */   public static WatchMonitor create(URL url, WatchEvent.Kind<?>... events) {
/*  28 */     return create(url, 0, events);
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
/*     */   public static WatchMonitor create(URL url, int maxDepth, WatchEvent.Kind<?>... events) {
/*  40 */     return create(URLUtil.toURI(url), maxDepth, events);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WatchMonitor create(URI uri, WatchEvent.Kind<?>... events) {
/*  51 */     return create(uri, 0, events);
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
/*     */   public static WatchMonitor create(URI uri, int maxDepth, WatchEvent.Kind<?>... events) {
/*  63 */     return create(Paths.get(uri), maxDepth, events);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WatchMonitor create(File file, WatchEvent.Kind<?>... events) {
/*  74 */     return create(file, 0, events);
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
/*     */   public static WatchMonitor create(File file, int maxDepth, WatchEvent.Kind<?>... events) {
/*  86 */     return create(file.toPath(), maxDepth, events);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WatchMonitor create(String path, WatchEvent.Kind<?>... events) {
/*  97 */     return create(path, 0, events);
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
/*     */   public static WatchMonitor create(String path, int maxDepth, WatchEvent.Kind<?>... events) {
/* 109 */     return create(Paths.get(path, new String[0]), maxDepth, events);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WatchMonitor create(Path path, WatchEvent.Kind<?>... events) {
/* 120 */     return create(path, 0, events);
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
/*     */   public static WatchMonitor create(Path path, int maxDepth, WatchEvent.Kind<?>... events) {
/* 132 */     return new WatchMonitor(path, maxDepth, events);
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
/*     */   public static WatchMonitor createAll(URL url, Watcher watcher) {
/* 144 */     return createAll(url, 0, watcher);
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
/*     */   public static WatchMonitor createAll(URL url, int maxDepth, Watcher watcher) {
/* 156 */     return createAll(URLUtil.toURI(url), maxDepth, watcher);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WatchMonitor createAll(URI uri, Watcher watcher) {
/* 167 */     return createAll(uri, 0, watcher);
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
/*     */   public static WatchMonitor createAll(URI uri, int maxDepth, Watcher watcher) {
/* 179 */     return createAll(Paths.get(uri), maxDepth, watcher);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WatchMonitor createAll(File file, Watcher watcher) {
/* 190 */     return createAll(file, 0, watcher);
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
/*     */   public static WatchMonitor createAll(File file, int maxDepth, Watcher watcher) {
/* 202 */     return createAll(file.toPath(), 0, watcher);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WatchMonitor createAll(String path, Watcher watcher) {
/* 213 */     return createAll(path, 0, watcher);
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
/*     */   public static WatchMonitor createAll(String path, int maxDepth, Watcher watcher) {
/* 225 */     return createAll(Paths.get(path, new String[0]), maxDepth, watcher);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WatchMonitor createAll(Path path, Watcher watcher) {
/* 236 */     return createAll(path, 0, watcher);
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
/*     */   public static WatchMonitor createAll(Path path, int maxDepth, Watcher watcher) {
/* 248 */     WatchMonitor watchMonitor = create(path, maxDepth, WatchMonitor.EVENTS_ALL);
/* 249 */     watchMonitor.setWatcher(watcher);
/* 250 */     return watchMonitor;
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
/*     */   public static WatchMonitor createModify(URL url, Watcher watcher) {
/* 263 */     return createModify(url, 0, watcher);
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
/*     */   public static WatchMonitor createModify(URL url, int maxDepth, Watcher watcher) {
/* 276 */     return createModify(URLUtil.toURI(url), maxDepth, watcher);
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
/*     */   public static WatchMonitor createModify(URI uri, Watcher watcher) {
/* 288 */     return createModify(uri, 0, watcher);
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
/*     */   public static WatchMonitor createModify(URI uri, int maxDepth, Watcher watcher) {
/* 301 */     return createModify(Paths.get(uri), maxDepth, watcher);
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
/*     */   public static WatchMonitor createModify(File file, Watcher watcher) {
/* 313 */     return createModify(file, 0, watcher);
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
/*     */   public static WatchMonitor createModify(File file, int maxDepth, Watcher watcher) {
/* 326 */     return createModify(file.toPath(), 0, watcher);
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
/*     */   public static WatchMonitor createModify(String path, Watcher watcher) {
/* 338 */     return createModify(path, 0, watcher);
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
/*     */   public static WatchMonitor createModify(String path, int maxDepth, Watcher watcher) {
/* 351 */     return createModify(Paths.get(path, new String[0]), maxDepth, watcher);
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
/*     */   public static WatchMonitor createModify(Path path, Watcher watcher) {
/* 363 */     return createModify(path, 0, watcher);
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
/*     */   public static WatchMonitor createModify(Path path, int maxDepth, Watcher watcher) {
/* 376 */     WatchMonitor watchMonitor = create(path, maxDepth, (WatchEvent.Kind<?>[])new WatchEvent.Kind[] { WatchMonitor.ENTRY_MODIFY });
/* 377 */     watchMonitor.setWatcher(watcher);
/* 378 */     return watchMonitor;
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
/*     */   public static WatchKey register(Watchable watchable, WatchService watcher, WatchEvent.Kind<?>... events) {
/*     */     try {
/* 392 */       return watchable.register(watcher, events);
/* 393 */     } catch (IOException e) {
/* 394 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\watch\WatchUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */