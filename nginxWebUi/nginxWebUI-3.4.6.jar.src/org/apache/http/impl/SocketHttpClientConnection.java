/*     */ package org.apache.http.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import org.apache.http.HttpInetConnection;
/*     */ import org.apache.http.impl.io.SocketInputBuffer;
/*     */ import org.apache.http.impl.io.SocketOutputBuffer;
/*     */ import org.apache.http.io.SessionInputBuffer;
/*     */ import org.apache.http.io.SessionOutputBuffer;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class SocketHttpClientConnection
/*     */   extends AbstractHttpClientConnection
/*     */   implements HttpInetConnection
/*     */ {
/*     */   private volatile boolean open;
/*  61 */   private volatile Socket socket = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void assertNotOpen() {
/*  68 */     Asserts.check(!this.open, "Connection is already open");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void assertOpen() {
/*  73 */     Asserts.check(this.open, "Connection is not open");
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
/*     */   protected SessionInputBuffer createSessionInputBuffer(Socket socket, int bufferSize, HttpParams params) throws IOException {
/*  95 */     return (SessionInputBuffer)new SocketInputBuffer(socket, bufferSize, params);
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
/*     */   protected SessionOutputBuffer createSessionOutputBuffer(Socket socket, int bufferSize, HttpParams params) throws IOException {
/* 117 */     return (SessionOutputBuffer)new SocketOutputBuffer(socket, bufferSize, params);
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
/*     */ 
/*     */   
/*     */   protected void bind(Socket socket, HttpParams params) throws IOException {
/* 141 */     Args.notNull(socket, "Socket");
/* 142 */     Args.notNull(params, "HTTP parameters");
/* 143 */     this.socket = socket;
/*     */     
/* 145 */     int bufferSize = params.getIntParameter("http.socket.buffer-size", -1);
/* 146 */     init(createSessionInputBuffer(socket, bufferSize, params), createSessionOutputBuffer(socket, bufferSize, params), params);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 151 */     this.open = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 156 */     return this.open;
/*     */   }
/*     */   
/*     */   protected Socket getSocket() {
/* 160 */     return this.socket;
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getLocalAddress() {
/* 165 */     if (this.socket != null) {
/* 166 */       return this.socket.getLocalAddress();
/*     */     }
/* 168 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLocalPort() {
/* 173 */     if (this.socket != null) {
/* 174 */       return this.socket.getLocalPort();
/*     */     }
/* 176 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getRemoteAddress() {
/* 181 */     if (this.socket != null) {
/* 182 */       return this.socket.getInetAddress();
/*     */     }
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRemotePort() {
/* 189 */     if (this.socket != null) {
/* 190 */       return this.socket.getPort();
/*     */     }
/* 192 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSocketTimeout(int timeout) {
/* 197 */     assertOpen();
/* 198 */     if (this.socket != null) {
/*     */       try {
/* 200 */         this.socket.setSoTimeout(timeout);
/* 201 */       } catch (SocketException ignore) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSocketTimeout() {
/* 211 */     if (this.socket != null) {
/*     */       try {
/* 213 */         return this.socket.getSoTimeout();
/* 214 */       } catch (SocketException ignore) {
/* 215 */         return -1;
/*     */       } 
/*     */     }
/* 218 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() throws IOException {
/* 223 */     this.open = false;
/* 224 */     Socket tmpsocket = this.socket;
/* 225 */     if (tmpsocket != null) {
/* 226 */       tmpsocket.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 232 */     if (!this.open) {
/*     */       return;
/*     */     }
/* 235 */     this.open = false;
/* 236 */     Socket sock = this.socket;
/*     */     try {
/* 238 */       doFlush();
/*     */       try {
/*     */         try {
/* 241 */           sock.shutdownOutput();
/* 242 */         } catch (IOException ignore) {}
/*     */ 
/*     */         
/*     */         try {
/* 246 */           sock.shutdownInput();
/* 247 */         } catch (IOException ignore) {}
/*     */       
/*     */       }
/* 250 */       catch (UnsupportedOperationException ignore) {}
/*     */     }
/*     */     finally {
/*     */       
/* 254 */       sock.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void formatAddress(StringBuilder buffer, SocketAddress socketAddress) {
/* 259 */     if (socketAddress instanceof InetSocketAddress) {
/* 260 */       InetSocketAddress addr = (InetSocketAddress)socketAddress;
/* 261 */       buffer.append((addr.getAddress() != null) ? addr.getAddress().getHostAddress() : addr.getAddress()).append(':').append(addr.getPort());
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 266 */       buffer.append(socketAddress);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 272 */     if (this.socket != null) {
/* 273 */       StringBuilder buffer = new StringBuilder();
/* 274 */       SocketAddress remoteAddress = this.socket.getRemoteSocketAddress();
/* 275 */       SocketAddress localAddress = this.socket.getLocalSocketAddress();
/* 276 */       if (remoteAddress != null && localAddress != null) {
/* 277 */         formatAddress(buffer, localAddress);
/* 278 */         buffer.append("<->");
/* 279 */         formatAddress(buffer, remoteAddress);
/*     */       } 
/* 281 */       return buffer.toString();
/*     */     } 
/* 283 */     return super.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\SocketHttpClientConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */