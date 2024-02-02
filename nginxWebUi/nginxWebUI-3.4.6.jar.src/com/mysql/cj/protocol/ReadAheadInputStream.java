/*     */ package com.mysql.cj.protocol;
/*     */ 
/*     */ import com.mysql.cj.log.Log;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ public class ReadAheadInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*     */   private InputStream underlyingStream;
/*     */   private byte[] buf;
/*     */   protected int endOfCurrentData;
/*     */   protected int currentPosition;
/*     */   protected boolean doDebug = false;
/*     */   protected Log log;
/*     */   
/*     */   private void fill(int readAtLeastTheseManyBytes) throws IOException {
/*  58 */     checkClosed();
/*     */     
/*  60 */     this.currentPosition = 0;
/*     */     
/*  62 */     this.endOfCurrentData = this.currentPosition;
/*     */ 
/*     */ 
/*     */     
/*  66 */     int bytesToRead = Math.min(this.buf.length - this.currentPosition, readAtLeastTheseManyBytes);
/*     */     
/*  68 */     int bytesAvailable = this.underlyingStream.available();
/*     */     
/*  70 */     if (bytesAvailable > bytesToRead)
/*     */     {
/*     */ 
/*     */       
/*  74 */       bytesToRead = Math.min(this.buf.length - this.currentPosition, bytesAvailable);
/*     */     }
/*     */     
/*  77 */     if (this.doDebug) {
/*  78 */       StringBuilder debugBuf = new StringBuilder();
/*  79 */       debugBuf.append("  ReadAheadInputStream.fill(");
/*  80 */       debugBuf.append(readAtLeastTheseManyBytes);
/*  81 */       debugBuf.append("), buffer_size=");
/*  82 */       debugBuf.append(this.buf.length);
/*  83 */       debugBuf.append(", current_position=");
/*  84 */       debugBuf.append(this.currentPosition);
/*  85 */       debugBuf.append(", need to read ");
/*  86 */       debugBuf.append(Math.min(this.buf.length - this.currentPosition, readAtLeastTheseManyBytes));
/*  87 */       debugBuf.append(" bytes to fill request,");
/*     */       
/*  89 */       if (bytesAvailable > 0) {
/*  90 */         debugBuf.append(" underlying InputStream reports ");
/*  91 */         debugBuf.append(bytesAvailable);
/*     */         
/*  93 */         debugBuf.append(" total bytes available,");
/*     */       } 
/*     */       
/*  96 */       debugBuf.append(" attempting to read ");
/*  97 */       debugBuf.append(bytesToRead);
/*  98 */       debugBuf.append(" bytes.");
/*     */       
/* 100 */       if (this.log != null) {
/* 101 */         this.log.logTrace(debugBuf.toString());
/*     */       } else {
/* 103 */         System.err.println(debugBuf.toString());
/*     */       } 
/*     */     } 
/*     */     
/* 107 */     int n = this.underlyingStream.read(this.buf, this.currentPosition, bytesToRead);
/*     */     
/* 109 */     if (n > 0) {
/* 110 */       this.endOfCurrentData = n + this.currentPosition;
/*     */     }
/*     */   }
/*     */   
/*     */   private int readFromUnderlyingStreamIfNecessary(byte[] b, int off, int len) throws IOException {
/* 115 */     checkClosed();
/*     */     
/* 117 */     int avail = this.endOfCurrentData - this.currentPosition;
/*     */     
/* 119 */     if (this.doDebug) {
/* 120 */       StringBuilder debugBuf = new StringBuilder();
/* 121 */       debugBuf.append("ReadAheadInputStream.readIfNecessary(");
/* 122 */       debugBuf.append(Arrays.toString(b));
/* 123 */       debugBuf.append(",");
/* 124 */       debugBuf.append(off);
/* 125 */       debugBuf.append(",");
/* 126 */       debugBuf.append(len);
/* 127 */       debugBuf.append(")");
/*     */       
/* 129 */       if (avail <= 0) {
/* 130 */         debugBuf.append(" not all data available in buffer, must read from stream");
/*     */         
/* 132 */         if (len >= this.buf.length) {
/* 133 */           debugBuf.append(", amount requested > buffer, returning direct read() from stream");
/*     */         }
/*     */       } 
/*     */       
/* 137 */       if (this.log != null) {
/* 138 */         this.log.logTrace(debugBuf.toString());
/*     */       } else {
/* 140 */         System.err.println(debugBuf.toString());
/*     */       } 
/*     */     } 
/*     */     
/* 144 */     if (avail <= 0) {
/*     */       
/* 146 */       if (len >= this.buf.length) {
/* 147 */         return this.underlyingStream.read(b, off, len);
/*     */       }
/*     */       
/* 150 */       fill(len);
/*     */       
/* 152 */       avail = this.endOfCurrentData - this.currentPosition;
/*     */       
/* 154 */       if (avail <= 0) {
/* 155 */         return -1;
/*     */       }
/*     */     } 
/*     */     
/* 159 */     int bytesActuallyRead = (avail < len) ? avail : len;
/*     */     
/* 161 */     System.arraycopy(this.buf, this.currentPosition, b, off, bytesActuallyRead);
/*     */     
/* 163 */     this.currentPosition += bytesActuallyRead;
/*     */     
/* 165 */     return bytesActuallyRead;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized int read(byte[] b, int off, int len) throws IOException {
/* 170 */     checkClosed();
/* 171 */     if ((off | len | off + len | b.length - off + len) < 0)
/* 172 */       throw new IndexOutOfBoundsException(); 
/* 173 */     if (len == 0) {
/* 174 */       return 0;
/*     */     }
/*     */     
/* 177 */     int totalBytesRead = 0;
/*     */     
/*     */     do {
/* 180 */       int bytesReadThisRound = readFromUnderlyingStreamIfNecessary(b, off + totalBytesRead, len - totalBytesRead);
/*     */ 
/*     */       
/* 183 */       if (bytesReadThisRound <= 0) {
/* 184 */         if (totalBytesRead == 0) {
/* 185 */           totalBytesRead = bytesReadThisRound;
/*     */         }
/*     */         
/*     */         break;
/*     */       } 
/*     */       
/* 191 */       totalBytesRead += bytesReadThisRound;
/*     */ 
/*     */       
/* 194 */       if (totalBytesRead >= len)
/*     */       {
/*     */         break;
/*     */       }
/*     */     }
/* 199 */     while (this.underlyingStream.available() > 0);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 204 */     return totalBytesRead;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 209 */     checkClosed();
/*     */     
/* 211 */     if (this.currentPosition >= this.endOfCurrentData) {
/* 212 */       fill(1);
/* 213 */       if (this.currentPosition >= this.endOfCurrentData) {
/* 214 */         return -1;
/*     */       }
/*     */     } 
/*     */     
/* 218 */     return this.buf[this.currentPosition++] & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 223 */     checkClosed();
/*     */     
/* 225 */     return this.underlyingStream.available() + this.endOfCurrentData - this.currentPosition;
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkClosed() throws IOException {
/* 230 */     if (this.buf == null) {
/* 231 */       throw new IOException("Stream closed");
/*     */     }
/*     */   }
/*     */   
/*     */   public ReadAheadInputStream(InputStream toBuffer, boolean debug, Log logTo) {
/* 236 */     this(toBuffer, 4096, debug, logTo);
/*     */   }
/*     */   
/*     */   public ReadAheadInputStream(InputStream toBuffer, int bufferSize, boolean debug, Log logTo) {
/* 240 */     this.underlyingStream = toBuffer;
/* 241 */     this.buf = new byte[bufferSize];
/* 242 */     this.doDebug = debug;
/* 243 */     this.log = logTo;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 248 */     if (this.underlyingStream != null) {
/*     */       try {
/* 250 */         this.underlyingStream.close();
/*     */       } finally {
/* 252 */         this.underlyingStream = null;
/* 253 */         this.buf = null;
/* 254 */         this.log = null;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/* 261 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 266 */     checkClosed();
/* 267 */     if (n <= 0L) {
/* 268 */       return 0L;
/*     */     }
/*     */     
/* 271 */     long bytesAvailInBuffer = (this.endOfCurrentData - this.currentPosition);
/*     */     
/* 273 */     if (bytesAvailInBuffer <= 0L) {
/*     */       
/* 275 */       fill((int)n);
/* 276 */       bytesAvailInBuffer = (this.endOfCurrentData - this.currentPosition);
/* 277 */       if (bytesAvailInBuffer <= 0L) {
/* 278 */         return 0L;
/*     */       }
/*     */     } 
/*     */     
/* 282 */     long bytesSkipped = (bytesAvailInBuffer < n) ? bytesAvailInBuffer : n;
/* 283 */     this.currentPosition = (int)(this.currentPosition + bytesSkipped);
/* 284 */     return bytesSkipped;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\ReadAheadInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */