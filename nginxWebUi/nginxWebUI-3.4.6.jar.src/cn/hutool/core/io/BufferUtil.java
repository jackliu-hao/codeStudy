/*     */ package cn.hutool.core.io;
/*     */ 
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
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
/*     */ public class BufferUtil
/*     */ {
/*     */   public static ByteBuffer copy(ByteBuffer src, int start, int end) {
/*  29 */     return copy(src, ByteBuffer.allocate(end - start));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuffer copy(ByteBuffer src, ByteBuffer dest) {
/*  40 */     return copy(src, dest, Math.min(src.limit(), dest.remaining()));
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
/*     */   public static ByteBuffer copy(ByteBuffer src, ByteBuffer dest, int length) {
/*  52 */     return copy(src, src.position(), dest, dest.position(), length);
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
/*     */   public static ByteBuffer copy(ByteBuffer src, int srcStart, ByteBuffer dest, int destStart, int length) {
/*  66 */     System.arraycopy(src.array(), srcStart, dest.array(), destStart, length);
/*  67 */     return dest;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String readUtf8Str(ByteBuffer buffer) {
/*  78 */     return readStr(buffer, CharsetUtil.CHARSET_UTF_8);
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
/*     */   public static String readStr(ByteBuffer buffer, Charset charset) {
/*  90 */     return StrUtil.str(readBytes(buffer), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] readBytes(ByteBuffer buffer) {
/* 100 */     int remaining = buffer.remaining();
/* 101 */     byte[] ab = new byte[remaining];
/* 102 */     buffer.get(ab);
/* 103 */     return ab;
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
/*     */   public static byte[] readBytes(ByteBuffer buffer, int maxLength) {
/* 115 */     int remaining = buffer.remaining();
/* 116 */     if (maxLength > remaining) {
/* 117 */       maxLength = remaining;
/*     */     }
/* 119 */     byte[] ab = new byte[maxLength];
/* 120 */     buffer.get(ab);
/* 121 */     return ab;
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
/*     */   public static byte[] readBytes(ByteBuffer buffer, int start, int end) {
/* 133 */     byte[] bs = new byte[end - start];
/* 134 */     System.arraycopy(buffer.array(), start, bs, 0, bs.length);
/* 135 */     return bs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int lineEnd(ByteBuffer buffer) {
/* 145 */     return lineEnd(buffer, buffer.remaining());
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
/*     */   public static int lineEnd(ByteBuffer buffer, int maxLength) {
/* 162 */     int primitivePosition = buffer.position();
/* 163 */     boolean canEnd = false;
/* 164 */     int charIndex = primitivePosition;
/*     */     
/* 166 */     while (buffer.hasRemaining()) {
/* 167 */       byte b = buffer.get();
/* 168 */       charIndex++;
/* 169 */       if (b == 13)
/* 170 */       { canEnd = true; }
/* 171 */       else { if (b == 10) {
/* 172 */           return canEnd ? (charIndex - 2) : (charIndex - 1);
/*     */         }
/*     */         
/* 175 */         canEnd = false; }
/*     */ 
/*     */       
/* 178 */       if (charIndex - primitivePosition > maxLength) {
/*     */         
/* 180 */         buffer.position(primitivePosition);
/* 181 */         throw new IndexOutOfBoundsException(StrUtil.format("Position is out of maxLength: {}", new Object[] { Integer.valueOf(maxLength) }));
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 186 */     buffer.position(primitivePosition);
/*     */     
/* 188 */     return -1;
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
/*     */   public static String readLine(ByteBuffer buffer, Charset charset) {
/* 205 */     int startPosition = buffer.position();
/* 206 */     int endPosition = lineEnd(buffer);
/*     */     
/* 208 */     if (endPosition > startPosition) {
/* 209 */       byte[] bs = readBytes(buffer, startPosition, endPosition);
/* 210 */       return StrUtil.str(bs, charset);
/* 211 */     }  if (endPosition == startPosition) {
/* 212 */       return "";
/*     */     }
/*     */     
/* 215 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuffer create(byte[] data) {
/* 226 */     return ByteBuffer.wrap(data);
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
/*     */   public static ByteBuffer create(CharSequence data, Charset charset) {
/* 238 */     return create(StrUtil.bytes(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuffer createUtf8(CharSequence data) {
/* 249 */     return create(StrUtil.utf8Bytes(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CharBuffer createCharBuffer(int capacity) {
/* 260 */     return CharBuffer.allocate(capacity);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\BufferUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */