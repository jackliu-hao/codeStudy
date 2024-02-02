/*     */ package cn.hutool.core.io;
/*     */ 
/*     */ import cn.hutool.core.io.copy.ChannelCopier;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.MappedByteBuffer;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.nio.charset.Charset;
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
/*     */ public class NioUtil
/*     */ {
/*     */   public static final int DEFAULT_BUFFER_SIZE = 8192;
/*     */   public static final int DEFAULT_MIDDLE_BUFFER_SIZE = 16384;
/*     */   public static final int DEFAULT_LARGE_BUFFER_SIZE = 32768;
/*     */   public static final int EOF = -1;
/*     */   
/*     */   public static long copyByNIO(InputStream in, OutputStream out, int bufferSize, StreamProgress streamProgress) throws IORuntimeException {
/*  56 */     return copyByNIO(in, out, bufferSize, -1L, streamProgress);
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
/*     */   public static long copyByNIO(InputStream in, OutputStream out, int bufferSize, long count, StreamProgress streamProgress) throws IORuntimeException {
/*  73 */     return copy(Channels.newChannel(in), Channels.newChannel(out), bufferSize, count, streamProgress);
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
/*     */   public static long copy(FileChannel inChannel, FileChannel outChannel) throws IORuntimeException {
/*  86 */     Assert.notNull(inChannel, "In channel is null!", new Object[0]);
/*  87 */     Assert.notNull(outChannel, "Out channel is null!", new Object[0]);
/*     */     
/*     */     try {
/*  90 */       return copySafely(inChannel, outChannel);
/*  91 */     } catch (IOException e) {
/*  92 */       throw new IORuntimeException(e);
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
/*     */   private static long copySafely(FileChannel inChannel, FileChannel outChannel) throws IOException {
/* 124 */     long totalBytes = inChannel.size(); long remaining;
/* 125 */     for (long pos = 0L; remaining > 0L; ) {
/* 126 */       long writeBytes = inChannel.transferTo(pos, remaining, outChannel);
/* 127 */       pos += writeBytes;
/* 128 */       remaining -= writeBytes;
/*     */     } 
/* 130 */     return totalBytes;
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
/*     */   public static long copy(ReadableByteChannel in, WritableByteChannel out) throws IORuntimeException {
/* 143 */     return copy(in, out, 8192);
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
/*     */   public static long copy(ReadableByteChannel in, WritableByteChannel out, int bufferSize) throws IORuntimeException {
/* 157 */     return copy(in, out, bufferSize, null);
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
/*     */   public static long copy(ReadableByteChannel in, WritableByteChannel out, int bufferSize, StreamProgress streamProgress) throws IORuntimeException {
/* 171 */     return copy(in, out, bufferSize, -1L, streamProgress);
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
/*     */   public static long copy(ReadableByteChannel in, WritableByteChannel out, int bufferSize, long count, StreamProgress streamProgress) throws IORuntimeException {
/* 187 */     return (new ChannelCopier(bufferSize, count, streamProgress)).copy(in, out);
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
/*     */   public static String read(ReadableByteChannel channel, Charset charset) throws IORuntimeException {
/* 200 */     FastByteArrayOutputStream out = read(channel);
/* 201 */     return (null == charset) ? out.toString() : out.toString(charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FastByteArrayOutputStream read(ReadableByteChannel channel) throws IORuntimeException {
/* 212 */     FastByteArrayOutputStream out = new FastByteArrayOutputStream();
/* 213 */     copy(channel, Channels.newChannel(out));
/* 214 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String readUtf8(FileChannel fileChannel) throws IORuntimeException {
/* 225 */     return read(fileChannel, CharsetUtil.CHARSET_UTF_8);
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
/*     */   public static String read(FileChannel fileChannel, String charsetName) throws IORuntimeException {
/* 237 */     return read(fileChannel, CharsetUtil.charset(charsetName));
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
/*     */   public static String read(FileChannel fileChannel, Charset charset) throws IORuntimeException {
/*     */     MappedByteBuffer buffer;
/*     */     try {
/* 251 */       buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, fileChannel.size()).load();
/* 252 */     } catch (IOException e) {
/* 253 */       throw new IORuntimeException(e);
/*     */     } 
/* 255 */     return StrUtil.str(buffer, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void close(AutoCloseable closeable) {
/* 265 */     if (null != closeable)
/*     */       try {
/* 267 */         closeable.close();
/* 268 */       } catch (Exception exception) {} 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\NioUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */