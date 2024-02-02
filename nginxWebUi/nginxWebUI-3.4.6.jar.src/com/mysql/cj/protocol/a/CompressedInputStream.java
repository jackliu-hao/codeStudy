/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompressedInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private byte[] buffer;
/*     */   private InputStream in;
/*     */   private Inflater inflater;
/*     */   private RuntimeProperty<Boolean> traceProtocol;
/*     */   private Log log;
/*  64 */   private byte[] packetHeaderBuffer = new byte[7];
/*     */ 
/*     */   
/*  67 */   private int pos = 0;
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
/*     */   public CompressedInputStream(InputStream streamFromServer, RuntimeProperty<Boolean> traceProtocol, Log log) {
/*  81 */     this.traceProtocol = traceProtocol;
/*  82 */     this.log = log;
/*  83 */     this.in = streamFromServer;
/*  84 */     this.inflater = new Inflater();
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/*  89 */     if (this.buffer == null) {
/*  90 */       return this.in.available();
/*     */     }
/*     */     
/*  93 */     return this.buffer.length - this.pos + this.in.available();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  98 */     this.in.close();
/*  99 */     this.buffer = null;
/* 100 */     this.inflater.end();
/* 101 */     this.inflater = null;
/* 102 */     this.traceProtocol = null;
/* 103 */     this.log = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void getNextPacketFromServer() throws IOException {
/* 114 */     byte[] uncompressedData = null;
/*     */     
/* 116 */     int lengthRead = readFully(this.packetHeaderBuffer, 0, 7);
/*     */     
/* 118 */     if (lengthRead < 7) {
/* 119 */       throw new IOException("Unexpected end of input stream");
/*     */     }
/*     */     
/* 122 */     int compressedPacketLength = (this.packetHeaderBuffer[0] & 0xFF) + ((this.packetHeaderBuffer[1] & 0xFF) << 8) + ((this.packetHeaderBuffer[2] & 0xFF) << 16);
/*     */ 
/*     */     
/* 125 */     int uncompressedLength = (this.packetHeaderBuffer[4] & 0xFF) + ((this.packetHeaderBuffer[5] & 0xFF) << 8) + ((this.packetHeaderBuffer[6] & 0xFF) << 16);
/*     */ 
/*     */     
/* 128 */     boolean doTrace = ((Boolean)this.traceProtocol.getValue()).booleanValue();
/*     */     
/* 130 */     if (doTrace) {
/* 131 */       this.log.logTrace("Reading compressed packet of length " + compressedPacketLength + " uncompressed to " + uncompressedLength);
/*     */     }
/*     */     
/* 134 */     if (uncompressedLength > 0) {
/* 135 */       uncompressedData = new byte[uncompressedLength];
/*     */       
/* 137 */       byte[] compressedBuffer = new byte[compressedPacketLength];
/*     */       
/* 139 */       readFully(compressedBuffer, 0, compressedPacketLength);
/*     */       
/* 141 */       this.inflater.reset();
/*     */       
/* 143 */       this.inflater.setInput(compressedBuffer);
/*     */       
/*     */       try {
/* 146 */         this.inflater.inflate(uncompressedData);
/* 147 */       } catch (DataFormatException dfe) {
/* 148 */         throw new IOException("Error while uncompressing packet from server.");
/*     */       } 
/*     */     } else {
/*     */       
/* 152 */       if (doTrace) {
/* 153 */         this.log.logTrace("Packet didn't meet compression threshold, not uncompressing...");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 159 */       uncompressedLength = compressedPacketLength;
/* 160 */       uncompressedData = new byte[uncompressedLength];
/* 161 */       readFully(uncompressedData, 0, uncompressedLength);
/*     */     } 
/*     */     
/* 164 */     if (doTrace) {
/* 165 */       if (uncompressedLength > 1024) {
/* 166 */         this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(uncompressedData, 256));
/* 167 */         byte[] tempData = new byte[256];
/* 168 */         System.arraycopy(uncompressedData, uncompressedLength - 256, tempData, 0, 256);
/* 169 */         this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(tempData, 256));
/* 170 */         this.log.logTrace("Large packet dump truncated. Showing first and last 256 bytes.");
/*     */       } else {
/* 172 */         this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(uncompressedData, uncompressedLength));
/*     */       } 
/*     */     }
/*     */     
/* 176 */     if (this.buffer != null && this.pos < this.buffer.length) {
/* 177 */       if (doTrace) {
/* 178 */         this.log.logTrace("Combining remaining packet with new: ");
/*     */       }
/*     */       
/* 181 */       int remaining = this.buffer.length - this.pos;
/* 182 */       byte[] newBuffer = new byte[remaining + uncompressedData.length];
/*     */       
/* 184 */       System.arraycopy(this.buffer, this.pos, newBuffer, 0, remaining);
/* 185 */       System.arraycopy(uncompressedData, 0, newBuffer, remaining, uncompressedData.length);
/*     */       
/* 187 */       uncompressedData = newBuffer;
/*     */     } 
/*     */     
/* 190 */     this.pos = 0;
/* 191 */     this.buffer = uncompressedData;
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
/*     */   private void getNextPacketIfRequired(int numBytes) throws IOException {
/* 207 */     if (this.buffer == null || this.pos + numBytes > this.buffer.length) {
/* 208 */       getNextPacketFromServer();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*     */     try {
/* 215 */       getNextPacketIfRequired(1);
/* 216 */     } catch (IOException ioEx) {
/* 217 */       return -1;
/*     */     } 
/*     */     
/* 220 */     return this.buffer[this.pos++] & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 225 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 230 */     if (b == null)
/* 231 */       throw new NullPointerException(); 
/* 232 */     if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
/* 233 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/* 236 */     if (len <= 0) {
/* 237 */       return 0;
/*     */     }
/*     */     
/*     */     try {
/* 241 */       getNextPacketIfRequired(len);
/* 242 */     } catch (IOException ioEx) {
/* 243 */       return -1;
/*     */     } 
/*     */     
/* 246 */     int remainingBufferLength = this.buffer.length - this.pos;
/* 247 */     int consummedBytesLength = Math.min(remainingBufferLength, len);
/*     */     
/* 249 */     System.arraycopy(this.buffer, this.pos, b, off, consummedBytesLength);
/* 250 */     this.pos += consummedBytesLength;
/*     */     
/* 252 */     return consummedBytesLength;
/*     */   }
/*     */   
/*     */   private final int readFully(byte[] b, int off, int len) throws IOException {
/* 256 */     if (len < 0) {
/* 257 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/* 260 */     int n = 0;
/*     */     
/* 262 */     while (n < len) {
/* 263 */       int count = this.in.read(b, off + n, len - n);
/*     */       
/* 265 */       if (count < 0) {
/* 266 */         throw new EOFException();
/*     */       }
/*     */       
/* 269 */       n += count;
/*     */     } 
/*     */     
/* 272 */     return n;
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 277 */     long count = 0L;
/*     */     long i;
/* 279 */     for (i = 0L; i < n; i++) {
/* 280 */       int bytesRead = read();
/*     */       
/* 282 */       if (bytesRead == -1) {
/*     */         break;
/*     */       }
/*     */       
/* 286 */       count++;
/*     */     } 
/*     */     
/* 289 */     return count;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\CompressedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */