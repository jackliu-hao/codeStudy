/*     */ package com.sun.jna.platform;
/*     */ 
/*     */ import com.sun.jna.platform.win32.W32FileMonitor;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.EventObject;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class FileMonitor
/*     */ {
/*     */   public static final int FILE_CREATED = 1;
/*     */   public static final int FILE_DELETED = 2;
/*     */   public static final int FILE_MODIFIED = 4;
/*     */   public static final int FILE_ACCESSED = 8;
/*     */   public static final int FILE_NAME_CHANGED_OLD = 16;
/*     */   public static final int FILE_NAME_CHANGED_NEW = 32;
/*     */   public static final int FILE_RENAMED = 48;
/*     */   public static final int FILE_SIZE_CHANGED = 64;
/*     */   public static final int FILE_ATTRIBUTES_CHANGED = 128;
/*     */   public static final int FILE_SECURITY_CHANGED = 256;
/*     */   public static final int FILE_ANY = 511;
/*     */   
/*     */   public static interface FileListener
/*     */   {
/*     */     void fileChanged(FileMonitor.FileEvent param1FileEvent);
/*     */   }
/*     */   
/*     */   public class FileEvent
/*     */     extends EventObject
/*     */   {
/*     */     private final File file;
/*     */     private final int type;
/*     */     
/*     */     public FileEvent(File file, int type) {
/*  66 */       super(FileMonitor.this);
/*  67 */       this.file = file;
/*  68 */       this.type = type;
/*     */     }
/*  70 */     public File getFile() { return this.file; } public int getType() {
/*  71 */       return this.type;
/*     */     } public String toString() {
/*  73 */       return "FileEvent: " + this.file + ":" + this.type;
/*     */     }
/*     */   }
/*     */   
/*  77 */   private final Map<File, Integer> watched = new HashMap<File, Integer>();
/*  78 */   private List<FileListener> listeners = new ArrayList<FileListener>();
/*     */   protected abstract void watch(File paramFile, int paramInt, boolean paramBoolean) throws IOException;
/*     */   protected abstract void unwatch(File paramFile);
/*     */   
/*     */   public abstract void dispose();
/*     */   
/*     */   public void addWatch(File dir) throws IOException {
/*  85 */     addWatch(dir, 511);
/*     */   }
/*     */   
/*     */   public void addWatch(File dir, int mask) throws IOException {
/*  89 */     addWatch(dir, mask, dir.isDirectory());
/*     */   }
/*     */   
/*     */   public void addWatch(File dir, int mask, boolean recursive) throws IOException {
/*  93 */     this.watched.put(dir, Integer.valueOf(mask));
/*  94 */     watch(dir, mask, recursive);
/*     */   }
/*     */   
/*     */   public void removeWatch(File file) {
/*  98 */     if (this.watched.remove(file) != null) {
/*  99 */       unwatch(file);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void notify(FileEvent e) {
/* 104 */     for (FileListener listener : this.listeners) {
/* 105 */       listener.fileChanged(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void addFileListener(FileListener listener) {
/* 110 */     List<FileListener> list = new ArrayList<FileListener>(this.listeners);
/* 111 */     list.add(listener);
/* 112 */     this.listeners = list;
/*     */   }
/*     */   
/*     */   public synchronized void removeFileListener(FileListener x) {
/* 116 */     List<FileListener> list = new ArrayList<FileListener>(this.listeners);
/* 117 */     list.remove(x);
/* 118 */     this.listeners = list;
/*     */   }
/*     */   
/*     */   protected void finalize() {
/* 122 */     for (File watchedFile : this.watched.keySet()) {
/* 123 */       removeWatch(watchedFile);
/*     */     }
/*     */     
/* 126 */     dispose();
/*     */   }
/*     */   
/*     */   private static class Holder {
/*     */     public static final FileMonitor INSTANCE;
/*     */     
/*     */     static {
/* 133 */       String os = System.getProperty("os.name");
/* 134 */       if (os.startsWith("Windows")) {
/* 135 */         INSTANCE = (FileMonitor)new W32FileMonitor();
/*     */       } else {
/*     */         
/* 138 */         throw new Error("FileMonitor not implemented for " + os);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public static FileMonitor getInstance() {
/* 144 */     return Holder.INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\FileMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */