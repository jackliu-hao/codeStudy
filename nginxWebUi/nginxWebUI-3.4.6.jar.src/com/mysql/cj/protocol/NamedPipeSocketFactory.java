/*     */ package com.mysql.cj.protocol;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.Session;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.log.Log;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NamedPipeSocketFactory
/*     */   implements SocketFactory
/*     */ {
/*     */   private static final int DEFAULT_TIMEOUT = 100;
/*     */   private Socket namedPipeSocket;
/*     */   
/*     */   class NamedPipeSocket
/*     */     extends Socket
/*     */   {
/*     */     private boolean isClosed = false;
/*     */     private RandomAccessFile namedPipeFile;
/*     */     
/*     */     NamedPipeSocket(String filePath, int timeout) throws IOException {
/*  64 */       if (filePath == null || filePath.length() == 0) {
/*  65 */         throw new IOException(Messages.getString("NamedPipeSocketFactory.4"));
/*     */       }
/*     */       
/*  68 */       int timeoutCountdown = (timeout == 0) ? 100 : timeout;
/*  69 */       long startTime = System.currentTimeMillis();
/*     */       while (true) {
/*     */         try {
/*  72 */           this.namedPipeFile = new RandomAccessFile(filePath, "rw");
/*     */           break;
/*  74 */         } catch (FileNotFoundException e) {
/*  75 */           if (timeout == 0) {
/*  76 */             throw new IOException("Named pipe busy error (ERROR_PIPE_BUSY).\nConsider setting a value for 'connectTimeout' or DriverManager.setLoginTimeout(int) to repeatedly try opening the named pipe before failing.", e);
/*     */           }
/*     */           
/*  79 */           if (System.currentTimeMillis() - startTime > timeoutCountdown) {
/*  80 */             throw e;
/*     */           }
/*     */           
/*     */           try {
/*  84 */             TimeUnit.MILLISECONDS.sleep(10L);
/*  85 */           } catch (InterruptedException interruptedException) {
/*  86 */             throw new IOException(interruptedException);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public synchronized void close() throws IOException {
/*  96 */       this.namedPipeFile.close();
/*  97 */       this.isClosed = true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public InputStream getInputStream() throws IOException {
/* 105 */       return new NamedPipeSocketFactory.RandomAccessFileInputStream(this.namedPipeFile);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public OutputStream getOutputStream() throws IOException {
/* 113 */       return new NamedPipeSocketFactory.RandomAccessFileOutputStream(this.namedPipeFile);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isClosed() {
/* 121 */       return this.isClosed;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void shutdownInput() throws IOException {}
/*     */   }
/*     */ 
/*     */   
/*     */   class RandomAccessFileInputStream
/*     */     extends InputStream
/*     */   {
/*     */     RandomAccessFile raFile;
/*     */ 
/*     */     
/*     */     RandomAccessFileInputStream(RandomAccessFile file) {
/* 137 */       this.raFile = file;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int available() throws IOException {
/* 145 */       return -1;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 153 */       this.raFile.close();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int read() throws IOException {
/* 161 */       return this.raFile.read();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int read(byte[] b) throws IOException {
/* 169 */       return this.raFile.read(b);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int read(byte[] b, int off, int len) throws IOException {
/* 177 */       return this.raFile.read(b, off, len);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   class RandomAccessFileOutputStream
/*     */     extends OutputStream
/*     */   {
/*     */     RandomAccessFile raFile;
/*     */     
/*     */     RandomAccessFileOutputStream(RandomAccessFile file) {
/* 188 */       this.raFile = file;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 196 */       this.raFile.close();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void write(byte[] b) throws IOException {
/* 204 */       this.raFile.write(b);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void write(byte[] b, int off, int len) throws IOException {
/* 212 */       this.raFile.write(b, off, len);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void write(int b) throws IOException {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends java.io.Closeable> T performTlsHandshake(SocketConnection socketConnection, ServerSession serverSession) throws IOException {
/* 234 */     return performTlsHandshake(socketConnection, serverSession, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends java.io.Closeable> T performTlsHandshake(SocketConnection socketConnection, ServerSession serverSession, Log log) throws IOException {
/* 240 */     return (T)this.namedPipeSocket;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends java.io.Closeable> T connect(String host, int portNumber, PropertySet props, int loginTimeout) throws IOException {
/* 246 */     String namedPipePath = null;
/*     */     
/* 248 */     RuntimeProperty<String> path = props.getStringProperty(PropertyKey.PATH);
/* 249 */     if (path != null) {
/* 250 */       namedPipePath = (String)path.getValue();
/*     */     }
/*     */     
/* 253 */     if (namedPipePath == null) {
/* 254 */       namedPipePath = "\\\\.\\pipe\\MySQL";
/* 255 */     } else if (namedPipePath.length() == 0) {
/* 256 */       throw new SocketException(
/* 257 */           Messages.getString("NamedPipeSocketFactory.2") + PropertyKey.PATH.getCcAlias() + Messages.getString("NamedPipeSocketFactory.3"));
/*     */     } 
/*     */     
/* 260 */     int connectTimeout = ((Integer)props.getIntegerProperty(PropertyKey.connectTimeout.getKeyName()).getValue()).intValue();
/* 261 */     int timeout = (connectTimeout > 0 && loginTimeout > 0) ? Math.min(connectTimeout, loginTimeout) : (connectTimeout + loginTimeout);
/*     */     
/* 263 */     this.namedPipeSocket = new NamedPipeSocket(namedPipePath, timeout);
/*     */     
/* 265 */     return (T)this.namedPipeSocket;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLocallyConnected(Session sess) {
/* 271 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\NamedPipeSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */