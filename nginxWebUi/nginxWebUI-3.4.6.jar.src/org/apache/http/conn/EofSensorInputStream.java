/*     */ package org.apache.http.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EofSensorInputStream
/*     */   extends InputStream
/*     */   implements ConnectionReleaseTrigger
/*     */ {
/*     */   protected InputStream wrappedStream;
/*     */   private boolean selfClosed;
/*     */   private final EofSensorWatcher eofWatcher;
/*     */   
/*     */   public EofSensorInputStream(InputStream in, EofSensorWatcher watcher) {
/*  82 */     Args.notNull(in, "Wrapped stream");
/*  83 */     this.wrappedStream = in;
/*  84 */     this.selfClosed = false;
/*  85 */     this.eofWatcher = watcher;
/*     */   }
/*     */   
/*     */   boolean isSelfClosed() {
/*  89 */     return this.selfClosed;
/*     */   }
/*     */   
/*     */   InputStream getWrappedStream() {
/*  93 */     return this.wrappedStream;
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
/*     */   protected boolean isReadAllowed() throws IOException {
/* 106 */     if (this.selfClosed) {
/* 107 */       throw new IOException("Attempted read on closed stream.");
/*     */     }
/* 109 */     return (this.wrappedStream != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 114 */     int readLen = -1;
/*     */     
/* 116 */     if (isReadAllowed()) {
/*     */       try {
/* 118 */         readLen = this.wrappedStream.read();
/* 119 */         checkEOF(readLen);
/* 120 */       } catch (IOException ex) {
/* 121 */         checkAbort();
/* 122 */         throw ex;
/*     */       } 
/*     */     }
/*     */     
/* 126 */     return readLen;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 131 */     int readLen = -1;
/*     */     
/* 133 */     if (isReadAllowed()) {
/*     */       try {
/* 135 */         readLen = this.wrappedStream.read(b, off, len);
/* 136 */         checkEOF(readLen);
/* 137 */       } catch (IOException ex) {
/* 138 */         checkAbort();
/* 139 */         throw ex;
/*     */       } 
/*     */     }
/*     */     
/* 143 */     return readLen;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 148 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 153 */     int a = 0;
/*     */     
/* 155 */     if (isReadAllowed()) {
/*     */       try {
/* 157 */         a = this.wrappedStream.available();
/*     */       }
/* 159 */       catch (IOException ex) {
/* 160 */         checkAbort();
/* 161 */         throw ex;
/*     */       } 
/*     */     }
/*     */     
/* 165 */     return a;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 171 */     this.selfClosed = true;
/* 172 */     checkClose();
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
/*     */   protected void checkEOF(int eof) throws IOException {
/* 194 */     InputStream toCheckStream = this.wrappedStream;
/* 195 */     if (toCheckStream != null && eof < 0) {
/*     */       try {
/* 197 */         boolean scws = true;
/* 198 */         if (this.eofWatcher != null) {
/* 199 */           scws = this.eofWatcher.eofDetected(toCheckStream);
/*     */         }
/* 201 */         if (scws) {
/* 202 */           toCheckStream.close();
/*     */         }
/*     */       } finally {
/* 205 */         this.wrappedStream = null;
/*     */       } 
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
/*     */   protected void checkClose() throws IOException {
/* 223 */     InputStream toCloseStream = this.wrappedStream;
/* 224 */     if (toCloseStream != null) {
/*     */       try {
/* 226 */         boolean scws = true;
/* 227 */         if (this.eofWatcher != null) {
/* 228 */           scws = this.eofWatcher.streamClosed(toCloseStream);
/*     */         }
/* 230 */         if (scws) {
/* 231 */           toCloseStream.close();
/*     */         }
/*     */       } finally {
/* 234 */         this.wrappedStream = null;
/*     */       } 
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
/*     */   protected void checkAbort() throws IOException {
/* 254 */     InputStream toAbortStream = this.wrappedStream;
/* 255 */     if (toAbortStream != null) {
/*     */       try {
/* 257 */         boolean scws = true;
/* 258 */         if (this.eofWatcher != null) {
/* 259 */           scws = this.eofWatcher.streamAbort(toAbortStream);
/*     */         }
/* 261 */         if (scws) {
/* 262 */           toAbortStream.close();
/*     */         }
/*     */       } finally {
/* 265 */         this.wrappedStream = null;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void releaseConnection() throws IOException {
/* 275 */     close();
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
/*     */   public void abortConnection() throws IOException {
/* 288 */     this.selfClosed = true;
/* 289 */     checkAbort();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\EofSensorInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */