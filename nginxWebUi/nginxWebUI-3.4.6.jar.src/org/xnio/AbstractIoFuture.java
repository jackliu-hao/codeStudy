/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.concurrent.locks.LockSupport;
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
/*     */ 
/*     */ public abstract class AbstractIoFuture<T>
/*     */   implements IoFuture<T>
/*     */ {
/*  37 */   private final AtomicReference<State<T>> stateRef = (AtomicReference)new AtomicReference<>(ST_INITIAL);
/*     */ 
/*     */   
/*  40 */   private static final State<?> ST_INITIAL = new InitialState();
/*  41 */   private static final State<?> ST_CANCELLED = new CancelledState();
/*     */   
/*     */   static abstract class State<T> {
/*     */     abstract IoFuture.Status getStatus();
/*     */     
/*     */     abstract void notifyDone(AbstractIoFuture<T> param1AbstractIoFuture, T param1T);
/*     */     
/*     */     abstract void notifyFailed(AbstractIoFuture<T> param1AbstractIoFuture, IOException param1IOException);
/*     */     
/*     */     abstract void notifyCancelled(AbstractIoFuture<T> param1AbstractIoFuture);
/*     */     
/*     */     abstract void cancel();
/*     */     
/*     */     abstract boolean cancelRequested();
/*     */     
/*     */     State<T> withWaiter(Thread thread) {
/*  57 */       return new AbstractIoFuture.WaiterState<>(this, thread);
/*     */     }
/*     */     
/*     */     <A> State<T> withNotifier(Executor executor, AbstractIoFuture<T> future, IoFuture.Notifier<? super T, A> notifier, A attachment) {
/*  61 */       return new AbstractIoFuture.NotifierState<>(this, notifier, attachment);
/*     */     }
/*     */     
/*     */     State<T> withCancelHandler(Cancellable cancellable) {
/*  65 */       return new AbstractIoFuture.CancellableState<>(this, cancellable);
/*     */     }
/*     */     
/*     */     T getResult() {
/*  69 */       throw new IllegalStateException();
/*     */     }
/*     */     
/*     */     IOException getException() {
/*  73 */       throw new IllegalStateException();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class InitialState<T>
/*     */     extends State<T> {
/*     */     IoFuture.Status getStatus() {
/*  80 */       return IoFuture.Status.WAITING;
/*     */     }
/*     */ 
/*     */     
/*     */     void notifyDone(AbstractIoFuture<T> future, T result) {}
/*     */ 
/*     */     
/*     */     void notifyFailed(AbstractIoFuture<T> future, IOException exception) {}
/*     */ 
/*     */     
/*     */     void notifyCancelled(AbstractIoFuture<T> future) {}
/*     */ 
/*     */     
/*     */     void cancel() {}
/*     */     
/*     */     boolean cancelRequested() {
/*  96 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class CompleteState<T> extends State<T> {
/*     */     private final T result;
/*     */     
/*     */     CompleteState(T result) {
/* 104 */       this.result = result;
/*     */     }
/*     */     
/*     */     IoFuture.Status getStatus() {
/* 108 */       return IoFuture.Status.DONE;
/*     */     }
/*     */ 
/*     */     
/*     */     void notifyDone(AbstractIoFuture<T> future, T result) {}
/*     */ 
/*     */     
/*     */     void notifyFailed(AbstractIoFuture<T> future, IOException exception) {}
/*     */ 
/*     */     
/*     */     void notifyCancelled(AbstractIoFuture<T> future) {}
/*     */ 
/*     */     
/*     */     void cancel() {}
/*     */     
/*     */     AbstractIoFuture.State<T> withCancelHandler(Cancellable cancellable) {
/* 124 */       return this;
/*     */     }
/*     */     
/*     */     AbstractIoFuture.State<T> withWaiter(Thread thread) {
/* 128 */       return this;
/*     */     }
/*     */     
/*     */     <A> AbstractIoFuture.State<T> withNotifier(Executor executor, AbstractIoFuture<T> future, IoFuture.Notifier<? super T, A> notifier, A attachment) {
/* 132 */       future.runNotifier(new AbstractIoFuture.NotifierRunnable<>(notifier, future, attachment));
/* 133 */       return this;
/*     */     }
/*     */     
/*     */     T getResult() {
/* 137 */       return this.result;
/*     */     }
/*     */     
/*     */     boolean cancelRequested() {
/* 141 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class FailedState<T> extends State<T> {
/*     */     private final IOException exception;
/*     */     
/*     */     FailedState(IOException exception) {
/* 149 */       this.exception = exception;
/*     */     }
/*     */     
/*     */     IoFuture.Status getStatus() {
/* 153 */       return IoFuture.Status.FAILED;
/*     */     }
/*     */ 
/*     */     
/*     */     void notifyDone(AbstractIoFuture<T> future, T result) {}
/*     */ 
/*     */     
/*     */     void notifyFailed(AbstractIoFuture<T> future, IOException exception) {}
/*     */ 
/*     */     
/*     */     void notifyCancelled(AbstractIoFuture<T> future) {}
/*     */ 
/*     */     
/*     */     void cancel() {}
/*     */     
/*     */     AbstractIoFuture.State<T> withCancelHandler(Cancellable cancellable) {
/* 169 */       return this;
/*     */     }
/*     */     
/*     */     AbstractIoFuture.State<T> withWaiter(Thread thread) {
/* 173 */       return this;
/*     */     }
/*     */     
/*     */     <A> AbstractIoFuture.State<T> withNotifier(Executor executor, AbstractIoFuture<T> future, IoFuture.Notifier<? super T, A> notifier, A attachment) {
/* 177 */       future.runNotifier(new AbstractIoFuture.NotifierRunnable<>(notifier, future, attachment));
/* 178 */       return this;
/*     */     }
/*     */     
/*     */     IOException getException() {
/* 182 */       return this.exception;
/*     */     }
/*     */     
/*     */     boolean cancelRequested() {
/* 186 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final class CancelledState<T>
/*     */     extends State<T>
/*     */   {
/*     */     IoFuture.Status getStatus() {
/* 196 */       return IoFuture.Status.CANCELLED;
/*     */     }
/*     */ 
/*     */     
/*     */     void notifyDone(AbstractIoFuture<T> future, T result) {}
/*     */ 
/*     */     
/*     */     void notifyFailed(AbstractIoFuture<T> future, IOException exception) {}
/*     */ 
/*     */     
/*     */     void notifyCancelled(AbstractIoFuture<T> future) {}
/*     */ 
/*     */     
/*     */     void cancel() {}
/*     */     
/*     */     AbstractIoFuture.State<T> withCancelHandler(Cancellable cancellable) {
/*     */       try {
/* 213 */         cancellable.cancel();
/* 214 */       } catch (Throwable throwable) {}
/* 215 */       return this;
/*     */     }
/*     */     
/*     */     <A> AbstractIoFuture.State<T> withNotifier(Executor executor, AbstractIoFuture<T> future, IoFuture.Notifier<? super T, A> notifier, A attachment) {
/* 219 */       future.runNotifier(new AbstractIoFuture.NotifierRunnable<>(notifier, future, attachment));
/* 220 */       return this;
/*     */     }
/*     */     
/*     */     AbstractIoFuture.State<T> withWaiter(Thread thread) {
/* 224 */       return this;
/*     */     }
/*     */     
/*     */     boolean cancelRequested() {
/* 228 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class NotifierState<T, A> extends State<T> {
/*     */     final AbstractIoFuture.State<T> next;
/*     */     final IoFuture.Notifier<? super T, A> notifier;
/*     */     final A attachment;
/*     */     
/*     */     NotifierState(AbstractIoFuture.State<T> next, IoFuture.Notifier<? super T, A> notifier, A attachment) {
/* 238 */       this.next = next;
/* 239 */       this.notifier = notifier;
/* 240 */       this.attachment = attachment;
/*     */     }
/*     */     
/*     */     IoFuture.Status getStatus() {
/* 244 */       return IoFuture.Status.WAITING;
/*     */     }
/*     */     
/*     */     void notifyDone(AbstractIoFuture<T> future, T result) {
/* 248 */       doNotify(future);
/* 249 */       this.next.notifyDone(future, result);
/*     */     }
/*     */     
/*     */     void notifyFailed(AbstractIoFuture<T> future, IOException exception) {
/* 253 */       doNotify(future);
/* 254 */       this.next.notifyFailed(future, exception);
/*     */     }
/*     */     
/*     */     void notifyCancelled(AbstractIoFuture<T> future) {
/* 258 */       doNotify(future);
/* 259 */       this.next.notifyCancelled(future);
/*     */     }
/*     */     
/*     */     void cancel() {
/* 263 */       this.next.cancel();
/*     */     }
/*     */     
/*     */     private void doNotify(AbstractIoFuture<T> future) {
/* 267 */       future.runNotifier(new AbstractIoFuture.NotifierRunnable<>(this.notifier, future, this.attachment));
/*     */     }
/*     */     
/*     */     boolean cancelRequested() {
/* 271 */       return this.next.cancelRequested();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class WaiterState<T> extends State<T> {
/*     */     final AbstractIoFuture.State<T> next;
/*     */     final Thread waiter;
/*     */     
/*     */     WaiterState(AbstractIoFuture.State<T> next, Thread waiter) {
/* 280 */       this.next = next;
/* 281 */       this.waiter = waiter;
/*     */     }
/*     */     
/*     */     IoFuture.Status getStatus() {
/* 285 */       return IoFuture.Status.WAITING;
/*     */     }
/*     */     
/*     */     void notifyDone(AbstractIoFuture<T> future, T result) {
/* 289 */       LockSupport.unpark(this.waiter);
/* 290 */       this.next.notifyDone(future, result);
/*     */     }
/*     */     
/*     */     void notifyFailed(AbstractIoFuture<T> future, IOException exception) {
/* 294 */       LockSupport.unpark(this.waiter);
/* 295 */       this.next.notifyFailed(future, exception);
/*     */     }
/*     */     
/*     */     void notifyCancelled(AbstractIoFuture<T> future) {
/* 299 */       LockSupport.unpark(this.waiter);
/* 300 */       this.next.notifyCancelled(future);
/*     */     }
/*     */     
/*     */     void cancel() {
/* 304 */       this.next.cancel();
/*     */     }
/*     */     
/*     */     boolean cancelRequested() {
/* 308 */       return this.next.cancelRequested();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class CancellableState<T> extends State<T> {
/*     */     final AbstractIoFuture.State<T> next;
/*     */     final Cancellable cancellable;
/*     */     
/*     */     CancellableState(AbstractIoFuture.State<T> next, Cancellable cancellable) {
/* 317 */       this.next = next;
/* 318 */       this.cancellable = cancellable;
/*     */     }
/*     */     
/*     */     IoFuture.Status getStatus() {
/* 322 */       return IoFuture.Status.WAITING;
/*     */     }
/*     */     
/*     */     void notifyDone(AbstractIoFuture<T> future, T result) {
/* 326 */       this.next.notifyDone(future, result);
/*     */     }
/*     */     
/*     */     void notifyFailed(AbstractIoFuture<T> future, IOException exception) {
/* 330 */       this.next.notifyFailed(future, exception);
/*     */     }
/*     */     
/*     */     void notifyCancelled(AbstractIoFuture<T> future) {
/* 334 */       this.next.notifyCancelled(future);
/*     */     }
/*     */     
/*     */     void cancel() {
/*     */       try {
/* 339 */         this.cancellable.cancel();
/* 340 */       } catch (Throwable throwable) {}
/* 341 */       this.next.cancel();
/*     */     }
/*     */     
/*     */     boolean cancelRequested() {
/* 345 */       return this.next.cancelRequested();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class CancelRequestedState<T> extends State<T> {
/*     */     final AbstractIoFuture.State<T> next;
/*     */     
/*     */     CancelRequestedState(AbstractIoFuture.State<T> next) {
/* 353 */       this.next = next;
/*     */     }
/*     */     
/*     */     IoFuture.Status getStatus() {
/* 357 */       return IoFuture.Status.WAITING;
/*     */     }
/*     */     
/*     */     void notifyDone(AbstractIoFuture<T> future, T result) {
/* 361 */       this.next.notifyDone(future, result);
/*     */     }
/*     */     
/*     */     void notifyFailed(AbstractIoFuture<T> future, IOException exception) {
/* 365 */       this.next.notifyFailed(future, exception);
/*     */     }
/*     */     
/*     */     void notifyCancelled(AbstractIoFuture<T> future) {
/* 369 */       this.next.notifyCancelled(future);
/*     */     }
/*     */ 
/*     */     
/*     */     void cancel() {}
/*     */ 
/*     */     
/*     */     boolean cancelRequested() {
/* 377 */       return true;
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
/*     */   public IoFuture.Status getStatus() {
/* 391 */     return getState().getStatus();
/*     */   }
/*     */   
/*     */   private State<T> getState() {
/* 395 */     return this.stateRef.get();
/*     */   }
/*     */   
/*     */   private boolean compareAndSetState(State<T> expect, State<T> update) {
/* 399 */     return this.stateRef.compareAndSet(expect, update);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IoFuture.Status await() {
/*     */     // Byte code:
/*     */     //   0: invokestatic currentThread : ()Ljava/lang/Thread;
/*     */     //   3: astore_1
/*     */     //   4: aload_0
/*     */     //   5: invokespecial getState : ()Lorg/xnio/AbstractIoFuture$State;
/*     */     //   8: astore_2
/*     */     //   9: aload_2
/*     */     //   10: invokevirtual getStatus : ()Lorg/xnio/IoFuture$Status;
/*     */     //   13: getstatic org/xnio/IoFuture$Status.WAITING : Lorg/xnio/IoFuture$Status;
/*     */     //   16: if_acmpeq -> 24
/*     */     //   19: aload_2
/*     */     //   20: invokevirtual getStatus : ()Lorg/xnio/IoFuture$Status;
/*     */     //   23: areturn
/*     */     //   24: invokestatic checkBlockingAllowed : ()V
/*     */     //   27: aload_2
/*     */     //   28: aload_1
/*     */     //   29: invokevirtual withWaiter : (Ljava/lang/Thread;)Lorg/xnio/AbstractIoFuture$State;
/*     */     //   32: astore_3
/*     */     //   33: aload_0
/*     */     //   34: aload_2
/*     */     //   35: aload_3
/*     */     //   36: invokespecial compareAndSetState : (Lorg/xnio/AbstractIoFuture$State;Lorg/xnio/AbstractIoFuture$State;)Z
/*     */     //   39: ifeq -> 107
/*     */     //   42: invokestatic interrupted : ()Z
/*     */     //   45: istore #4
/*     */     //   47: aload_0
/*     */     //   48: invokestatic park : (Ljava/lang/Object;)V
/*     */     //   51: invokestatic interrupted : ()Z
/*     */     //   54: ifeq -> 60
/*     */     //   57: iconst_1
/*     */     //   58: istore #4
/*     */     //   60: aload_0
/*     */     //   61: invokespecial getState : ()Lorg/xnio/AbstractIoFuture$State;
/*     */     //   64: astore_2
/*     */     //   65: aload_2
/*     */     //   66: invokevirtual getStatus : ()Lorg/xnio/IoFuture$Status;
/*     */     //   69: getstatic org/xnio/IoFuture$Status.WAITING : Lorg/xnio/IoFuture$Status;
/*     */     //   72: if_acmpeq -> 47
/*     */     //   75: aload_2
/*     */     //   76: invokevirtual getStatus : ()Lorg/xnio/IoFuture$Status;
/*     */     //   79: astore #5
/*     */     //   81: iload #4
/*     */     //   83: ifeq -> 90
/*     */     //   86: aload_1
/*     */     //   87: invokevirtual interrupt : ()V
/*     */     //   90: aload #5
/*     */     //   92: areturn
/*     */     //   93: astore #6
/*     */     //   95: iload #4
/*     */     //   97: ifeq -> 104
/*     */     //   100: aload_1
/*     */     //   101: invokevirtual interrupt : ()V
/*     */     //   104: aload #6
/*     */     //   106: athrow
/*     */     //   107: goto -> 4
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #406	-> 0
/*     */     //   #409	-> 4
/*     */     //   #410	-> 9
/*     */     //   #411	-> 19
/*     */     //   #413	-> 24
/*     */     //   #414	-> 27
/*     */     //   #415	-> 33
/*     */     //   #416	-> 42
/*     */     //   #419	-> 47
/*     */     //   #420	-> 51
/*     */     //   #421	-> 60
/*     */     //   #422	-> 65
/*     */     //   #423	-> 75
/*     */     //   #425	-> 81
/*     */     //   #423	-> 90
/*     */     //   #425	-> 93
/*     */     //   #426	-> 104
/*     */     //   #429	-> 107
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   47	60	4	intr	Z
/*     */     //   33	74	3	withWaiter	Lorg/xnio/AbstractIoFuture$State;
/*     */     //   0	110	0	this	Lorg/xnio/AbstractIoFuture;
/*     */     //   4	106	1	thread	Ljava/lang/Thread;
/*     */     //   9	101	2	state	Lorg/xnio/AbstractIoFuture$State;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   33	74	3	withWaiter	Lorg/xnio/AbstractIoFuture$State<TT;>;
/*     */     //   0	110	0	this	Lorg/xnio/AbstractIoFuture<TT;>;
/*     */     //   9	101	2	state	Lorg/xnio/AbstractIoFuture$State<TT;>;
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   47	81	93	finally
/*     */     //   93	95	93	finally
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IoFuture.Status await(long time, TimeUnit timeUnit) {
/*     */     // Byte code:
/*     */     //   0: lload_1
/*     */     //   1: lconst_0
/*     */     //   2: lcmp
/*     */     //   3: ifge -> 8
/*     */     //   6: lconst_0
/*     */     //   7: lstore_1
/*     */     //   8: aload_3
/*     */     //   9: lload_1
/*     */     //   10: invokevirtual toNanos : (J)J
/*     */     //   13: lstore #4
/*     */     //   15: invokestatic nanoTime : ()J
/*     */     //   18: lstore #6
/*     */     //   20: invokestatic currentThread : ()Ljava/lang/Thread;
/*     */     //   23: astore #10
/*     */     //   25: aload_0
/*     */     //   26: invokespecial getState : ()Lorg/xnio/AbstractIoFuture$State;
/*     */     //   29: astore #11
/*     */     //   31: aload #11
/*     */     //   33: invokevirtual getStatus : ()Lorg/xnio/IoFuture$Status;
/*     */     //   36: getstatic org/xnio/IoFuture$Status.WAITING : Lorg/xnio/IoFuture$Status;
/*     */     //   39: if_acmpne -> 49
/*     */     //   42: lload #4
/*     */     //   44: lconst_0
/*     */     //   45: lcmp
/*     */     //   46: ifne -> 55
/*     */     //   49: aload #11
/*     */     //   51: invokevirtual getStatus : ()Lorg/xnio/IoFuture$Status;
/*     */     //   54: areturn
/*     */     //   55: invokestatic checkBlockingAllowed : ()V
/*     */     //   58: aload #11
/*     */     //   60: aload #10
/*     */     //   62: invokevirtual withWaiter : (Ljava/lang/Thread;)Lorg/xnio/AbstractIoFuture$State;
/*     */     //   65: astore #12
/*     */     //   67: aload_0
/*     */     //   68: aload #11
/*     */     //   70: aload #12
/*     */     //   72: invokespecial compareAndSetState : (Lorg/xnio/AbstractIoFuture$State;Lorg/xnio/AbstractIoFuture$State;)Z
/*     */     //   75: ifeq -> 175
/*     */     //   78: invokestatic interrupted : ()Z
/*     */     //   81: istore #13
/*     */     //   83: aload_0
/*     */     //   84: lload #4
/*     */     //   86: invokestatic parkNanos : (Ljava/lang/Object;J)V
/*     */     //   89: invokestatic interrupted : ()Z
/*     */     //   92: ifeq -> 98
/*     */     //   95: iconst_1
/*     */     //   96: istore #13
/*     */     //   98: aload_0
/*     */     //   99: invokespecial getState : ()Lorg/xnio/AbstractIoFuture$State;
/*     */     //   102: astore #11
/*     */     //   104: lload #4
/*     */     //   106: invokestatic nanoTime : ()J
/*     */     //   109: dup2
/*     */     //   110: lstore #8
/*     */     //   112: lload #6
/*     */     //   114: lsub
/*     */     //   115: lsub
/*     */     //   116: lstore #4
/*     */     //   118: lload #8
/*     */     //   120: lstore #6
/*     */     //   122: aload #11
/*     */     //   124: invokevirtual getStatus : ()Lorg/xnio/IoFuture$Status;
/*     */     //   127: getstatic org/xnio/IoFuture$Status.WAITING : Lorg/xnio/IoFuture$Status;
/*     */     //   130: if_acmpne -> 140
/*     */     //   133: lload #4
/*     */     //   135: lconst_0
/*     */     //   136: lcmp
/*     */     //   137: ifgt -> 83
/*     */     //   140: aload #11
/*     */     //   142: invokevirtual getStatus : ()Lorg/xnio/IoFuture$Status;
/*     */     //   145: astore #14
/*     */     //   147: iload #13
/*     */     //   149: ifeq -> 157
/*     */     //   152: aload #10
/*     */     //   154: invokevirtual interrupt : ()V
/*     */     //   157: aload #14
/*     */     //   159: areturn
/*     */     //   160: astore #15
/*     */     //   162: iload #13
/*     */     //   164: ifeq -> 172
/*     */     //   167: aload #10
/*     */     //   169: invokevirtual interrupt : ()V
/*     */     //   172: aload #15
/*     */     //   174: athrow
/*     */     //   175: goto -> 25
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #436	-> 0
/*     */     //   #437	-> 6
/*     */     //   #439	-> 8
/*     */     //   #440	-> 15
/*     */     //   #442	-> 20
/*     */     //   #445	-> 25
/*     */     //   #446	-> 31
/*     */     //   #447	-> 49
/*     */     //   #449	-> 55
/*     */     //   #450	-> 58
/*     */     //   #451	-> 67
/*     */     //   #452	-> 78
/*     */     //   #455	-> 83
/*     */     //   #456	-> 89
/*     */     //   #457	-> 98
/*     */     //   #458	-> 104
/*     */     //   #459	-> 118
/*     */     //   #460	-> 122
/*     */     //   #461	-> 140
/*     */     //   #463	-> 147
/*     */     //   #461	-> 157
/*     */     //   #463	-> 160
/*     */     //   #464	-> 172
/*     */     //   #467	-> 175
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   112	48	8	tick	J
/*     */     //   83	92	13	intr	Z
/*     */     //   67	108	12	withWaiter	Lorg/xnio/AbstractIoFuture$State;
/*     */     //   0	178	0	this	Lorg/xnio/AbstractIoFuture;
/*     */     //   0	178	1	time	J
/*     */     //   0	178	3	timeUnit	Ljava/util/concurrent/TimeUnit;
/*     */     //   15	163	4	duration	J
/*     */     //   20	158	6	now	J
/*     */     //   25	153	10	thread	Ljava/lang/Thread;
/*     */     //   31	147	11	state	Lorg/xnio/AbstractIoFuture$State;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   67	108	12	withWaiter	Lorg/xnio/AbstractIoFuture$State<TT;>;
/*     */     //   0	178	0	this	Lorg/xnio/AbstractIoFuture<TT;>;
/*     */     //   31	147	11	state	Lorg/xnio/AbstractIoFuture$State<TT;>;
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   83	147	160	finally
/*     */     //   160	162	160	finally
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
/*     */ 
/*     */ 
/*     */   
/*     */   public IoFuture.Status awaitInterruptibly() throws InterruptedException {
/*     */     State<T> state, withWaiter;
/* 474 */     Thread thread = Thread.currentThread();
/*     */     
/*     */     do {
/* 477 */       state = getState();
/* 478 */       if (state.getStatus() != IoFuture.Status.WAITING) {
/* 479 */         return state.getStatus();
/*     */       }
/* 481 */       Xnio.checkBlockingAllowed();
/* 482 */       if (Thread.interrupted()) throw new InterruptedException(); 
/* 483 */       withWaiter = state.withWaiter(thread);
/* 484 */     } while (!compareAndSetState(state, withWaiter));
/*     */     while (true) {
/* 486 */       LockSupport.park(this);
/* 487 */       if (Thread.interrupted()) throw new InterruptedException(); 
/* 488 */       state = getState();
/* 489 */       if (state.getStatus() != IoFuture.Status.WAITING) {
/* 490 */         return state.getStatus();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public IoFuture.Status awaitInterruptibly(long time, TimeUnit timeUnit) throws InterruptedException {
/*     */     long tick;
/*     */     State<T> state, withWaiter;
/* 500 */     if (time < 0L) {
/* 501 */       time = 0L;
/*     */     }
/* 503 */     long duration = timeUnit.toNanos(time);
/* 504 */     long now = System.nanoTime();
/*     */     
/* 506 */     Thread thread = Thread.currentThread();
/*     */     
/*     */     do {
/* 509 */       state = getState();
/* 510 */       if (state.getStatus() != IoFuture.Status.WAITING || duration == 0L) {
/* 511 */         return state.getStatus();
/*     */       }
/* 513 */       Xnio.checkBlockingAllowed();
/* 514 */       if (Thread.interrupted()) throw new InterruptedException(); 
/* 515 */       withWaiter = state.withWaiter(thread);
/* 516 */     } while (!compareAndSetState(state, withWaiter));
/*     */     do {
/* 518 */       LockSupport.parkNanos(this, duration);
/* 519 */       if (Thread.interrupted()) throw new InterruptedException(); 
/* 520 */       state = getState();
/* 521 */       duration -= (tick = System.nanoTime()) - now;
/* 522 */       now = tick;
/* 523 */     } while (state.getStatus() == IoFuture.Status.WAITING && duration > 0L);
/* 524 */     return state.getStatus();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T get() throws IOException, CancellationException {
/* 535 */     switch (await()) { case DONE:
/* 536 */         return getState().getResult();
/* 537 */       case FAILED: throw getState().getException();
/* 538 */       case CANCELLED: throw Messages.futureMsg.opCancelled(); }
/* 539 */      throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T getInterruptibly() throws IOException, InterruptedException, CancellationException {
/* 548 */     switch (awaitInterruptibly()) { case DONE:
/* 549 */         return getState().getResult();
/* 550 */       case FAILED: throw getState().getException();
/* 551 */       case CANCELLED: throw Messages.futureMsg.opCancelled(); }
/* 552 */      throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IOException getException() throws IllegalStateException {
/* 560 */     return getState().getException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <A> IoFuture<T> addNotifier(IoFuture.Notifier<? super T, A> notifier, A attachment) {
/*     */     while (true) {
/* 569 */       State<T> oldState = getState();
/* 570 */       State<T> newState = oldState.withNotifier(getNotifierExecutor(), this, notifier, attachment);
/* 571 */       if (compareAndSetState(oldState, newState)) {
/* 572 */         return this;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean setException(IOException exception) {
/* 583 */     State<T> oldState = getState();
/* 584 */     if (oldState.getStatus() != IoFuture.Status.WAITING) {
/* 585 */       return false;
/*     */     }
/* 587 */     State<T> newState = new FailedState<>(exception);
/* 588 */     while (!compareAndSetState(oldState, newState)) {
/* 589 */       oldState = getState();
/* 590 */       if (oldState.getStatus() != IoFuture.Status.WAITING) {
/* 591 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 595 */     oldState.notifyFailed(this, exception);
/* 596 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean setResult(T result) {
/* 607 */     State<T> oldState = getState();
/* 608 */     if (oldState.getStatus() != IoFuture.Status.WAITING) {
/* 609 */       return false;
/*     */     }
/* 611 */     State<T> newState = new CompleteState<>(result);
/* 612 */     while (!compareAndSetState(oldState, newState)) {
/* 613 */       oldState = getState();
/* 614 */       if (oldState.getStatus() != IoFuture.Status.WAITING) {
/* 615 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 619 */     oldState.notifyDone(this, result);
/* 620 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean setCancelled() {
/* 630 */     State<T> oldState = getState();
/* 631 */     if (oldState.getStatus() != IoFuture.Status.WAITING) {
/* 632 */       return false;
/*     */     }
/*     */     
/* 635 */     State<T> newState = (State)ST_CANCELLED;
/* 636 */     while (!compareAndSetState(oldState, newState)) {
/* 637 */       oldState = getState();
/* 638 */       if (oldState.getStatus() != IoFuture.Status.WAITING) {
/* 639 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 643 */     oldState.notifyCancelled(this);
/* 644 */     return true;
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
/*     */   public IoFuture<T> cancel() {
/*     */     while (true) {
/* 657 */       State<T> state = getState();
/* 658 */       if (state.getStatus() != IoFuture.Status.WAITING || state.cancelRequested()) return this; 
/* 659 */       if (compareAndSetState(state, new CancelRequestedState<>(state))) {
/* 660 */         state.cancel();
/* 661 */         return this;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void addCancelHandler(Cancellable cancellable) {
/*     */     State<T> oldState;
/*     */     State<T> newState;
/*     */     
/* 673 */     do { oldState = getState();
/* 674 */       if (oldState.getStatus() != IoFuture.Status.WAITING || oldState.cancelRequested()) {
/*     */         try {
/* 676 */           cancellable.cancel();
/* 677 */         } catch (Throwable throwable) {}
/*     */         
/*     */         return;
/*     */       } 
/* 681 */       newState = oldState.withCancelHandler(cancellable);
/* 682 */       if (oldState == newState)
/* 683 */         return;  } while (!compareAndSetState(oldState, newState));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void runNotifier(Runnable runnable) {
/* 693 */     getNotifierExecutor().execute(runnable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Executor getNotifierExecutor() {
/* 703 */     return IoUtils.directExecutor();
/*     */   }
/*     */   
/*     */   static class NotifierRunnable<T, A>
/*     */     implements Runnable {
/*     */     private final IoFuture.Notifier<? super T, A> notifier;
/*     */     private final IoFuture<T> future;
/*     */     private final A attachment;
/*     */     
/*     */     NotifierRunnable(IoFuture.Notifier<? super T, A> notifier, IoFuture<T> future, A attachment) {
/* 713 */       this.notifier = notifier;
/* 714 */       this.future = future;
/* 715 */       this.attachment = attachment;
/*     */     }
/*     */     
/*     */     public void run() {
/*     */       try {
/* 720 */         this.notifier.notify(this.future, this.attachment);
/* 721 */       } catch (Throwable t) {
/* 722 */         Messages.futureMsg.notifierFailed(t, this.notifier, this.attachment);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\AbstractIoFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */