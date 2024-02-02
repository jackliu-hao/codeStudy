/*    */ package io.undertow.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.FileChannel;
/*    */ import java.nio.channels.ReadableByteChannel;
/*    */ import java.nio.channels.WritableByteChannel;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.CopyOnWriteArrayList;
/*    */ import org.xnio.Buffers;
/*    */ import org.xnio.IoUtils;
/*    */ import org.xnio.channels.StreamSinkChannel;
/*    */ import org.xnio.conduits.AbstractStreamSourceConduit;
/*    */ import org.xnio.conduits.ConduitReadableByteChannel;
/*    */ import org.xnio.conduits.StreamSourceConduit;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DebuggingStreamSourceConduit
/*    */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*    */ {
/* 43 */   private static final List<byte[]> data = (List)new CopyOnWriteArrayList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DebuggingStreamSourceConduit(StreamSourceConduit next) {
/* 51 */     super(next);
/*    */   }
/*    */   
/*    */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 55 */     return target.transferFrom((ReadableByteChannel)new ConduitReadableByteChannel((StreamSourceConduit)this), position, count);
/*    */   }
/*    */   
/*    */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 59 */     return IoUtils.transfer((ReadableByteChannel)new ConduitReadableByteChannel((StreamSourceConduit)this), count, throughBuffer, (WritableByteChannel)target);
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(ByteBuffer dst) throws IOException {
/* 64 */     int pos = dst.position();
/* 65 */     int res = super.read(dst);
/* 66 */     if (res > 0) {
/* 67 */       byte[] d = new byte[res];
/* 68 */       for (int i = 0; i < res; i++) {
/* 69 */         d[i] = dst.get(i + pos);
/*    */       }
/* 71 */       data.add(d);
/*    */     } 
/* 73 */     return res;
/*    */   }
/*    */ 
/*    */   
/*    */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 78 */     for (int i = offs; i < len; i++) {
/* 79 */       if (dsts[i].hasRemaining()) {
/* 80 */         return read(dsts[i]);
/*    */       }
/*    */     } 
/* 83 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public static void dump() {
/* 88 */     for (int i = 0; i < data.size(); i++) {
/* 89 */       System.out.println("Buffer " + i);
/* 90 */       StringBuilder sb = new StringBuilder();
/*    */       try {
/* 92 */         Buffers.dump(ByteBuffer.wrap(data.get(i)), sb, 0, 20);
/* 93 */       } catch (IOException e) {
/* 94 */         throw new RuntimeException(e);
/*    */       } 
/* 96 */       System.out.println(sb);
/* 97 */       System.out.println();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\DebuggingStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */