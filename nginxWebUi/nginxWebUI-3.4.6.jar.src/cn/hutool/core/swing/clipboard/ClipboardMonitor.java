/*     */ package cn.hutool.core.swing.clipboard;
/*     */ 
/*     */ import cn.hutool.core.thread.ThreadUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.awt.datatransfer.Clipboard;
/*     */ import java.awt.datatransfer.ClipboardOwner;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.io.Closeable;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum ClipboardMonitor
/*     */   implements ClipboardOwner, Runnable, Closeable
/*     */ {
/*  20 */   INSTANCE;
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
/*  34 */   private final Set<ClipboardListener> listenerSet = new LinkedHashSet<>();
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_TRY_COUNT = 10;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final long DEFAULT_DELAY = 100L;
/*     */ 
/*     */ 
/*     */   
/*     */   private int tryCount;
/*     */ 
/*     */ 
/*     */   
/*     */   private long delay;
/*     */ 
/*     */ 
/*     */   
/*     */   private final Clipboard clipboard;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isRunning;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ClipboardMonitor(int tryCount, long delay, Clipboard clipboard) {
/*  64 */     this.tryCount = tryCount;
/*  65 */     this.delay = delay;
/*  66 */     this.clipboard = clipboard;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClipboardMonitor setTryCount(int tryCount) {
/*  77 */     this.tryCount = tryCount;
/*  78 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClipboardMonitor setDelay(long delay) {
/*  88 */     this.delay = delay;
/*  89 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClipboardMonitor addListener(ClipboardListener listener) {
/*  99 */     this.listenerSet.add(listener);
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClipboardMonitor removeListener(ClipboardListener listener) {
/* 110 */     this.listenerSet.remove(listener);
/* 111 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClipboardMonitor clearListener() {
/* 120 */     this.listenerSet.clear();
/* 121 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void lostOwnership(Clipboard clipboard, Transferable contents) {
/*     */     Transferable newContents;
/*     */     try {
/* 128 */       newContents = tryGetContent(clipboard);
/* 129 */     } catch (InterruptedException e) {
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 134 */     Transferable transferable = null;
/* 135 */     for (ClipboardListener listener : this.listenerSet) {
/*     */       try {
/* 137 */         transferable = listener.onChange(clipboard, (Transferable)ObjectUtil.defaultIfNull(transferable, newContents));
/* 138 */       } catch (Throwable throwable) {}
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 143 */     if (this.isRunning)
/*     */     {
/* 145 */       clipboard.setContents((Transferable)ObjectUtil.defaultIfNull(transferable, ObjectUtil.defaultIfNull(newContents, contents)), this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void run() {
/* 151 */     if (false == this.isRunning) {
/* 152 */       Clipboard clipboard = this.clipboard;
/* 153 */       clipboard.setContents(clipboard.getContents(null), this);
/* 154 */       this.isRunning = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void listen(boolean sync) {
/* 164 */     run();
/*     */     
/* 166 */     if (sync) {
/* 167 */       ThreadUtil.sync(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 176 */     this.isRunning = false;
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
/*     */   private Transferable tryGetContent(Clipboard clipboard) throws InterruptedException {
/* 188 */     Transferable newContents = null;
/* 189 */     for (int i = 0; i < this.tryCount; i++) {
/* 190 */       if (this.delay > 0L && i > 0)
/*     */       {
/*     */         
/* 193 */         Thread.sleep(this.delay);
/*     */       }
/*     */       
/*     */       try {
/* 197 */         newContents = clipboard.getContents(null);
/* 198 */       } catch (IllegalStateException illegalStateException) {}
/*     */ 
/*     */       
/* 201 */       if (null != newContents) {
/* 202 */         return newContents;
/*     */       }
/*     */     } 
/* 205 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\swing\clipboard\ClipboardMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */