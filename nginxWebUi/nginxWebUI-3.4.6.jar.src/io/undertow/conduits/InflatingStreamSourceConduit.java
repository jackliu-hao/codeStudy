/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import io.undertow.util.NewInstanceObjectPool;
/*     */ import io.undertow.util.ObjectPool;
/*     */ import io.undertow.util.PooledObject;
/*     */ import io.undertow.util.SimpleObjectPool;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.conduits.AbstractStreamSourceConduit;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.ConduitReadableByteChannel;
/*     */ import org.xnio.conduits.StreamSourceConduit;
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
/*     */ public class InflatingStreamSourceConduit
/*     */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*     */ {
/*  49 */   public static final ConduitWrapper<StreamSourceConduit> WRAPPER = new ConduitWrapper<StreamSourceConduit>()
/*     */     {
/*     */       public StreamSourceConduit wrap(ConduitFactory<StreamSourceConduit> factory, HttpServerExchange exchange) {
/*  52 */         return (StreamSourceConduit)new InflatingStreamSourceConduit(exchange, (StreamSourceConduit)factory.create());
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   private volatile Inflater inflater;
/*     */   private final PooledObject<Inflater> pooledObject;
/*     */   private final HttpServerExchange exchange;
/*     */   private PooledByteBuffer compressed;
/*     */   private PooledByteBuffer uncompressed;
/*     */   private boolean nextDone = false;
/*     */   private boolean headerDone = false;
/*     */   
/*     */   public InflatingStreamSourceConduit(HttpServerExchange exchange, StreamSourceConduit next) {
/*  66 */     this(exchange, next, newInstanceInflaterPool());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InflatingStreamSourceConduit(HttpServerExchange exchange, StreamSourceConduit next, ObjectPool<Inflater> inflaterPool) {
/*  73 */     super(next);
/*  74 */     this.exchange = exchange;
/*  75 */     this.pooledObject = inflaterPool.allocate();
/*  76 */     this.inflater = (Inflater)this.pooledObject.getObject();
/*     */   }
/*     */   
/*     */   public static ObjectPool<Inflater> newInstanceInflaterPool() {
/*  80 */     return (ObjectPool<Inflater>)new NewInstanceObjectPool(() -> new Inflater(true), Inflater::end);
/*     */   }
/*     */   
/*     */   public static ObjectPool<Inflater> simpleInflaterPool(int poolSize) {
/*  84 */     return (ObjectPool<Inflater>)new SimpleObjectPool(poolSize, () -> new Inflater(true), Inflater::reset, Inflater::end);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*  89 */     if (isReadShutdown()) {
/*  90 */       throw new ClosedChannelException();
/*     */     }
/*  92 */     if (this.uncompressed != null) {
/*  93 */       int ret = Buffers.copy(dst, this.uncompressed.getBuffer());
/*  94 */       if (!this.uncompressed.getBuffer().hasRemaining()) {
/*  95 */         this.uncompressed.close();
/*  96 */         this.uncompressed = null;
/*     */       } 
/*  98 */       return ret;
/*     */     } 
/*     */     while (true) {
/* 101 */       if (this.compressed == null && !this.nextDone) {
/* 102 */         this.compressed = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate();
/* 103 */         ByteBuffer buf = this.compressed.getBuffer();
/* 104 */         int res = ((StreamSourceConduit)this.next).read(buf);
/* 105 */         if (res == -1)
/* 106 */         { this.nextDone = true;
/* 107 */           this.compressed.close();
/* 108 */           this.compressed = null; }
/* 109 */         else { if (res == 0) {
/* 110 */             this.compressed.close();
/* 111 */             this.compressed = null;
/* 112 */             return 0;
/*     */           } 
/* 114 */           buf.flip();
/* 115 */           if (!this.headerDone) {
/* 116 */             this.headerDone = readHeader(buf);
/*     */           }
/* 118 */           this.inflater.setInput(buf.array(), buf.arrayOffset() + buf.position(), buf.remaining()); }
/*     */       
/*     */       } 
/* 121 */       if (this.nextDone && this.inflater.needsInput() && !this.inflater.finished())
/* 122 */         throw UndertowLogger.ROOT_LOGGER.unexpectedEndOfCompressedInput(); 
/* 123 */       if (this.nextDone && this.inflater.finished()) {
/* 124 */         done();
/* 125 */         return -1;
/* 126 */       }  if (this.inflater.finished() && this.compressed != null) {
/* 127 */         int rem = this.inflater.getRemaining();
/* 128 */         ByteBuffer buf = this.compressed.getBuffer();
/* 129 */         buf.position(buf.limit() - rem);
/* 130 */         readFooter(buf);
/*     */         
/*     */         while (true)
/* 133 */         { buf.clear();
/* 134 */           int res = ((StreamSourceConduit)this.next).read(buf);
/* 135 */           buf.flip();
/* 136 */           if (res == -1) {
/* 137 */             done();
/* 138 */             this.nextDone = true;
/* 139 */             return -1;
/* 140 */           }  if (res > 0) {
/* 141 */             readFooter(buf);
/*     */           }
/* 143 */           if (res == 0)
/* 144 */           { this.compressed.close();
/* 145 */             this.compressed = null;
/* 146 */             return 0; }  }  break;
/* 147 */       }  if (this.compressed == null) {
/* 148 */         throw new RuntimeException();
/*     */       }
/* 150 */       this.uncompressed = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate();
/*     */       try {
/* 152 */         int read = this.inflater.inflate(this.uncompressed.getBuffer().array(), this.uncompressed.getBuffer().arrayOffset(), this.uncompressed.getBuffer().limit());
/* 153 */         this.uncompressed.getBuffer().limit(read);
/* 154 */         dataDeflated(this.uncompressed.getBuffer().array(), this.uncompressed.getBuffer().arrayOffset(), read);
/* 155 */         if (this.inflater.needsInput()) {
/* 156 */           this.compressed.close();
/* 157 */           this.compressed = null;
/*     */         } 
/* 159 */         int ret = Buffers.copy(dst, this.uncompressed.getBuffer());
/* 160 */         if (!this.uncompressed.getBuffer().hasRemaining()) {
/* 161 */           this.uncompressed.close();
/* 162 */           this.uncompressed = null;
/*     */         } 
/* 164 */         if (ret > 0) {
/* 165 */           return ret;
/*     */         }
/* 167 */       } catch (DataFormatException e) {
/* 168 */         done();
/* 169 */         throw new IOException(e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void readFooter(ByteBuffer buf) throws IOException {}
/*     */ 
/*     */   
/*     */   protected boolean readHeader(ByteBuffer byteBuffer) throws IOException {
/* 179 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void dataDeflated(byte[] data, int off, int len) {}
/*     */ 
/*     */   
/*     */   private void done() {
/* 187 */     if (this.compressed != null) {
/* 188 */       this.compressed.close();
/*     */     }
/* 190 */     if (this.uncompressed != null) {
/* 191 */       this.uncompressed.close();
/*     */     }
/* 193 */     if (this.inflater != null) {
/* 194 */       this.pooledObject.close();
/* 195 */       this.inflater = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*     */     try {
/* 201 */       return target.transferFrom((ReadableByteChannel)new ConduitReadableByteChannel((StreamSourceConduit)this), position, count);
/* 202 */     } catch (IOException|RuntimeException|Error e) {
/* 203 */       IoUtils.safeClose((Closeable)this.exchange.getConnection());
/* 204 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*     */     try {
/* 211 */       return IoUtils.transfer((ReadableByteChannel)new ConduitReadableByteChannel((StreamSourceConduit)this), count, throughBuffer, (WritableByteChannel)target);
/* 212 */     } catch (IOException|RuntimeException|Error e) {
/* 213 */       IoUtils.safeClose((Closeable)this.exchange.getConnection());
/* 214 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 221 */     for (int i = offset; i < length; i++) {
/* 222 */       if (dsts[i].hasRemaining()) {
/* 223 */         return read(dsts[i]);
/*     */       }
/*     */     } 
/* 226 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateReads() throws IOException {
/* 231 */     done();
/* 232 */     ((StreamSourceConduit)this.next).terminateReads();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\InflatingStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */