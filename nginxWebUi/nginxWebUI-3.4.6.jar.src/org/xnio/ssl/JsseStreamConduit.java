/*      */ package org.xnio.ssl;
/*      */ 
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.InterruptedIOException;
/*      */ import java.nio.Buffer;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.ReadableByteChannel;
/*      */ import java.nio.channels.WritableByteChannel;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import javax.net.ssl.SSLEngine;
/*      */ import javax.net.ssl.SSLEngineResult;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import org.xnio.Bits;
/*      */ import org.xnio.Buffers;
/*      */ import org.xnio.Pool;
/*      */ import org.xnio.Pooled;
/*      */ import org.xnio.XnioIoThread;
/*      */ import org.xnio.XnioWorker;
/*      */ import org.xnio._private.Messages;
/*      */ import org.xnio.channels.StreamSinkChannel;
/*      */ import org.xnio.channels.StreamSourceChannel;
/*      */ import org.xnio.conduits.ConduitReadableByteChannel;
/*      */ import org.xnio.conduits.ConduitWritableByteChannel;
/*      */ import org.xnio.conduits.Conduits;
/*      */ import org.xnio.conduits.ReadReadyHandler;
/*      */ import org.xnio.conduits.StreamSinkConduit;
/*      */ import org.xnio.conduits.StreamSourceConduit;
/*      */ import org.xnio.conduits.WriteReadyHandler;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ final class JsseStreamConduit
/*      */   implements StreamSourceConduit, StreamSinkConduit, Runnable
/*      */ {
/*   62 */   private static final boolean TRACE_SSL = Boolean.getBoolean("org.xnio.ssl.TRACE_SSL");
/*      */   private final JsseSslConnection connection;
/*      */   private final SSLEngine engine;
/*      */   private final StreamSourceConduit sourceConduit;
/*      */   private final StreamSinkConduit sinkConduit;
/*      */   private final Pooled<ByteBuffer> receiveBuffer;
/*      */   private final Pooled<ByteBuffer> sendBuffer;
/*      */   private final Pooled<ByteBuffer> readBuffer;
/*      */   private int state;
/*      */   private int tasks; private ReadReadyHandler readReadyHandler; private WriteReadyHandler writeReadyHandler; private static final int FLAG_TLS = 131072; private static final int FLAG_INLINE_TASKS = 262144; private static final int FLAG_TASK_QUEUED = 524288; private static final int FLAG_NEED_ENGINE_TASK = 1048576; private static final int FLAG_FLUSH_NEEDED = 2097152; private static final int READ_FLAG_SHUTDOWN = 1; private static final int READ_FLAG_EOF = 2; private static final int READ_FLAG_RESUMED = 4; private static final int READ_FLAG_UP_RESUMED = 8; private static final int READ_FLAG_WAKEUP = 16; private static final int READ_FLAG_READY = 32; private static final int READ_FLAG_NEEDS_WRITE = 64; private static final int WRITE_FLAG_SHUTDOWN = 256; private static final int WRITE_FLAG_SHUTDOWN2 = 512; private static final int WRITE_FLAG_SHUTDOWN3 = 1024; private static final int WRITE_FLAG_FINISHED = 2048; private static final int WRITE_FLAG_RESUMED = 4096; private static final int WRITE_FLAG_UP_RESUMED = 8192; private static final int WRITE_FLAG_WAKEUP = 16384; private static final int WRITE_FLAG_READY = 32768; private static final int WRITE_FLAG_NEEDS_READ = 65536; private final WriteReadyHandler writeReady; private final ReadReadyHandler readReady; private final ByteBuffer[] readBufferHolder; private final ByteBuffer[] writeBufferHolder; public String getStatus() { StringBuilder b = new StringBuilder(); b.append("General flags:"); int state = this.state; if (Bits.allAreSet(state, 131072)) b.append(" TLS");  if (Bits.allAreSet(state, 262144)) b.append(" INLINE_TASKS");  if (Bits.allAreSet(state, 524288)) b.append(" TASK_QUEUED");  if (Bits.allAreSet(state, 1048576)) b.append(" NEED_ENGINE_TASK");  if (Bits.allAreSet(state, 2097152)) b.append(" FLUSH_NEEDED");  b.append("\nRead flags:"); if (Bits.allAreSet(state, 1)) b.append(" SHUTDOWN");  if (Bits.allAreSet(state, 2))
/*      */       b.append(" EOF");  if (Bits.allAreSet(state, 4))
/*      */       b.append(" RESUMED");  if (Bits.allAreSet(state, 8))
/*      */       b.append(" UP_RESUMED");  if (Bits.allAreSet(state, 16))
/*      */       b.append(" WAKEUP");  if (Bits.allAreSet(state, 32))
/*      */       b.append(" READY");  if (Bits.allAreSet(state, 64))
/*      */       b.append(" NEEDS_WRITE");  b.append("\nWrite flags:"); if (Bits.allAreSet(state, 256))
/*      */       b.append(" SHUTDOWN");  if (Bits.allAreSet(state, 512))
/*      */       b.append(" SHUTDOWN2");  if (Bits.allAreSet(state, 1024))
/*      */       b.append(" SHUTDOWN3");  if (Bits.allAreSet(state, 2048))
/*      */       b.append(" FINISHED");  if (Bits.allAreSet(state, 4096))
/*      */       b.append(" RESUMED");  if (Bits.allAreSet(state, 8192))
/*      */       b.append(" UP_RESUMED");  if (Bits.allAreSet(state, 16384))
/*      */       b.append(" WAKEUP");  if (Bits.allAreSet(state, 32768))
/*      */       b.append(" READY");  if (Bits.allAreSet(state, 65536))
/*      */       b.append(" NEEDS_READ");  b.append('\n'); return b.toString(); } public String toString() { return String.format("JSSE Stream Conduit for %s, status:%n%s", new Object[] { this.connection, getStatus() }); } public XnioWorker getWorker() { return this.connection.getIoThread().getWorker(); } public XnioIoThread getReadThread() { return this.connection.getIoThread(); } public XnioIoThread getWriteThread() { return this.connection.getIoThread(); } void beginHandshake() throws IOException { int state = this.state; if (Bits.anyAreSet(state, 258))
/*      */       throw new ClosedChannelException();  if (Bits.allAreClear(state, 131072))
/*   88 */       this.state = state | 0x20000;  this.engine.beginHandshake(); } SSLSession getSslSession() { return Bits.allAreSet(this.state, 131072) ? this.engine.getSession() : null; } SSLEngine getEngine() { return this.engine; } boolean isTls() { return Bits.allAreSet(this.state, 131072); } JsseStreamConduit(JsseSslConnection connection, SSLEngine engine, StreamSourceConduit sourceConduit, StreamSinkConduit sinkConduit, Pool<ByteBuffer> socketBufferPool, Pool<ByteBuffer> applicationBufferPool) { Pooled<ByteBuffer> sendBuffer, readBuffer; this.state = 262144;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  259 */     this.writeReady = new WriteReadyHandler()
/*      */       {
/*      */         public void forceTermination()
/*      */         {
/*  263 */           if (Bits.anyAreClear(JsseStreamConduit.this.state, 2048)) {
/*  264 */             JsseStreamConduit.this.state = JsseStreamConduit.this.state | 0xF00;
/*      */           }
/*  266 */           WriteReadyHandler writeReadyHandler = JsseStreamConduit.this.writeReadyHandler;
/*  267 */           if (writeReadyHandler != null) {
/*  268 */             try { writeReadyHandler.forceTermination(); }
/*  269 */             catch (Throwable throwable) {}
/*      */           }
/*      */         }
/*      */ 
/*      */         
/*      */         public void terminated() {
/*  275 */           if (Bits.anyAreClear(JsseStreamConduit.this.state, 2048)) {
/*  276 */             JsseStreamConduit.this.state = JsseStreamConduit.this.state | 0xF00;
/*      */           }
/*  278 */           WriteReadyHandler writeReadyHandler = JsseStreamConduit.this.writeReadyHandler;
/*  279 */           if (writeReadyHandler != null) {
/*  280 */             try { writeReadyHandler.terminated(); }
/*  281 */             catch (Throwable throwable) {}
/*      */           }
/*      */         }
/*      */ 
/*      */         
/*      */         public void writeReady() {
/*  287 */           JsseStreamConduit.this.writeReady();
/*      */         }
/*      */       };
/*      */     
/*  291 */     this.readReady = new ReadReadyHandler()
/*      */       {
/*      */         public void forceTermination()
/*      */         {
/*  295 */           if (Bits.anyAreClear(JsseStreamConduit.this.state, 1)) {
/*  296 */             JsseStreamConduit.this.state = JsseStreamConduit.this.state | 0x1;
/*      */           }
/*  298 */           ReadReadyHandler readReadyHandler = JsseStreamConduit.this.readReadyHandler;
/*  299 */           if (readReadyHandler != null) {
/*  300 */             try { readReadyHandler.forceTermination(); }
/*  301 */             catch (Throwable throwable) {}
/*      */           }
/*      */         }
/*      */ 
/*      */         
/*      */         public void terminated() {
/*  307 */           if (Bits.anyAreClear(JsseStreamConduit.this.state, 1)) {
/*  308 */             JsseStreamConduit.this.state = JsseStreamConduit.this.state | 0x1;
/*      */           }
/*  310 */           ReadReadyHandler readReadyHandler = JsseStreamConduit.this.readReadyHandler;
/*  311 */           if (readReadyHandler != null) {
/*  312 */             try { readReadyHandler.terminated(); }
/*  313 */             catch (Throwable throwable) {}
/*      */           }
/*      */         }
/*      */ 
/*      */         
/*      */         public void readReady() {
/*  319 */           JsseStreamConduit.this.readReady();
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  933 */     this.readBufferHolder = new ByteBuffer[1];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1031 */     this.writeBufferHolder = new ByteBuffer[1]; boolean ok = false; SSLSession session = engine.getSession(); int packetBufferSize = session.getPacketBufferSize(); Pooled<ByteBuffer> receiveBuffer = socketBufferPool.allocate(); try { ((ByteBuffer)receiveBuffer.getResource()).flip(); sendBuffer = socketBufferPool.allocate(); try { if (((ByteBuffer)receiveBuffer.getResource()).capacity() < packetBufferSize || ((ByteBuffer)sendBuffer.getResource()).capacity() < packetBufferSize) throw Messages.msg.socketBufferTooSmall();  int applicationBufferSize = session.getApplicationBufferSize(); readBuffer = applicationBufferPool.allocate(); try { if (((ByteBuffer)readBuffer.getResource()).capacity() < applicationBufferSize) throw Messages.msg.appBufferTooSmall();  ok = true; } finally { if (!ok) readBuffer.free();  }  } finally { if (!ok) sendBuffer.free();  }  } finally { if (!ok) receiveBuffer.free();  }  this.receiveBuffer = receiveBuffer; this.sendBuffer = sendBuffer; this.readBuffer = readBuffer; ((ByteBuffer)receiveBuffer.getResource()).clear().limit(0); if (sourceConduit.getReadThread() != sinkConduit.getWriteThread()) throw new IllegalArgumentException("Source and sink thread mismatch");  this.connection = connection; this.engine = engine; this.sourceConduit = sourceConduit; this.sinkConduit = sinkConduit; sourceConduit.setReadReadyHandler(this.readReady); sinkConduit.setWriteReadyHandler(this.writeReady); }
/*      */   boolean markTerminated() { this.readBuffer.free(); this.receiveBuffer.free(); this.sendBuffer.free(); if (Bits.anyAreClear(this.state, 2049)) { this.state |= 0xF01; return true; }  return false; }
/*      */   public void run() { assert Thread.currentThread() == getWriteThread(); int state = this.state; boolean flagTaskQueued = Bits.allAreSet(state, 524288); boolean modify = flagTaskQueued; boolean queueTask = false; state &= 0xFFF7FFFF; try { if (Bits.allAreSet(state, 1048576)) throw new UnsupportedOperationException();  if (Bits.anyAreSet(state, 16384) || Bits.allAreSet(state, 36864)) { WriteReadyHandler writeReadyHandler = this.writeReadyHandler; if (Bits.allAreSet(state, 16384)) { state = state & 0xFFFFBFFF | 0x1000; modify = true; }  if (writeReadyHandler != null) { if (Bits.allAreSet(state, 4096)) { try { if (modify) { modify = false; this.state = state; }  writeReadyHandler.writeReady(); } catch (Throwable throwable) {  } finally { state = this.state & 0xFFF7FFFF; modify = true; }  if (Bits.allAreSet(state, 4096)) if (!Bits.allAreSet(state, 32768) && Bits.allAreSet(state, 65536) && Bits.allAreClear(state, 8)) { state |= 0x8; modify = true; this.sourceConduit.resumeReads(); } else if (Bits.allAreClear(state, 8192)) { this.sinkConduit.resumeWrites(); }   } else if (Bits.allAreClear(state, 68) && Bits.allAreSet(state, 8192)) { state &= 0xFFFFDFFF; modify = true; suspendWrites(); }  } else { state &= 0xFFFFEFFF; modify = true; if (Bits.allAreClear(state, 68) && Bits.allAreSet(state, 8192)) { state &= 0xFFFFDFFF; modify = true; suspendWrites(); }  }  }  if (Bits.anyAreSet(state, 16) || Bits.allAreSet(state, 36)) { ReadReadyHandler readReadyHandler = this.readReadyHandler; if (Bits.allAreSet(state, 16)) { state = state & 0xFFFFFFEF | 0x4; modify = true; }  if (readReadyHandler != null) { if (Bits.allAreSet(state, 4)) { try { if (modify) { modify = false; this.state = state; }  readReadyHandler.readReady(); } catch (Throwable throwable) {  } finally { state = this.state & 0xFFF7FFFF; modify = true; }  if (Bits.allAreSet(state, 4)) if (Bits.allAreSet(state, 32)) { if (!flagTaskQueued) { state |= 0x80000; modify = queueTask = true; }  } else if (Bits.allAreSet(state, 64) && Bits.allAreClear(state, 8192)) { state |= 0x2000; modify = true; this.sinkConduit.resumeWrites(); } else if (Bits.allAreClear(state, 8)) { this.sourceConduit.resumeReads(); }   } else if (Bits.allAreClear(state, 69632) && Bits.allAreSet(state, 8)) { state &= 0xFFFFFFF7; modify = true; suspendReads(); }  } else { state &= 0xFFFFFFFB; modify = true; if (Bits.allAreClear(state, 69632) && Bits.allAreSet(state, 8)) { state &= 0xFFFFFFF7; suspendReads(); }  }  }  } finally { if (modify) { this.state = state; if (queueTask) getReadThread().execute(this);  }  }  }
/* 1034 */   public void setWriteReadyHandler(WriteReadyHandler handler) { this.writeReadyHandler = handler; } public void setReadReadyHandler(ReadReadyHandler handler) { this.readReadyHandler = handler; } public void writeReady() { int state = this.state; state |= 0x8000; if (Bits.allAreSet(state, 64)) state |= 0x20;  this.state = state; if (Bits.allAreClear(state, 524288)) run();  state = this.state; if (this.sinkConduit.isWriteResumed() && Bits.allAreClear(state, 4160)) this.sinkConduit.suspendWrites();  if (this.sourceConduit.isReadResumed() && Bits.allAreClear(state, 65540)) this.sourceConduit.suspendReads();  } public void readReady() { int state = this.state; state |= 0x20; if (Bits.allAreSet(state, 65536)) state |= 0x8000;  this.state = state; if (Bits.allAreClear(state, 524288)) run();  state = this.state; if (this.sourceConduit.isReadResumed() && Bits.allAreClear(state, 65540)) this.sourceConduit.suspendReads();  if (this.sinkConduit.isWriteResumed() && Bits.allAreClear(state, 4160)) this.sinkConduit.suspendWrites();  } public void suspendWrites() { int state = this.state; try { if (Bits.allAreSet(state, 4096)) { state &= 0xFFFFEFFF; if (Bits.allAreSet(state, 8192) && Bits.allAreClear(state, 64)) { state &= 0xFFFFDFFF; this.sinkConduit.suspendWrites(); }  if (Bits.allAreSet(state, 8) && Bits.allAreClear(state, 4)) { state &= 0xFFFFFFF7; this.sourceConduit.suspendReads(); }  }  } finally { this.state = state; }  } public void resumeWrites() { int state = this.state; if (Bits.allAreClear(state, 4096)) { if (Bits.allAreSet(state, 2048)) { wakeupWrites(); return; }  boolean queueTask = false; try { state |= 0x1000; if (Bits.allAreSet(state, 32768)) { if (queueTask = Bits.allAreClear(state, 524288)) state |= 0x80000;  } else if (Bits.allAreSet(state, 65536) && Bits.allAreClear(state, 8)) { state |= 0x8; this.sourceConduit.resumeReads(); } else if (Bits.allAreClear(state, 8192)) { state |= 0x2000; this.sinkConduit.resumeWrites(); }  } finally { this.state = state; if (queueTask) getReadThread().execute(this);  }  }  } public void wakeupWrites() { int state = this.state; if (Bits.allAreClear(state, 16384)) if (Bits.allAreClear(state, 524288)) { this.state = state | 0x4000 | 0x80000; getReadThread().execute(this); } else { this.state = state | 0x4000; }   } public int write(ByteBuffer src) throws IOException { if (Bits.allAreSet(this.state, 256)) {
/* 1035 */       throw new ClosedChannelException();
/*      */     }
/* 1037 */     if (Bits.allAreClear(this.state, 131072)) {
/* 1038 */       return this.sinkConduit.write(src);
/*      */     }
/* 1040 */     ByteBuffer[] writeBufferHolder = this.writeBufferHolder;
/* 1041 */     writeBufferHolder[0] = src;
/*      */     
/* 1043 */     try { return (int)write(writeBufferHolder, 0, 1); }
/*      */     finally
/* 1045 */     { writeBufferHolder[0] = null; }  } public void terminateWrites() throws IOException { int state = this.state; if (Bits.allAreClear(state, 2048)) { this.state = state | 0x100; if (Bits.allAreSet(state, 131072)) { try { if (this.engine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) this.engine.closeOutbound();  performIO(2, NO_BUFFERS, 0, 0, NO_BUFFERS, 0, 0); if (Bits.allAreSet(this.state, 2048)) this.sinkConduit.terminateWrites();  } catch (Throwable t) { this.state |= 0x800; try { this.sinkConduit.truncateWrites(); } catch (Throwable t2) { t.addSuppressed(t2); }  throw t; }  } else { this.sinkConduit.terminateWrites(); }  }  } public void truncateWrites() throws IOException { int state = this.state; if (Bits.allAreClear(state, 256)) if (Bits.allAreSet(state, 131072)) { try { state |= 0xD00; try { this.engine.closeOutbound(); } catch (Throwable t) { try { this.sinkConduit.truncateWrites(); } catch (Throwable t2) { t.addSuppressed(t2); }  throw t; }  this.sinkConduit.truncateWrites(); } finally { this.state = state; }  } else { this.state = state | 0x100 | 0x400 | 0x800; this.sinkConduit.truncateWrites(); }   } public boolean isWriteResumed() { return Bits.anyAreSet(this.state, 20480); } public boolean isWriteShutdown() { return Bits.allAreSet(this.state, 256); } public void awaitWritable() throws IOException { int state = this.state; while (Bits.allAreSet(state, 1048576)) { synchronized (this) { while (this.tasks != 0) { try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new InterruptedIOException(); }  }  state &= 0xFFEFFFFF; this.state = state; }  }  if (Bits.allAreClear(state, 32768)) if (Bits.allAreSet(state, 65536)) { this.sourceConduit.awaitReadable(); } else { this.sinkConduit.awaitWritable(); }   } public void awaitWritable(long time, TimeUnit timeUnit) throws IOException { int state = this.state; long nanos = timeUnit.toNanos(time); while (Bits.allAreSet(state, 1048576)) { synchronized (this) { long start = System.nanoTime(); if (this.tasks != 0) try { if (nanos <= 0L) return;  wait(nanos / 1000000L, (int)(nanos % 1000000L)); nanos -= -start + (start = System.nanoTime()); } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new InterruptedIOException(); }   state &= 0xFFEFFFFF; this.state = state; }  }  if (Bits.allAreClear(state, 32768)) if (Bits.allAreSet(state, 65536)) { this.sourceConduit.awaitReadable(nanos, TimeUnit.NANOSECONDS); } else { this.sinkConduit.awaitWritable(nanos, TimeUnit.NANOSECONDS); }   } public void suspendReads() { int state = this.state; try { if (Bits.allAreSet(state, 4)) { state &= 0xFFFFFFFB; if (Bits.allAreSet(state, 8) && Bits.allAreClear(state, 65536)) { state &= 0xFFFFFFF7; this.sourceConduit.suspendReads(); }  if (Bits.allAreSet(state, 8192) && Bits.allAreClear(state, 4096)) { state &= 0xFFFFDFFF; this.sinkConduit.suspendWrites(); }  }  } finally { this.state = state; }  } public void resumeReads() { int state = this.state; boolean queueTask = false; if (Bits.allAreClear(state, 4)) try { state |= 0x4; if (Bits.allAreClear(state, 4096)) state |= 0x20;  if (Bits.allAreSet(state, 32)) { if (queueTask = Bits.allAreClear(state, 524288)) state |= 0x80000;  } else if (Bits.allAreSet(state, 64) && Bits.allAreClear(state, 8192)) { state |= 0x2000; this.sinkConduit.resumeWrites(); } else if (Bits.allAreClear(state, 8)) { state |= 0x8; this.sourceConduit.resumeReads(); }  } finally { this.state = state; if (queueTask) getReadThread().execute(this);  }   } public void wakeupReads() { int state = this.state; if (Bits.allAreClear(state, 16)) if (Bits.allAreClear(state, 524288)) { this.state = state | 0x10 | 0x80000; getReadThread().execute(this); } else { this.state = state | 0x10; }   } public void terminateReads() throws IOException { int state = this.state; if (Bits.allAreClear(state, 1)) if (Bits.allAreClear(state, 131072)) { this.sourceConduit.terminateReads(); } else { this.state = state | 0x1; if (Bits.allAreClear(state, 2)) { performIO(2, NO_BUFFERS, 0, 0, NO_BUFFERS, 0, 0); if (Bits.allAreSet(state, 65536) && Bits.allAreClear(state, 2)) return;  if (!this.engine.isInboundDone() && this.engine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) this.engine.closeInbound();  long res = performIO(0, NO_BUFFERS, 0, 0, NO_BUFFERS, 0, 0); if (res == -1L) this.state |= 0x2;  }  if (Bits.allAreClear(this.state, 2) || ((ByteBuffer)this.receiveBuffer.getResource()).hasRemaining()) { EOFException exception = Messages.msg.connectionClosedEarly(); try { this.sourceConduit.terminateReads(); } catch (IOException e) { exception.addSuppressed(e); }  throw exception; }  this.sourceConduit.terminateReads(); }   } public boolean isReadResumed() { return Bits.anyAreSet(this.state, 20); } public boolean isReadShutdown() { return Bits.allAreSet(this.state, 1); } public void awaitReadable() throws IOException { int state = this.state; while (Bits.allAreSet(state, 1048576)) { synchronized (this) { while (this.tasks != 0) { try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new InterruptedIOException(); }  }  state &= 0xFFEFFFFF; this.state = state; }  }  if (Bits.allAreClear(state, 32)) if (Bits.allAreSet(state, 64)) { this.sinkConduit.awaitWritable(); } else { this.sourceConduit.awaitReadable(); }   }
/*      */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException { int state = this.state; long nanos = timeUnit.toNanos(time); while (Bits.allAreSet(state, 1048576)) { synchronized (this) { long start = System.nanoTime(); if (this.tasks != 0) try { if (nanos <= 0L) return;  wait(nanos / 1000000L, (int)(nanos % 1000000L)); nanos -= -start + (start = System.nanoTime()); } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new InterruptedIOException(); }   state &= 0xFFEFFFFF; this.state = state; }  }  if (Bits.allAreClear(state, 32)) if (Bits.allAreSet(state, 64)) { this.sinkConduit.awaitWritable(nanos, TimeUnit.NANOSECONDS); } else { this.sourceConduit.awaitReadable(nanos, TimeUnit.NANOSECONDS); }   }
/*      */   public int read(ByteBuffer dst) throws IOException { int state = this.state; if (Bits.anyAreSet(state, 1)) return -1;  if (Bits.anyAreSet(state, 2)) { if (((ByteBuffer)this.readBuffer.getResource()).position() > 0) { ByteBuffer readBufferResource = (ByteBuffer)this.readBuffer.getResource(); readBufferResource.flip(); try { if (TRACE_SSL) Messages.msg.tracef("TLS copy unwrapped data from %s to %s", Buffers.debugString(readBufferResource), Buffers.debugString(dst));  return Buffers.copy(dst, readBufferResource); } finally { readBufferResource.compact(); }  }  return -1; }  if (Bits.allAreClear(state, 131072)) { int res = this.sourceConduit.read(dst); if (res == 0) { if (Bits.allAreSet(state, 32)) this.state = state & 0xFFFFFFDF;  } else if (res == -1) { this.state = (state | 0x2) & 0xFFFFFFDF; }  return res; }  ByteBuffer[] readBufferHolder = this.readBufferHolder; readBufferHolder[0] = dst; try { return (int)performIO(0, NO_BUFFERS, 0, 0, readBufferHolder, 0, 1); } finally { readBufferHolder[0] = null; }  }
/*      */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException { int state = this.state; if (Bits.anyAreSet(state, 1)) return -1L;  if (Bits.anyAreSet(state, 2)) { if (((ByteBuffer)this.readBuffer.getResource()).position() > 0) { ByteBuffer readBufferResource = (ByteBuffer)this.readBuffer.getResource(); readBufferResource.flip(); try { if (TRACE_SSL) Messages.msg.tracef("TLS copy unwrapped data from %s to %s", Buffers.debugString(readBufferResource), Buffers.debugString(dsts, offs, len));  return Buffers.copy(dsts, offs, len, readBufferResource); } finally { readBufferResource.compact(); }  }  return -1L; }  if (Bits.allAreClear(state, 131072)) { long res = this.sourceConduit.read(dsts, offs, len); if (res == 0L) { if (Bits.allAreSet(state, 32)) this.state = state & 0xFFFFFFDF;  } else if (res == -1L) { this.state = (state | 0x2) & 0xFFFFFFDF; }  return res; }  return performIO(0, NO_BUFFERS, 0, 0, dsts, offs, len); }
/*      */   public long transferTo(long position, long count, FileChannel target) throws IOException { if (Bits.allAreClear(this.state, 131072)) return this.sourceConduit.transferTo(position, count, target);  return target.transferFrom((ReadableByteChannel)new ConduitReadableByteChannel(this), position, count); }
/*      */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException { if (Bits.allAreClear(this.state, 131072)) return this.sourceConduit.transferTo(count, throughBuffer, target);  return Conduits.transfer(this, count, throughBuffer, (WritableByteChannel)target); }
/* 1051 */   public int writeFinal(ByteBuffer src) throws IOException { if (Bits.allAreSet(this.state, 256)) {
/* 1052 */       throw new ClosedChannelException();
/*      */     }
/* 1054 */     if (Bits.allAreClear(this.state, 131072)) {
/* 1055 */       return this.sinkConduit.writeFinal(src);
/*      */     }
/* 1057 */     ByteBuffer[] writeBufferHolder = this.writeBufferHolder;
/* 1058 */     writeBufferHolder[0] = src;
/*      */     try {
/* 1060 */       return (int)writeFinal(writeBufferHolder, 0, 1);
/*      */     } finally {
/* 1062 */       writeBufferHolder[0] = null;
/*      */     }  }
/*      */ 
/*      */ 
/*      */   
/*      */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 1068 */     if (Bits.allAreSet(this.state, 256)) {
/* 1069 */       throw new ClosedChannelException();
/*      */     }
/* 1071 */     if (Bits.allAreClear(this.state, 131072)) {
/* 1072 */       return this.sinkConduit.write(srcs, offs, len);
/*      */     }
/* 1074 */     long r1 = Buffers.remaining((Buffer[])srcs, offs, len);
/* 1075 */     performIO(1, srcs, offs, len, NO_BUFFERS, 0, 0);
/* 1076 */     return r1 - Buffers.remaining((Buffer[])srcs, offs, len);
/*      */   }
/*      */ 
/*      */   
/*      */   public long writeFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 1081 */     if (Bits.allAreSet(this.state, 256)) {
/* 1082 */       throw new ClosedChannelException();
/*      */     }
/* 1084 */     if (Bits.allAreClear(this.state, 131072)) {
/* 1085 */       return this.sinkConduit.writeFinal(srcs, offs, len);
/*      */     }
/* 1087 */     long r1 = Buffers.remaining((Buffer[])srcs, offs, len);
/* 1088 */     performIO(3, srcs, offs, len, NO_BUFFERS, 0, 0);
/* 1089 */     return r1 - Buffers.remaining((Buffer[])srcs, offs, len);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean flush() throws IOException {
/* 1094 */     int state = this.state;
/* 1095 */     if (Bits.allAreSet(state, 2048))
/* 1096 */       return true; 
/* 1097 */     if (Bits.allAreSet(state, 1024)) {
/*      */       
/* 1099 */       if (this.sinkConduit.flush()) {
/* 1100 */         this.state = state | 0x800;
/* 1101 */         return true;
/*      */       } 
/* 1103 */       return false;
/*      */     } 
/* 1105 */     if (Bits.allAreClear(state, 131072)) {
/* 1106 */       boolean flushed = this.sinkConduit.flush();
/* 1107 */       if (Bits.allAreSet(state, 256) && flushed) {
/* 1108 */         this.state = state | 0x200 | 0x400 | 0x800;
/*      */       }
/* 1110 */       return flushed;
/* 1111 */     }  if (Bits.allAreSet(state, 256))
/*      */     {
/* 1113 */       return (performIO(2, NO_BUFFERS, 0, 0, NO_BUFFERS, 0, 0) != 0L);
/*      */     }
/*      */     
/* 1116 */     return (performIO(2, NO_BUFFERS, 0, 0, NO_BUFFERS, 0, 0) != 0L);
/*      */   }
/*      */ 
/*      */   
/*      */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 1121 */     if (Bits.allAreClear(this.state, 131072)) {
/* 1122 */       return this.sinkConduit.transferFrom(src, position, count);
/*      */     }
/* 1124 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel(this));
/*      */   }
/*      */ 
/*      */   
/*      */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 1129 */     if (Bits.allAreClear(this.state, 131072)) {
/* 1130 */       return this.sinkConduit.transferFrom(source, count, throughBuffer);
/*      */     }
/* 1132 */     return Conduits.transfer((ReadableByteChannel)source, count, throughBuffer, this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1142 */   private static final ByteBuffer[] NO_BUFFERS = new ByteBuffer[0];
/*      */   
/*      */   private static final int IO_GOAL_READ = 0;
/*      */   private static final int IO_GOAL_WRITE = 1;
/*      */   private static final int IO_GOAL_FLUSH = 2;
/*      */   private static final int IO_GOAL_WRITE_FINAL = 3;
/*      */   
/*      */   private static long actualIOResult(long xfer, int goal, boolean flushed, boolean eof) {
/* 1150 */     long result = (goal == 2 && flushed) ? 1L : ((goal == 0 && eof && xfer == 0L) ? -1L : xfer);
/* 1151 */     if (TRACE_SSL) Messages.msg.tracef("returned TLS result %d", result); 
/* 1152 */     return result;
/*      */   }
/*      */   
/*      */   private static String decodeGoal(int goal) {
/* 1156 */     switch (goal) { case 0:
/* 1157 */         return "READ";
/* 1158 */       case 1: return "WRITE";
/* 1159 */       case 2: return "FLUSH";
/* 1160 */       case 3: return "WRITE_FINAL"; }
/* 1161 */      return "UNKNOWN(" + goal + ")";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long performIO(int goal, ByteBuffer[] srcs, int srcOff, int srcLen, ByteBuffer[] dsts, int dstOff, int dstLen) throws IOException {
/*      */     // Byte code:
/*      */     //   0: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   3: ifeq -> 37
/*      */     //   6: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   9: ldc_w 'performing TLS I/O operation, goal %s, src: %s, dst: %s'
/*      */     //   12: iload_1
/*      */     //   13: invokestatic decodeGoal : (I)Ljava/lang/String;
/*      */     //   16: aload_2
/*      */     //   17: iload_3
/*      */     //   18: iload #4
/*      */     //   20: invokestatic debugString : ([Ljava/nio/ByteBuffer;II)Ljava/lang/String;
/*      */     //   23: aload #5
/*      */     //   25: iload #6
/*      */     //   27: iload #7
/*      */     //   29: invokestatic debugString : ([Ljava/nio/ByteBuffer;II)Ljava/lang/String;
/*      */     //   32: invokeinterface tracef : (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
/*      */     //   37: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   40: ifne -> 66
/*      */     //   43: aload_2
/*      */     //   44: getstatic org/xnio/ssl/JsseStreamConduit.NO_BUFFERS : [Ljava/nio/ByteBuffer;
/*      */     //   47: if_acmpeq -> 66
/*      */     //   50: aload #5
/*      */     //   52: getstatic org/xnio/ssl/JsseStreamConduit.NO_BUFFERS : [Ljava/nio/ByteBuffer;
/*      */     //   55: if_acmpeq -> 66
/*      */     //   58: new java/lang/AssertionError
/*      */     //   61: dup
/*      */     //   62: invokespecial <init> : ()V
/*      */     //   65: athrow
/*      */     //   66: aload_0
/*      */     //   67: getfield state : I
/*      */     //   70: istore #8
/*      */     //   72: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   75: ifne -> 97
/*      */     //   78: iload #8
/*      */     //   80: ldc_w 65600
/*      */     //   83: invokestatic allAreSet : (II)Z
/*      */     //   86: ifeq -> 97
/*      */     //   89: new java/lang/AssertionError
/*      */     //   92: dup
/*      */     //   93: invokespecial <init> : ()V
/*      */     //   96: athrow
/*      */     //   97: iload #8
/*      */     //   99: ldc 1048576
/*      */     //   101: invokestatic allAreSet : (II)Z
/*      */     //   104: ifeq -> 109
/*      */     //   107: lconst_0
/*      */     //   108: lreturn
/*      */     //   109: aload_0
/*      */     //   110: getfield engine : Ljavax/net/ssl/SSLEngine;
/*      */     //   113: astore #9
/*      */     //   115: aload_0
/*      */     //   116: getfield sendBuffer : Lorg/xnio/Pooled;
/*      */     //   119: invokeinterface getResource : ()Ljava/lang/Object;
/*      */     //   124: checkcast java/nio/ByteBuffer
/*      */     //   127: astore #10
/*      */     //   129: aload_0
/*      */     //   130: getfield receiveBuffer : Lorg/xnio/Pooled;
/*      */     //   133: invokeinterface getResource : ()Ljava/lang/Object;
/*      */     //   138: checkcast java/nio/ByteBuffer
/*      */     //   141: astore #11
/*      */     //   143: aload_0
/*      */     //   144: getfield readBuffer : Lorg/xnio/Pooled;
/*      */     //   147: invokeinterface getResource : ()Ljava/lang/Object;
/*      */     //   152: checkcast java/nio/ByteBuffer
/*      */     //   155: astore #12
/*      */     //   157: aload #5
/*      */     //   159: iload #6
/*      */     //   161: iload #7
/*      */     //   163: iconst_1
/*      */     //   164: iadd
/*      */     //   165: invokestatic copyOfRange : ([Ljava/lang/Object;II)[Ljava/lang/Object;
/*      */     //   168: checkcast [Ljava/nio/ByteBuffer;
/*      */     //   171: astore #13
/*      */     //   173: aload #13
/*      */     //   175: iload #7
/*      */     //   177: aload #12
/*      */     //   179: aastore
/*      */     //   180: aload_2
/*      */     //   181: iload_3
/*      */     //   182: iload #4
/*      */     //   184: invokestatic remaining : ([Ljava/nio/Buffer;II)J
/*      */     //   187: aload #5
/*      */     //   189: iload #6
/*      */     //   191: iload #7
/*      */     //   193: invokestatic remaining : ([Ljava/nio/Buffer;II)J
/*      */     //   196: invokestatic max : (JJ)J
/*      */     //   199: lstore #14
/*      */     //   201: iload_1
/*      */     //   202: ifne -> 216
/*      */     //   205: iload #8
/*      */     //   207: ldc_w 2097216
/*      */     //   210: invokestatic anyAreSet : (II)Z
/*      */     //   213: goto -> 241
/*      */     //   216: iload #8
/*      */     //   218: ldc 2097152
/*      */     //   220: invokestatic allAreSet : (II)Z
/*      */     //   223: ifne -> 236
/*      */     //   226: iload #8
/*      */     //   228: ldc 65536
/*      */     //   230: invokestatic allAreClear : (II)Z
/*      */     //   233: ifeq -> 240
/*      */     //   236: iconst_1
/*      */     //   237: goto -> 241
/*      */     //   240: iconst_0
/*      */     //   241: istore #16
/*      */     //   243: iload #16
/*      */     //   245: ifne -> 252
/*      */     //   248: iconst_1
/*      */     //   249: goto -> 253
/*      */     //   252: iconst_0
/*      */     //   253: istore #17
/*      */     //   255: iconst_0
/*      */     //   256: istore #18
/*      */     //   258: iconst_0
/*      */     //   259: istore #19
/*      */     //   261: iconst_0
/*      */     //   262: istore #20
/*      */     //   264: iconst_0
/*      */     //   265: istore #21
/*      */     //   267: iconst_0
/*      */     //   268: istore #22
/*      */     //   270: iconst_0
/*      */     //   271: istore #23
/*      */     //   273: iconst_0
/*      */     //   274: istore #26
/*      */     //   276: lconst_0
/*      */     //   277: lstore #27
/*      */     //   279: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   282: ifeq -> 296
/*      */     //   285: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   288: ldc_w 'TLS perform IO'
/*      */     //   291: invokeinterface trace : (Ljava/lang/Object;)V
/*      */     //   296: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   299: ifeq -> 313
/*      */     //   302: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   305: ldc_w 'TLS begin IO operation'
/*      */     //   308: invokeinterface trace : (Ljava/lang/Object;)V
/*      */     //   313: iload_1
/*      */     //   314: ifne -> 462
/*      */     //   317: lload #14
/*      */     //   319: lconst_0
/*      */     //   320: lcmp
/*      */     //   321: ifle -> 462
/*      */     //   324: aload #12
/*      */     //   326: invokevirtual position : ()I
/*      */     //   329: ifle -> 462
/*      */     //   332: aload #12
/*      */     //   334: invokevirtual flip : ()Ljava/nio/Buffer;
/*      */     //   337: pop
/*      */     //   338: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   341: ifeq -> 369
/*      */     //   344: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   347: ldc_w 'TLS copy unwrapped data from %s to %s'
/*      */     //   350: aload #12
/*      */     //   352: invokestatic debugString : (Ljava/nio/ByteBuffer;)Ljava/lang/String;
/*      */     //   355: aload #5
/*      */     //   357: iload #6
/*      */     //   359: iload #7
/*      */     //   361: invokestatic debugString : ([Ljava/nio/ByteBuffer;II)Ljava/lang/String;
/*      */     //   364: invokeinterface tracef : (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
/*      */     //   369: aload #5
/*      */     //   371: iload #6
/*      */     //   373: iload #7
/*      */     //   375: aload #12
/*      */     //   377: invokestatic copy : ([Ljava/nio/ByteBuffer;IILjava/nio/ByteBuffer;)I
/*      */     //   380: istore #26
/*      */     //   382: aload #12
/*      */     //   384: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*      */     //   387: pop
/*      */     //   388: goto -> 402
/*      */     //   391: astore #29
/*      */     //   393: aload #12
/*      */     //   395: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*      */     //   398: pop
/*      */     //   399: aload #29
/*      */     //   401: athrow
/*      */     //   402: iload #26
/*      */     //   404: ifle -> 462
/*      */     //   407: iconst_1
/*      */     //   408: istore #22
/*      */     //   410: lload #27
/*      */     //   412: iload #26
/*      */     //   414: i2l
/*      */     //   415: ladd
/*      */     //   416: lstore #27
/*      */     //   418: lload #14
/*      */     //   420: iload #26
/*      */     //   422: i2l
/*      */     //   423: lsub
/*      */     //   424: dup2
/*      */     //   425: lstore #14
/*      */     //   427: lconst_0
/*      */     //   428: lcmp
/*      */     //   429: ifne -> 462
/*      */     //   432: lload #27
/*      */     //   434: iload_1
/*      */     //   435: iload #18
/*      */     //   437: iload #19
/*      */     //   439: invokestatic actualIOResult : (JIZZ)J
/*      */     //   442: lstore #29
/*      */     //   444: aload_0
/*      */     //   445: iload #8
/*      */     //   447: putfield state : I
/*      */     //   450: iload #23
/*      */     //   452: ifeq -> 459
/*      */     //   455: aload_0
/*      */     //   456: invokevirtual wakeupReads : ()V
/*      */     //   459: lload #29
/*      */     //   461: lreturn
/*      */     //   462: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   465: ifne -> 486
/*      */     //   468: iload #16
/*      */     //   470: ifeq -> 486
/*      */     //   473: iload #17
/*      */     //   475: ifeq -> 486
/*      */     //   478: new java/lang/AssertionError
/*      */     //   481: dup
/*      */     //   482: invokespecial <init> : ()V
/*      */     //   485: athrow
/*      */     //   486: iload #16
/*      */     //   488: ifeq -> 1467
/*      */     //   491: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   494: ifeq -> 520
/*      */     //   497: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   500: ldc_w 'TLS wrap from %s to %s'
/*      */     //   503: aload_2
/*      */     //   504: iload_3
/*      */     //   505: iload #4
/*      */     //   507: invokestatic debugString : ([Ljava/nio/ByteBuffer;II)Ljava/lang/String;
/*      */     //   510: aload #10
/*      */     //   512: invokestatic debugString : (Ljava/nio/ByteBuffer;)Ljava/lang/String;
/*      */     //   515: invokeinterface tracef : (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
/*      */     //   520: aload #9
/*      */     //   522: aload_2
/*      */     //   523: iload_3
/*      */     //   524: iload #4
/*      */     //   526: aload #10
/*      */     //   528: invokevirtual wrap : ([Ljava/nio/ByteBuffer;IILjava/nio/ByteBuffer;)Ljavax/net/ssl/SSLEngineResult;
/*      */     //   531: astore #24
/*      */     //   533: getstatic org/xnio/ssl/JsseStreamConduit$3.$SwitchMap$javax$net$ssl$SSLEngineResult$Status : [I
/*      */     //   536: aload #24
/*      */     //   538: invokevirtual getStatus : ()Ljavax/net/ssl/SSLEngineResult$Status;
/*      */     //   541: invokevirtual ordinal : ()I
/*      */     //   544: iaload
/*      */     //   545: tableswitch default -> 1453, 1 -> 576, 2 -> 640, 3 -> 912, 4 -> 1065
/*      */     //   576: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   579: ifne -> 598
/*      */     //   582: aload #24
/*      */     //   584: invokevirtual bytesConsumed : ()I
/*      */     //   587: ifeq -> 598
/*      */     //   590: new java/lang/AssertionError
/*      */     //   593: dup
/*      */     //   594: invokespecial <init> : ()V
/*      */     //   597: athrow
/*      */     //   598: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   601: ifne -> 620
/*      */     //   604: aload #24
/*      */     //   606: invokevirtual bytesProduced : ()I
/*      */     //   609: ifeq -> 620
/*      */     //   612: new java/lang/AssertionError
/*      */     //   615: dup
/*      */     //   616: invokespecial <init> : ()V
/*      */     //   619: athrow
/*      */     //   620: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   623: ifeq -> 2445
/*      */     //   626: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   629: ldc_w 'TLS wrap operation UNDERFLOW'
/*      */     //   632: invokeinterface trace : (Ljava/lang/Object;)V
/*      */     //   637: goto -> 2445
/*      */     //   640: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   643: ifne -> 662
/*      */     //   646: aload #24
/*      */     //   648: invokevirtual bytesConsumed : ()I
/*      */     //   651: ifeq -> 662
/*      */     //   654: new java/lang/AssertionError
/*      */     //   657: dup
/*      */     //   658: invokespecial <init> : ()V
/*      */     //   661: athrow
/*      */     //   662: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   665: ifne -> 684
/*      */     //   668: aload #24
/*      */     //   670: invokevirtual bytesProduced : ()I
/*      */     //   673: ifeq -> 684
/*      */     //   676: new java/lang/AssertionError
/*      */     //   679: dup
/*      */     //   680: invokespecial <init> : ()V
/*      */     //   683: athrow
/*      */     //   684: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   687: ifeq -> 701
/*      */     //   690: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   693: ldc_w 'TLS wrap operation OVERFLOW'
/*      */     //   696: invokeinterface trace : (Ljava/lang/Object;)V
/*      */     //   701: aload #10
/*      */     //   703: invokevirtual position : ()I
/*      */     //   706: ifne -> 718
/*      */     //   709: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   712: invokeinterface wrongBufferExpansion : ()Ljava/io/IOException;
/*      */     //   717: athrow
/*      */     //   718: aload #10
/*      */     //   720: invokevirtual flip : ()Ljava/nio/Buffer;
/*      */     //   723: pop
/*      */     //   724: aload #10
/*      */     //   726: invokevirtual hasRemaining : ()Z
/*      */     //   729: ifeq -> 827
/*      */     //   732: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   735: ifeq -> 754
/*      */     //   738: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   741: ldc_w 'TLS wrap operation send %s'
/*      */     //   744: aload #10
/*      */     //   746: invokestatic debugString : (Ljava/nio/ByteBuffer;)Ljava/lang/String;
/*      */     //   749: invokeinterface tracef : (Ljava/lang/String;Ljava/lang/Object;)V
/*      */     //   754: aload_0
/*      */     //   755: getfield sinkConduit : Lorg/xnio/conduits/StreamSinkConduit;
/*      */     //   758: aload #10
/*      */     //   760: invokeinterface write : (Ljava/nio/ByteBuffer;)I
/*      */     //   765: istore #29
/*      */     //   767: iload #29
/*      */     //   769: ifne -> 824
/*      */     //   772: iconst_1
/*      */     //   773: istore #21
/*      */     //   775: iload #8
/*      */     //   777: ldc_w -32769
/*      */     //   780: iand
/*      */     //   781: istore #8
/*      */     //   783: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   786: ifne -> 809
/*      */     //   789: iload_1
/*      */     //   790: iconst_2
/*      */     //   791: if_icmpne -> 809
/*      */     //   794: lload #27
/*      */     //   796: lconst_0
/*      */     //   797: lcmp
/*      */     //   798: ifeq -> 809
/*      */     //   801: new java/lang/AssertionError
/*      */     //   804: dup
/*      */     //   805: invokespecial <init> : ()V
/*      */     //   808: athrow
/*      */     //   809: iconst_0
/*      */     //   810: istore #18
/*      */     //   812: iconst_0
/*      */     //   813: istore #16
/*      */     //   815: aload #10
/*      */     //   817: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*      */     //   820: pop
/*      */     //   821: goto -> 2445
/*      */     //   824: goto -> 724
/*      */     //   827: aload #10
/*      */     //   829: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*      */     //   832: pop
/*      */     //   833: goto -> 847
/*      */     //   836: astore #31
/*      */     //   838: aload #10
/*      */     //   840: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*      */     //   843: pop
/*      */     //   844: aload #31
/*      */     //   846: athrow
/*      */     //   847: iload_1
/*      */     //   848: iconst_2
/*      */     //   849: if_icmpeq -> 862
/*      */     //   852: iload #8
/*      */     //   854: ldc 2097152
/*      */     //   856: invokestatic allAreSet : (II)Z
/*      */     //   859: ifeq -> 885
/*      */     //   862: aload_0
/*      */     //   863: getfield sinkConduit : Lorg/xnio/conduits/StreamSinkConduit;
/*      */     //   866: invokeinterface flush : ()Z
/*      */     //   871: dup
/*      */     //   872: istore #18
/*      */     //   874: ifeq -> 885
/*      */     //   877: iload #8
/*      */     //   879: ldc_w -2097153
/*      */     //   882: iand
/*      */     //   883: istore #8
/*      */     //   885: iload_1
/*      */     //   886: iconst_2
/*      */     //   887: if_icmpne -> 2445
/*      */     //   890: iload #8
/*      */     //   892: sipush #256
/*      */     //   895: invokestatic allAreSet : (II)Z
/*      */     //   898: ifeq -> 2445
/*      */     //   901: iload #8
/*      */     //   903: sipush #512
/*      */     //   906: ior
/*      */     //   907: istore #8
/*      */     //   909: goto -> 2445
/*      */     //   912: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   915: ifeq -> 929
/*      */     //   918: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   921: ldc_w 'TLS wrap operation CLOSED'
/*      */     //   924: invokeinterface trace : (Ljava/lang/Object;)V
/*      */     //   929: iload #8
/*      */     //   931: sipush #256
/*      */     //   934: invokestatic allAreClear : (II)Z
/*      */     //   937: ifeq -> 1046
/*      */     //   940: aload #24
/*      */     //   942: invokevirtual bytesProduced : ()I
/*      */     //   945: ifne -> 1046
/*      */     //   948: iload_1
/*      */     //   949: iconst_2
/*      */     //   950: if_icmpne -> 997
/*      */     //   953: iconst_0
/*      */     //   954: istore #16
/*      */     //   956: iload_1
/*      */     //   957: iconst_2
/*      */     //   958: if_icmpeq -> 971
/*      */     //   961: iload #8
/*      */     //   963: ldc 2097152
/*      */     //   965: invokestatic allAreSet : (II)Z
/*      */     //   968: ifeq -> 2445
/*      */     //   971: aload_0
/*      */     //   972: getfield sinkConduit : Lorg/xnio/conduits/StreamSinkConduit;
/*      */     //   975: invokeinterface flush : ()Z
/*      */     //   980: dup
/*      */     //   981: istore #18
/*      */     //   983: ifeq -> 2445
/*      */     //   986: iload #8
/*      */     //   988: ldc_w -2097153
/*      */     //   991: iand
/*      */     //   992: istore #8
/*      */     //   994: goto -> 2445
/*      */     //   997: iload #8
/*      */     //   999: ldc_w -65601
/*      */     //   1002: iand
/*      */     //   1003: istore #8
/*      */     //   1005: iload #8
/*      */     //   1007: sipush #3840
/*      */     //   1010: ior
/*      */     //   1011: istore #8
/*      */     //   1013: new java/nio/channels/ClosedChannelException
/*      */     //   1016: dup
/*      */     //   1017: invokespecial <init> : ()V
/*      */     //   1020: astore #29
/*      */     //   1022: aload_0
/*      */     //   1023: getfield sinkConduit : Lorg/xnio/conduits/StreamSinkConduit;
/*      */     //   1026: invokeinterface truncateWrites : ()V
/*      */     //   1031: goto -> 1043
/*      */     //   1034: astore #30
/*      */     //   1036: aload #29
/*      */     //   1038: aload #30
/*      */     //   1040: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*      */     //   1043: aload #29
/*      */     //   1045: athrow
/*      */     //   1046: iload #8
/*      */     //   1048: sipush #512
/*      */     //   1051: invokestatic allAreSet : (II)Z
/*      */     //   1054: ifeq -> 1065
/*      */     //   1057: iload #8
/*      */     //   1059: sipush #1024
/*      */     //   1062: ior
/*      */     //   1063: istore #8
/*      */     //   1065: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   1068: ifeq -> 1092
/*      */     //   1071: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   1074: ldc_w 'TLS wrap operation OK consumed: %d produced: %d'
/*      */     //   1077: aload #24
/*      */     //   1079: invokevirtual bytesConsumed : ()I
/*      */     //   1082: aload #24
/*      */     //   1084: invokevirtual bytesProduced : ()I
/*      */     //   1087: invokeinterface tracef : (Ljava/lang/String;II)V
/*      */     //   1092: iload #8
/*      */     //   1094: ldc_w -65601
/*      */     //   1097: iand
/*      */     //   1098: istore #8
/*      */     //   1100: aload #24
/*      */     //   1102: invokevirtual bytesConsumed : ()I
/*      */     //   1105: istore #29
/*      */     //   1107: iload_1
/*      */     //   1108: ifne -> 1139
/*      */     //   1111: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   1114: ifne -> 1130
/*      */     //   1117: iload #29
/*      */     //   1119: ifeq -> 1130
/*      */     //   1122: new java/lang/AssertionError
/*      */     //   1125: dup
/*      */     //   1126: invokespecial <init> : ()V
/*      */     //   1129: athrow
/*      */     //   1130: iconst_0
/*      */     //   1131: istore #16
/*      */     //   1133: iconst_1
/*      */     //   1134: istore #17
/*      */     //   1136: goto -> 1196
/*      */     //   1139: iload #29
/*      */     //   1141: ifgt -> 1151
/*      */     //   1144: lload #14
/*      */     //   1146: lconst_0
/*      */     //   1147: lcmp
/*      */     //   1148: ifne -> 1180
/*      */     //   1151: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   1154: ifne -> 1177
/*      */     //   1157: lload #14
/*      */     //   1159: lconst_0
/*      */     //   1160: lcmp
/*      */     //   1161: ifne -> 1177
/*      */     //   1164: iload #29
/*      */     //   1166: ifeq -> 1177
/*      */     //   1169: new java/lang/AssertionError
/*      */     //   1172: dup
/*      */     //   1173: invokespecial <init> : ()V
/*      */     //   1176: athrow
/*      */     //   1177: iconst_0
/*      */     //   1178: istore #16
/*      */     //   1180: lload #27
/*      */     //   1182: iload #29
/*      */     //   1184: i2l
/*      */     //   1185: ladd
/*      */     //   1186: lstore #27
/*      */     //   1188: lload #14
/*      */     //   1190: iload #29
/*      */     //   1192: i2l
/*      */     //   1193: lsub
/*      */     //   1194: lstore #14
/*      */     //   1196: aload #10
/*      */     //   1198: invokevirtual flip : ()Ljava/nio/Buffer;
/*      */     //   1201: pop
/*      */     //   1202: iconst_0
/*      */     //   1203: istore #18
/*      */     //   1205: aload #10
/*      */     //   1207: invokevirtual hasRemaining : ()Z
/*      */     //   1210: ifeq -> 1268
/*      */     //   1213: iload #8
/*      */     //   1215: sipush #1024
/*      */     //   1218: invokestatic allAreSet : (II)Z
/*      */     //   1221: ifeq -> 1238
/*      */     //   1224: aload_0
/*      */     //   1225: getfield sinkConduit : Lorg/xnio/conduits/StreamSinkConduit;
/*      */     //   1228: aload #10
/*      */     //   1230: invokeinterface writeFinal : (Ljava/nio/ByteBuffer;)I
/*      */     //   1235: goto -> 1249
/*      */     //   1238: aload_0
/*      */     //   1239: getfield sinkConduit : Lorg/xnio/conduits/StreamSinkConduit;
/*      */     //   1242: aload #10
/*      */     //   1244: invokeinterface write : (Ljava/nio/ByteBuffer;)I
/*      */     //   1249: istore #30
/*      */     //   1251: iload #30
/*      */     //   1253: ifne -> 1265
/*      */     //   1256: iconst_1
/*      */     //   1257: istore #21
/*      */     //   1259: iconst_0
/*      */     //   1260: istore #16
/*      */     //   1262: goto -> 1268
/*      */     //   1265: goto -> 1205
/*      */     //   1268: aload #10
/*      */     //   1270: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*      */     //   1273: pop
/*      */     //   1274: goto -> 1288
/*      */     //   1277: astore #32
/*      */     //   1279: aload #10
/*      */     //   1281: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*      */     //   1284: pop
/*      */     //   1285: aload #32
/*      */     //   1287: athrow
/*      */     //   1288: aload #10
/*      */     //   1290: invokevirtual position : ()I
/*      */     //   1293: ifne -> 2445
/*      */     //   1296: iload_1
/*      */     //   1297: iconst_2
/*      */     //   1298: if_icmpeq -> 1311
/*      */     //   1301: iload #8
/*      */     //   1303: ldc 2097152
/*      */     //   1305: invokestatic allAreSet : (II)Z
/*      */     //   1308: ifeq -> 1334
/*      */     //   1311: aload_0
/*      */     //   1312: getfield sinkConduit : Lorg/xnio/conduits/StreamSinkConduit;
/*      */     //   1315: invokeinterface flush : ()Z
/*      */     //   1320: dup
/*      */     //   1321: istore #18
/*      */     //   1323: ifeq -> 1334
/*      */     //   1326: iload #8
/*      */     //   1328: ldc_w -2097153
/*      */     //   1331: iand
/*      */     //   1332: istore #8
/*      */     //   1334: iload #8
/*      */     //   1336: sipush #256
/*      */     //   1339: invokestatic allAreSet : (II)Z
/*      */     //   1342: ifeq -> 2445
/*      */     //   1345: iload #8
/*      */     //   1347: sipush #512
/*      */     //   1350: invokestatic allAreClear : (II)Z
/*      */     //   1353: ifeq -> 1405
/*      */     //   1356: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   1359: ifne -> 1378
/*      */     //   1362: aload #10
/*      */     //   1364: invokevirtual position : ()I
/*      */     //   1367: ifeq -> 1378
/*      */     //   1370: new java/lang/AssertionError
/*      */     //   1373: dup
/*      */     //   1374: invokespecial <init> : ()V
/*      */     //   1377: athrow
/*      */     //   1378: iload #8
/*      */     //   1380: sipush #512
/*      */     //   1383: ior
/*      */     //   1384: istore #8
/*      */     //   1386: aload #24
/*      */     //   1388: invokevirtual getHandshakeStatus : ()Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*      */     //   1391: getstatic javax/net/ssl/SSLEngineResult$HandshakeStatus.NOT_HANDSHAKING : Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*      */     //   1394: if_acmpne -> 1405
/*      */     //   1397: iload #8
/*      */     //   1399: sipush #1024
/*      */     //   1402: ior
/*      */     //   1403: istore #8
/*      */     //   1405: iload #8
/*      */     //   1407: sipush #1024
/*      */     //   1410: invokestatic allAreSet : (II)Z
/*      */     //   1413: ifeq -> 2445
/*      */     //   1416: iload_1
/*      */     //   1417: iconst_2
/*      */     //   1418: if_icmpeq -> 1433
/*      */     //   1421: aload_0
/*      */     //   1422: getfield sinkConduit : Lorg/xnio/conduits/StreamSinkConduit;
/*      */     //   1425: invokeinterface flush : ()Z
/*      */     //   1430: ifeq -> 1441
/*      */     //   1433: iload #8
/*      */     //   1435: sipush #2048
/*      */     //   1438: ior
/*      */     //   1439: istore #8
/*      */     //   1441: aload_0
/*      */     //   1442: getfield sinkConduit : Lorg/xnio/conduits/StreamSinkConduit;
/*      */     //   1445: invokeinterface terminateWrites : ()V
/*      */     //   1450: goto -> 2445
/*      */     //   1453: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   1456: aload #24
/*      */     //   1458: invokevirtual getStatus : ()Ljavax/net/ssl/SSLEngineResult$Status;
/*      */     //   1461: invokeinterface unexpectedWrapResult : (Ljavax/net/ssl/SSLEngineResult$Status;)Ljava/io/IOException;
/*      */     //   1466: athrow
/*      */     //   1467: iload #17
/*      */     //   1469: ifeq -> 2415
/*      */     //   1472: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   1475: ifeq -> 1504
/*      */     //   1478: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   1481: ldc_w 'TLS unwrap from %s to %s'
/*      */     //   1484: aload #11
/*      */     //   1486: invokestatic debugString : (Ljava/nio/ByteBuffer;)Ljava/lang/String;
/*      */     //   1489: aload #13
/*      */     //   1491: iconst_0
/*      */     //   1492: iload #7
/*      */     //   1494: iconst_1
/*      */     //   1495: iadd
/*      */     //   1496: invokestatic debugString : ([Ljava/nio/ByteBuffer;II)Ljava/lang/String;
/*      */     //   1499: invokeinterface tracef : (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
/*      */     //   1504: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   1507: ifne -> 1537
/*      */     //   1510: aload #13
/*      */     //   1512: arraylength
/*      */     //   1513: iconst_1
/*      */     //   1514: if_icmpeq -> 1537
/*      */     //   1517: aload #13
/*      */     //   1519: iconst_0
/*      */     //   1520: aaload
/*      */     //   1521: aload #5
/*      */     //   1523: iload #6
/*      */     //   1525: aaload
/*      */     //   1526: if_acmpeq -> 1537
/*      */     //   1529: new java/lang/AssertionError
/*      */     //   1532: dup
/*      */     //   1533: invokespecial <init> : ()V
/*      */     //   1536: athrow
/*      */     //   1537: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   1540: ifne -> 1561
/*      */     //   1543: aload #13
/*      */     //   1545: iload #7
/*      */     //   1547: aaload
/*      */     //   1548: aload #12
/*      */     //   1550: if_acmpeq -> 1561
/*      */     //   1553: new java/lang/AssertionError
/*      */     //   1556: dup
/*      */     //   1557: invokespecial <init> : ()V
/*      */     //   1560: athrow
/*      */     //   1561: aload #5
/*      */     //   1563: iload #6
/*      */     //   1565: iload #7
/*      */     //   1567: invokestatic remaining : ([Ljava/nio/Buffer;II)J
/*      */     //   1570: lstore #29
/*      */     //   1572: aload #9
/*      */     //   1574: aload #11
/*      */     //   1576: aload #13
/*      */     //   1578: iconst_0
/*      */     //   1579: iload #7
/*      */     //   1581: iconst_1
/*      */     //   1582: iadd
/*      */     //   1583: invokevirtual unwrap : (Ljava/nio/ByteBuffer;[Ljava/nio/ByteBuffer;II)Ljavax/net/ssl/SSLEngineResult;
/*      */     //   1586: astore #24
/*      */     //   1588: lload #29
/*      */     //   1590: aload #5
/*      */     //   1592: iload #6
/*      */     //   1594: iload #7
/*      */     //   1596: invokestatic remaining : ([Ljava/nio/Buffer;II)J
/*      */     //   1599: lsub
/*      */     //   1600: lstore #31
/*      */     //   1602: getstatic org/xnio/ssl/JsseStreamConduit$3.$SwitchMap$javax$net$ssl$SSLEngineResult$Status : [I
/*      */     //   1605: aload #24
/*      */     //   1607: invokevirtual getStatus : ()Ljavax/net/ssl/SSLEngineResult$Status;
/*      */     //   1610: invokevirtual ordinal : ()I
/*      */     //   1613: iaload
/*      */     //   1614: tableswitch default -> 2398, 1 -> 1767, 2 -> 1644, 3 -> 2001, 4 -> 2305
/*      */     //   1644: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   1647: ifne -> 1666
/*      */     //   1650: aload #24
/*      */     //   1652: invokevirtual bytesConsumed : ()I
/*      */     //   1655: ifeq -> 1666
/*      */     //   1658: new java/lang/AssertionError
/*      */     //   1661: dup
/*      */     //   1662: invokespecial <init> : ()V
/*      */     //   1665: athrow
/*      */     //   1666: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   1669: ifne -> 1688
/*      */     //   1672: aload #24
/*      */     //   1674: invokevirtual bytesProduced : ()I
/*      */     //   1677: ifeq -> 1688
/*      */     //   1680: new java/lang/AssertionError
/*      */     //   1683: dup
/*      */     //   1684: invokespecial <init> : ()V
/*      */     //   1687: athrow
/*      */     //   1688: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   1691: ifne -> 1709
/*      */     //   1694: lload #31
/*      */     //   1696: lconst_0
/*      */     //   1697: lcmp
/*      */     //   1698: ifeq -> 1709
/*      */     //   1701: new java/lang/AssertionError
/*      */     //   1704: dup
/*      */     //   1705: invokespecial <init> : ()V
/*      */     //   1708: athrow
/*      */     //   1709: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   1712: ifeq -> 1726
/*      */     //   1715: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   1718: ldc_w 'TLS unwrap operation OVERFLOW'
/*      */     //   1721: invokeinterface trace : (Ljava/lang/Object;)V
/*      */     //   1726: iload #22
/*      */     //   1728: ifne -> 1761
/*      */     //   1731: lload #27
/*      */     //   1733: iload_1
/*      */     //   1734: iload #18
/*      */     //   1736: iload #19
/*      */     //   1738: invokestatic actualIOResult : (JIZZ)J
/*      */     //   1741: lstore #33
/*      */     //   1743: aload_0
/*      */     //   1744: iload #8
/*      */     //   1746: putfield state : I
/*      */     //   1749: iload #23
/*      */     //   1751: ifeq -> 1758
/*      */     //   1754: aload_0
/*      */     //   1755: invokevirtual wakeupReads : ()V
/*      */     //   1758: lload #33
/*      */     //   1760: lreturn
/*      */     //   1761: iconst_0
/*      */     //   1762: istore #17
/*      */     //   1764: goto -> 2412
/*      */     //   1767: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   1770: ifne -> 1789
/*      */     //   1773: aload #24
/*      */     //   1775: invokevirtual bytesConsumed : ()I
/*      */     //   1778: ifeq -> 1789
/*      */     //   1781: new java/lang/AssertionError
/*      */     //   1784: dup
/*      */     //   1785: invokespecial <init> : ()V
/*      */     //   1788: athrow
/*      */     //   1789: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   1792: ifne -> 1811
/*      */     //   1795: aload #24
/*      */     //   1797: invokevirtual bytesProduced : ()I
/*      */     //   1800: ifeq -> 1811
/*      */     //   1803: new java/lang/AssertionError
/*      */     //   1806: dup
/*      */     //   1807: invokespecial <init> : ()V
/*      */     //   1810: athrow
/*      */     //   1811: getstatic org/xnio/ssl/JsseStreamConduit.$assertionsDisabled : Z
/*      */     //   1814: ifne -> 1832
/*      */     //   1817: lload #31
/*      */     //   1819: lconst_0
/*      */     //   1820: lcmp
/*      */     //   1821: ifeq -> 1832
/*      */     //   1824: new java/lang/AssertionError
/*      */     //   1827: dup
/*      */     //   1828: invokespecial <init> : ()V
/*      */     //   1831: athrow
/*      */     //   1832: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   1835: ifeq -> 1849
/*      */     //   1838: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   1841: ldc_w 'TLS unwrap operation UNDERFLOW'
/*      */     //   1844: invokeinterface trace : (Ljava/lang/Object;)V
/*      */     //   1849: aload #11
/*      */     //   1851: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*      */     //   1854: pop
/*      */     //   1855: aload_0
/*      */     //   1856: getfield sourceConduit : Lorg/xnio/conduits/StreamSourceConduit;
/*      */     //   1859: aload #11
/*      */     //   1861: invokeinterface read : (Ljava/nio/ByteBuffer;)I
/*      */     //   1866: istore #33
/*      */     //   1868: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   1871: ifeq -> 1890
/*      */     //   1874: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   1877: ldc_w 'TLS unwrap operation read %s'
/*      */     //   1880: aload #11
/*      */     //   1882: invokestatic debugString : (Ljava/nio/ByteBuffer;)Ljava/lang/String;
/*      */     //   1885: invokeinterface tracef : (Ljava/lang/String;Ljava/lang/Object;)V
/*      */     //   1890: iload #33
/*      */     //   1892: iconst_m1
/*      */     //   1893: if_icmpne -> 1911
/*      */     //   1896: iload #8
/*      */     //   1898: bipush #-33
/*      */     //   1900: iand
/*      */     //   1901: istore #8
/*      */     //   1903: aload #9
/*      */     //   1905: invokevirtual closeInbound : ()V
/*      */     //   1908: goto -> 1978
/*      */     //   1911: iload #33
/*      */     //   1913: ifne -> 1932
/*      */     //   1916: iconst_1
/*      */     //   1917: istore #20
/*      */     //   1919: iload #8
/*      */     //   1921: bipush #-33
/*      */     //   1923: iand
/*      */     //   1924: istore #8
/*      */     //   1926: iconst_0
/*      */     //   1927: istore #17
/*      */     //   1929: goto -> 1978
/*      */     //   1932: aload #11
/*      */     //   1934: invokevirtual hasRemaining : ()Z
/*      */     //   1937: ifeq -> 1978
/*      */     //   1940: aload_0
/*      */     //   1941: getfield sourceConduit : Lorg/xnio/conduits/StreamSourceConduit;
/*      */     //   1944: aload #11
/*      */     //   1946: invokeinterface read : (Ljava/nio/ByteBuffer;)I
/*      */     //   1951: istore #33
/*      */     //   1953: iload #33
/*      */     //   1955: ifle -> 1966
/*      */     //   1958: aload #11
/*      */     //   1960: invokevirtual hasRemaining : ()Z
/*      */     //   1963: ifne -> 1940
/*      */     //   1966: iload #33
/*      */     //   1968: ifne -> 1978
/*      */     //   1971: iload #8
/*      */     //   1973: bipush #-33
/*      */     //   1975: iand
/*      */     //   1976: istore #8
/*      */     //   1978: aload #11
/*      */     //   1980: invokevirtual flip : ()Ljava/nio/Buffer;
/*      */     //   1983: pop
/*      */     //   1984: goto -> 1998
/*      */     //   1987: astore #35
/*      */     //   1989: aload #11
/*      */     //   1991: invokevirtual flip : ()Ljava/nio/Buffer;
/*      */     //   1994: pop
/*      */     //   1995: aload #35
/*      */     //   1997: athrow
/*      */     //   1998: goto -> 2412
/*      */     //   2001: aload #24
/*      */     //   2003: invokevirtual getHandshakeStatus : ()Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*      */     //   2006: getstatic javax/net/ssl/SSLEngineResult$HandshakeStatus.NEED_UNWRAP : Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*      */     //   2009: if_acmpne -> 2230
/*      */     //   2012: aload #11
/*      */     //   2014: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*      */     //   2017: pop
/*      */     //   2018: aload_0
/*      */     //   2019: getfield sourceConduit : Lorg/xnio/conduits/StreamSourceConduit;
/*      */     //   2022: aload #11
/*      */     //   2024: invokeinterface read : (Ljava/nio/ByteBuffer;)I
/*      */     //   2029: istore #33
/*      */     //   2031: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   2034: ifeq -> 2053
/*      */     //   2037: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   2040: ldc_w 'TLS unwrap operation read %s'
/*      */     //   2043: aload #11
/*      */     //   2045: invokestatic debugString : (Ljava/nio/ByteBuffer;)Ljava/lang/String;
/*      */     //   2048: invokeinterface tracef : (Ljava/lang/String;Ljava/lang/Object;)V
/*      */     //   2053: iload #33
/*      */     //   2055: iconst_m1
/*      */     //   2056: if_icmpne -> 2107
/*      */     //   2059: iload #8
/*      */     //   2061: bipush #-33
/*      */     //   2063: iand
/*      */     //   2064: istore #8
/*      */     //   2066: aload #9
/*      */     //   2068: invokevirtual closeInbound : ()V
/*      */     //   2071: lload #27
/*      */     //   2073: iload_1
/*      */     //   2074: iload #18
/*      */     //   2076: iload #19
/*      */     //   2078: invokestatic actualIOResult : (JIZZ)J
/*      */     //   2081: lstore #34
/*      */     //   2083: aload #11
/*      */     //   2085: invokevirtual flip : ()Ljava/nio/Buffer;
/*      */     //   2088: pop
/*      */     //   2089: aload_0
/*      */     //   2090: iload #8
/*      */     //   2092: putfield state : I
/*      */     //   2095: iload #23
/*      */     //   2097: ifeq -> 2104
/*      */     //   2100: aload_0
/*      */     //   2101: invokevirtual wakeupReads : ()V
/*      */     //   2104: lload #34
/*      */     //   2106: lreturn
/*      */     //   2107: iload #33
/*      */     //   2109: ifne -> 2161
/*      */     //   2112: iconst_1
/*      */     //   2113: istore #20
/*      */     //   2115: iload #8
/*      */     //   2117: bipush #-33
/*      */     //   2119: iand
/*      */     //   2120: istore #8
/*      */     //   2122: iconst_0
/*      */     //   2123: istore #17
/*      */     //   2125: lload #27
/*      */     //   2127: iload_1
/*      */     //   2128: iload #18
/*      */     //   2130: iload #19
/*      */     //   2132: invokestatic actualIOResult : (JIZZ)J
/*      */     //   2135: lstore #34
/*      */     //   2137: aload #11
/*      */     //   2139: invokevirtual flip : ()Ljava/nio/Buffer;
/*      */     //   2142: pop
/*      */     //   2143: aload_0
/*      */     //   2144: iload #8
/*      */     //   2146: putfield state : I
/*      */     //   2149: iload #23
/*      */     //   2151: ifeq -> 2158
/*      */     //   2154: aload_0
/*      */     //   2155: invokevirtual wakeupReads : ()V
/*      */     //   2158: lload #34
/*      */     //   2160: lreturn
/*      */     //   2161: aload #11
/*      */     //   2163: invokevirtual hasRemaining : ()Z
/*      */     //   2166: ifeq -> 2207
/*      */     //   2169: aload_0
/*      */     //   2170: getfield sourceConduit : Lorg/xnio/conduits/StreamSourceConduit;
/*      */     //   2173: aload #11
/*      */     //   2175: invokeinterface read : (Ljava/nio/ByteBuffer;)I
/*      */     //   2180: istore #33
/*      */     //   2182: iload #33
/*      */     //   2184: ifle -> 2195
/*      */     //   2187: aload #11
/*      */     //   2189: invokevirtual hasRemaining : ()Z
/*      */     //   2192: ifne -> 2169
/*      */     //   2195: iload #33
/*      */     //   2197: ifne -> 2207
/*      */     //   2200: iload #8
/*      */     //   2202: bipush #-33
/*      */     //   2204: iand
/*      */     //   2205: istore #8
/*      */     //   2207: aload #11
/*      */     //   2209: invokevirtual flip : ()Ljava/nio/Buffer;
/*      */     //   2212: pop
/*      */     //   2213: goto -> 2227
/*      */     //   2216: astore #36
/*      */     //   2218: aload #11
/*      */     //   2220: invokevirtual flip : ()Ljava/nio/Buffer;
/*      */     //   2223: pop
/*      */     //   2224: aload #36
/*      */     //   2226: athrow
/*      */     //   2227: goto -> 2412
/*      */     //   2230: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   2233: ifeq -> 2247
/*      */     //   2236: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   2239: ldc_w 'TLS unwrap operation CLOSED'
/*      */     //   2242: invokeinterface trace : (Ljava/lang/Object;)V
/*      */     //   2247: iload #8
/*      */     //   2249: ldc_w -65601
/*      */     //   2252: iand
/*      */     //   2253: istore #8
/*      */     //   2255: iload_1
/*      */     //   2256: ifne -> 2285
/*      */     //   2259: lload #27
/*      */     //   2261: lload #31
/*      */     //   2263: ladd
/*      */     //   2264: lstore #27
/*      */     //   2266: lload #14
/*      */     //   2268: lload #31
/*      */     //   2270: lsub
/*      */     //   2271: lstore #14
/*      */     //   2273: iload #8
/*      */     //   2275: bipush #-33
/*      */     //   2277: iand
/*      */     //   2278: iconst_2
/*      */     //   2279: ior
/*      */     //   2280: istore #8
/*      */     //   2282: goto -> 2288
/*      */     //   2285: iconst_1
/*      */     //   2286: istore #23
/*      */     //   2288: iconst_1
/*      */     //   2289: istore #19
/*      */     //   2291: iconst_0
/*      */     //   2292: istore #17
/*      */     //   2294: iload_1
/*      */     //   2295: iconst_2
/*      */     //   2296: if_icmpne -> 2412
/*      */     //   2299: iconst_1
/*      */     //   2300: istore #16
/*      */     //   2302: goto -> 2412
/*      */     //   2305: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   2308: ifeq -> 2332
/*      */     //   2311: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   2314: ldc_w 'TLS unwrap operation OK consumed: %d produced: %d'
/*      */     //   2317: aload #24
/*      */     //   2319: invokevirtual bytesConsumed : ()I
/*      */     //   2322: aload #24
/*      */     //   2324: invokevirtual bytesProduced : ()I
/*      */     //   2327: invokeinterface tracef : (Ljava/lang/String;II)V
/*      */     //   2332: iload #8
/*      */     //   2334: bipush #32
/*      */     //   2336: invokestatic allAreClear : (II)Z
/*      */     //   2339: ifeq -> 2349
/*      */     //   2342: iload #8
/*      */     //   2344: bipush #32
/*      */     //   2346: ior
/*      */     //   2347: istore #8
/*      */     //   2349: iload #8
/*      */     //   2351: ldc_w -65601
/*      */     //   2354: iand
/*      */     //   2355: istore #8
/*      */     //   2357: iload_1
/*      */     //   2358: ifne -> 2378
/*      */     //   2361: lload #27
/*      */     //   2363: lload #31
/*      */     //   2365: ladd
/*      */     //   2366: lstore #27
/*      */     //   2368: lload #14
/*      */     //   2370: lload #31
/*      */     //   2372: lsub
/*      */     //   2373: lstore #14
/*      */     //   2375: goto -> 2412
/*      */     //   2378: iconst_1
/*      */     //   2379: istore #16
/*      */     //   2381: iconst_0
/*      */     //   2382: istore #17
/*      */     //   2384: aload #24
/*      */     //   2386: invokevirtual bytesProduced : ()I
/*      */     //   2389: ifle -> 2412
/*      */     //   2392: iconst_1
/*      */     //   2393: istore #23
/*      */     //   2395: goto -> 2412
/*      */     //   2398: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   2401: aload #24
/*      */     //   2403: invokevirtual getStatus : ()Ljavax/net/ssl/SSLEngineResult$Status;
/*      */     //   2406: invokeinterface unexpectedUnwrapResult : (Ljavax/net/ssl/SSLEngineResult$Status;)Ljava/io/IOException;
/*      */     //   2411: athrow
/*      */     //   2412: goto -> 2445
/*      */     //   2415: lload #27
/*      */     //   2417: iload_1
/*      */     //   2418: iload #18
/*      */     //   2420: iload #19
/*      */     //   2422: invokestatic actualIOResult : (JIZZ)J
/*      */     //   2425: lstore #29
/*      */     //   2427: aload_0
/*      */     //   2428: iload #8
/*      */     //   2430: putfield state : I
/*      */     //   2433: iload #23
/*      */     //   2435: ifeq -> 2442
/*      */     //   2438: aload_0
/*      */     //   2439: invokevirtual wakeupReads : ()V
/*      */     //   2442: lload #29
/*      */     //   2444: lreturn
/*      */     //   2445: aload #24
/*      */     //   2447: invokevirtual getHandshakeStatus : ()Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*      */     //   2450: astore #25
/*      */     //   2452: getstatic org/xnio/ssl/JsseStreamConduit$3.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus : [I
/*      */     //   2455: aload #25
/*      */     //   2457: invokevirtual ordinal : ()I
/*      */     //   2460: iaload
/*      */     //   2461: tableswitch default -> 2962, 1 -> 2496, 2 -> 2520, 3 -> 2539, 4 -> 2796, 5 -> 2865
/*      */     //   2496: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   2499: ifeq -> 2513
/*      */     //   2502: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   2505: ldc_w 'TLS handshake FINISHED'
/*      */     //   2508: invokeinterface trace : (Ljava/lang/Object;)V
/*      */     //   2513: aload_0
/*      */     //   2514: getfield connection : Lorg/xnio/ssl/JsseSslConnection;
/*      */     //   2517: invokevirtual invokeHandshakeListener : ()V
/*      */     //   2520: iload #8
/*      */     //   2522: sipush #256
/*      */     //   2525: invokestatic allAreSet : (II)Z
/*      */     //   2528: ifeq -> 296
/*      */     //   2531: aload #9
/*      */     //   2533: invokevirtual closeOutbound : ()V
/*      */     //   2536: goto -> 296
/*      */     //   2539: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   2542: ifeq -> 2556
/*      */     //   2545: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   2548: ldc_w 'TLS handshake NEED_TASK'
/*      */     //   2551: invokeinterface trace : (Ljava/lang/Object;)V
/*      */     //   2556: lload #27
/*      */     //   2558: lconst_0
/*      */     //   2559: lcmp
/*      */     //   2560: ifeq -> 2593
/*      */     //   2563: lload #27
/*      */     //   2565: iload_1
/*      */     //   2566: iload #18
/*      */     //   2568: iload #19
/*      */     //   2570: invokestatic actualIOResult : (JIZZ)J
/*      */     //   2573: lstore #29
/*      */     //   2575: aload_0
/*      */     //   2576: iload #8
/*      */     //   2578: putfield state : I
/*      */     //   2581: iload #23
/*      */     //   2583: ifeq -> 2590
/*      */     //   2586: aload_0
/*      */     //   2587: invokevirtual wakeupReads : ()V
/*      */     //   2590: lload #29
/*      */     //   2592: lreturn
/*      */     //   2593: iload #8
/*      */     //   2595: ldc 262144
/*      */     //   2597: invokestatic allAreSet : (II)Z
/*      */     //   2600: ifeq -> 2653
/*      */     //   2603: aload #9
/*      */     //   2605: invokevirtual getDelegatedTask : ()Ljava/lang/Runnable;
/*      */     //   2608: astore #29
/*      */     //   2610: aload #29
/*      */     //   2612: ifnonnull -> 2618
/*      */     //   2615: goto -> 2643
/*      */     //   2618: aload #29
/*      */     //   2620: invokeinterface run : ()V
/*      */     //   2625: goto -> 2603
/*      */     //   2628: astore #30
/*      */     //   2630: new javax/net/ssl/SSLException
/*      */     //   2633: dup
/*      */     //   2634: ldc_w 'Delegated task threw an exception'
/*      */     //   2637: aload #30
/*      */     //   2639: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   2642: athrow
/*      */     //   2643: aload #9
/*      */     //   2645: invokevirtual getHandshakeStatus : ()Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*      */     //   2648: astore #25
/*      */     //   2650: goto -> 2452
/*      */     //   2653: iload #8
/*      */     //   2655: ldc 1048576
/*      */     //   2657: ior
/*      */     //   2658: istore #8
/*      */     //   2660: new java/util/ArrayList
/*      */     //   2663: dup
/*      */     //   2664: iconst_4
/*      */     //   2665: invokespecial <init> : (I)V
/*      */     //   2668: astore #29
/*      */     //   2670: aload #9
/*      */     //   2672: invokevirtual getDelegatedTask : ()Ljava/lang/Runnable;
/*      */     //   2675: astore #30
/*      */     //   2677: aload #30
/*      */     //   2679: ifnull -> 2693
/*      */     //   2682: aload #29
/*      */     //   2684: aload #30
/*      */     //   2686: invokevirtual add : (Ljava/lang/Object;)Z
/*      */     //   2689: pop
/*      */     //   2690: goto -> 2670
/*      */     //   2693: aload #29
/*      */     //   2695: invokevirtual size : ()I
/*      */     //   2698: istore #31
/*      */     //   2700: aload_0
/*      */     //   2701: dup
/*      */     //   2702: astore #32
/*      */     //   2704: monitorenter
/*      */     //   2705: aload_0
/*      */     //   2706: iload #31
/*      */     //   2708: putfield tasks : I
/*      */     //   2711: aload #32
/*      */     //   2713: monitorexit
/*      */     //   2714: goto -> 2725
/*      */     //   2717: astore #37
/*      */     //   2719: aload #32
/*      */     //   2721: monitorexit
/*      */     //   2722: aload #37
/*      */     //   2724: athrow
/*      */     //   2725: iconst_0
/*      */     //   2726: istore #32
/*      */     //   2728: iload #32
/*      */     //   2730: iload #31
/*      */     //   2732: if_icmpge -> 2766
/*      */     //   2735: aload_0
/*      */     //   2736: invokevirtual getWorker : ()Lorg/xnio/XnioWorker;
/*      */     //   2739: new org/xnio/ssl/JsseStreamConduit$TaskWrapper
/*      */     //   2742: dup
/*      */     //   2743: aload_0
/*      */     //   2744: aload #29
/*      */     //   2746: iload #32
/*      */     //   2748: invokevirtual get : (I)Ljava/lang/Object;
/*      */     //   2751: checkcast java/lang/Runnable
/*      */     //   2754: invokespecial <init> : (Lorg/xnio/ssl/JsseStreamConduit;Ljava/lang/Runnable;)V
/*      */     //   2757: invokevirtual execute : (Ljava/lang/Runnable;)V
/*      */     //   2760: iinc #32, 1
/*      */     //   2763: goto -> 2728
/*      */     //   2766: lload #27
/*      */     //   2768: iload_1
/*      */     //   2769: iload #18
/*      */     //   2771: iload #19
/*      */     //   2773: invokestatic actualIOResult : (JIZZ)J
/*      */     //   2776: lstore #32
/*      */     //   2778: aload_0
/*      */     //   2779: iload #8
/*      */     //   2781: putfield state : I
/*      */     //   2784: iload #23
/*      */     //   2786: ifeq -> 2793
/*      */     //   2789: aload_0
/*      */     //   2790: invokevirtual wakeupReads : ()V
/*      */     //   2793: lload #32
/*      */     //   2795: lreturn
/*      */     //   2796: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   2799: ifeq -> 2813
/*      */     //   2802: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   2805: ldc_w 'TLS handshake NEED_WRAP'
/*      */     //   2808: invokeinterface trace : (Ljava/lang/Object;)V
/*      */     //   2813: iload #8
/*      */     //   2815: ldc_w 2097216
/*      */     //   2818: ior
/*      */     //   2819: istore #8
/*      */     //   2821: iload #21
/*      */     //   2823: ifeq -> 2856
/*      */     //   2826: lload #27
/*      */     //   2828: iload_1
/*      */     //   2829: iload #18
/*      */     //   2831: iload #19
/*      */     //   2833: invokestatic actualIOResult : (JIZZ)J
/*      */     //   2836: lstore #29
/*      */     //   2838: aload_0
/*      */     //   2839: iload #8
/*      */     //   2841: putfield state : I
/*      */     //   2844: iload #23
/*      */     //   2846: ifeq -> 2853
/*      */     //   2849: aload_0
/*      */     //   2850: invokevirtual wakeupReads : ()V
/*      */     //   2853: lload #29
/*      */     //   2855: lreturn
/*      */     //   2856: iconst_1
/*      */     //   2857: istore #16
/*      */     //   2859: iconst_0
/*      */     //   2860: istore #17
/*      */     //   2862: goto -> 296
/*      */     //   2865: getstatic org/xnio/ssl/JsseStreamConduit.TRACE_SSL : Z
/*      */     //   2868: ifeq -> 2882
/*      */     //   2871: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   2874: ldc_w 'TLS handshake NEED_UNWRAP'
/*      */     //   2877: invokeinterface trace : (Ljava/lang/Object;)V
/*      */     //   2882: iload #16
/*      */     //   2884: ifeq -> 2911
/*      */     //   2887: iload #18
/*      */     //   2889: ifne -> 2911
/*      */     //   2892: aload_0
/*      */     //   2893: getfield sinkConduit : Lorg/xnio/conduits/StreamSinkConduit;
/*      */     //   2896: invokeinterface flush : ()Z
/*      */     //   2901: ifne -> 2911
/*      */     //   2904: iload #8
/*      */     //   2906: ldc 2097152
/*      */     //   2908: ior
/*      */     //   2909: istore #8
/*      */     //   2911: iload #8
/*      */     //   2913: ldc 65536
/*      */     //   2915: ior
/*      */     //   2916: istore #8
/*      */     //   2918: iload #20
/*      */     //   2920: ifeq -> 2953
/*      */     //   2923: lload #27
/*      */     //   2925: iload_1
/*      */     //   2926: iload #18
/*      */     //   2928: iload #19
/*      */     //   2930: invokestatic actualIOResult : (JIZZ)J
/*      */     //   2933: lstore #29
/*      */     //   2935: aload_0
/*      */     //   2936: iload #8
/*      */     //   2938: putfield state : I
/*      */     //   2941: iload #23
/*      */     //   2943: ifeq -> 2950
/*      */     //   2946: aload_0
/*      */     //   2947: invokevirtual wakeupReads : ()V
/*      */     //   2950: lload #29
/*      */     //   2952: lreturn
/*      */     //   2953: iconst_0
/*      */     //   2954: istore #16
/*      */     //   2956: iconst_1
/*      */     //   2957: istore #17
/*      */     //   2959: goto -> 296
/*      */     //   2962: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*      */     //   2965: aload #24
/*      */     //   2967: invokevirtual getHandshakeStatus : ()Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*      */     //   2970: invokeinterface unexpectedHandshakeStatus : (Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;)Ljava/io/IOException;
/*      */     //   2975: athrow
/*      */     //   2976: astore #38
/*      */     //   2978: aload_0
/*      */     //   2979: iload #8
/*      */     //   2981: putfield state : I
/*      */     //   2984: iload #23
/*      */     //   2986: ifeq -> 2993
/*      */     //   2989: aload_0
/*      */     //   2990: invokevirtual wakeupReads : ()V
/*      */     //   2993: aload #38
/*      */     //   2995: athrow
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #1166	-> 0
/*      */     //   #1168	-> 37
/*      */     //   #1169	-> 66
/*      */     //   #1171	-> 72
/*      */     //   #1172	-> 97
/*      */     //   #1174	-> 107
/*      */     //   #1176	-> 109
/*      */     //   #1177	-> 115
/*      */     //   #1178	-> 129
/*      */     //   #1179	-> 143
/*      */     //   #1181	-> 157
/*      */     //   #1182	-> 173
/*      */     //   #1184	-> 180
/*      */     //   #1185	-> 201
/*      */     //   #1186	-> 243
/*      */     //   #1187	-> 255
/*      */     //   #1188	-> 258
/*      */     //   #1189	-> 261
/*      */     //   #1190	-> 264
/*      */     //   #1191	-> 267
/*      */     //   #1192	-> 270
/*      */     //   #1195	-> 273
/*      */     //   #1197	-> 276
/*      */     //   #1198	-> 279
/*      */     //   #1201	-> 296
/*      */     //   #1202	-> 313
/*      */     //   #1204	-> 332
/*      */     //   #1206	-> 338
/*      */     //   #1207	-> 369
/*      */     //   #1209	-> 382
/*      */     //   #1210	-> 388
/*      */     //   #1209	-> 391
/*      */     //   #1210	-> 399
/*      */     //   #1211	-> 402
/*      */     //   #1212	-> 407
/*      */     //   #1213	-> 410
/*      */     //   #1214	-> 418
/*      */     //   #1215	-> 432
/*      */     //   #1603	-> 444
/*      */     //   #1604	-> 450
/*      */     //   #1605	-> 455
/*      */     //   #1215	-> 459
/*      */     //   #1219	-> 462
/*      */     //   #1220	-> 486
/*      */     //   #1221	-> 491
/*      */     //   #1222	-> 520
/*      */     //   #1223	-> 533
/*      */     //   #1225	-> 576
/*      */     //   #1226	-> 598
/*      */     //   #1228	-> 620
/*      */     //   #1232	-> 640
/*      */     //   #1233	-> 662
/*      */     //   #1234	-> 684
/*      */     //   #1235	-> 701
/*      */     //   #1237	-> 709
/*      */     //   #1240	-> 718
/*      */     //   #1242	-> 724
/*      */     //   #1243	-> 732
/*      */     //   #1244	-> 754
/*      */     //   #1245	-> 767
/*      */     //   #1246	-> 772
/*      */     //   #1247	-> 775
/*      */     //   #1249	-> 783
/*      */     //   #1250	-> 809
/*      */     //   #1251	-> 812
/*      */     //   #1256	-> 815
/*      */     //   #1254	-> 824
/*      */     //   #1256	-> 827
/*      */     //   #1257	-> 833
/*      */     //   #1256	-> 836
/*      */     //   #1257	-> 844
/*      */     //   #1258	-> 847
/*      */     //   #1259	-> 862
/*      */     //   #1260	-> 877
/*      */     //   #1263	-> 885
/*      */     //   #1264	-> 901
/*      */     //   #1271	-> 912
/*      */     //   #1272	-> 929
/*      */     //   #1273	-> 948
/*      */     //   #1276	-> 953
/*      */     //   #1277	-> 956
/*      */     //   #1278	-> 971
/*      */     //   #1279	-> 986
/*      */     //   #1285	-> 997
/*      */     //   #1286	-> 1005
/*      */     //   #1287	-> 1013
/*      */     //   #1289	-> 1022
/*      */     //   #1292	-> 1031
/*      */     //   #1290	-> 1034
/*      */     //   #1291	-> 1036
/*      */     //   #1293	-> 1043
/*      */     //   #1295	-> 1046
/*      */     //   #1296	-> 1057
/*      */     //   #1302	-> 1065
/*      */     //   #1303	-> 1092
/*      */     //   #1304	-> 1100
/*      */     //   #1305	-> 1107
/*      */     //   #1307	-> 1111
/*      */     //   #1308	-> 1130
/*      */     //   #1309	-> 1133
/*      */     //   #1311	-> 1139
/*      */     //   #1313	-> 1151
/*      */     //   #1315	-> 1177
/*      */     //   #1317	-> 1180
/*      */     //   #1318	-> 1188
/*      */     //   #1321	-> 1196
/*      */     //   #1323	-> 1202
/*      */     //   #1324	-> 1205
/*      */     //   #1325	-> 1213
/*      */     //   #1326	-> 1251
/*      */     //   #1328	-> 1256
/*      */     //   #1329	-> 1259
/*      */     //   #1330	-> 1262
/*      */     //   #1332	-> 1265
/*      */     //   #1334	-> 1268
/*      */     //   #1335	-> 1274
/*      */     //   #1334	-> 1277
/*      */     //   #1335	-> 1285
/*      */     //   #1337	-> 1288
/*      */     //   #1338	-> 1296
/*      */     //   #1339	-> 1311
/*      */     //   #1340	-> 1326
/*      */     //   #1343	-> 1334
/*      */     //   #1344	-> 1345
/*      */     //   #1346	-> 1356
/*      */     //   #1347	-> 1378
/*      */     //   #1348	-> 1386
/*      */     //   #1350	-> 1397
/*      */     //   #1353	-> 1405
/*      */     //   #1355	-> 1416
/*      */     //   #1356	-> 1433
/*      */     //   #1358	-> 1441
/*      */     //   #1366	-> 1453
/*      */     //   #1369	-> 1467
/*      */     //   #1370	-> 1472
/*      */     //   #1373	-> 1504
/*      */     //   #1374	-> 1537
/*      */     //   #1376	-> 1561
/*      */     //   #1377	-> 1572
/*      */     //   #1378	-> 1588
/*      */     //   #1379	-> 1602
/*      */     //   #1381	-> 1644
/*      */     //   #1382	-> 1666
/*      */     //   #1383	-> 1688
/*      */     //   #1384	-> 1709
/*      */     //   #1386	-> 1726
/*      */     //   #1387	-> 1731
/*      */     //   #1603	-> 1743
/*      */     //   #1604	-> 1749
/*      */     //   #1605	-> 1754
/*      */     //   #1387	-> 1758
/*      */     //   #1389	-> 1761
/*      */     //   #1390	-> 1764
/*      */     //   #1393	-> 1767
/*      */     //   #1394	-> 1789
/*      */     //   #1395	-> 1811
/*      */     //   #1396	-> 1832
/*      */     //   #1398	-> 1849
/*      */     //   #1401	-> 1855
/*      */     //   #1402	-> 1868
/*      */     //   #1403	-> 1890
/*      */     //   #1404	-> 1896
/*      */     //   #1405	-> 1903
/*      */     //   #1406	-> 1911
/*      */     //   #1407	-> 1916
/*      */     //   #1408	-> 1919
/*      */     //   #1409	-> 1926
/*      */     //   #1410	-> 1932
/*      */     //   #1413	-> 1940
/*      */     //   #1414	-> 1953
/*      */     //   #1415	-> 1966
/*      */     //   #1416	-> 1971
/*      */     //   #1420	-> 1978
/*      */     //   #1421	-> 1984
/*      */     //   #1420	-> 1987
/*      */     //   #1421	-> 1995
/*      */     //   #1423	-> 1998
/*      */     //   #1426	-> 2001
/*      */     //   #1429	-> 2012
/*      */     //   #1432	-> 2018
/*      */     //   #1433	-> 2031
/*      */     //   #1434	-> 2053
/*      */     //   #1435	-> 2059
/*      */     //   #1436	-> 2066
/*      */     //   #1437	-> 2071
/*      */     //   #1453	-> 2083
/*      */     //   #1603	-> 2089
/*      */     //   #1604	-> 2095
/*      */     //   #1605	-> 2100
/*      */     //   #1437	-> 2104
/*      */     //   #1438	-> 2107
/*      */     //   #1439	-> 2112
/*      */     //   #1440	-> 2115
/*      */     //   #1441	-> 2122
/*      */     //   #1442	-> 2125
/*      */     //   #1453	-> 2137
/*      */     //   #1603	-> 2143
/*      */     //   #1604	-> 2149
/*      */     //   #1605	-> 2154
/*      */     //   #1442	-> 2158
/*      */     //   #1443	-> 2161
/*      */     //   #1446	-> 2169
/*      */     //   #1447	-> 2182
/*      */     //   #1448	-> 2195
/*      */     //   #1449	-> 2200
/*      */     //   #1453	-> 2207
/*      */     //   #1454	-> 2213
/*      */     //   #1453	-> 2216
/*      */     //   #1454	-> 2224
/*      */     //   #1456	-> 2227
/*      */     //   #1458	-> 2230
/*      */     //   #1459	-> 2247
/*      */     //   #1460	-> 2255
/*      */     //   #1461	-> 2259
/*      */     //   #1462	-> 2266
/*      */     //   #1465	-> 2273
/*      */     //   #1467	-> 2285
/*      */     //   #1470	-> 2288
/*      */     //   #1471	-> 2291
/*      */     //   #1472	-> 2294
/*      */     //   #1473	-> 2299
/*      */     //   #1478	-> 2305
/*      */     //   #1479	-> 2332
/*      */     //   #1481	-> 2342
/*      */     //   #1483	-> 2349
/*      */     //   #1484	-> 2357
/*      */     //   #1485	-> 2361
/*      */     //   #1486	-> 2368
/*      */     //   #1488	-> 2378
/*      */     //   #1489	-> 2381
/*      */     //   #1490	-> 2384
/*      */     //   #1491	-> 2392
/*      */     //   #1498	-> 2398
/*      */     //   #1501	-> 2412
/*      */     //   #1503	-> 2415
/*      */     //   #1603	-> 2427
/*      */     //   #1604	-> 2433
/*      */     //   #1605	-> 2438
/*      */     //   #1503	-> 2442
/*      */     //   #1506	-> 2445
/*      */     //   #1508	-> 2452
/*      */     //   #1511	-> 2496
/*      */     //   #1512	-> 2513
/*      */     //   #1517	-> 2520
/*      */     //   #1518	-> 2531
/*      */     //   #1524	-> 2539
/*      */     //   #1525	-> 2556
/*      */     //   #1527	-> 2563
/*      */     //   #1603	-> 2575
/*      */     //   #1604	-> 2581
/*      */     //   #1605	-> 2586
/*      */     //   #1527	-> 2590
/*      */     //   #1529	-> 2593
/*      */     //   #1532	-> 2603
/*      */     //   #1533	-> 2610
/*      */     //   #1534	-> 2615
/*      */     //   #1537	-> 2618
/*      */     //   #1540	-> 2625
/*      */     //   #1538	-> 2628
/*      */     //   #1539	-> 2630
/*      */     //   #1543	-> 2643
/*      */     //   #1545	-> 2650
/*      */     //   #1547	-> 2653
/*      */     //   #1549	-> 2660
/*      */     //   #1552	-> 2670
/*      */     //   #1553	-> 2677
/*      */     //   #1554	-> 2682
/*      */     //   #1559	-> 2693
/*      */     //   #1560	-> 2700
/*      */     //   #1561	-> 2705
/*      */     //   #1562	-> 2711
/*      */     //   #1565	-> 2725
/*      */     //   #1566	-> 2735
/*      */     //   #1565	-> 2760
/*      */     //   #1568	-> 2766
/*      */     //   #1603	-> 2778
/*      */     //   #1604	-> 2784
/*      */     //   #1605	-> 2789
/*      */     //   #1568	-> 2793
/*      */     //   #1572	-> 2796
/*      */     //   #1573	-> 2813
/*      */     //   #1574	-> 2821
/*      */     //   #1575	-> 2826
/*      */     //   #1603	-> 2838
/*      */     //   #1604	-> 2844
/*      */     //   #1605	-> 2849
/*      */     //   #1575	-> 2853
/*      */     //   #1577	-> 2856
/*      */     //   #1578	-> 2859
/*      */     //   #1579	-> 2862
/*      */     //   #1582	-> 2865
/*      */     //   #1583	-> 2882
/*      */     //   #1586	-> 2904
/*      */     //   #1588	-> 2911
/*      */     //   #1589	-> 2918
/*      */     //   #1590	-> 2923
/*      */     //   #1603	-> 2935
/*      */     //   #1604	-> 2941
/*      */     //   #1605	-> 2946
/*      */     //   #1590	-> 2950
/*      */     //   #1592	-> 2953
/*      */     //   #1593	-> 2956
/*      */     //   #1594	-> 2959
/*      */     //   #1597	-> 2962
/*      */     //   #1603	-> 2976
/*      */     //   #1604	-> 2984
/*      */     //   #1605	-> 2989
/*      */     //   #1607	-> 2993
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   767	57	29	res	I
/*      */     //   1036	7	30	e	Ljava/io/IOException;
/*      */     //   1022	24	29	exception	Ljava/nio/channels/ClosedChannelException;
/*      */     //   1251	14	30	res	I
/*      */     //   1107	346	29	consumed	I
/*      */     //   533	934	24	result	Ljavax/net/ssl/SSLEngineResult;
/*      */     //   1868	110	33	res	I
/*      */     //   2031	176	33	res	I
/*      */     //   1572	840	29	preRem	J
/*      */     //   1602	810	31	userProduced	J
/*      */     //   1588	827	24	result	Ljavax/net/ssl/SSLEngineResult;
/*      */     //   2630	13	30	cause	Ljava/lang/Throwable;
/*      */     //   2610	43	29	task	Ljava/lang/Runnable;
/*      */     //   2728	38	32	i	I
/*      */     //   2670	126	29	tasks	Ljava/util/ArrayList;
/*      */     //   2677	119	30	task	Ljava/lang/Runnable;
/*      */     //   2700	96	31	size	I
/*      */     //   2445	531	24	result	Ljavax/net/ssl/SSLEngineResult;
/*      */     //   2452	524	25	handshakeStatus	Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*      */     //   0	2996	0	this	Lorg/xnio/ssl/JsseStreamConduit;
/*      */     //   0	2996	1	goal	I
/*      */     //   0	2996	2	srcs	[Ljava/nio/ByteBuffer;
/*      */     //   0	2996	3	srcOff	I
/*      */     //   0	2996	4	srcLen	I
/*      */     //   0	2996	5	dsts	[Ljava/nio/ByteBuffer;
/*      */     //   0	2996	6	dstOff	I
/*      */     //   0	2996	7	dstLen	I
/*      */     //   72	2924	8	state	I
/*      */     //   115	2881	9	engine	Ljavax/net/ssl/SSLEngine;
/*      */     //   129	2867	10	sendBuffer	Ljava/nio/ByteBuffer;
/*      */     //   143	2853	11	receiveBuffer	Ljava/nio/ByteBuffer;
/*      */     //   157	2839	12	readBuffer	Ljava/nio/ByteBuffer;
/*      */     //   173	2823	13	realDsts	[Ljava/nio/ByteBuffer;
/*      */     //   201	2795	14	remaining	J
/*      */     //   243	2753	16	wrap	Z
/*      */     //   255	2741	17	unwrap	Z
/*      */     //   258	2738	18	flushed	Z
/*      */     //   261	2735	19	eof	Z
/*      */     //   264	2732	20	readBlocked	Z
/*      */     //   267	2729	21	writeBlocked	Z
/*      */     //   270	2726	22	copiedUnwrappedBytes	Z
/*      */     //   273	2723	23	wakeupReads	Z
/*      */     //   276	2720	26	rv	I
/*      */     //   279	2717	27	xfer	J
/*      */     // Local variable type table:
/*      */     //   start	length	slot	name	signature
/*      */     //   2670	126	29	tasks	Ljava/util/ArrayList<Ljava/lang/Runnable;>;
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   296	444	2976	finally
/*      */     //   338	382	391	finally
/*      */     //   391	393	391	finally
/*      */     //   462	1743	2976	finally
/*      */     //   724	815	836	finally
/*      */     //   824	827	836	finally
/*      */     //   836	838	836	finally
/*      */     //   1022	1031	1034	java/io/IOException
/*      */     //   1202	1268	1277	finally
/*      */     //   1277	1279	1277	finally
/*      */     //   1761	2089	2976	finally
/*      */     //   1855	1978	1987	finally
/*      */     //   1987	1989	1987	finally
/*      */     //   2018	2083	2216	finally
/*      */     //   2107	2137	2216	finally
/*      */     //   2107	2143	2976	finally
/*      */     //   2161	2207	2216	finally
/*      */     //   2161	2427	2976	finally
/*      */     //   2216	2218	2216	finally
/*      */     //   2445	2575	2976	finally
/*      */     //   2593	2778	2976	finally
/*      */     //   2618	2625	2628	java/lang/Throwable
/*      */     //   2705	2714	2717	finally
/*      */     //   2717	2722	2717	finally
/*      */     //   2796	2838	2976	finally
/*      */     //   2856	2935	2976	finally
/*      */     //   2953	2978	2976	finally
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   class TaskWrapper
/*      */     implements Runnable
/*      */   {
/*      */     private final Runnable task;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     TaskWrapper(Runnable task) {
/* 1614 */       this.task = task;
/*      */     }
/*      */     
/*      */     public void run() {
/*      */       try {
/* 1619 */         this.task.run();
/*      */       } finally {
/* 1621 */         synchronized (JsseStreamConduit.this) {
/* 1622 */           if (JsseStreamConduit.this.tasks-- == 1) JsseStreamConduit.this.notifyAll(); 
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ssl\JsseStreamConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */