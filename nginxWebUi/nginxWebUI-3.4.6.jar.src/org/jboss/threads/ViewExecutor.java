/*     */ package org.jboss.threads;
/*     */ 
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.AbstractExecutorService;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.Condition;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common.lock.ExtendedLock;
/*     */ import org.wildfly.common.lock.Locks;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ViewExecutor
/*     */   extends AbstractExecutorService
/*     */ {
/*  25 */   private static final Logger log = Logger.getLogger("org.jboss.threads.view-executor");
/*  26 */   private static final Runnable[] NO_RUNNABLES = new Runnable[0];
/*     */   
/*     */   private final Executor delegate;
/*     */   
/*     */   private final ExtendedLock lock;
/*     */   private final Condition shutDownCondition;
/*     */   private final ArrayDeque<Runnable> queue;
/*  33 */   private final Set<TaskWrapper> allWrappers = Collections.newSetFromMap(new ConcurrentHashMap<>());
/*     */   private int queueLimit;
/*     */   private short submittedCount;
/*     */   private short maxCount;
/*     */   private short runningCount;
/*  38 */   private int state = 0;
/*     */   
/*     */   private volatile Thread.UncaughtExceptionHandler handler;
/*     */   
/*     */   private volatile Runnable terminationTask;
/*     */   private static final int ST_RUNNING = 0;
/*     */   private static final int ST_SHUTDOWN_REQ = 1;
/*     */   private static final int ST_SHUTDOWN_INT_REQ = 2;
/*     */   private static final int ST_STOPPED = 3;
/*     */   
/*     */   ViewExecutor(Builder builder) {
/*  49 */     this.delegate = builder.getDelegate();
/*  50 */     this.maxCount = (short)builder.getMaxSize();
/*  51 */     int queueLimit = builder.getQueueLimit();
/*  52 */     this.queueLimit = queueLimit;
/*  53 */     this.handler = builder.getUncaughtHandler();
/*  54 */     this.queue = new ArrayDeque<>(Math.min(queueLimit, builder.getQueueInitialSize()));
/*  55 */     this.lock = Locks.reentrantLock();
/*  56 */     this.shutDownCondition = this.lock.newCondition();
/*     */   }
/*     */   
/*     */   public void execute(Runnable command) {
/*  60 */     this.lock.lock();
/*     */     try {
/*  62 */       if (this.state != 0) {
/*  63 */         throw new RejectedExecutionException("Executor has been shut down");
/*     */       }
/*  65 */       short submittedCount = this.submittedCount;
/*  66 */       if (this.runningCount + submittedCount < this.maxCount) {
/*  67 */         this.submittedCount = (short)(submittedCount + 1);
/*  68 */         TaskWrapper tw = new TaskWrapper(JBossExecutors.classLoaderPreservingTask(command));
/*  69 */         this.allWrappers.add(tw);
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/*  74 */           this.delegate.execute(tw);
/*  75 */         } catch (Throwable t) {
/*  76 */           this.submittedCount = (short)(this.submittedCount - 1);
/*  77 */           this.allWrappers.remove(tw);
/*  78 */           throw t;
/*     */         } 
/*  80 */       } else if (this.queue.size() < this.queueLimit) {
/*  81 */         this.queue.add(command);
/*     */       } else {
/*  83 */         throw new RejectedExecutionException("No executor queue space remaining");
/*     */       } 
/*     */     } finally {
/*  86 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void shutdown() {
/*  91 */     shutdown(false);
/*     */   }
/*     */   
/*     */   public void shutdown(boolean interrupt) {
/*  95 */     this.lock.lock();
/*  96 */     int oldState = this.state;
/*  97 */     if (oldState < 1) {
/*     */       boolean emptyQueue;
/*     */       
/*     */       try {
/* 101 */         emptyQueue = this.queue.isEmpty();
/* 102 */       } catch (Throwable t) {
/* 103 */         this.lock.unlock();
/* 104 */         throw t;
/*     */       } 
/* 106 */       if (this.runningCount == 0 && this.submittedCount == 0 && emptyQueue) {
/* 107 */         this.state = 3;
/*     */         try {
/* 109 */           this.shutDownCondition.signalAll();
/*     */         } finally {
/* 111 */           this.lock.unlock();
/*     */         } 
/* 113 */         runTermination();
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 118 */     this.state = interrupt ? 2 : 1;
/* 119 */     this.lock.unlock();
/* 120 */     if (interrupt && oldState < 2)
/*     */     {
/* 122 */       for (TaskWrapper wrapper : this.allWrappers)
/* 123 */         wrapper.interrupt(); 
/*     */     }
/*     */   }
/*     */   
/*     */   public List<Runnable> shutdownNow() {
/*     */     Runnable[] tasks;
/* 129 */     this.lock.lock();
/* 130 */     int oldState = this.state;
/*     */     
/*     */     try {
/* 133 */       tasks = this.queue.<Runnable>toArray(NO_RUNNABLES);
/* 134 */       this.queue.clear();
/* 135 */     } catch (Throwable t) {
/* 136 */       this.lock.unlock();
/* 137 */       throw t;
/*     */     } 
/* 139 */     if (oldState < 2) {
/*     */       
/* 141 */       if (this.runningCount == 0 && this.submittedCount == 0) {
/* 142 */         this.state = 3;
/*     */         try {
/* 144 */           this.shutDownCondition.signalAll();
/*     */         } finally {
/* 146 */           this.lock.unlock();
/*     */         } 
/* 148 */         runTermination();
/*     */       } else {
/* 150 */         this.lock.unlock();
/* 151 */         this.state = 2;
/*     */         
/* 153 */         for (TaskWrapper wrapper : this.allWrappers) {
/* 154 */           wrapper.interrupt();
/*     */         }
/*     */       } 
/*     */     } else {
/* 158 */       this.lock.unlock();
/*     */     } 
/* 160 */     return Arrays.asList(tasks);
/*     */   }
/*     */   
/*     */   public boolean isShutdown() {
/* 164 */     this.lock.lock();
/*     */     try {
/* 166 */       return (this.state >= 1);
/*     */     } finally {
/* 168 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isTerminated() {
/* 173 */     this.lock.lock();
/*     */     try {
/* 175 */       return (this.state == 3);
/*     */     } finally {
/* 177 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
/* 182 */     this.lock.lock();
/*     */     try {
/* 184 */       if (this.state == 3) {
/* 185 */         return true;
/*     */       }
/* 187 */       long nanos = unit.toNanos(timeout);
/* 188 */       if (nanos <= 0L) {
/* 189 */         return false;
/*     */       }
/* 191 */       long elapsed = 0L;
/* 192 */       long start = System.nanoTime();
/*     */       while (true) {
/* 194 */         this.shutDownCondition.awaitNanos(nanos - elapsed);
/* 195 */         if (this.state == 3) {
/* 196 */           return true;
/*     */         }
/* 198 */         elapsed = System.nanoTime() - start;
/* 199 */         if (elapsed >= nanos) {
/* 200 */           return false;
/*     */         }
/*     */       } 
/*     */     } finally {
/* 204 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public Thread.UncaughtExceptionHandler getExceptionHandler() {
/* 209 */     return this.handler;
/*     */   }
/*     */   
/*     */   public void setExceptionHandler(Thread.UncaughtExceptionHandler handler) {
/* 213 */     Assert.checkNotNullParam("handler", handler);
/* 214 */     this.handler = handler;
/*     */   }
/*     */   
/*     */   public Runnable getTerminationTask() {
/* 218 */     return this.terminationTask;
/*     */   }
/*     */   
/*     */   public void setTerminationTask(Runnable terminationTask) {
/* 222 */     this.terminationTask = terminationTask;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 226 */     return "view of " + this.delegate;
/*     */   }
/*     */   
/*     */   public static Builder builder(Executor delegate) {
/* 230 */     Assert.checkNotNullParam("delegate", delegate);
/* 231 */     return new Builder(delegate);
/*     */   }
/*     */   
/*     */   public static final class Builder {
/*     */     private final Executor delegate;
/* 236 */     private short maxSize = 1;
/* 237 */     private int queueLimit = Integer.MAX_VALUE;
/* 238 */     private int queueInitialSize = 256;
/* 239 */     private Thread.UncaughtExceptionHandler handler = JBossExecutors.loggingExceptionHandler();
/*     */     
/*     */     Builder(Executor delegate) {
/* 242 */       this.delegate = delegate;
/*     */     }
/*     */     
/*     */     public int getMaxSize() {
/* 246 */       return this.maxSize;
/*     */     }
/*     */     
/*     */     public Builder setMaxSize(int maxSize) {
/* 250 */       Assert.checkMinimumParameter("maxSize", 1, maxSize);
/* 251 */       Assert.checkMaximumParameter("maxSize", 32767, maxSize);
/* 252 */       this.maxSize = (short)maxSize;
/* 253 */       return this;
/*     */     }
/*     */     
/*     */     public int getQueueLimit() {
/* 257 */       return this.queueLimit;
/*     */     }
/*     */     
/*     */     public Builder setQueueLimit(int queueLimit) {
/* 261 */       Assert.checkMinimumParameter("queueLimit", 0, queueLimit);
/* 262 */       this.queueLimit = queueLimit;
/* 263 */       return this;
/*     */     }
/*     */     
/*     */     public Executor getDelegate() {
/* 267 */       return this.delegate;
/*     */     }
/*     */     
/*     */     public Thread.UncaughtExceptionHandler getUncaughtHandler() {
/* 271 */       return this.handler;
/*     */     }
/*     */     
/*     */     public Builder setUncaughtHandler(Thread.UncaughtExceptionHandler handler) {
/* 275 */       this.handler = handler;
/* 276 */       return this;
/*     */     }
/*     */     
/*     */     public int getQueueInitialSize() {
/* 280 */       return this.queueInitialSize;
/*     */     }
/*     */     
/*     */     public Builder setQueueInitialSize(int queueInitialSize) {
/* 284 */       this.queueInitialSize = queueInitialSize;
/* 285 */       return this;
/*     */     }
/*     */     
/*     */     public ViewExecutor build() {
/* 289 */       return new ViewExecutor(this);
/*     */     }
/*     */   }
/*     */   
/*     */   class TaskWrapper implements Runnable {
/*     */     private volatile Thread thread;
/*     */     private Runnable command;
/*     */     
/*     */     TaskWrapper(Runnable command) {
/* 298 */       this.command = command;
/*     */     }
/*     */     
/*     */     void interrupt() {
/* 302 */       Thread thread = this.thread;
/* 303 */       if (thread != null) {
/* 304 */         thread.interrupt();
/*     */       }
/*     */     }
/*     */     
/*     */     public void run() {
/* 309 */       this.thread = Thread.currentThread();
/*     */       try {
/*     */         while (true) {
/* 312 */           ViewExecutor.this.lock.lock();
/*     */           try {
/* 314 */             ViewExecutor.access$110(ViewExecutor.this);
/* 315 */             ViewExecutor.access$208(ViewExecutor.this);
/*     */           } finally {
/* 317 */             ViewExecutor.this.lock.unlock();
/*     */           } 
/*     */           try {
/* 320 */             this.command.run();
/* 321 */           } catch (Throwable t) {
/*     */             try {
/* 323 */               ViewExecutor.this.handler.uncaughtException(Thread.currentThread(), t);
/* 324 */             } catch (Throwable throwable) {}
/*     */           } 
/*     */           
/* 327 */           ViewExecutor.this.lock.lock();
/* 328 */           ViewExecutor.access$210(ViewExecutor.this);
/*     */           try {
/* 330 */             this.command = ViewExecutor.this.queue.pollFirst();
/* 331 */           } catch (Throwable t) {
/* 332 */             ViewExecutor.this.lock.unlock();
/* 333 */             throw t;
/*     */           } 
/* 335 */           if (ViewExecutor.this.runningCount + ViewExecutor.this.submittedCount < ViewExecutor.this.maxCount && this.command != null)
/*     */           
/* 337 */           { ViewExecutor.access$108(ViewExecutor.this);
/* 338 */             ViewExecutor.this.lock.unlock(); }
/* 339 */           else { if (this.command == null && ViewExecutor.this.runningCount == 0 && ViewExecutor.this.submittedCount == 0 && ViewExecutor.this.state != 0) {
/*     */               
/* 341 */               ViewExecutor.this.state = 3;
/*     */               try {
/* 343 */                 ViewExecutor.this.shutDownCondition.signalAll();
/*     */               } finally {
/* 345 */                 ViewExecutor.this.lock.unlock();
/*     */               } 
/* 347 */               ViewExecutor.this.runTermination();
/*     */               return;
/*     */             } 
/* 350 */             ViewExecutor.this.lock.unlock();
/*     */             return; }
/*     */           
/*     */           try {
/* 354 */             ViewExecutor.this.delegate.execute(this);
/*     */             
/*     */             return;
/* 357 */           } catch (Throwable t) {
/* 358 */             ViewExecutor.log.warn("Failed to resubmit executor task to delegate executor (executing task immediately instead)", t);
/*     */           }
/*     */         
/*     */         } 
/*     */       } finally {
/*     */         
/* 364 */         this.thread = null;
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void runTermination() {
/* 370 */     Runnable task = this.terminationTask;
/* 371 */     this.terminationTask = null;
/* 372 */     if (task != null)
/* 373 */       try { task.run(); }
/* 374 */       catch (Throwable t)
/*     */       { try {
/* 376 */           this.handler.uncaughtException(Thread.currentThread(), t);
/* 377 */         } catch (Throwable throwable) {} }
/*     */        
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\ViewExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */