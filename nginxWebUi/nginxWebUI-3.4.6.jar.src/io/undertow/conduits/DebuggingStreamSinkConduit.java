/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.AbstractStreamSinkConduit;
/*     */ import org.xnio.conduits.ConduitWritableByteChannel;
/*     */ import org.xnio.conduits.Conduits;
/*     */ import org.xnio.conduits.StreamSinkConduit;
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
/*     */ public class DebuggingStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*  44 */   private static final List<byte[]> data = (List)new CopyOnWriteArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DebuggingStreamSinkConduit(StreamSinkConduit next) {
/*  52 */     super(next);
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  57 */     int pos = src.position();
/*  58 */     int res = super.write(src);
/*  59 */     if (res > 0) {
/*  60 */       byte[] d = new byte[res];
/*  61 */       for (int i = 0; i < res; i++) {
/*  62 */         d[i] = src.get(i + pos);
/*     */       }
/*  64 */       data.add(d);
/*     */     } 
/*  66 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] dsts, int offs, int len) throws IOException {
/*  71 */     for (int i = offs; i < len; i++) {
/*  72 */       if (dsts[i].hasRemaining()) {
/*  73 */         return write(dsts[i]);
/*     */       }
/*     */     } 
/*  76 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  81 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  86 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/*  91 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*  96 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void dump() {
/* 101 */     for (int i = 0; i < data.size(); i++) {
/* 102 */       System.out.println("Write Buffer " + i);
/* 103 */       StringBuilder sb = new StringBuilder();
/*     */       try {
/* 105 */         Buffers.dump(ByteBuffer.wrap(data.get(i)), sb, 0, 20);
/* 106 */       } catch (IOException e) {
/* 107 */         throw new RuntimeException(e);
/*     */       } 
/* 109 */       System.out.println(sb);
/* 110 */       System.out.println();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\DebuggingStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */