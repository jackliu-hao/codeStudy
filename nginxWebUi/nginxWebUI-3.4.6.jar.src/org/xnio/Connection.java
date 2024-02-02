/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.channels.CloseableChannel;
/*     */ import org.xnio.channels.ConnectedChannel;
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
/*     */ 
/*     */ public abstract class Connection
/*     */   implements CloseableChannel, ConnectedChannel
/*     */ {
/*     */   protected final XnioIoThread thread;
/*     */   private volatile int state;
/*     */   private static final int FLAG_READ_CLOSED = 1;
/*     */   private static final int FLAG_WRITE_CLOSED = 2;
/*  45 */   private static final AtomicIntegerFieldUpdater<Connection> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(Connection.class, "state");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Connection(XnioIoThread thread) {
/*  53 */     this.thread = thread;
/*     */   }
/*     */   
/*     */   private static <A extends SocketAddress> A castAddress(Class<A> type, SocketAddress address) {
/*  57 */     return type.isInstance(address) ? type.cast(address) : null;
/*     */   }
/*     */   
/*     */   public final <A extends SocketAddress> A getPeerAddress(Class<A> type) {
/*  61 */     return castAddress(type, getPeerAddress());
/*     */   }
/*     */   
/*     */   public final <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/*  65 */     return castAddress(type, getLocalAddress());
/*     */   }
/*     */   
/*     */   public final XnioWorker getWorker() {
/*  69 */     return this.thread.getWorker();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/*  73 */     return this.thread;
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
/*     */   protected boolean readClosed() {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield state : I
/*     */     //   4: istore_1
/*     */     //   5: iload_1
/*     */     //   6: iconst_1
/*     */     //   7: invokestatic allAreSet : (II)Z
/*     */     //   10: ifeq -> 15
/*     */     //   13: iconst_0
/*     */     //   14: ireturn
/*     */     //   15: iload_1
/*     */     //   16: iconst_1
/*     */     //   17: ior
/*     */     //   18: istore_2
/*     */     //   19: getstatic org/xnio/Connection.stateUpdater : Ljava/util/concurrent/atomic/AtomicIntegerFieldUpdater;
/*     */     //   22: aload_0
/*     */     //   23: iload_1
/*     */     //   24: iload_2
/*     */     //   25: invokevirtual compareAndSet : (Ljava/lang/Object;II)Z
/*     */     //   28: ifeq -> 0
/*     */     //   31: iload_2
/*     */     //   32: iconst_3
/*     */     //   33: invokestatic allAreSet : (II)Z
/*     */     //   36: ifeq -> 51
/*     */     //   39: aload_0
/*     */     //   40: invokevirtual closeAction : ()V
/*     */     //   43: goto -> 47
/*     */     //   46: astore_3
/*     */     //   47: aload_0
/*     */     //   48: invokevirtual invokeCloseListener : ()V
/*     */     //   51: iconst_1
/*     */     //   52: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #84	-> 0
/*     */     //   #85	-> 5
/*     */     //   #86	-> 13
/*     */     //   #88	-> 15
/*     */     //   #89	-> 19
/*     */     //   #90	-> 31
/*     */     //   #92	-> 39
/*     */     //   #93	-> 43
/*     */     //   #94	-> 47
/*     */     //   #96	-> 51
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	53	0	this	Lorg/xnio/Connection;
/*     */     //   5	48	1	oldVal	I
/*     */     //   19	34	2	newVal	I
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   39	43	46	java/lang/Throwable
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
/*     */   protected boolean writeClosed() {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield state : I
/*     */     //   4: istore_1
/*     */     //   5: iload_1
/*     */     //   6: iconst_2
/*     */     //   7: invokestatic allAreSet : (II)Z
/*     */     //   10: ifeq -> 15
/*     */     //   13: iconst_0
/*     */     //   14: ireturn
/*     */     //   15: iload_1
/*     */     //   16: iconst_2
/*     */     //   17: ior
/*     */     //   18: istore_2
/*     */     //   19: getstatic org/xnio/Connection.stateUpdater : Ljava/util/concurrent/atomic/AtomicIntegerFieldUpdater;
/*     */     //   22: aload_0
/*     */     //   23: iload_1
/*     */     //   24: iload_2
/*     */     //   25: invokevirtual compareAndSet : (Ljava/lang/Object;II)Z
/*     */     //   28: ifeq -> 0
/*     */     //   31: iload_2
/*     */     //   32: iconst_3
/*     */     //   33: invokestatic allAreSet : (II)Z
/*     */     //   36: ifeq -> 51
/*     */     //   39: aload_0
/*     */     //   40: invokevirtual closeAction : ()V
/*     */     //   43: goto -> 47
/*     */     //   46: astore_3
/*     */     //   47: aload_0
/*     */     //   48: invokevirtual invokeCloseListener : ()V
/*     */     //   51: iconst_1
/*     */     //   52: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #107	-> 0
/*     */     //   #108	-> 5
/*     */     //   #109	-> 13
/*     */     //   #111	-> 15
/*     */     //   #112	-> 19
/*     */     //   #113	-> 31
/*     */     //   #115	-> 39
/*     */     //   #116	-> 43
/*     */     //   #117	-> 47
/*     */     //   #119	-> 51
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	53	0	this	Lorg/xnio/Connection;
/*     */     //   5	48	1	oldVal	I
/*     */     //   19	34	2	newVal	I
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   39	43	46	java/lang/Throwable
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
/*     */   public final void close() throws IOException {
/*     */     int oldVal;
/*     */     int newVal;
/*     */     do {
/* 125 */       oldVal = this.state;
/* 126 */       if (Bits.allAreSet(oldVal, 3)) {
/*     */         return;
/*     */       }
/* 129 */       newVal = oldVal | 0x1 | 0x2;
/* 130 */     } while (!stateUpdater.compareAndSet(this, oldVal, newVal));
/*     */     try {
/* 132 */       closeAction();
/*     */     } finally {
/* 134 */       if (Bits.allAreClear(oldVal, 2)) {
/* 135 */         try { notifyWriteClosed(); }
/* 136 */         catch (Throwable throwable) {}
/*     */       }
/* 138 */       if (Bits.allAreClear(oldVal, 1)) {
/* 139 */         try { notifyReadClosed(); }
/* 140 */         catch (Throwable throwable) {}
/*     */       }
/* 142 */       invokeCloseListener();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReadShutdown() {
/* 152 */     return Bits.allAreSet(this.state, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWriteShutdown() {
/* 161 */     return Bits.allAreSet(this.state, 2);
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 165 */     return Bits.anyAreClear(this.state, 3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void notifyWriteClosed();
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void notifyReadClosed();
/*     */ 
/*     */ 
/*     */   
/*     */   abstract void invokeCloseListener();
/*     */ 
/*     */ 
/*     */   
/*     */   protected void closeAction() throws IOException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 188 */     return false;
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 192 */     return null;
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 196 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Connection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */