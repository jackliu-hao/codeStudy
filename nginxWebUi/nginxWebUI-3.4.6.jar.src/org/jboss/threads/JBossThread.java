/*     */ package org.jboss.threads;
/*     */ 
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.locks.LockSupport;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common.function.ExceptionBiConsumer;
/*     */ import org.wildfly.common.function.ExceptionBiFunction;
/*     */ import org.wildfly.common.function.ExceptionConsumer;
/*     */ import org.wildfly.common.function.ExceptionFunction;
/*     */ import org.wildfly.common.function.ExceptionObjIntConsumer;
/*     */ import org.wildfly.common.function.ExceptionObjLongConsumer;
/*     */ import org.wildfly.common.function.ExceptionRunnable;
/*     */ import org.wildfly.common.function.ExceptionSupplier;
/*     */ import org.wildfly.common.function.Functions;
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
/*     */ public class JBossThread
/*     */   extends Thread
/*     */ {
/*  45 */   private static final RuntimePermission MODIFY_THREAD_PERMISSION = new RuntimePermission("modifyThread");
/*     */   
/*     */   static {
/*  48 */     Version.getVersionString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile InterruptHandler interruptHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ThreadNameInfo threadNameInfo;
/*     */ 
/*     */ 
/*     */   
/*     */   private List<Runnable> exitHandlers;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int STATE_MAYBE_INTERRUPTED = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int STATE_INTERRUPT_DEFERRED = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int STATE_INTERRUPT_PENDING = 2;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int STATE_INTERRUPT_IN_PROGRESS = 3;
/*     */ 
/*     */ 
/*     */   
/*  84 */   private final AtomicInteger stateRef = new AtomicInteger();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JBossThread(Runnable target) {
/*  93 */     super(target);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JBossThread(Runnable target, String name) {
/* 104 */     super(target, name);
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
/*     */   public JBossThread(ThreadGroup group, Runnable target) throws SecurityException {
/* 116 */     super(group, target);
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
/*     */   public JBossThread(ThreadGroup group, Runnable target, String name) throws SecurityException {
/* 129 */     super(group, target, name);
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
/*     */   public JBossThread(ThreadGroup group, Runnable target, String name, long stackSize) throws SecurityException {
/* 142 */     super(group, target, name, stackSize);
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
/*     */   
/*     */   public void interrupt() {
/*     */     // Byte code:
/*     */     //   0: invokestatic currentThread : ()Ljava/lang/Thread;
/*     */     //   3: aload_0
/*     */     //   4: if_acmpeq -> 11
/*     */     //   7: iconst_1
/*     */     //   8: goto -> 12
/*     */     //   11: iconst_0
/*     */     //   12: istore_1
/*     */     //   13: iload_1
/*     */     //   14: ifeq -> 21
/*     */     //   17: aload_0
/*     */     //   18: invokevirtual checkAccess : ()V
/*     */     //   21: aload_0
/*     */     //   22: invokevirtual isInterrupted : ()Z
/*     */     //   25: ifeq -> 29
/*     */     //   28: return
/*     */     //   29: aload_0
/*     */     //   30: getfield stateRef : Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   33: astore_2
/*     */     //   34: aload_2
/*     */     //   35: invokevirtual get : ()I
/*     */     //   38: istore_3
/*     */     //   39: iload_3
/*     */     //   40: iconst_2
/*     */     //   41: if_icmpeq -> 49
/*     */     //   44: iload_3
/*     */     //   45: iconst_3
/*     */     //   46: if_icmpne -> 61
/*     */     //   49: getstatic org/jboss/threads/Messages.msg : Lorg/jboss/threads/Messages;
/*     */     //   52: ldc 'Interrupting thread "%s" (already interrupted)'
/*     */     //   54: aload_0
/*     */     //   55: invokeinterface tracef : (Ljava/lang/String;Ljava/lang/Object;)V
/*     */     //   60: return
/*     */     //   61: iload_3
/*     */     //   62: iconst_1
/*     */     //   63: if_icmpne -> 72
/*     */     //   66: iconst_2
/*     */     //   67: istore #4
/*     */     //   69: goto -> 75
/*     */     //   72: iconst_3
/*     */     //   73: istore #4
/*     */     //   75: aload_2
/*     */     //   76: iload_3
/*     */     //   77: iload #4
/*     */     //   79: invokevirtual compareAndSet : (II)Z
/*     */     //   82: ifeq -> 34
/*     */     //   85: iload #4
/*     */     //   87: iconst_3
/*     */     //   88: if_icmpne -> 132
/*     */     //   91: aload_0
/*     */     //   92: invokespecial doInterrupt : ()V
/*     */     //   95: aload_2
/*     */     //   96: iconst_0
/*     */     //   97: invokevirtual set : (I)V
/*     */     //   100: iload_1
/*     */     //   101: ifeq -> 129
/*     */     //   104: aload_0
/*     */     //   105: invokestatic unpark : (Ljava/lang/Thread;)V
/*     */     //   108: goto -> 129
/*     */     //   111: astore #5
/*     */     //   113: aload_2
/*     */     //   114: iconst_0
/*     */     //   115: invokevirtual set : (I)V
/*     */     //   118: iload_1
/*     */     //   119: ifeq -> 126
/*     */     //   122: aload_0
/*     */     //   123: invokestatic unpark : (Ljava/lang/Thread;)V
/*     */     //   126: aload #5
/*     */     //   128: athrow
/*     */     //   129: goto -> 143
/*     */     //   132: getstatic org/jboss/threads/Messages.intMsg : Lorg/jboss/threads/Messages;
/*     */     //   135: ldc 'Interrupting thread "%s" (deferred)'
/*     */     //   137: aload_0
/*     */     //   138: invokeinterface tracef : (Ljava/lang/String;Ljava/lang/Object;)V
/*     */     //   143: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #150	-> 0
/*     */     //   #151	-> 13
/*     */     //   #153	-> 21
/*     */     //   #154	-> 29
/*     */     //   #157	-> 34
/*     */     //   #158	-> 39
/*     */     //   #160	-> 49
/*     */     //   #161	-> 60
/*     */     //   #162	-> 61
/*     */     //   #163	-> 66
/*     */     //   #165	-> 72
/*     */     //   #167	-> 75
/*     */     //   #168	-> 85
/*     */     //   #169	-> 91
/*     */     //   #172	-> 95
/*     */     //   #173	-> 100
/*     */     //   #176	-> 104
/*     */     //   #172	-> 111
/*     */     //   #173	-> 118
/*     */     //   #176	-> 122
/*     */     //   #178	-> 126
/*     */     //   #179	-> 132
/*     */     //   #181	-> 143
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   69	3	4	newVal	I
/*     */     //   0	144	0	this	Lorg/jboss/threads/JBossThread;
/*     */     //   13	131	1	differentThread	Z
/*     */     //   34	110	2	stateRef	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   39	105	3	oldVal	I
/*     */     //   75	69	4	newVal	I
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   91	95	111	finally
/*     */     //   111	113	111	finally
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
/*     */   
/*     */   private void doInterrupt() {
/* 184 */     if (isInterrupted())
/* 185 */       return;  Messages.msg.tracef("Interrupting thread \"%s\"", this);
/*     */     try {
/* 187 */       super.interrupt();
/*     */     } finally {
/* 189 */       InterruptHandler interruptHandler = this.interruptHandler;
/* 190 */       if (interruptHandler != null) {
/*     */         try {
/* 192 */           interruptHandler.handleInterrupt(this);
/* 193 */         } catch (Throwable t) {
/* 194 */           Messages.msg.interruptHandlerThrew(t, interruptHandler);
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isInterrupted() {
/* 201 */     return (this == Thread.currentThread()) ? super.isInterrupted() : ((super.isInterrupted() || this.stateRef.get() == 2));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void executeWithInterruptDeferred(Runnable task) {
/* 212 */     JBossThread thread = currentThread();
/* 213 */     if (registerDeferral(thread)) { try {
/* 214 */         task.run();
/*     */       } finally {
/* 216 */         unregisterDeferral(thread);
/*     */       }  }
/*     */     else
/* 219 */     { task.run(); }
/*     */   
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
/*     */   public static <T> T executeWithInterruptDeferred(Callable<T> action) throws Exception {
/* 234 */     JBossThread thread = currentThread();
/* 235 */     if (registerDeferral(thread)) {
/* 236 */       try { return action.call(); }
/*     */       finally
/* 238 */       { unregisterDeferral(thread); }
/*     */     
/*     */     }
/* 241 */     return action.call();
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
/*     */   public static <T> T executeWithInterruptDeferred(PrivilegedAction<T> action) {
/* 255 */     JBossThread thread = currentThread();
/* 256 */     if (registerDeferral(thread)) {
/* 257 */       try { return action.run(); }
/*     */       finally
/* 259 */       { unregisterDeferral(thread); }
/*     */     
/*     */     }
/* 262 */     return action.run();
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
/*     */   public static <T> T executeWithInterruptDeferred(PrivilegedExceptionAction<T> action) throws Exception {
/* 277 */     JBossThread thread = currentThread();
/* 278 */     if (registerDeferral(thread)) {
/* 279 */       try { return action.run(); }
/*     */       finally
/* 281 */       { unregisterDeferral(thread); }
/*     */     
/*     */     }
/* 284 */     return action.run();
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T, U, R, E extends Exception> R applyInterruptDeferredEx(ExceptionBiFunction<T, U, R, E> function, T param1, U param2) throws E {
/* 289 */     JBossThread thread = currentThread();
/* 290 */     if (registerDeferral(thread)) {
/* 291 */       try { return (R)function.apply(param1, param2); }
/*     */       finally
/* 293 */       { unregisterDeferral(thread); }
/*     */     
/*     */     }
/* 296 */     return (R)function.apply(param1, param2);
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T, R, E extends Exception> R applyInterruptDeferredEx(ExceptionFunction<T, R, E> function, T param) throws E {
/* 301 */     return applyInterruptDeferredEx(Functions.exceptionFunctionBiFunction(), function, param);
/*     */   }
/*     */   
/*     */   public static <T, E extends Exception> T getInterruptDeferredEx(ExceptionSupplier<T, E> supplier) throws E {
/* 305 */     return applyInterruptDeferredEx(Functions.exceptionFunctionBiFunction(), Functions.exceptionSupplierFunction(), supplier);
/*     */   }
/*     */   
/*     */   public static <T, E extends Exception> void acceptInterruptDeferredEx(ExceptionObjLongConsumer<T, E> consumer, T param1, long param2) throws E {
/* 309 */     JBossThread thread = currentThread();
/* 310 */     if (registerDeferral(thread)) { try {
/* 311 */         consumer.accept(param1, param2);
/*     */       } finally {
/* 313 */         unregisterDeferral(thread);
/*     */       }  }
/*     */     else
/* 316 */     { consumer.accept(param1, param2); }
/*     */   
/*     */   }
/*     */   
/*     */   public static <T, E extends Exception> void acceptInterruptDeferredEx(ExceptionObjIntConsumer<T, E> consumer, T param1, int param2) throws E {
/* 321 */     JBossThread thread = currentThread();
/* 322 */     if (registerDeferral(thread)) { try {
/* 323 */         consumer.accept(param1, param2);
/*     */       } finally {
/* 325 */         unregisterDeferral(thread);
/*     */       }  }
/*     */     else
/* 328 */     { consumer.accept(param1, param2); }
/*     */   
/*     */   }
/*     */   
/*     */   public static <T, U, E extends Exception> void acceptInterruptDeferredEx(ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) throws E {
/* 333 */     JBossThread thread = currentThread();
/* 334 */     if (registerDeferral(thread)) { try {
/* 335 */         consumer.accept(param1, param2);
/*     */       } finally {
/* 337 */         unregisterDeferral(thread);
/*     */       }  }
/*     */     else
/* 340 */     { consumer.accept(param1, param2); }
/*     */   
/*     */   }
/*     */   
/*     */   public static <T, E extends Exception> void acceptInterruptDeferredEx(ExceptionConsumer<T, E> consumer, T param) throws E {
/* 345 */     acceptInterruptDeferredEx(Functions.exceptionConsumerBiConsumer(), consumer, param);
/*     */   }
/*     */   
/*     */   public static <E extends Exception> void runInterruptDeferredEx(ExceptionRunnable<E> runnable) throws E {
/* 349 */     acceptInterruptDeferredEx(Functions.exceptionConsumerBiConsumer(), Functions.exceptionRunnableConsumer(), runnable);
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T, U, R, E extends Exception> R applyInterruptResumedEx(ExceptionBiFunction<T, U, R, E> function, T param1, U param2) throws E {
/* 354 */     JBossThread thread = currentThread();
/* 355 */     if (unregisterDeferral(thread)) {
/* 356 */       try { return (R)function.apply(param1, param2); }
/*     */       finally
/* 358 */       { registerDeferral(thread); }
/*     */     
/*     */     }
/* 361 */     return (R)function.apply(param1, param2);
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T, R, E extends Exception> R applyInterruptResumedEx(ExceptionFunction<T, R, E> function, T param) throws E {
/* 366 */     return applyInterruptResumedEx(Functions.exceptionFunctionBiFunction(), function, param);
/*     */   }
/*     */   
/*     */   public static <T, E extends Exception> T getInterruptResumedEx(ExceptionSupplier<T, E> supplier) throws E {
/* 370 */     return applyInterruptResumedEx(Functions.exceptionFunctionBiFunction(), Functions.exceptionSupplierFunction(), supplier);
/*     */   }
/*     */   
/*     */   public static <T, E extends Exception> void acceptInterruptResumedEx(ExceptionObjLongConsumer<T, E> consumer, T param1, long param2) throws E {
/* 374 */     JBossThread thread = currentThread();
/* 375 */     if (unregisterDeferral(thread)) { try {
/* 376 */         consumer.accept(param1, param2);
/*     */       } finally {
/* 378 */         registerDeferral(thread);
/*     */       }  }
/*     */     else
/* 381 */     { consumer.accept(param1, param2); }
/*     */   
/*     */   }
/*     */   
/*     */   public static <T, E extends Exception> void acceptInterruptResumedEx(ExceptionObjIntConsumer<T, E> consumer, T param1, int param2) throws E {
/* 386 */     JBossThread thread = currentThread();
/* 387 */     if (unregisterDeferral(thread)) { try {
/* 388 */         consumer.accept(param1, param2);
/*     */       } finally {
/* 390 */         registerDeferral(thread);
/*     */       }  }
/*     */     else
/* 393 */     { consumer.accept(param1, param2); }
/*     */   
/*     */   }
/*     */   
/*     */   public static <T, U, E extends Exception> void acceptInterruptResumedEx(ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) throws E {
/* 398 */     JBossThread thread = currentThread();
/* 399 */     if (unregisterDeferral(thread)) { try {
/* 400 */         consumer.accept(param1, param2);
/*     */       } finally {
/* 402 */         registerDeferral(thread);
/*     */       }  }
/*     */     else
/* 405 */     { consumer.accept(param1, param2); }
/*     */   
/*     */   }
/*     */   
/*     */   public static <T, E extends Exception> void acceptInterruptResumedEx(ExceptionConsumer<T, E> consumer, T param) throws E {
/* 410 */     acceptInterruptResumedEx(Functions.exceptionConsumerBiConsumer(), consumer, param);
/*     */   }
/*     */   
/*     */   public static <E extends Exception> void runInterruptResumedEx(ExceptionRunnable<E> runnable) throws E {
/* 414 */     acceptInterruptResumedEx(Functions.exceptionConsumerBiConsumer(), Functions.exceptionRunnableConsumer(), runnable);
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
/*     */   private static boolean unregisterDeferral(JBossThread thread) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: ifnonnull -> 6
/*     */     //   4: iconst_0
/*     */     //   5: ireturn
/*     */     //   6: aload_0
/*     */     //   7: getfield stateRef : Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   10: astore_3
/*     */     //   11: aload_3
/*     */     //   12: invokevirtual get : ()I
/*     */     //   15: istore_1
/*     */     //   16: iload_1
/*     */     //   17: ifeq -> 25
/*     */     //   20: iload_1
/*     */     //   21: iconst_3
/*     */     //   22: if_icmpne -> 27
/*     */     //   25: iconst_0
/*     */     //   26: ireturn
/*     */     //   27: iload_1
/*     */     //   28: iconst_1
/*     */     //   29: if_icmpne -> 37
/*     */     //   32: iconst_0
/*     */     //   33: istore_2
/*     */     //   34: goto -> 51
/*     */     //   37: iload_1
/*     */     //   38: iconst_2
/*     */     //   39: if_icmpne -> 47
/*     */     //   42: iconst_3
/*     */     //   43: istore_2
/*     */     //   44: goto -> 51
/*     */     //   47: invokestatic unreachableCode : ()Ljava/lang/IllegalStateException;
/*     */     //   50: athrow
/*     */     //   51: aload_3
/*     */     //   52: iload_1
/*     */     //   53: iload_2
/*     */     //   54: invokevirtual compareAndSet : (II)Z
/*     */     //   57: ifeq -> 11
/*     */     //   60: iload_2
/*     */     //   61: iconst_3
/*     */     //   62: if_icmpne -> 87
/*     */     //   65: aload_0
/*     */     //   66: invokespecial doInterrupt : ()V
/*     */     //   69: aload_3
/*     */     //   70: iconst_0
/*     */     //   71: invokevirtual set : (I)V
/*     */     //   74: goto -> 87
/*     */     //   77: astore #4
/*     */     //   79: aload_3
/*     */     //   80: iconst_0
/*     */     //   81: invokevirtual set : (I)V
/*     */     //   84: aload #4
/*     */     //   86: athrow
/*     */     //   87: iconst_1
/*     */     //   88: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #418	-> 0
/*     */     //   #419	-> 4
/*     */     //   #422	-> 6
/*     */     //   #424	-> 11
/*     */     //   #425	-> 16
/*     */     //   #427	-> 25
/*     */     //   #428	-> 27
/*     */     //   #429	-> 32
/*     */     //   #430	-> 37
/*     */     //   #431	-> 42
/*     */     //   #433	-> 47
/*     */     //   #435	-> 51
/*     */     //   #436	-> 60
/*     */     //   #437	-> 65
/*     */     //   #439	-> 69
/*     */     //   #440	-> 74
/*     */     //   #439	-> 77
/*     */     //   #440	-> 84
/*     */     //   #441	-> 87
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   34	3	2	newVal	I
/*     */     //   44	3	2	newVal	I
/*     */     //   0	89	0	thread	Lorg/jboss/threads/JBossThread;
/*     */     //   16	73	1	oldVal	I
/*     */     //   51	38	2	newVal	I
/*     */     //   11	78	3	stateRef	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   65	69	77	finally
/*     */     //   77	79	77	finally
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
/*     */   private static boolean registerDeferral(JBossThread thread) {
/* 445 */     if (thread == null) {
/* 446 */       return false;
/*     */     }
/* 448 */     AtomicInteger stateRef = thread.stateRef;
/*     */     
/*     */     while (true) {
/* 451 */       int newVal, oldVal = stateRef.get();
/* 452 */       while (oldVal == 3) {
/* 453 */         LockSupport.park();
/* 454 */         oldVal = stateRef.get();
/*     */       } 
/* 456 */       if (oldVal == 0)
/* 457 */       { newVal = Thread.interrupted() ? 1 : 2; }
/* 458 */       else { if (oldVal == 1 || oldVal == 2)
/*     */         {
/* 460 */           return false;
/*     */         }
/* 462 */         throw Assert.unreachableCode(); }
/*     */       
/* 464 */       if (stateRef.compareAndSet(oldVal, newVal)) {
/* 465 */         if (newVal == 1 && Thread.interrupted())
/*     */         {
/* 467 */           stateRef.set(2);
/*     */         }
/* 469 */         return true;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 477 */     Messages.msg.tracef("Thread \"%s\" starting execution", this);
/*     */     try {
/* 479 */       super.run();
/*     */     } finally {
/* 481 */       Messages.msg.tracef("Thread \"%s\" exiting", this);
/* 482 */       List<Runnable> exitHandlers = this.exitHandlers;
/* 483 */       if (exitHandlers != null) for (Runnable exitHandler : exitHandlers) {
/*     */           try {
/* 485 */             exitHandler.run();
/* 486 */           } catch (Throwable t) {
/*     */             try {
/* 488 */               getUncaughtExceptionHandler().uncaughtException(this, t);
/* 489 */             } catch (Throwable throwable) {}
/*     */           } 
/*     */         } 
/*     */     
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
/*     */   public static boolean onExit(Runnable hook) throws SecurityException {
/* 505 */     SecurityManager sm = System.getSecurityManager();
/* 506 */     if (sm != null) {
/* 507 */       sm.checkPermission(MODIFY_THREAD_PERMISSION);
/*     */     }
/* 509 */     JBossThread thread = currentThread();
/* 510 */     if (thread == null || hook == null) return false; 
/* 511 */     List<Runnable> exitHandlers = thread.exitHandlers;
/* 512 */     if (exitHandlers == null) {
/* 513 */       exitHandlers = new ArrayList<>();
/* 514 */       thread.exitHandlers = exitHandlers;
/*     */     } 
/* 516 */     exitHandlers.add(new ContextClassLoaderSavingRunnable(JBossExecutors.getContextClassLoader(thread), hook));
/* 517 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JBossThread currentThread() {
/* 526 */     Thread thread = Thread.currentThread();
/* 527 */     return (thread instanceof JBossThread) ? (JBossThread)thread : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/* 536 */     super.start();
/* 537 */     Messages.msg.tracef("Started thread \"%s\"", this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler eh) {
/* 546 */     super.setUncaughtExceptionHandler(eh);
/* 547 */     Messages.msg.tracef("Changed uncaught exception handler for \"%s\" to %s", this, eh);
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
/*     */   public static InterruptHandler getAndSetInterruptHandler(InterruptHandler newInterruptHandler) {
/* 566 */     JBossThread thread = currentThread();
/* 567 */     if (thread == null) {
/* 568 */       throw Messages.msg.noInterruptHandlers();
/*     */     }
/*     */     try {
/* 571 */       return thread.interruptHandler;
/*     */     } finally {
/* 573 */       thread.interruptHandler = newInterruptHandler;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static <T, U, R, E extends Exception> R applyWithInterruptHandler(InterruptHandler interruptHandler, ExceptionBiFunction<T, U, R, E> function, T param1, U param2) throws E {
/* 578 */     JBossThread thread = currentThread();
/* 579 */     if (thread == null) {
/* 580 */       return (R)function.apply(param1, param2);
/*     */     }
/* 582 */     InterruptHandler old = thread.interruptHandler;
/* 583 */     thread.interruptHandler = interruptHandler;
/*     */     try {
/* 585 */       return (R)function.apply(param1, param2);
/*     */     } finally {
/* 587 */       thread.interruptHandler = old;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T, R, E extends Exception> R applyWithInterruptHandler(InterruptHandler interruptHandler, ExceptionFunction<T, R, E> function, T param1) throws E {
/* 593 */     return applyWithInterruptHandler(interruptHandler, Functions.exceptionFunctionBiFunction(), function, param1);
/*     */   }
/*     */   
/*     */   public static <R, E extends Exception> R getWithInterruptHandler(InterruptHandler interruptHandler, ExceptionSupplier<R, E> function) throws E {
/* 597 */     return applyWithInterruptHandler(interruptHandler, Functions.exceptionFunctionBiFunction(), Functions.exceptionSupplierFunction(), function);
/*     */   }
/*     */   
/*     */   public static <T, E extends Exception> void acceptWithInterruptHandler(InterruptHandler interruptHandler, ExceptionObjLongConsumer<T, E> function, T param1, long param2) throws E {
/* 601 */     JBossThread thread = currentThread();
/* 602 */     if (thread == null) {
/* 603 */       function.accept(param1, param2);
/*     */       return;
/*     */     } 
/* 606 */     InterruptHandler old = thread.interruptHandler;
/* 607 */     thread.interruptHandler = interruptHandler;
/*     */     try {
/* 609 */       function.accept(param1, param2);
/*     */       return;
/*     */     } finally {
/* 612 */       thread.interruptHandler = old;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T, E extends Exception> void acceptWithInterruptHandler(InterruptHandler interruptHandler, ExceptionObjIntConsumer<T, E> function, T param1, int param2) throws E {
/* 618 */     JBossThread thread = currentThread();
/* 619 */     if (thread == null) {
/* 620 */       function.accept(param1, param2);
/*     */       return;
/*     */     } 
/* 623 */     InterruptHandler old = thread.interruptHandler;
/* 624 */     thread.interruptHandler = interruptHandler;
/*     */     try {
/* 626 */       function.accept(param1, param2);
/*     */       return;
/*     */     } finally {
/* 629 */       thread.interruptHandler = old;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T, U, E extends Exception> void acceptWithInterruptHandler(InterruptHandler interruptHandler, ExceptionBiConsumer<T, U, E> function, T param1, U param2) throws E {
/* 635 */     JBossThread thread = currentThread();
/* 636 */     if (thread == null) {
/* 637 */       function.accept(param1, param2);
/*     */       return;
/*     */     } 
/* 640 */     InterruptHandler old = thread.interruptHandler;
/* 641 */     thread.interruptHandler = interruptHandler;
/*     */     try {
/* 643 */       function.accept(param1, param2);
/*     */       return;
/*     */     } finally {
/* 646 */       thread.interruptHandler = old;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T, E extends Exception> void acceptWithInterruptHandler(InterruptHandler interruptHandler, ExceptionConsumer<T, E> function, T param1) throws E {
/* 652 */     acceptWithInterruptHandler(interruptHandler, Functions.exceptionConsumerBiConsumer(), function, param1);
/*     */   }
/*     */   
/*     */   public static <E extends Exception> void runWithInterruptHandler(InterruptHandler interruptHandler, ExceptionRunnable<E> function) throws E {
/* 656 */     acceptWithInterruptHandler(interruptHandler, Functions.exceptionConsumerBiConsumer(), Functions.exceptionRunnableConsumer(), function);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ThreadNameInfo getThreadNameInfo() {
/* 665 */     return this.threadNameInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setThreadNameInfo(ThreadNameInfo threadNameInfo) throws SecurityException {
/* 675 */     checkAccess();
/* 676 */     this.threadNameInfo = threadNameInfo;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\JBossThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */