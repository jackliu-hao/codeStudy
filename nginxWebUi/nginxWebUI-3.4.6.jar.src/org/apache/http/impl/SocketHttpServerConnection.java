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
/*     */ @Deprecated
/*     */ public class SocketHttpServerConnection
/*     */   extends AbstractHttpServerConnection
/*     */   implements HttpInetConnection
/*     */ {
/*     */   private volatile boolean open;
/*  55 */   private volatile Socket socket = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void assertNotOpen() {
/*  62 */     Asserts.check(!this.open, "Connection is already open");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void assertOpen() {
/*  67 */     Asserts.check(this.open, "Connection is not open");
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
/*  89 */     return (SessionInputBuffer)new SocketInputBuffer(socket, bufferSize, params);
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
/* 111 */     return (SessionOutputBuffer)new SocketOutputBuffer(socket, bufferSize, params);
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
/*     */   protected void bind(Socket socket, HttpParams params) throws IOException {
/* 133 */     Args.notNull(socket, "Socket");
/* 134 */     Args.notNull(params, "HTTP parameters");
/* 135 */     this.socket = socket;
/*     */     
/* 137 */     int bufferSize = params.getIntParameter("http.socket.buffer-size", -1);
/* 138 */     init(createSessionInputBuffer(socket, bufferSize, params), createSessionOutputBuffer(socket, bufferSize, params), params);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 143 */     this.open = true;
/*     */   }
/*     */   
/*     */   protected Socket getSocket() {
/* 147 */     return this.socket;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 152 */     return this.open;
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getLocalAddress() {
/* 157 */     if (this.socket != null) {
/* 158 */       return this.socket.getLocalAddress();
/*     */     }
/* 160 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLocalPort() {
/* 165 */     if (this.socket != null) {
/* 166 */       return this.socket.getLocalPort();
/*     */     }
/* 168 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getRemoteAddress() {
/* 173 */     if (this.socket != null) {
/* 174 */       return this.socket.getInetAddress();
/*     */     }
/* 176 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRemotePort() {
/* 181 */     if (this.socket != null) {
/* 182 */       return this.socket.getPort();
/*     */     }
/* 184 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSocketTimeout(int timeout) {
/* 189 */     assertOpen();
/* 190 */     if (this.socket != null) {
/*     */       try {
/* 192 */         this.socket.setSoTimeout(timeout);
/* 193 */       } catch (SocketException ignore) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSocketTimeout() {
/* 203 */     if (this.socket != null) {
/*     */       try {
/* 205 */         return this.socket.getSoTimeout();
/* 206 */       } catch (SocketException ignore) {
/* 207 */         return -1;
/*     */       } 
/*     */     }
/* 210 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() throws IOException {
/* 215 */     this.open = false;
/* 216 */     Socket tmpsocket = this.socket;
/* 217 */     if (tmpsocket != null) {
/* 218 */       tmpsocket.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 224 */     if (!this.open) {
/*     */       return;
/*     */     }
/* 227 */     this.open = false;
/* 228 */     this.open = false;
/* 229 */     Socket sock = this.socket;
/*     */     try {
/* 231 */       doFlush();
/*     */       try {
/*     */         try {
/* 234 */           sock.shutdownOutput();
/* 235 */         } catch (IOException ignore) {}
/*     */         
/*     */         try {
/* 238 */           sock.shutdownInput();
/* 239 */         } catch (IOException ignore) {}
/*     */       }
/* 241 */       catch (UnsupportedOperationException ignore) {}
/*     */     }
/*     */     finally {
/*     */       
/* 245 */       sock.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void formatAddress(StringBuilder buffer, SocketAddress socketAddress) {
/* 250 */     if (socketAddress instanceof InetSocketAddress) {
/* 251 */       InetSocketAddress addr = (InetSocketAddress)socketAddress;
/* 252 */       buffer.append((addr.getAddress() != null) ? addr.getAddress().getHostAddress() : addr.getAddress()).append(':').append(addr.getPort());
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 257 */       buffer.append(socketAddress);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 263 */     if (this.socket != null) {
/* 264 */       StringBuilder buffer = new StringBuilder();
/* 265 */       SocketAddress remoteAddress = this.socket.getRemoteSocketAddress();
/* 266 */       SocketAddress localAddress = this.socket.getLocalSocketAddress();
/* 267 */       if (remoteAddress != null && localAddress != null) {
/* 268 */         formatAddress(buffer, localAddress);
/* 269 */         buffer.append("<->");
/* 270 */         formatAddress(buffer, remoteAddress);
/*     */       } 
/* 272 */       return buffer.toString();
/*     */     } 
/* 274 */     return super.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\SocketHttpServerConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */