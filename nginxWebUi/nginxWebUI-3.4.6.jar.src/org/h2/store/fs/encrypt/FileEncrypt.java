/*     */ package org.h2.store.fs.encrypt;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Arrays;
/*     */ import org.h2.security.AES;
/*     */ import org.h2.security.BlockCipher;
/*     */ import org.h2.security.SHA256;
/*     */ import org.h2.store.fs.FileBaseDefault;
/*     */ import org.h2.util.MathUtils;
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
/*     */ public class FileEncrypt
/*     */   extends FileBaseDefault
/*     */ {
/*     */   public static final int BLOCK_SIZE = 4096;
/*     */   static final int BLOCK_SIZE_MASK = 4095;
/*     */   static final int HEADER_LENGTH = 4096;
/*  41 */   private static final byte[] HEADER = "H2encrypt\n".getBytes(StandardCharsets.ISO_8859_1);
/*  42 */   private static final int SALT_POS = HEADER.length;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int SALT_LENGTH = 8;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int HASH_ITERATIONS = 10;
/*     */ 
/*     */ 
/*     */   
/*     */   private final FileChannel base;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile long size;
/*     */ 
/*     */   
/*     */   private final String name;
/*     */ 
/*     */   
/*     */   private volatile XTS xts;
/*     */ 
/*     */   
/*     */   private byte[] encryptionKey;
/*     */ 
/*     */ 
/*     */   
/*     */   public FileEncrypt(String paramString, byte[] paramArrayOfbyte, FileChannel paramFileChannel) {
/*  72 */     this.name = paramString;
/*  73 */     this.base = paramFileChannel;
/*  74 */     this.encryptionKey = paramArrayOfbyte;
/*     */   }
/*     */ 
/*     */   
/*     */   private XTS init() throws IOException {
/*  79 */     XTS xTS = this.xts;
/*  80 */     if (xTS == null) {
/*  81 */       xTS = createXTS();
/*     */     }
/*  83 */     return xTS;
/*     */   }
/*     */   private synchronized XTS createXTS() throws IOException {
/*     */     byte[] arrayOfByte;
/*  87 */     XTS xTS = this.xts;
/*  88 */     if (xTS != null) {
/*  89 */       return xTS;
/*     */     }
/*  91 */     this.size = this.base.size() - 4096L;
/*  92 */     boolean bool = (this.size < 0L) ? true : false;
/*     */     
/*  94 */     if (bool) {
/*  95 */       byte[] arrayOfByte1 = Arrays.copyOf(HEADER, 4096);
/*  96 */       arrayOfByte = MathUtils.secureRandomBytes(8);
/*  97 */       System.arraycopy(arrayOfByte, 0, arrayOfByte1, SALT_POS, arrayOfByte.length);
/*  98 */       writeFully(this.base, 0L, ByteBuffer.wrap(arrayOfByte1));
/*  99 */       this.size = 0L;
/*     */     } else {
/* 101 */       arrayOfByte = new byte[8];
/* 102 */       readFully(this.base, SALT_POS, ByteBuffer.wrap(arrayOfByte));
/* 103 */       if ((this.size & 0xFFFL) != 0L) {
/* 104 */         this.size -= 4096L;
/*     */       }
/*     */     } 
/* 107 */     AES aES = new AES();
/* 108 */     aES.setKey(SHA256.getPBKDF2(this.encryptionKey, arrayOfByte, 10, 16));
/* 109 */     this.encryptionKey = null;
/* 110 */     return this.xts = new XTS((BlockCipher)aES);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void implCloseChannel() throws IOException {
/* 115 */     this.base.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/* 120 */     int i = paramByteBuffer.remaining();
/* 121 */     if (i == 0) {
/* 122 */       return 0;
/*     */     }
/* 124 */     XTS xTS = init();
/* 125 */     i = (int)Math.min(i, this.size - paramLong);
/* 126 */     if (paramLong >= this.size)
/* 127 */       return -1; 
/* 128 */     if (paramLong < 0L) {
/* 129 */       throw new IllegalArgumentException("pos: " + paramLong);
/*     */     }
/* 131 */     if ((paramLong & 0xFFFL) != 0L || (i & 0xFFF) != 0) {
/*     */ 
/*     */       
/* 134 */       long l = paramLong / 4096L * 4096L;
/* 135 */       int j = (int)(paramLong - l);
/* 136 */       int k = (i + j + 4096 - 1) / 4096 * 4096;
/* 137 */       ByteBuffer byteBuffer = ByteBuffer.allocate(k);
/* 138 */       readInternal(byteBuffer, l, k, xTS);
/* 139 */       byteBuffer.flip().limit(j + i).position(j);
/* 140 */       paramByteBuffer.put(byteBuffer);
/* 141 */       return i;
/*     */     } 
/* 143 */     readInternal(paramByteBuffer, paramLong, i, xTS);
/* 144 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readInternal(ByteBuffer paramByteBuffer, long paramLong, int paramInt, XTS paramXTS) throws IOException {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: invokevirtual position : ()I
/*     */     //   4: istore #6
/*     */     //   6: aload_0
/*     */     //   7: getfield base : Ljava/nio/channels/FileChannel;
/*     */     //   10: lload_2
/*     */     //   11: ldc2_w 4096
/*     */     //   14: ladd
/*     */     //   15: aload_1
/*     */     //   16: invokestatic readFully : (Ljava/nio/channels/FileChannel;JLjava/nio/ByteBuffer;)V
/*     */     //   19: lload_2
/*     */     //   20: ldc2_w 4096
/*     */     //   23: ldiv
/*     */     //   24: lstore #7
/*     */     //   26: iload #4
/*     */     //   28: ifle -> 72
/*     */     //   31: aload #5
/*     */     //   33: lload #7
/*     */     //   35: dup2
/*     */     //   36: lconst_1
/*     */     //   37: ladd
/*     */     //   38: lstore #7
/*     */     //   40: sipush #4096
/*     */     //   43: aload_1
/*     */     //   44: invokevirtual array : ()[B
/*     */     //   47: aload_1
/*     */     //   48: invokevirtual arrayOffset : ()I
/*     */     //   51: iload #6
/*     */     //   53: iadd
/*     */     //   54: invokevirtual decrypt : (JI[BI)V
/*     */     //   57: wide iinc #6 4096
/*     */     //   63: wide iinc #4 -4096
/*     */     //   69: goto -> 26
/*     */     //   72: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #148	-> 0
/*     */     //   #149	-> 6
/*     */     //   #150	-> 19
/*     */     //   #151	-> 26
/*     */     //   #152	-> 31
/*     */     //   #153	-> 57
/*     */     //   #154	-> 63
/*     */     //   #156	-> 72
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void readFully(FileChannel paramFileChannel, long paramLong, ByteBuffer paramByteBuffer) throws IOException {
/*     */     do {
/* 160 */       int i = paramFileChannel.read(paramByteBuffer, paramLong);
/* 161 */       if (i < 0) {
/* 162 */         throw new EOFException();
/*     */       }
/* 164 */       paramLong += i;
/* 165 */     } while (paramByteBuffer.remaining() > 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/* 170 */     XTS xTS = init();
/* 171 */     int i = paramByteBuffer.remaining();
/* 172 */     if ((paramLong & 0xFFFL) != 0L || (i & 0xFFF) != 0) {
/*     */ 
/*     */       
/* 175 */       long l1 = paramLong / 4096L * 4096L;
/* 176 */       int j = (int)(paramLong - l1);
/* 177 */       int k = (i + j + 4096 - 1) / 4096 * 4096;
/* 178 */       ByteBuffer byteBuffer = ByteBuffer.allocate(k);
/* 179 */       int m = (int)(this.size - l1 + 4096L - 1L) / 4096 * 4096;
/* 180 */       int n = Math.min(k, m);
/* 181 */       if (n > 0) {
/* 182 */         readInternal(byteBuffer, l1, n, xTS);
/* 183 */         byteBuffer.rewind();
/*     */       } 
/* 185 */       byteBuffer.limit(j + i).position(j);
/* 186 */       byteBuffer.put(paramByteBuffer).limit(k).rewind();
/* 187 */       writeInternal(byteBuffer, l1, k, xTS);
/* 188 */       long l2 = paramLong + i;
/* 189 */       this.size = Math.max(this.size, l2);
/* 190 */       int i1 = (int)(this.size & 0xFFFL);
/* 191 */       if (i1 > 0) {
/* 192 */         byteBuffer = ByteBuffer.allocate(i1);
/* 193 */         writeFully(this.base, l1 + 4096L + k, byteBuffer);
/*     */       } 
/* 195 */       return i;
/*     */     } 
/* 197 */     writeInternal(paramByteBuffer, paramLong, i, xTS);
/* 198 */     long l = paramLong + i;
/* 199 */     this.size = Math.max(this.size, l);
/* 200 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeInternal(ByteBuffer paramByteBuffer, long paramLong, int paramInt, XTS paramXTS) throws IOException {
/*     */     // Byte code:
/*     */     //   0: iload #4
/*     */     //   2: invokestatic allocate : (I)Ljava/nio/ByteBuffer;
/*     */     //   5: aload_1
/*     */     //   6: invokevirtual put : (Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */     //   9: astore #6
/*     */     //   11: aload #6
/*     */     //   13: invokevirtual flip : ()Ljava/nio/Buffer;
/*     */     //   16: pop
/*     */     //   17: lload_2
/*     */     //   18: ldc2_w 4096
/*     */     //   21: ldiv
/*     */     //   22: lstore #7
/*     */     //   24: iconst_0
/*     */     //   25: istore #9
/*     */     //   27: iload #4
/*     */     //   29: istore #10
/*     */     //   31: iload #10
/*     */     //   33: ifle -> 79
/*     */     //   36: aload #5
/*     */     //   38: lload #7
/*     */     //   40: dup2
/*     */     //   41: lconst_1
/*     */     //   42: ladd
/*     */     //   43: lstore #7
/*     */     //   45: sipush #4096
/*     */     //   48: aload #6
/*     */     //   50: invokevirtual array : ()[B
/*     */     //   53: aload #6
/*     */     //   55: invokevirtual arrayOffset : ()I
/*     */     //   58: iload #9
/*     */     //   60: iadd
/*     */     //   61: invokevirtual encrypt : (JI[BI)V
/*     */     //   64: wide iinc #9 4096
/*     */     //   70: wide iinc #10 -4096
/*     */     //   76: goto -> 31
/*     */     //   79: aload_0
/*     */     //   80: getfield base : Ljava/nio/channels/FileChannel;
/*     */     //   83: lload_2
/*     */     //   84: ldc2_w 4096
/*     */     //   87: ladd
/*     */     //   88: aload #6
/*     */     //   90: invokestatic writeFully : (Ljava/nio/channels/FileChannel;JLjava/nio/ByteBuffer;)V
/*     */     //   93: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #204	-> 0
/*     */     //   #205	-> 11
/*     */     //   #206	-> 17
/*     */     //   #207	-> 24
/*     */     //   #208	-> 31
/*     */     //   #209	-> 36
/*     */     //   #210	-> 64
/*     */     //   #211	-> 70
/*     */     //   #213	-> 79
/*     */     //   #214	-> 93
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void writeFully(FileChannel paramFileChannel, long paramLong, ByteBuffer paramByteBuffer) throws IOException {
/*     */     do {
/* 218 */       paramLong += paramFileChannel.write(paramByteBuffer, paramLong);
/* 219 */     } while (paramByteBuffer.remaining() > 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() throws IOException {
/* 224 */     init();
/* 225 */     return this.size;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void implTruncate(long paramLong) throws IOException {
/* 230 */     init();
/* 231 */     if (paramLong > this.size) {
/*     */       return;
/*     */     }
/* 234 */     if (paramLong < 0L) {
/* 235 */       throw new IllegalArgumentException("newSize: " + paramLong);
/*     */     }
/* 237 */     int i = (int)(paramLong & 0xFFFL);
/* 238 */     if (i > 0) {
/* 239 */       this.base.truncate(paramLong + 4096L + 4096L);
/*     */     } else {
/* 241 */       this.base.truncate(paramLong + 4096L);
/*     */     } 
/* 243 */     this.size = paramLong;
/*     */   }
/*     */ 
/*     */   
/*     */   public void force(boolean paramBoolean) throws IOException {
/* 248 */     this.base.force(paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
/* 253 */     return this.base.tryLock(paramLong1, paramLong2, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 258 */     return this.name;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\encrypt\FileEncrypt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */