/*     */ package cn.hutool.core.io.watch;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.WatchEvent;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WatchMonitor
/*     */   extends WatchServer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  37 */   public static final WatchEvent.Kind<?> OVERFLOW = WatchKind.OVERFLOW.getValue();
/*     */ 
/*     */ 
/*     */   
/*  41 */   public static final WatchEvent.Kind<?> ENTRY_MODIFY = WatchKind.MODIFY.getValue();
/*     */ 
/*     */ 
/*     */   
/*  45 */   public static final WatchEvent.Kind<?> ENTRY_CREATE = WatchKind.CREATE.getValue();
/*     */ 
/*     */ 
/*     */   
/*  49 */   public static final WatchEvent.Kind<?> ENTRY_DELETE = WatchKind.DELETE.getValue();
/*     */ 
/*     */ 
/*     */   
/*  53 */   public static final WatchEvent.Kind<?>[] EVENTS_ALL = WatchKind.ALL;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Path path;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int maxDepth;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Path filePath;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Watcher watcher;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WatchMonitor create(URL url, WatchEvent.Kind<?>... events) {
/*  82 */     return create(url, 0, events);
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
/*  94 */     return create(URLUtil.toURI(url), maxDepth, events);
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
/* 105 */     return create(uri, 0, events);
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
/* 117 */     return create(Paths.get(uri), maxDepth, events);
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
/* 128 */     return create(file, 0, events);
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
/* 140 */     return create(file.toPath(), maxDepth, events);
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
/* 151 */     return create(path, 0, events);
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
/* 163 */     return create(Paths.get(path, new String[0]), maxDepth, events);
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
/* 174 */     return create(path, 0, events);
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
/* 186 */     return new WatchMonitor(path, maxDepth, events);
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
/*     */   public static WatchMonitor createAll(URI uri, Watcher watcher) {
/* 199 */     return createAll(Paths.get(uri), watcher);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WatchMonitor createAll(URL url, Watcher watcher) {
/*     */     try {
/* 211 */       return createAll(Paths.get(url.toURI()), watcher);
/* 212 */     } catch (URISyntaxException e) {
/* 213 */       throw new WatchException(e);
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
/*     */   public static WatchMonitor createAll(File file, Watcher watcher) {
/* 225 */     return createAll(file.toPath(), watcher);
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
/* 236 */     return createAll(Paths.get(path, new String[0]), watcher);
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
/* 247 */     WatchMonitor watchMonitor = create(path, EVENTS_ALL);
/* 248 */     watchMonitor.setWatcher(watcher);
/* 249 */     return watchMonitor;
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
/*     */   public WatchMonitor(File file, WatchEvent.Kind<?>... events) {
/* 262 */     this(file.toPath(), events);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WatchMonitor(String path, WatchEvent.Kind<?>... events) {
/* 272 */     this(Paths.get(path, new String[0]), events);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WatchMonitor(Path path, WatchEvent.Kind<?>... events) {
/* 282 */     this(path, 0, events);
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
/*     */   public WatchMonitor(Path path, int maxDepth, WatchEvent.Kind<?>... events) {
/* 299 */     this.path = path;
/* 300 */     this.maxDepth = maxDepth;
/* 301 */     this.events = events;
/* 302 */     init();
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
/*     */   public void init() throws WatchException {
/* 319 */     if (false == Files.exists(this.path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
/*     */       
/* 321 */       Path lastPathEle = FileUtil.getLastPathEle(this.path);
/* 322 */       if (null != lastPathEle) {
/* 323 */         String lastPathEleStr = lastPathEle.toString();
/*     */         
/* 325 */         if (StrUtil.contains(lastPathEleStr, '.') && false == StrUtil.endWithIgnoreCase(lastPathEleStr, ".d")) {
/* 326 */           this.filePath = this.path;
/* 327 */           this.path = this.filePath.getParent();
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 333 */         Files.createDirectories(this.path, (FileAttribute<?>[])new FileAttribute[0]);
/* 334 */       } catch (IOException e) {
/* 335 */         throw new IORuntimeException(e);
/*     */       } 
/* 337 */     } else if (Files.isRegularFile(this.path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
/*     */       
/* 339 */       this.filePath = this.path;
/* 340 */       this.path = this.filePath.getParent();
/*     */     } 
/*     */     
/* 343 */     super.init();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WatchMonitor setWatcher(Watcher watcher) {
/* 354 */     this.watcher = watcher;
/* 355 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/* 360 */     watch();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void watch() {
/* 367 */     watch(this.watcher);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void watch(Watcher watcher) throws WatchException {
/* 377 */     if (this.isClosed) {
/* 378 */       throw new WatchException("Watch Monitor is closed !");
/*     */     }
/*     */ 
/*     */     
/* 382 */     registerPath();
/*     */ 
/*     */     
/* 385 */     while (false == this.isClosed) {
/* 386 */       doTakeAndWatch(watcher);
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
/*     */   public WatchMonitor setMaxDepth(int maxDepth) {
/* 404 */     this.maxDepth = maxDepth;
/* 405 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doTakeAndWatch(Watcher watcher) {
/* 416 */     watch(watcher, watchEvent -> (null == this.filePath || this.filePath.endsWith(watchEvent.context().toString())));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void registerPath() {
/* 423 */     registerPath(this.path, (null != this.filePath) ? 0 : this.maxDepth);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\watch\WatchMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */