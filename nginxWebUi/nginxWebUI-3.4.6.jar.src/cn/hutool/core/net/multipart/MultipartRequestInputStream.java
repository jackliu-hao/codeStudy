/*     */ package cn.hutool.core.net.multipart;
/*     */ 
/*     */ import cn.hutool.core.io.FastByteArrayOutputStream;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultipartRequestInputStream
/*     */   extends BufferedInputStream
/*     */ {
/*     */   protected byte[] boundary;
/*     */   protected UploadFileHeader lastHeader;
/*     */   
/*     */   public MultipartRequestInputStream(InputStream in) {
/*  21 */     super(in);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte readByte() throws IOException {
/*  31 */     int i = read();
/*  32 */     if (i == -1) {
/*  33 */       throw new IOException("End of HTTP request stream reached");
/*     */     }
/*  35 */     return (byte)i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void skipBytes(long i) throws IOException {
/*  45 */     long len = skip(i);
/*  46 */     if (len != i) {
/*  47 */       throw new IOException("Unable to skip data in HTTP request");
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
/*     */   public byte[] readBoundary() throws IOException {
/*  65 */     ByteArrayOutputStream boundaryOutput = new ByteArrayOutputStream(1024);
/*     */     
/*     */     byte b;
/*     */     
/*  69 */     while ((b = readByte()) <= 32);
/*     */     
/*  71 */     boundaryOutput.write(b);
/*     */ 
/*     */     
/*  74 */     while ((b = readByte()) != 13) {
/*  75 */       boundaryOutput.write(b);
/*     */     }
/*  77 */     if (boundaryOutput.size() == 0) {
/*  78 */       throw new IOException("Problems with parsing request: invalid boundary");
/*     */     }
/*  80 */     skipBytes(1L);
/*  81 */     this.boundary = new byte[boundaryOutput.size() + 2];
/*  82 */     System.arraycopy(boundaryOutput.toByteArray(), 0, this.boundary, 2, this.boundary.length - 2);
/*  83 */     this.boundary[0] = 13;
/*  84 */     this.boundary[1] = 10;
/*  85 */     return this.boundary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UploadFileHeader getLastHeader() {
/*  93 */     return this.lastHeader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UploadFileHeader readDataHeader(Charset encoding) throws IOException {
/* 104 */     String dataHeader = readDataHeaderString(encoding);
/* 105 */     if (dataHeader != null) {
/* 106 */       this.lastHeader = new UploadFileHeader(dataHeader);
/*     */     } else {
/* 108 */       this.lastHeader = null;
/*     */     } 
/* 110 */     return this.lastHeader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String readDataHeaderString(Charset charset) throws IOException {
/* 121 */     ByteArrayOutputStream data = new ByteArrayOutputStream();
/*     */     
/*     */     while (true) {
/*     */       byte b;
/* 125 */       while ((b = readByte()) != 13) {
/* 126 */         data.write(b);
/*     */       }
/*     */       
/* 129 */       mark(4);
/* 130 */       skipBytes(1L);
/* 131 */       int i = read();
/* 132 */       if (i == -1)
/*     */       {
/* 134 */         return null;
/*     */       }
/* 136 */       if (i == 13) {
/* 137 */         reset();
/*     */         break;
/*     */       } 
/* 140 */       reset();
/* 141 */       data.write(b);
/*     */     } 
/* 143 */     skipBytes(3L);
/* 144 */     return (charset == null) ? data.toString() : data.toString(charset.name());
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
/*     */   public String readString(Charset charset) throws IOException {
/* 156 */     FastByteArrayOutputStream out = new FastByteArrayOutputStream();
/* 157 */     copy((OutputStream)out);
/* 158 */     return out.toString(charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long copy(OutputStream out) throws IOException {
/* 169 */     long count = 0L;
/*     */     while (true) {
/* 171 */       byte b = readByte();
/* 172 */       if (isBoundary(b)) {
/*     */         break;
/*     */       }
/* 175 */       out.write(b);
/* 176 */       count++;
/*     */     } 
/* 178 */     return count;
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
/*     */   public long copy(OutputStream out, long limit) throws IOException {
/* 190 */     long count = 0L;
/*     */     do {
/* 192 */       byte b = readByte();
/* 193 */       if (isBoundary(b)) {
/*     */         break;
/*     */       }
/* 196 */       out.write(b);
/* 197 */       count++;
/* 198 */     } while (count <= limit);
/*     */ 
/*     */ 
/*     */     
/* 202 */     return count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long skipToBoundary() throws IOException {
/*     */     byte b;
/* 212 */     long count = 0L;
/*     */     do {
/* 214 */       b = readByte();
/* 215 */       count++;
/* 216 */     } while (!isBoundary(b));
/*     */ 
/*     */ 
/*     */     
/* 220 */     return count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBoundary(byte b) throws IOException {
/* 229 */     int boundaryLen = this.boundary.length;
/* 230 */     mark(boundaryLen + 1);
/* 231 */     int bpos = 0;
/* 232 */     while (b == this.boundary[bpos]) {
/* 233 */       b = readByte();
/* 234 */       bpos++;
/* 235 */       if (bpos == boundaryLen) {
/* 236 */         return true;
/*     */       }
/*     */     } 
/* 239 */     reset();
/* 240 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\multipart\MultipartRequestInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */