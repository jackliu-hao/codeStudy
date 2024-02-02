/*     */ package org.h2.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import org.h2.util.Bits;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.Utils;
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
/*     */ public class Data
/*     */ {
/*     */   private byte[] data;
/*     */   private int pos;
/*     */   
/*     */   private Data(byte[] paramArrayOfbyte) {
/*  40 */     this.data = paramArrayOfbyte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeInt(int paramInt) {
/*  50 */     Bits.writeInt(this.data, this.pos, paramInt);
/*  51 */     this.pos += 4;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int readInt() {
/*  61 */     int i = Bits.readInt(this.data, this.pos);
/*  62 */     this.pos += 4;
/*  63 */     return i;
/*     */   }
/*     */   
/*     */   private void writeStringWithoutLength(char[] paramArrayOfchar, int paramInt) {
/*  67 */     int i = this.pos;
/*  68 */     byte[] arrayOfByte = this.data;
/*  69 */     for (byte b = 0; b < paramInt; b++) {
/*  70 */       char c = paramArrayOfchar[b];
/*  71 */       if (c < '') {
/*  72 */         arrayOfByte[i++] = (byte)c;
/*  73 */       } else if (c >= 'ࠀ') {
/*  74 */         arrayOfByte[i++] = (byte)(0xE0 | c >> 12);
/*  75 */         arrayOfByte[i++] = (byte)(c >> 6 & 0x3F);
/*  76 */         arrayOfByte[i++] = (byte)(c & 0x3F);
/*     */       } else {
/*  78 */         arrayOfByte[i++] = (byte)(0xC0 | c >> 6);
/*  79 */         arrayOfByte[i++] = (byte)(c & 0x3F);
/*     */       } 
/*     */     } 
/*  82 */     this.pos = i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Data create(int paramInt) {
/*  92 */     return new Data(new byte[paramInt]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int length() {
/* 102 */     return this.pos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getBytes() {
/* 111 */     return this.data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 118 */     this.pos = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 129 */     System.arraycopy(paramArrayOfbyte, paramInt1, this.data, this.pos, paramInt2);
/* 130 */     this.pos += paramInt2;
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
/*     */   public void read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 142 */     System.arraycopy(this.data, this.pos, paramArrayOfbyte, paramInt1, paramInt2);
/* 143 */     this.pos += paramInt2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPos(int paramInt) {
/* 152 */     this.pos = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte readByte() {
/* 161 */     return this.data[this.pos++];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkCapacity(int paramInt) {
/* 171 */     if (this.pos + paramInt >= this.data.length)
/*     */     {
/* 173 */       expand(paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void expand(int paramInt) {
/* 180 */     this.data = Utils.copyBytes(this.data, (this.data.length + paramInt) * 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void fillAligned() {
/* 189 */     int i = MathUtils.roundUpInt(this.pos + 2, 16);
/* 190 */     this.pos = i;
/* 191 */     if (this.data.length < i) {
/* 192 */       checkCapacity(i - this.data.length);
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
/*     */   public static void copyString(Reader paramReader, OutputStream paramOutputStream) throws IOException {
/* 205 */     char[] arrayOfChar = new char[4096];
/* 206 */     Data data = new Data(new byte[12288]);
/*     */     while (true) {
/* 208 */       int i = paramReader.read(arrayOfChar);
/* 209 */       if (i < 0) {
/*     */         break;
/*     */       }
/* 212 */       data.writeStringWithoutLength(arrayOfChar, i);
/* 213 */       paramOutputStream.write(data.data, 0, data.pos);
/* 214 */       data.reset();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\Data.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */