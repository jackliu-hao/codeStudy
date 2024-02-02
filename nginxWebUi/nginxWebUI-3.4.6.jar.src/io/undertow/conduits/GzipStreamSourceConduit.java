/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import io.undertow.util.ObjectPool;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.Inflater;
/*     */ import org.xnio.conduits.Conduit;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GzipStreamSourceConduit
/*     */   extends InflatingStreamSourceConduit
/*     */ {
/*  39 */   public static final ConduitWrapper<StreamSourceConduit> WRAPPER = new ConduitWrapper<StreamSourceConduit>()
/*     */     {
/*     */       public StreamSourceConduit wrap(ConduitFactory<StreamSourceConduit> factory, HttpServerExchange exchange) {
/*  42 */         return (StreamSourceConduit)new GzipStreamSourceConduit(exchange, (StreamSourceConduit)factory.create());
/*     */       }
/*     */     };
/*     */   
/*     */   private static final int GZIP_MAGIC = 35615;
/*  47 */   private static final byte[] HEADER = new byte[] { 31, -117, 8, 0, 0, 0, 0, 0, 0, 0 };
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
/*  59 */   private final CRC32 crc = new CRC32();
/*     */   private int totalOut; private int headerRead; private int footerRead; byte[] expectedFooter;
/*     */   
/*  62 */   public GzipStreamSourceConduit(HttpServerExchange exchange, StreamSourceConduit next) { super(exchange, next);
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
/*  73 */     this.headerRead = 0;
/*  74 */     this.footerRead = 0; } public GzipStreamSourceConduit(HttpServerExchange exchange, StreamSourceConduit next, ObjectPool<Inflater> inflaterPool) { super(exchange, next, inflaterPool); this.headerRead = 0; this.footerRead = 0; }
/*     */ 
/*     */   
/*     */   protected boolean readHeader(ByteBuffer headerData) throws IOException {
/*  78 */     while (this.headerRead < HEADER.length && headerData.hasRemaining()) {
/*  79 */       byte data = headerData.get();
/*  80 */       if (this.headerRead == 0 && data != HEADER[0])
/*  81 */         throw UndertowMessages.MESSAGES.invalidGzipHeader(); 
/*  82 */       if (this.headerRead == 1 && data != HEADER[1]) {
/*  83 */         throw UndertowMessages.MESSAGES.invalidGzipHeader();
/*     */       }
/*  85 */       this.headerRead++;
/*     */     } 
/*  87 */     return (this.headerRead == HEADER.length);
/*     */   }
/*     */   
/*     */   protected void readFooter(ByteBuffer buf) throws IOException {
/*  91 */     if (this.expectedFooter == null) {
/*  92 */       byte[] ret = new byte[8];
/*  93 */       int checksum = (int)this.crc.getValue();
/*  94 */       int total = this.totalOut;
/*  95 */       ret[0] = (byte)(checksum & 0xFF);
/*  96 */       ret[1] = (byte)(checksum >> 8 & 0xFF);
/*  97 */       ret[2] = (byte)(checksum >> 16 & 0xFF);
/*  98 */       ret[3] = (byte)(checksum >> 24 & 0xFF);
/*  99 */       ret[4] = (byte)(total & 0xFF);
/* 100 */       ret[5] = (byte)(total >> 8 & 0xFF);
/* 101 */       ret[6] = (byte)(total >> 16 & 0xFF);
/* 102 */       ret[7] = (byte)(total >> 24 & 0xFF);
/* 103 */       this.expectedFooter = ret;
/*     */     } 
/* 105 */     while (buf.hasRemaining() && this.footerRead < this.expectedFooter.length) {
/* 106 */       byte data = buf.get();
/* 107 */       if (this.expectedFooter[this.footerRead++] != data) {
/* 108 */         throw UndertowMessages.MESSAGES.invalidGZIPFooter();
/*     */       }
/*     */     } 
/*     */     
/* 112 */     if (buf.hasRemaining() && this.footerRead == this.expectedFooter.length) {
/* 113 */       throw UndertowMessages.MESSAGES.invalidGZIPFooter();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void dataDeflated(byte[] data, int off, int len) {
/* 118 */     this.crc.update(data, off, len);
/* 119 */     this.totalOut += len;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\GzipStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */