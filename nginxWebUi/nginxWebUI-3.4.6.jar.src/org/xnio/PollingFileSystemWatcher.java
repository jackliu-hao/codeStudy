/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Deque;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.xnio._private.Messages;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class PollingFileSystemWatcher
/*     */   implements FileSystemWatcher, Runnable
/*     */ {
/*  40 */   private static final AtomicInteger threadIdCounter = new AtomicInteger(0);
/*     */   
/*     */   public static final String THREAD_NAME = "xnio-polling-file-watcher";
/*  43 */   private final Map<File, PollHolder> files = Collections.synchronizedMap(new HashMap<>());
/*     */   
/*     */   private final Thread watchThread;
/*     */   
/*     */   private final int pollInterval;
/*     */   private volatile boolean stopped = false;
/*     */   
/*     */   PollingFileSystemWatcher(String name, int pollInterval, boolean daemon) {
/*  51 */     this.watchThread = new Thread(this, "xnio-polling-file-watcher[" + name + "]-" + threadIdCounter);
/*  52 */     this.watchThread.setDaemon(daemon);
/*  53 */     this.watchThread.start();
/*  54 */     this.pollInterval = pollInterval;
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*  59 */     while (!this.stopped) {
/*     */       try {
/*  61 */         doNotify();
/*  62 */         Thread.sleep(this.pollInterval);
/*  63 */       } catch (InterruptedException interruptedException) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void doNotify() {
/*  70 */     for (Map.Entry<File, PollHolder> entry : this.files.entrySet()) {
/*  71 */       Map<File, Long> result = doScan(entry.getKey());
/*  72 */       List<FileChangeEvent> currentDiff = doDiff(result, ((PollHolder)entry.getValue()).currentFileState);
/*  73 */       if (!currentDiff.isEmpty()) {
/*  74 */         ((PollHolder)entry.getValue()).currentFileState = result;
/*  75 */         for (FileChangeCallback callback : ((PollHolder)entry.getValue()).callbacks) {
/*  76 */           invokeCallback(callback, currentDiff);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private List<FileChangeEvent> doDiff(Map<File, Long> newFileState, Map<File, Long> currentFileState) {
/*  83 */     List<FileChangeEvent> results = new ArrayList<>();
/*  84 */     Map<File, Long> currentCopy = new HashMap<>(currentFileState);
/*  85 */     for (Map.Entry<File, Long> newEntry : newFileState.entrySet()) {
/*  86 */       Long old = currentCopy.remove(newEntry.getKey());
/*  87 */       if (old == null) {
/*  88 */         results.add(new FileChangeEvent(newEntry.getKey(), FileChangeEvent.Type.ADDED)); continue;
/*     */       } 
/*  90 */       if (!old.equals(newEntry.getValue()) && !((File)newEntry.getKey()).isDirectory())
/*     */       {
/*     */         
/*  93 */         results.add(new FileChangeEvent(newEntry.getKey(), FileChangeEvent.Type.MODIFIED));
/*     */       }
/*     */     } 
/*     */     
/*  97 */     for (Map.Entry<File, Long> old : currentCopy.entrySet()) {
/*  98 */       results.add(new FileChangeEvent(old.getKey(), FileChangeEvent.Type.REMOVED));
/*     */     }
/* 100 */     return results;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void watchPath(File file, FileChangeCallback callback) {
/* 105 */     PollHolder holder = this.files.get(file);
/* 106 */     if (holder == null) {
/* 107 */       this.files.put(file, holder = new PollHolder(doScan(file)));
/*     */     }
/* 109 */     holder.callbacks.add(callback);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void unwatchPath(File file, FileChangeCallback callback) {
/* 114 */     PollHolder holder = this.files.get(file);
/* 115 */     if (holder != null) {
/* 116 */       holder.callbacks.remove(callback);
/* 117 */       if (holder.callbacks.isEmpty()) {
/* 118 */         this.files.remove(file);
/*     */       }
/*     */     } 
/* 121 */     this.files.remove(file);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 126 */     this.stopped = true;
/* 127 */     this.watchThread.interrupt();
/*     */   }
/*     */   
/*     */   private class PollHolder {
/*     */     Map<File, Long> currentFileState;
/* 132 */     final List<FileChangeCallback> callbacks = new ArrayList<>();
/*     */     
/*     */     private PollHolder(Map<File, Long> currentFileState) {
/* 135 */       this.currentFileState = currentFileState;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static Map<File, Long> doScan(File file) {
/* 142 */     Map<File, Long> results = new HashMap<>();
/*     */     
/* 144 */     Deque<File> toScan = new ArrayDeque<>();
/* 145 */     toScan.add(file);
/* 146 */     while (!toScan.isEmpty()) {
/* 147 */       File next = toScan.pop();
/* 148 */       if (next.isDirectory()) {
/* 149 */         results.put(next, Long.valueOf(next.lastModified()));
/* 150 */         File[] list = next.listFiles();
/* 151 */         if (list != null)
/* 152 */           for (File f : list) {
/* 153 */             toScan.push(new File(f.getAbsolutePath()));
/*     */           } 
/*     */         continue;
/*     */       } 
/* 157 */       results.put(next, Long.valueOf(next.lastModified()));
/*     */     } 
/*     */     
/* 160 */     return results;
/*     */   }
/*     */   
/*     */   static void invokeCallback(FileChangeCallback callback, List<FileChangeEvent> results) {
/*     */     try {
/* 165 */       callback.handleChanges(results);
/* 166 */     } catch (Exception e) {
/* 167 */       Messages.msg.failedToInvokeFileWatchCallback(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\PollingFileSystemWatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */