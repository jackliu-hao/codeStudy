/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOError;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Locale;
/*     */ import org.xnio.Buffers;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Conduits
/*     */ {
/*     */   private static final FileChannel NULL_FILE_CHANNEL;
/*     */   
/*     */   public static long transfer(StreamSourceConduit source, long count, ByteBuffer throughBuffer, WritableByteChannel sink) throws IOException {
/*  59 */     long total = 0L;
/*  60 */     throughBuffer.limit(0);
/*  61 */     while (total < count) {
/*  62 */       throughBuffer.compact();
/*     */       try {
/*  64 */         if (count - total < throughBuffer.remaining()) {
/*  65 */           throughBuffer.limit((int)(count - total));
/*     */         }
/*  67 */         long l = source.read(throughBuffer);
/*  68 */         if (l <= 0L) {
/*  69 */           return (total == 0L) ? l : total;
/*     */         }
/*     */       } finally {
/*  72 */         throughBuffer.flip();
/*     */       } 
/*  74 */       long res = sink.write(throughBuffer);
/*  75 */       if (res == 0L) {
/*  76 */         return total;
/*     */       }
/*  78 */       total += res;
/*     */     } 
/*  80 */     return total;
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
/*     */   public static long transfer(ReadableByteChannel source, long count, ByteBuffer throughBuffer, StreamSinkConduit sink) throws IOException {
/* 100 */     long total = 0L;
/* 101 */     throughBuffer.limit(0);
/* 102 */     while (total < count) {
/* 103 */       throughBuffer.compact();
/*     */       try {
/* 105 */         if (count - total < throughBuffer.remaining()) {
/* 106 */           throughBuffer.limit((int)(count - total));
/*     */         }
/* 108 */         long l = source.read(throughBuffer);
/* 109 */         if (l <= 0L) {
/* 110 */           return (total == 0L) ? l : total;
/*     */         }
/*     */       } finally {
/* 113 */         throughBuffer.flip();
/*     */       } 
/* 115 */       long res = sink.write(throughBuffer);
/* 116 */       if (res == 0L) {
/* 117 */         return total;
/*     */       }
/* 119 */       total += res;
/*     */     } 
/* 121 */     return total;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int writeFinalBasic(StreamSinkConduit conduit, ByteBuffer src) throws IOException {
/* 132 */     int res = conduit.write(src);
/* 133 */     if (!src.hasRemaining()) {
/* 134 */       conduit.terminateWrites();
/*     */     }
/* 136 */     return res;
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
/*     */   public static long writeFinalBasic(StreamSinkConduit conduit, ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 149 */     long res = conduit.write(srcs, offset, length);
/* 150 */     if (!Buffers.hasRemaining((Buffer[])srcs, offset, length)) {
/* 151 */       conduit.terminateWrites();
/*     */     }
/* 153 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean sendFinalBasic(MessageSinkConduit conduit, ByteBuffer src) throws IOException {
/* 163 */     if (conduit.send(src)) {
/* 164 */       conduit.terminateWrites();
/* 165 */       return true;
/*     */     } 
/* 167 */     return false;
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
/*     */   public static boolean sendFinalBasic(MessageSinkConduit conduit, ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 179 */     if (conduit.send(srcs, offset, length)) {
/* 180 */       conduit.terminateWrites();
/* 181 */       return true;
/*     */     } 
/* 183 */     return false;
/*     */   }
/*     */ 
/*     */   
/* 187 */   private static final ByteBuffer DRAIN_BUFFER = ByteBuffer.allocateDirect(16384);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long drain(StreamSourceConduit conduit, long count) throws IOException {
/* 198 */     long total = 0L;
/*     */     
/* 200 */     ByteBuffer buffer = null;
/*     */     while (true) {
/* 202 */       if (count == 0L) return total; 
/* 203 */       if (NULL_FILE_CHANNEL != null) {
/* 204 */         long lres; while (count > 0L && (
/* 205 */           lres = conduit.transferTo(0L, count, NULL_FILE_CHANNEL)) != 0L) {
/*     */ 
/*     */           
/* 208 */           total += lres;
/* 209 */           count -= lres;
/*     */         } 
/*     */         
/* 212 */         if (total > 0L) return total; 
/*     */       } 
/* 214 */       if (buffer == null) buffer = DRAIN_BUFFER.duplicate(); 
/* 215 */       if (buffer.limit() > count) buffer.limit((int)count); 
/* 216 */       int ires = conduit.read(buffer);
/* 217 */       buffer.clear();
/* 218 */       switch (ires) { case -1:
/* 219 */           return (total == 0L) ? -1L : total;
/* 220 */         case 0: return total; }
/* 221 */        total += ires;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static {
/* 227 */     NULL_FILE_CHANNEL = AccessController.<FileChannel>doPrivileged(new PrivilegedAction<FileChannel>() {
/*     */           public FileChannel run() {
/* 229 */             String osName = System.getProperty("os.name", "unknown").toLowerCase(Locale.US);
/*     */             try {
/* 231 */               if (osName.contains("windows")) {
/* 232 */                 return (new FileOutputStream("NUL:")).getChannel();
/*     */               }
/* 234 */               return (new FileOutputStream("/dev/null")).getChannel();
/*     */             }
/* 236 */             catch (FileNotFoundException e) {
/* 237 */               throw new IOError(e);
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\Conduits.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */