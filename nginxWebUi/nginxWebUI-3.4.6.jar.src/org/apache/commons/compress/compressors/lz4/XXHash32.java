/*     */ package org.apache.commons.compress.compressors.lz4;
/*     */ 
/*     */ import java.util.zip.Checksum;
/*     */ import org.apache.commons.compress.utils.ByteUtils;
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
/*     */ public class XXHash32
/*     */   implements Checksum
/*     */ {
/*     */   private static final int BUF_SIZE = 16;
/*     */   private static final int ROTATE_BITS = 13;
/*     */   private static final int PRIME1 = -1640531535;
/*     */   private static final int PRIME2 = -2048144777;
/*     */   private static final int PRIME3 = -1028477379;
/*     */   private static final int PRIME4 = 668265263;
/*     */   private static final int PRIME5 = 374761393;
/*  45 */   private final byte[] oneByte = new byte[1];
/*  46 */   private final int[] state = new int[4];
/*     */ 
/*     */   
/*  49 */   private final byte[] buffer = new byte[16];
/*     */   
/*     */   private final int seed;
/*     */   
/*     */   private int totalLen;
/*     */   
/*     */   private int pos;
/*     */ 
/*     */   
/*     */   public XXHash32() {
/*  59 */     this(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XXHash32(int seed) {
/*  67 */     this.seed = seed;
/*  68 */     initializeState();
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/*  73 */     initializeState();
/*  74 */     this.totalLen = 0;
/*  75 */     this.pos = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(int b) {
/*  80 */     this.oneByte[0] = (byte)(b & 0xFF);
/*  81 */     update(this.oneByte, 0, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(byte[] b, int off, int len) {
/*  86 */     if (len <= 0) {
/*     */       return;
/*     */     }
/*  89 */     this.totalLen += len;
/*     */     
/*  91 */     int end = off + len;
/*     */     
/*  93 */     if (this.pos + len < 16) {
/*  94 */       System.arraycopy(b, off, this.buffer, this.pos, len);
/*  95 */       this.pos += len;
/*     */       
/*     */       return;
/*     */     } 
/*  99 */     if (this.pos > 0) {
/* 100 */       int size = 16 - this.pos;
/* 101 */       System.arraycopy(b, off, this.buffer, this.pos, size);
/* 102 */       process(this.buffer, 0);
/* 103 */       off += size;
/*     */     } 
/*     */     
/* 106 */     int limit = end - 16;
/* 107 */     while (off <= limit) {
/* 108 */       process(b, off);
/* 109 */       off += 16;
/*     */     } 
/*     */     
/* 112 */     if (off < end) {
/* 113 */       this.pos = end - off;
/* 114 */       System.arraycopy(b, off, this.buffer, 0, this.pos);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long getValue() {
/*     */     int hash;
/* 121 */     if (this.totalLen > 16) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 126 */       hash = Integer.rotateLeft(this.state[0], 1) + Integer.rotateLeft(this.state[1], 7) + Integer.rotateLeft(this.state[2], 12) + Integer.rotateLeft(this.state[3], 18);
/*     */     } else {
/* 128 */       hash = this.state[2] + 374761393;
/*     */     } 
/* 130 */     hash += this.totalLen;
/*     */     
/* 132 */     int idx = 0;
/* 133 */     int limit = this.pos - 4;
/* 134 */     for (; idx <= limit; idx += 4) {
/* 135 */       hash = Integer.rotateLeft(hash + getInt(this.buffer, idx) * -1028477379, 17) * 668265263;
/*     */     }
/* 137 */     while (idx < this.pos) {
/* 138 */       hash = Integer.rotateLeft(hash + (this.buffer[idx++] & 0xFF) * 374761393, 11) * -1640531535;
/*     */     }
/*     */     
/* 141 */     hash ^= hash >>> 15;
/* 142 */     hash *= -2048144777;
/* 143 */     hash ^= hash >>> 13;
/* 144 */     hash *= -1028477379;
/* 145 */     hash ^= hash >>> 16;
/* 146 */     return hash & 0xFFFFFFFFL;
/*     */   }
/*     */   
/*     */   private static int getInt(byte[] buffer, int idx) {
/* 150 */     return (int)(ByteUtils.fromLittleEndian(buffer, idx, 4) & 0xFFFFFFFFL);
/*     */   }
/*     */   
/*     */   private void initializeState() {
/* 154 */     this.state[0] = this.seed + -1640531535 + -2048144777;
/* 155 */     this.state[1] = this.seed + -2048144777;
/* 156 */     this.state[2] = this.seed;
/* 157 */     this.state[3] = this.seed - -1640531535;
/*     */   }
/*     */ 
/*     */   
/*     */   private void process(byte[] b, int offset) {
/* 162 */     int s0 = this.state[0];
/* 163 */     int s1 = this.state[1];
/* 164 */     int s2 = this.state[2];
/* 165 */     int s3 = this.state[3];
/*     */     
/* 167 */     s0 = Integer.rotateLeft(s0 + getInt(b, offset) * -2048144777, 13) * -1640531535;
/* 168 */     s1 = Integer.rotateLeft(s1 + getInt(b, offset + 4) * -2048144777, 13) * -1640531535;
/* 169 */     s2 = Integer.rotateLeft(s2 + getInt(b, offset + 8) * -2048144777, 13) * -1640531535;
/* 170 */     s3 = Integer.rotateLeft(s3 + getInt(b, offset + 12) * -2048144777, 13) * -1640531535;
/*     */     
/* 172 */     this.state[0] = s0;
/* 173 */     this.state[1] = s1;
/* 174 */     this.state[2] = s2;
/* 175 */     this.state[3] = s3;
/*     */     
/* 177 */     this.pos = 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\lz4\XXHash32.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */