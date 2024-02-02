/*     */ package org.h2.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.BindException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.security.CipherFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetUtils
/*     */ {
/*     */   private static final int CACHE_MILLIS = 1000;
/*     */   private static InetAddress cachedBindAddress;
/*     */   private static String cachedLocalAddress;
/*     */   private static long cachedLocalAddressTime;
/*     */   
/*     */   public static Socket createLoopbackSocket(int paramInt, boolean paramBoolean) throws IOException {
/*  47 */     String str = getLocalAddress();
/*     */     try {
/*  49 */       return createSocket(str, paramInt, paramBoolean);
/*  50 */     } catch (IOException iOException) {
/*     */       try {
/*  52 */         return createSocket("localhost", paramInt, paramBoolean);
/*  53 */       } catch (IOException iOException1) {
/*     */         
/*  55 */         throw iOException;
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
/*     */   public static Socket createSocket(String paramString, int paramInt, boolean paramBoolean) throws IOException {
/*  71 */     return createSocket(paramString, paramInt, paramBoolean, 0);
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
/*     */   public static Socket createSocket(String paramString, int paramInt1, boolean paramBoolean, int paramInt2) throws IOException {
/*  87 */     int i = paramInt1;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  92 */     boolean bool = paramString.startsWith("[") ? paramString.indexOf(']') : false;
/*  93 */     int j = paramString.indexOf(':', bool);
/*  94 */     if (j >= 0) {
/*  95 */       i = Integer.decode(paramString.substring(j + 1)).intValue();
/*  96 */       paramString = paramString.substring(0, j);
/*     */     } 
/*  98 */     InetAddress inetAddress = InetAddress.getByName(paramString);
/*  99 */     return createSocket(inetAddress, i, paramBoolean, paramInt2);
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
/*     */   public static Socket createSocket(InetAddress paramInetAddress, int paramInt, boolean paramBoolean) throws IOException {
/* 113 */     return createSocket(paramInetAddress, paramInt, paramBoolean, 0);
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
/*     */   public static Socket createSocket(InetAddress paramInetAddress, int paramInt1, boolean paramBoolean, int paramInt2) throws IOException {
/* 127 */     long l = System.nanoTime();
/* 128 */     for (byte b = 0;; b++) {
/*     */       try {
/* 130 */         if (paramBoolean) {
/* 131 */           return CipherFactory.createSocket(paramInetAddress, paramInt1);
/*     */         }
/* 133 */         Socket socket = new Socket();
/* 134 */         socket.setSoTimeout(paramInt2);
/* 135 */         socket.connect(new InetSocketAddress(paramInetAddress, paramInt1), SysProperties.SOCKET_CONNECT_TIMEOUT);
/*     */         
/* 137 */         return socket;
/* 138 */       } catch (IOException iOException) {
/* 139 */         if (System.nanoTime() - l >= SysProperties.SOCKET_CONNECT_TIMEOUT * 1000000L)
/*     */         {
/*     */           
/* 142 */           throw iOException;
/*     */         }
/* 144 */         if (b >= SysProperties.SOCKET_CONNECT_RETRY) {
/* 145 */           throw iOException;
/*     */         }
/*     */ 
/*     */         
/*     */         try {
/* 150 */           long l1 = Math.min(256, b * b);
/* 151 */           Thread.sleep(l1);
/* 152 */         } catch (InterruptedException interruptedException) {}
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
/*     */   
/*     */   public static ServerSocket createServerSocket(int paramInt, boolean paramBoolean) {
/*     */     try {
/* 174 */       return createServerSocketTry(paramInt, paramBoolean);
/* 175 */     } catch (Exception exception) {
/*     */       
/* 177 */       return createServerSocketTry(paramInt, paramBoolean);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static InetAddress getBindAddress() throws UnknownHostException {
/* 188 */     String str = SysProperties.BIND_ADDRESS;
/* 189 */     if (str == null || str.isEmpty()) {
/* 190 */       return null;
/*     */     }
/* 192 */     synchronized (NetUtils.class) {
/* 193 */       if (cachedBindAddress == null) {
/* 194 */         cachedBindAddress = InetAddress.getByName(str);
/*     */       }
/*     */     } 
/* 197 */     return cachedBindAddress;
/*     */   }
/*     */   
/*     */   private static ServerSocket createServerSocketTry(int paramInt, boolean paramBoolean) {
/*     */     try {
/* 202 */       InetAddress inetAddress = getBindAddress();
/* 203 */       if (paramBoolean) {
/* 204 */         return CipherFactory.createServerSocket(paramInt, inetAddress);
/*     */       }
/* 206 */       if (inetAddress == null) {
/* 207 */         return new ServerSocket(paramInt);
/*     */       }
/* 209 */       return new ServerSocket(paramInt, 0, inetAddress);
/* 210 */     } catch (BindException bindException) {
/* 211 */       throw DbException.get(90061, bindException, new String[] {
/* 212 */             Integer.toString(paramInt), bindException.toString() });
/* 213 */     } catch (IOException iOException) {
/* 214 */       throw DbException.convertIOException(iOException, "port: " + paramInt + " ssl: " + paramBoolean);
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
/*     */   public static boolean isLocalAddress(Socket paramSocket) throws UnknownHostException {
/* 227 */     InetAddress inetAddress1 = paramSocket.getInetAddress();
/* 228 */     if (inetAddress1.isLoopbackAddress()) {
/* 229 */       return true;
/*     */     }
/* 231 */     InetAddress inetAddress2 = InetAddress.getLocalHost();
/*     */     
/* 233 */     String str = inetAddress2.getHostAddress();
/* 234 */     for (InetAddress inetAddress : InetAddress.getAllByName(str)) {
/* 235 */       if (inetAddress1.equals(inetAddress)) {
/* 236 */         return true;
/*     */       }
/*     */     } 
/* 239 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ServerSocket closeSilently(ServerSocket paramServerSocket) {
/* 249 */     if (paramServerSocket != null) {
/*     */       try {
/* 251 */         paramServerSocket.close();
/* 252 */       } catch (IOException iOException) {}
/*     */     }
/*     */ 
/*     */     
/* 256 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized String getLocalAddress() {
/*     */     String str;
/* 266 */     long l = System.nanoTime();
/* 267 */     if (cachedLocalAddress != null && l - cachedLocalAddressTime < 1000000000L) {
/* 268 */       return cachedLocalAddress;
/*     */     }
/* 270 */     InetAddress inetAddress = null;
/* 271 */     boolean bool = false;
/*     */     try {
/* 273 */       inetAddress = getBindAddress();
/* 274 */       if (inetAddress == null) {
/* 275 */         bool = true;
/*     */       }
/* 277 */     } catch (UnknownHostException unknownHostException) {}
/*     */ 
/*     */     
/* 280 */     if (bool) {
/*     */       try {
/* 282 */         inetAddress = InetAddress.getLocalHost();
/* 283 */       } catch (UnknownHostException unknownHostException) {
/* 284 */         throw DbException.convert(unknownHostException);
/*     */       } 
/*     */     }
/*     */     
/* 288 */     if (inetAddress == null) {
/* 289 */       str = "localhost";
/*     */     } else {
/* 291 */       str = inetAddress.getHostAddress();
/* 292 */       if (inetAddress instanceof java.net.Inet6Address) {
/* 293 */         if (str.indexOf('%') >= 0) {
/* 294 */           str = "localhost";
/* 295 */         } else if (str.indexOf(':') >= 0 && !str.startsWith("[")) {
/*     */ 
/*     */           
/* 298 */           str = "[" + str + "]";
/*     */         } 
/*     */       }
/*     */     } 
/* 302 */     if (str.equals("127.0.0.1")) {
/* 303 */       str = "localhost";
/*     */     }
/* 305 */     cachedLocalAddress = str;
/* 306 */     cachedLocalAddressTime = l;
/* 307 */     return str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getHostName(String paramString) {
/*     */     try {
/* 318 */       InetAddress inetAddress = InetAddress.getByName(paramString);
/* 319 */       return inetAddress.getHostName();
/* 320 */     } catch (Exception exception) {
/* 321 */       return "unknown";
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
/*     */   public static StringBuilder ipToShortForm(StringBuilder paramStringBuilder, byte[] paramArrayOfbyte, boolean paramBoolean) {
/*     */     short[] arrayOfShort;
/*     */     int i;
/*     */     byte b1, b2;
/*     */     int j, k;
/* 339 */     switch (paramArrayOfbyte.length)
/*     */     { case 4:
/* 341 */         if (paramStringBuilder == null) {
/* 342 */           paramStringBuilder = new StringBuilder(15);
/*     */         }
/* 344 */         paramStringBuilder
/* 345 */           .append(paramArrayOfbyte[0] & 0xFF).append('.')
/* 346 */           .append(paramArrayOfbyte[1] & 0xFF).append('.')
/* 347 */           .append(paramArrayOfbyte[2] & 0xFF).append('.')
/* 348 */           .append(paramArrayOfbyte[3] & 0xFF);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 396 */         return paramStringBuilder;case 16: arrayOfShort = new short[8]; i = 0; b1 = 0; b2 = 0; for (j = 0, k = 0; j < 8; j++) { arrayOfShort[j] = (short)((paramArrayOfbyte[k++] & 0xFF) << 8 | paramArrayOfbyte[k++] & 0xFF); if ((short)((paramArrayOfbyte[k++] & 0xFF) << 8 | paramArrayOfbyte[k++] & 0xFF) == 0) { b2++; if (b2 > b1) { b1 = b2; i = j - b2 + 1; }  } else { b2 = 0; }  }  if (paramStringBuilder == null) paramStringBuilder = new StringBuilder(paramBoolean ? 41 : 39);  if (paramBoolean) paramStringBuilder.append('[');  if (b1 > 1) { for (k = 0; k < i; k++) paramStringBuilder.append(Integer.toHexString(arrayOfShort[k] & 0xFFFF)).append(':');  if (i == 0) paramStringBuilder.append(':');  paramStringBuilder.append(':'); j = i + b1; } else { j = 0; }  for (k = j; k < 8; k++) { paramStringBuilder.append(Integer.toHexString(arrayOfShort[k] & 0xFFFF)); if (k < 7) paramStringBuilder.append(':');  }  if (paramBoolean) paramStringBuilder.append(']');  return paramStringBuilder; }  StringUtils.convertBytesToHex(paramStringBuilder, paramArrayOfbyte); return paramStringBuilder;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\NetUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */