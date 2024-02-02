/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.ClosedWatchServiceException;
/*     */ import java.nio.file.FileSystems;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.StandardWatchEventKinds;
/*     */ import java.nio.file.WatchEvent;
/*     */ import java.nio.file.WatchKey;
/*     */ import java.nio.file.WatchService;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Deque;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.xnio.FileChangeCallback;
/*     */ import org.xnio.FileChangeEvent;
/*     */ import org.xnio.FileSystemWatcher;
/*     */ import org.xnio.IoUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class WatchServiceFileSystemWatcher
/*     */   implements FileSystemWatcher, Runnable
/*     */ {
/*  61 */   private static final AtomicInteger threadIdCounter = new AtomicInteger(0);
/*     */   
/*     */   public static final String THREAD_NAME = "xnio-file-watcher";
/*     */   private WatchService watchService;
/*  65 */   private final Map<File, PathData> files = Collections.synchronizedMap(new HashMap<>());
/*  66 */   private final Map<WatchKey, PathData> pathDataByKey = Collections.synchronizedMap(new IdentityHashMap<>());
/*     */   
/*     */   private volatile boolean stopped = false;
/*     */   private final Thread watchThread;
/*     */   
/*     */   WatchServiceFileSystemWatcher(String name, boolean daemon) {
/*     */     try {
/*  73 */       this.watchService = FileSystems.getDefault().newWatchService();
/*  74 */     } catch (IOException e) {
/*  75 */       throw new RuntimeException(e);
/*     */     } 
/*  77 */     this.watchThread = new Thread(this, "xnio-file-watcher[" + name + "]-" + threadIdCounter);
/*  78 */     this.watchThread.setDaemon(daemon);
/*  79 */     this.watchThread.start();
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*  84 */     while (!this.stopped) {
/*     */       try {
/*  86 */         WatchKey key = this.watchService.take();
/*  87 */         if (key != null) {
/*     */           try {
/*  89 */             PathData pathData = this.pathDataByKey.get(key);
/*  90 */             if (pathData != null) {
/*  91 */               List<FileChangeEvent> results = new ArrayList<>();
/*  92 */               List<WatchEvent<?>> events = key.pollEvents();
/*  93 */               Set<File> addedFiles = new HashSet<>();
/*  94 */               Set<File> deletedFiles = new HashSet<>();
/*  95 */               for (WatchEvent<?> event : events) {
/*  96 */                 FileChangeEvent.Type type; Path eventPath = (Path)event.context();
/*  97 */                 File targetFile = ((Path)key.watchable()).resolve(eventPath).toFile();
/*     */ 
/*     */                 
/* 100 */                 if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
/* 101 */                   type = FileChangeEvent.Type.ADDED;
/* 102 */                   addedFiles.add(targetFile);
/* 103 */                   if (targetFile.isDirectory()) {
/*     */                     try {
/* 105 */                       addWatchedDirectory(pathData, targetFile);
/* 106 */                     } catch (IOException e) {
/* 107 */                       Log.log.debugf(e, "Could not add watched directory %s", targetFile);
/*     */                     } 
/*     */                   }
/* 110 */                 } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
/* 111 */                   type = FileChangeEvent.Type.MODIFIED;
/* 112 */                 } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
/* 113 */                   type = FileChangeEvent.Type.REMOVED;
/* 114 */                   deletedFiles.add(targetFile);
/*     */                 } else {
/*     */                   continue;
/*     */                 } 
/* 118 */                 results.add(new FileChangeEvent(targetFile, type));
/*     */               } 
/* 120 */               key.pollEvents().clear();
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 125 */               Iterator<FileChangeEvent> it = results.iterator();
/* 126 */               while (it.hasNext()) {
/* 127 */                 FileChangeEvent event = it.next();
/* 128 */                 if (event.getType() == FileChangeEvent.Type.MODIFIED) {
/* 129 */                   if (addedFiles.contains(event.getFile()) && deletedFiles
/* 130 */                     .contains(event.getFile())) {
/*     */                     continue;
/*     */                   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                   
/* 139 */                   if (addedFiles.contains(event.getFile()) || deletedFiles
/* 140 */                     .contains(event.getFile()))
/* 141 */                     it.remove();  continue;
/*     */                 } 
/* 143 */                 if (event.getType() == FileChangeEvent.Type.ADDED) {
/* 144 */                   if (deletedFiles.contains(event.getFile()))
/* 145 */                     it.remove();  continue;
/*     */                 } 
/* 147 */                 if (event.getType() == FileChangeEvent.Type.REMOVED && 
/* 148 */                   addedFiles.contains(event.getFile())) {
/* 149 */                   it.remove();
/*     */                 }
/*     */               } 
/*     */ 
/*     */               
/* 154 */               if (!results.isEmpty()) {
/* 155 */                 for (FileChangeCallback callback : pathData.callbacks) {
/* 156 */                   invokeCallback(callback, results);
/*     */                 }
/*     */               }
/*     */             } 
/*     */           } finally {
/*     */             
/* 162 */             if (!key.reset()) {
/* 163 */               this.files.remove(key.watchable());
/*     */             }
/*     */           } 
/*     */         }
/* 167 */       } catch (InterruptedException interruptedException) {
/*     */       
/* 169 */       } catch (ClosedWatchServiceException cwse) {
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void watchPath(File file, FileChangeCallback callback) {
/*     */     try {
/* 180 */       PathData data = this.files.get(file);
/* 181 */       if (data == null) {
/* 182 */         Set<File> allDirectories = doScan(file).keySet();
/* 183 */         Path path = Paths.get(file.toURI());
/* 184 */         data = new PathData(path);
/* 185 */         for (File dir : allDirectories) {
/* 186 */           addWatchedDirectory(data, dir);
/*     */         }
/* 188 */         this.files.put(file, data);
/*     */       } 
/* 190 */       data.callbacks.add(callback);
/*     */     }
/* 192 */     catch (IOException e) {
/* 193 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addWatchedDirectory(PathData data, File dir) throws IOException {
/* 198 */     Path path = Paths.get(dir.toURI());
/* 199 */     WatchKey key = path.register(this.watchService, (WatchEvent.Kind<?>[])new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY });
/* 200 */     this.pathDataByKey.put(key, data);
/* 201 */     data.keys.add(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void unwatchPath(File file, FileChangeCallback callback) {
/* 206 */     PathData data = this.files.get(file);
/* 207 */     if (data != null) {
/* 208 */       data.callbacks.remove(callback);
/* 209 */       if (data.callbacks.isEmpty()) {
/* 210 */         this.files.remove(file);
/* 211 */         for (WatchKey key : data.keys) {
/* 212 */           key.cancel();
/* 213 */           this.pathDataByKey.remove(key);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 222 */     this.stopped = true;
/* 223 */     this.watchThread.interrupt();
/* 224 */     IoUtils.safeClose(this.watchService);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Map<File, Long> doScan(File file) {
/* 229 */     Map<File, Long> results = new HashMap<>();
/*     */     
/* 231 */     Deque<File> toScan = new ArrayDeque<>();
/* 232 */     toScan.add(file);
/* 233 */     while (!toScan.isEmpty()) {
/* 234 */       File next = toScan.pop();
/* 235 */       if (next.isDirectory()) {
/* 236 */         results.put(next, Long.valueOf(next.lastModified()));
/* 237 */         File[] list = next.listFiles();
/* 238 */         if (list != null) {
/* 239 */           for (File f : list) {
/* 240 */             toScan.push(new File(f.getAbsolutePath()));
/*     */           }
/*     */         }
/*     */       } 
/*     */     } 
/* 245 */     return results;
/*     */   }
/*     */   
/*     */   private static void invokeCallback(FileChangeCallback callback, List<FileChangeEvent> results) {
/*     */     try {
/* 250 */       callback.handleChanges(results);
/* 251 */     } catch (Exception e) {
/* 252 */       Log.log.failedToInvokeFileWatchCallback(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private class PathData {
/*     */     final Path path;
/* 258 */     final List<FileChangeCallback> callbacks = new ArrayList<>();
/* 259 */     final List<WatchKey> keys = new ArrayList<>();
/*     */     
/*     */     private PathData(Path path) {
/* 262 */       this.path = path;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\WatchServiceFileSystemWatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */