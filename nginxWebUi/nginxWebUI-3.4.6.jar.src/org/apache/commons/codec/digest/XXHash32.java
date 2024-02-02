/*     */ package org.apache.commons.codec.digest;
/*     */ 
/*     */ import java.util.zip.Checksum;
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
/*  49 */   private final byte[] oneByte = new byte[1];
/*  50 */   private final int[] state = new int[4];
/*     */ 
/*     */   
/*  53 */   private final byte[] buffer = new byte[16];
/*     */   
/*     */   private final int seed;
/*     */   
/*     */   private int totalLen;
/*     */   
/*     */   private int pos;
/*     */   
/*     */   private boolean stateUpdated;
/*     */ 
/*     */   
/*     */   public XXHash32() {
/*  65 */     this(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XXHash32(int seed) {
/*  73 */     this.seed = seed;
/*  74 */     initializeState();
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/*  79 */     initializeState();
/*  80 */     this.totalLen = 0;
/*  81 */     this.pos = 0;
/*  82 */     this.stateUpdated = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(int b) {
/*  87 */     this.oneByte[0] = (byte)(b & 0xFF);
/*  88 */     update(this.oneByte, 0, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(byte[] b, int off, int len) {
/*  93 */     if (len <= 0) {
/*     */       return;
/*     */     }
/*  96 */     this.totalLen += len;
/*     */     
/*  98 */     int end = off + len;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 103 */     if (this.pos + len - 16 < 0) {
/* 104 */       System.arraycopy(b, off, this.buffer, this.pos, len);
/* 105 */       this.pos += len;
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 110 */     if (this.pos > 0) {
/* 111 */       int size = 16 - this.pos;
/* 112 */       System.arraycopy(b, off, this.buffer, this.pos, size);
/* 113 */       process(this.buffer, 0);
/* 114 */       off += size;
/*     */     } 
/*     */     
/* 117 */     int limit = end - 16;
/* 118 */     while (off <= limit) {
/* 119 */       process(b, off);
/* 120 */       off += 16;
/*     */     } 
/*     */ 
/*     */     
/* 124 */     if (off < end) {
/* 125 */       this.pos = end - off;
/* 126 */       System.arraycopy(b, off, this.buffer, 0, this.pos);
/*     */     } else {
/* 128 */       this.pos = 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long getValue() {
/*     */     int hash;
/* 135 */     if (this.stateUpdated) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 141 */       hash = Integer.rotateLeft(this.state[0], 1) + Integer.rotateLeft(this.state[1], 7) + Integer.rotateLeft(this.state[2], 12) + Integer.rotateLeft(this.state[3], 18);
/*     */     } else {
/*     */       
/* 144 */       hash = this.state[2] + 374761393;
/*     */     } 
/* 146 */     hash += this.totalLen;
/*     */     
/* 148 */     int idx = 0;
/* 149 */     int limit = this.pos - 4;
/* 150 */     for (; idx <= limit; idx += 4) {
/* 151 */       hash = Integer.rotateLeft(hash + getInt(this.buffer, idx) * -1028477379, 17) * 668265263;
/*     */     }
/* 153 */     while (idx < this.pos) {
/* 154 */       hash = Integer.rotateLeft(hash + (this.buffer[idx++] & 0xFF) * 374761393, 11) * -1640531535;
/*     */     }
/*     */     
/* 157 */     hash ^= hash >>> 15;
/* 158 */     hash *= -2048144777;
/* 159 */     hash ^= hash >>> 13;
/* 160 */     hash *= -1028477379;
/* 161 */     hash ^= hash >>> 16;
/* 162 */     return hash & 0xFFFFFFFFL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getInt(byte[] buffer, int idx) {
/* 173 */     return buffer[idx] & 0xFF | (buffer[idx + 1] & 0xFF) << 8 | (buffer[idx + 2] & 0xFF) << 16 | (buffer[idx + 3] & 0xFF) << 24;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void initializeState() {
/* 180 */     this.state[0] = this.seed + -1640531535 + -2048144777;
/* 181 */     this.state[1] = this.seed + -2048144777;
/* 182 */     this.state[2] = this.seed;
/* 183 */     this.state[3] = this.seed - -1640531535;
/*     */   }
/*     */ 
/*     */   
/*     */   private void process(byte[] b, int offset) {
/* 188 */     int s0 = this.state[0];
/* 189 */     int s1 = this.state[1];
/* 190 */     int s2 = this.state[2];
/* 191 */     int s3 = this.state[3];
/*     */     
/* 193 */     s0 = Integer.rotateLeft(s0 + getInt(b, offset) * -2048144777, 13) * -1640531535;
/* 194 */     s1 = Integer.rotateLeft(s1 + getInt(b, offset + 4) * -2048144777, 13) * -1640531535;
/* 195 */     s2 = Integer.rotateLeft(s2 + getInt(b, offset + 8) * -2048144777, 13) * -1640531535;
/* 196 */     s3 = Integer.rotateLeft(s3 + getInt(b, offset + 12) * -2048144777, 13) * -1640531535;
/*     */     
/* 198 */     this.state[0] = s0;
/* 199 */     this.state[1] = s1;
/* 200 */     this.state[2] = s2;
/* 201 */     this.state[3] = s3;
/*     */     
/* 203 */     this.stateUpdated = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\digest\XXHash32.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */