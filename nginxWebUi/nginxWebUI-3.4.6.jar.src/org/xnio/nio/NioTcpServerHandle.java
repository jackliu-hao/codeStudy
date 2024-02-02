/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.ChannelListeners;
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
/*     */ final class NioTcpServerHandle
/*     */   extends NioHandle
/*     */   implements ChannelClosed
/*     */ {
/*  41 */   private static final String FQCN = NioTcpServerHandle.class.getName();
/*     */   private final Runnable freeTask;
/*     */   private final NioTcpServer server;
/*     */   private int count;
/*     */   private int low;
/*     */   private int high;
/*  47 */   private int tokenCount = -1;
/*     */   private boolean stopped;
/*     */   private boolean backOff;
/*  50 */   private int backOffTime = 0;
/*     */   
/*     */   NioTcpServerHandle(NioTcpServer server, SelectionKey key, WorkerThread thread, int low, int high) {
/*  53 */     super(thread, key);
/*  54 */     this.server = server;
/*  55 */     this.low = low;
/*  56 */     this.high = high;
/*  57 */     this.freeTask = new Runnable() {
/*     */         public void run() {
/*  59 */           NioTcpServerHandle.this.freeConnection();
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   void handleReady(int ops) {
/*  65 */     ChannelListeners.invokeChannelListener((Channel)this.server, this.server.getAcceptListener());
/*     */   }
/*     */   
/*     */   void forceTermination() {
/*  69 */     IoUtils.safeClose((Closeable)this.server);
/*     */   }
/*     */   
/*     */   void terminated() {
/*  73 */     this.server.invokeCloseHandler();
/*     */   }
/*     */   
/*     */   Runnable getFreeTask() {
/*  77 */     return this.freeTask;
/*     */   }
/*     */   
/*     */   void resume() {
/*  81 */     WorkerThread thread = getWorkerThread();
/*  82 */     if (thread == Thread.currentThread()) {
/*  83 */       if (!this.stopped && !this.backOff && this.server.resumed) resume(16); 
/*     */     } else {
/*  85 */       thread.execute(new Runnable() {
/*     */             public void run() {
/*  87 */               NioTcpServerHandle.this.resume();
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   void suspend() {
/*  94 */     WorkerThread thread = getWorkerThread();
/*  95 */     if (thread == Thread.currentThread()) {
/*  96 */       if (this.stopped || this.backOff || !this.server.resumed) suspend(16); 
/*     */     } else {
/*  98 */       thread.execute(new Runnable() {
/*     */             public void run() {
/* 100 */               NioTcpServerHandle.this.suspend();
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   public void channelClosed() {
/* 107 */     WorkerThread thread = getWorkerThread();
/* 108 */     if (thread == Thread.currentThread()) {
/* 109 */       freeConnection();
/*     */     } else {
/* 111 */       thread.execute(this.freeTask);
/*     */     } 
/*     */   }
/*     */   
/*     */   void freeConnection() {
/* 116 */     assert Thread.currentThread() == getWorkerThread();
/* 117 */     if (this.count-- <= this.low && this.tokenCount != 0 && this.stopped) {
/* 118 */       Log.tcpServerConnectionLimitLog.logf(FQCN, Logger.Level.DEBUG, null, "Connection freed, resuming accept connections", new Object[0]);
/*     */       
/* 120 */       this.stopped = false;
/* 121 */       if (this.server.resumed) {
/*     */         
/* 123 */         this.backOff = false;
/* 124 */         resume(16);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   void setTokenCount(int newCount) {
/* 130 */     WorkerThread workerThread = getWorkerThread();
/* 131 */     if (workerThread == Thread.currentThread()) {
/* 132 */       if (this.tokenCount == 0) {
/* 133 */         this.tokenCount = newCount;
/* 134 */         if (this.count <= this.low && this.stopped) {
/* 135 */           this.stopped = false;
/* 136 */           if (this.server.resumed && !this.backOff) {
/* 137 */             resume(16);
/*     */           }
/*     */         } 
/*     */         return;
/*     */       } 
/* 142 */       workerThread = workerThread.getNextThread();
/*     */     } 
/* 144 */     setThreadNewCount(workerThread, newCount);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void startBackOff() {
/* 151 */     this.backOff = true;
/* 152 */     this.backOffTime = Math.max(250, Math.min(30000, this.backOffTime << 2));
/* 153 */     suspend();
/* 154 */     getWorkerThread().executeAfter(this::endBackOff, this.backOffTime, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void endBackOff() {
/* 161 */     this.backOff = false;
/* 162 */     resume();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void resetBackOff() {
/* 169 */     this.backOffTime = 0;
/*     */   }
/*     */   
/*     */   private void setThreadNewCount(WorkerThread workerThread, final int newCount) {
/* 173 */     final int number = workerThread.getNumber();
/* 174 */     workerThread.execute(new Runnable() {
/*     */           public void run() {
/* 176 */             NioTcpServerHandle.this.server.getHandle(number).setTokenCount(newCount);
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   void initializeTokenCount(final int newCount) {
/* 182 */     WorkerThread workerThread = getWorkerThread();
/* 183 */     final int number = workerThread.getNumber();
/* 184 */     if (workerThread == Thread.currentThread()) {
/* 185 */       this.tokenCount = newCount;
/* 186 */       if (newCount == 0) {
/* 187 */         this.stopped = true;
/* 188 */         suspend(16);
/*     */       } 
/*     */     } else {
/* 191 */       workerThread.execute(new Runnable() {
/*     */             public void run() {
/* 193 */               NioTcpServerHandle.this.server.getHandle(number).initializeTokenCount(newCount);
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   boolean getConnection() {
/* 200 */     assert Thread.currentThread() == getWorkerThread();
/* 201 */     if (this.stopped || this.backOff) {
/* 202 */       Log.tcpServerConnectionLimitLog.logf(FQCN, Logger.Level.DEBUG, null, "Refusing accepting request (temporarily stopped: %s, backed off: %s)", Boolean.valueOf(this.stopped), Boolean.valueOf(this.backOff));
/* 203 */       return false;
/*     */     } 
/* 205 */     if (this.tokenCount != -1 && --this.tokenCount == 0) {
/* 206 */       setThreadNewCount(getWorkerThread().getNextThread(), this.server.getTokenConnectionCount());
/*     */     }
/* 208 */     if (++this.count >= this.high || this.tokenCount == 0) {
/* 209 */       if (Log.tcpServerLog.isDebugEnabled() && this.count >= this.high)
/* 210 */         Log.tcpServerConnectionLimitLog.logf(FQCN, Logger.Level.DEBUG, null, "Total open connections reach high water limit (%s) by this new accepting request. Temporarily stopping accept connections", 
/*     */             
/* 212 */             Integer.valueOf(this.high)); 
/* 213 */       this.stopped = true;
/* 214 */       suspend(16);
/*     */     } 
/* 216 */     return true;
/*     */   }
/*     */   
/*     */   public void executeSetTask(final int high, final int low) {
/* 220 */     WorkerThread thread = getWorkerThread();
/* 221 */     if (thread == Thread.currentThread()) {
/* 222 */       this.high = high;
/* 223 */       this.low = low;
/* 224 */       if (this.count >= high && !this.stopped) {
/* 225 */         this.stopped = true;
/* 226 */         suspend();
/* 227 */       } else if (this.count <= low && this.stopped) {
/* 228 */         this.stopped = false;
/* 229 */         if (this.server.resumed && !this.backOff) resume(); 
/*     */       } 
/*     */     } else {
/* 232 */       thread.execute(new Runnable() {
/*     */             public void run() {
/* 234 */               NioTcpServerHandle.this.executeSetTask(high, low);
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   int getConnectionCount() {
/* 241 */     assert Thread.currentThread() == getWorkerThread();
/* 242 */     return this.count;
/*     */   }
/*     */   
/*     */   int getBackOffTime() {
/* 246 */     return this.backOffTime;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioTcpServerHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */