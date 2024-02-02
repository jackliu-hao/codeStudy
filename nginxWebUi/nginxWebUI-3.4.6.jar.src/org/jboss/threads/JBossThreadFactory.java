/*     */ package org.jboss.threads;
/*     */ 
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ public final class JBossThreadFactory
/*     */   implements ThreadFactory
/*     */ {
/*     */   private final ThreadGroup threadGroup;
/*     */   private final Boolean daemon;
/*     */   private final Integer initialPriority;
/*     */   private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
/*     */   private final Long stackSize;
/*     */   private final String namePattern;
/*  38 */   private final AtomicLong factoryThreadIndexSequence = new AtomicLong(1L);
/*     */   
/*     */   private final long factoryIndex;
/*     */   
/*     */   private final AccessControlContext creatingContext;
/*     */   
/*  44 */   private static final AtomicLong globalThreadIndexSequence = new AtomicLong(1L);
/*  45 */   private static final AtomicLong factoryIndexSequence = new AtomicLong(1L);
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
/*     */   public JBossThreadFactory(ThreadGroup threadGroup, Boolean daemon, Integer initialPriority, String namePattern, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, Long stackSize) {
/*  59 */     if (threadGroup == null) {
/*  60 */       SecurityManager sm = System.getSecurityManager();
/*  61 */       threadGroup = (sm != null) ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
/*     */     } 
/*  63 */     this.threadGroup = threadGroup;
/*  64 */     this.daemon = daemon;
/*  65 */     this.initialPriority = initialPriority;
/*  66 */     this.uncaughtExceptionHandler = uncaughtExceptionHandler;
/*  67 */     this.stackSize = stackSize;
/*  68 */     this.factoryIndex = factoryIndexSequence.getAndIncrement();
/*  69 */     if (namePattern == null) {
/*  70 */       namePattern = "pool-%f-thread-%t";
/*     */     }
/*  72 */     this.namePattern = namePattern;
/*  73 */     this.creatingContext = AccessController.getContext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JBossThreadFactory(ThreadGroup threadGroup, Boolean daemon, Integer initialPriority, String namePattern, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, Long stackSize, AccessControlContext ignored) {
/*  80 */     this(threadGroup, daemon, initialPriority, namePattern, uncaughtExceptionHandler, stackSize);
/*     */   }
/*     */   
/*     */   public Thread newThread(Runnable target) {
/*     */     AccessControlContext context;
/*  85 */     if ((context = this.creatingContext) != null) {
/*  86 */       return AccessController.<Thread>doPrivileged(new ThreadCreateAction(target), context);
/*     */     }
/*  88 */     return createThread(target);
/*     */   }
/*     */ 
/*     */   
/*     */   private final class ThreadCreateAction
/*     */     implements PrivilegedAction<Thread>
/*     */   {
/*     */     private ThreadCreateAction(Runnable target) {
/*  96 */       this.target = target;
/*     */     }
/*     */     private final Runnable target;
/*     */     public Thread run() {
/* 100 */       return JBossThreadFactory.this.createThread(this.target);
/*     */     } }
/*     */   
/*     */   private Thread createThread(Runnable target) {
/*     */     JBossThread thread;
/* 105 */     ThreadNameInfo nameInfo = new ThreadNameInfo(globalThreadIndexSequence.getAndIncrement(), this.factoryThreadIndexSequence.getAndIncrement(), this.factoryIndex);
/*     */     
/* 107 */     if (this.stackSize != null) {
/* 108 */       thread = new JBossThread(this.threadGroup, target, "<new>", this.stackSize.longValue());
/*     */     } else {
/* 110 */       thread = new JBossThread(this.threadGroup, target);
/*     */     } 
/* 112 */     thread.setThreadNameInfo(nameInfo);
/* 113 */     thread.setName(nameInfo.format(thread, this.namePattern));
/* 114 */     if (this.initialPriority != null) thread.setPriority(this.initialPriority.intValue()); 
/* 115 */     if (this.daemon != null) thread.setDaemon(this.daemon.booleanValue()); 
/* 116 */     if (this.uncaughtExceptionHandler != null) thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler); 
/* 117 */     JBossExecutors.clearContextClassLoader(thread);
/* 118 */     return thread;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\JBossThreadFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */