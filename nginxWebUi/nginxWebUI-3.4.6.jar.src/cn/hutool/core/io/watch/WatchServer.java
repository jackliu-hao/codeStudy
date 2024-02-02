/*     */ package cn.hutool.core.io.watch;
/*     */ 
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.nio.file.FileSystems;
/*     */ import java.nio.file.FileVisitOption;
/*     */ import java.nio.file.FileVisitResult;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.SimpleFileVisitor;
/*     */ import java.nio.file.WatchEvent;
/*     */ import java.nio.file.WatchKey;
/*     */ import java.nio.file.WatchService;
/*     */ import java.util.EnumSet;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WatchServer
/*     */   extends Thread
/*     */   implements Closeable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private WatchService watchService;
/*     */   protected WatchEvent.Kind<?>[] events;
/*     */   private WatchEvent.Modifier[] modifiers;
/*     */   protected boolean isClosed;
/*  53 */   private final Map<WatchKey, Path> watchKeyPathMap = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
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
/*     */     try {
/*  68 */       this.watchService = FileSystems.getDefault().newWatchService();
/*  69 */     } catch (IOException e) {
/*  70 */       throw new WatchException(e);
/*     */     } 
/*     */     
/*  73 */     this.isClosed = false;
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
/*     */   public void setModifiers(WatchEvent.Modifier[] modifiers) {
/*  87 */     this.modifiers = modifiers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerPath(Path path, int maxDepth) {
/*  97 */     WatchEvent.Kind[] arrayOfKind = (WatchEvent.Kind[])ArrayUtil.defaultIfEmpty((Object[])this.events, (Object[])WatchKind.ALL);
/*     */     
/*     */     try {
/*     */       WatchKey key;
/* 101 */       if (ArrayUtil.isEmpty((Object[])this.modifiers)) {
/* 102 */         key = path.register(this.watchService, (WatchEvent.Kind<?>[])arrayOfKind);
/*     */       } else {
/* 104 */         key = path.register(this.watchService, (WatchEvent.Kind<?>[])arrayOfKind, this.modifiers);
/*     */       } 
/* 106 */       this.watchKeyPathMap.put(key, path);
/*     */ 
/*     */       
/* 109 */       if (maxDepth > 1)
/*     */       {
/* 111 */         Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), maxDepth, new SimpleFileVisitor<Path>()
/*     */             {
/*     */               public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
/* 114 */                 WatchServer.this.registerPath(dir, 0);
/* 115 */                 return super.postVisitDirectory(dir, exc);
/*     */               }
/*     */             });
/*     */       }
/* 119 */     } catch (IOException e) {
/* 120 */       if (false == e instanceof java.nio.file.AccessDeniedException) {
/* 121 */         throw new WatchException(e);
/*     */       }
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
/*     */   public void watch(WatchAction action, Filter<WatchEvent<?>> watchFilter) {
/*     */     WatchKey wk;
/*     */     try {
/* 138 */       wk = this.watchService.take();
/* 139 */     } catch (InterruptedException|java.nio.file.ClosedWatchServiceException e) {
/*     */       
/* 141 */       close();
/*     */       
/*     */       return;
/*     */     } 
/* 145 */     Path currentPath = this.watchKeyPathMap.get(wk);
/*     */     
/* 147 */     for (WatchEvent<?> event : wk.pollEvents()) {
/*     */       
/* 149 */       if (null != watchFilter && false == watchFilter.accept(event)) {
/*     */         continue;
/*     */       }
/*     */       
/* 153 */       action.doAction(event, currentPath);
/*     */     } 
/*     */     
/* 156 */     wk.reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void watch(Watcher watcher, Filter<WatchEvent<?>> watchFilter) {
/* 166 */     watch((event, currentPath) -> { WatchEvent.Kind<?> kind = event.kind(); if (kind == WatchKind.CREATE.getValue()) { watcher.onCreate(event, currentPath); } else if (kind == WatchKind.MODIFY.getValue()) { watcher.onModify(event, currentPath); } else if (kind == WatchKind.DELETE.getValue()) { watcher.onDelete(event, currentPath); } else if (kind == WatchKind.OVERFLOW.getValue()) { watcher.onOverflow(event, currentPath); }  }watchFilter);
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
/*     */   public void close() {
/* 186 */     this.isClosed = true;
/* 187 */     IoUtil.close(this.watchService);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\watch\WatchServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */