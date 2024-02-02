/*     */ package org.h2.server.web;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.NetUtils;
/*     */ import org.h2.util.NetworkConnectionInfo;
/*     */ import org.h2.util.StringUtils;
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
/*     */ class WebThread
/*     */   extends WebApp
/*     */   implements Runnable
/*     */ {
/*  36 */   private static final byte[] RN = new byte[] { 13, 10 };
/*     */   
/*  38 */   private static final byte[] RNRN = new byte[] { 13, 10, 13, 10 };
/*     */   
/*     */   protected OutputStream output;
/*     */   protected final Socket socket;
/*     */   private final Thread thread;
/*     */   private InputStream input;
/*     */   private String host;
/*     */   private int dataLength;
/*     */   private String ifModifiedSince;
/*     */   
/*     */   WebThread(Socket paramSocket, WebServer paramWebServer) {
/*  49 */     super(paramWebServer);
/*  50 */     this.socket = paramSocket;
/*  51 */     this.thread = new Thread(this, "H2 Console thread");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void start() {
/*  58 */     this.thread.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void join(int paramInt) throws InterruptedException {
/*  68 */     this.thread.join(paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void stopNow() {
/*  75 */     this.stop = true;
/*     */     try {
/*  77 */       this.socket.close();
/*  78 */     } catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String getAllowedFile(String paramString) {
/*  84 */     if (!allow()) {
/*  85 */       return "notAllowed.jsp";
/*     */     }
/*  87 */     if (paramString.length() == 0) {
/*  88 */       return "index.do";
/*     */     }
/*  90 */     if (paramString.charAt(0) == '?') {
/*  91 */       return "index.do" + paramString;
/*     */     }
/*  93 */     return paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/*  99 */       this.input = new BufferedInputStream(this.socket.getInputStream());
/* 100 */       this.output = new BufferedOutputStream(this.socket.getOutputStream()); do {  }
/* 101 */       while (!this.stop && 
/* 102 */         process());
/*     */ 
/*     */     
/*     */     }
/* 106 */     catch (Exception exception) {
/* 107 */       DbException.traceThrowable(exception);
/*     */     } 
/* 109 */     IOUtils.closeSilently(this.output);
/* 110 */     IOUtils.closeSilently(this.input);
/*     */     try {
/* 112 */       this.socket.close();
/* 113 */     } catch (IOException iOException) {
/*     */     
/*     */     } finally {
/* 116 */       this.server.remove(this);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean process() throws IOException {
/* 122 */     String str1 = readHeaderLine();
/* 123 */     boolean bool1 = str1.startsWith("GET ");
/* 124 */     if ((!bool1 && !str1.startsWith("POST ")) || !str1.endsWith(" HTTP/1.1")) {
/* 125 */       writeSimple("HTTP/1.1 400 Bad Request", "Bad request");
/* 126 */       return false;
/*     */     } 
/* 128 */     String str2 = StringUtils.trimSubstring(str1, bool1 ? 4 : 5, str1.length() - 9);
/* 129 */     if (str2.isEmpty() || str2.charAt(0) != '/') {
/* 130 */       writeSimple("HTTP/1.1 400 Bad Request", "Bad request");
/* 131 */       return false;
/*     */     } 
/* 133 */     this.attributes = new Properties();
/* 134 */     boolean bool2 = parseHeader();
/* 135 */     if (!checkHost(this.host)) {
/* 136 */       return false;
/*     */     }
/* 138 */     str2 = str2.substring(1);
/* 139 */     trace(str1 + ": " + str2);
/* 140 */     str2 = getAllowedFile(str2);
/* 141 */     int i = str2.indexOf('?');
/* 142 */     this.session = null;
/* 143 */     String str3 = null;
/* 144 */     if (i >= 0) {
/* 145 */       String str5 = str2.substring(i + 1);
/* 146 */       parseAttributes(str5);
/* 147 */       String str6 = this.attributes.getProperty("jsessionid");
/* 148 */       str3 = this.attributes.getProperty("key");
/* 149 */       str2 = str2.substring(0, i);
/* 150 */       this.session = this.server.getSession(str6);
/*     */     } 
/* 152 */     parseBodyAttributes();
/* 153 */     str2 = processRequest(str2, new NetworkConnectionInfo(
/*     */           
/* 155 */           NetUtils.ipToShortForm(new StringBuilder(this.server.getSSL() ? "https://" : "http://"), this.socket
/* 156 */             .getLocalAddress().getAddress(), true)
/* 157 */           .append(':').append(this.socket.getLocalPort()).toString(), this.socket
/* 158 */           .getInetAddress().getAddress(), this.socket.getPort(), null));
/* 159 */     if (str2.length() == 0)
/*     */     {
/* 161 */       return true;
/*     */     }
/*     */     
/* 164 */     if (this.cache && this.ifModifiedSince != null && this.ifModifiedSince.equals(this.server.getStartDateTime())) {
/* 165 */       writeSimple("HTTP/1.1 304 Not Modified", (byte[])null);
/* 166 */       return bool2;
/*     */     } 
/* 168 */     byte[] arrayOfByte = this.server.getFile(str2);
/* 169 */     if (arrayOfByte == null) {
/* 170 */       writeSimple("HTTP/1.1 404 Not Found", "File not found: " + str2);
/* 171 */       return bool2;
/*     */     } 
/* 173 */     if (this.session != null && str2.endsWith(".jsp")) {
/* 174 */       if (str3 != null) {
/* 175 */         this.session.put("key", str3);
/*     */       }
/* 177 */       String str = new String(arrayOfByte, StandardCharsets.UTF_8);
/* 178 */       if (SysProperties.CONSOLE_STREAM) {
/* 179 */         Iterator<String> iterator = (Iterator)this.session.map.remove("chunks");
/* 180 */         if (iterator != null) {
/* 181 */           String str5 = "HTTP/1.1 200 OK\r\n";
/* 182 */           str5 = str5 + "Content-Type: " + this.mimeType + "\r\n";
/* 183 */           str5 = str5 + "Cache-Control: no-cache\r\n";
/* 184 */           str5 = str5 + "Transfer-Encoding: chunked\r\n";
/* 185 */           str5 = str5 + "\r\n";
/* 186 */           trace(str5);
/* 187 */           this.output.write(str5.getBytes(StandardCharsets.ISO_8859_1));
/* 188 */           while (iterator.hasNext()) {
/* 189 */             String str6 = iterator.next();
/* 190 */             str6 = PageParser.parse(str6, this.session.map);
/* 191 */             arrayOfByte = str6.getBytes(StandardCharsets.UTF_8);
/* 192 */             if (arrayOfByte.length == 0) {
/*     */               continue;
/*     */             }
/* 195 */             this.output.write(Integer.toHexString(arrayOfByte.length).getBytes(StandardCharsets.ISO_8859_1));
/* 196 */             this.output.write(RN);
/* 197 */             this.output.write(arrayOfByte);
/* 198 */             this.output.write(RN);
/* 199 */             this.output.flush();
/*     */           } 
/* 201 */           this.output.write(48);
/* 202 */           this.output.write(RNRN);
/* 203 */           this.output.flush();
/* 204 */           return bool2;
/*     */         } 
/*     */       } 
/* 207 */       str = PageParser.parse(str, this.session.map);
/* 208 */       arrayOfByte = str.getBytes(StandardCharsets.UTF_8);
/*     */     } 
/* 210 */     String str4 = "HTTP/1.1 200 OK\r\n";
/* 211 */     str4 = str4 + "Content-Type: " + this.mimeType + "\r\n";
/* 212 */     if (!this.cache) {
/* 213 */       str4 = str4 + "Cache-Control: no-cache\r\n";
/*     */     } else {
/* 215 */       str4 = str4 + "Cache-Control: max-age=10\r\n";
/* 216 */       str4 = str4 + "Last-Modified: " + this.server.getStartDateTime() + "\r\n";
/*     */     } 
/* 218 */     str4 = str4 + "Content-Length: " + arrayOfByte.length + "\r\n";
/* 219 */     str4 = str4 + "\r\n";
/* 220 */     trace(str4);
/* 221 */     this.output.write(str4.getBytes(StandardCharsets.ISO_8859_1));
/* 222 */     this.output.write(arrayOfByte);
/* 223 */     this.output.flush();
/* 224 */     return bool2;
/*     */   }
/*     */   
/*     */   private void writeSimple(String paramString1, String paramString2) throws IOException {
/* 228 */     writeSimple(paramString1, (paramString2 != null) ? paramString2.getBytes(StandardCharsets.UTF_8) : null);
/*     */   }
/*     */   
/*     */   private void writeSimple(String paramString, byte[] paramArrayOfbyte) throws IOException {
/* 232 */     trace(paramString);
/* 233 */     this.output.write(paramString.getBytes(StandardCharsets.ISO_8859_1));
/* 234 */     if (paramArrayOfbyte != null) {
/* 235 */       this.output.write(RN);
/* 236 */       String str = "Content-Length: " + paramArrayOfbyte.length;
/* 237 */       trace(str);
/* 238 */       this.output.write(str.getBytes(StandardCharsets.ISO_8859_1));
/* 239 */       this.output.write(RNRN);
/* 240 */       this.output.write(paramArrayOfbyte);
/*     */     } else {
/* 242 */       this.output.write(RNRN);
/*     */     } 
/* 244 */     this.output.flush();
/*     */   }
/*     */   
/*     */   private boolean checkHost(String paramString) throws IOException {
/* 248 */     if (paramString == null) {
/* 249 */       writeSimple("HTTP/1.1 400 Bad Request", "Bad request");
/* 250 */       return false;
/*     */     } 
/* 252 */     int i = paramString.indexOf(':');
/* 253 */     if (i >= 0) {
/* 254 */       paramString = paramString.substring(0, i);
/*     */     }
/* 256 */     if (paramString.isEmpty()) {
/* 257 */       return false;
/*     */     }
/* 259 */     paramString = StringUtils.toLowerEnglish(paramString);
/* 260 */     if (paramString.equals(this.server.getHost()) || paramString.equals("localhost") || paramString.equals("127.0.0.1")) {
/* 261 */       return true;
/*     */     }
/* 263 */     String str = this.server.getExternalNames();
/* 264 */     if (str != null && !str.isEmpty()) {
/* 265 */       for (String str1 : str.split(",")) {
/* 266 */         if (paramString.equals(str1.trim())) {
/* 267 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 271 */     writeSimple("HTTP/1.1 404 Not Found", "Host " + paramString + " not found");
/* 272 */     return false;
/*     */   }
/*     */   
/*     */   private String readHeaderLine() throws IOException {
/* 276 */     StringBuilder stringBuilder = new StringBuilder();
/*     */     while (true) {
/* 278 */       int i = this.input.read();
/* 279 */       if (i == -1)
/* 280 */         throw new IOException("Unexpected EOF"); 
/* 281 */       if (i == 13) {
/* 282 */         if (this.input.read() == 10)
/* 283 */           return (stringBuilder.length() > 0) ? stringBuilder.toString() : null;  continue;
/*     */       } 
/* 285 */       if (i == 10) {
/* 286 */         return (stringBuilder.length() > 0) ? stringBuilder.toString() : null;
/*     */       }
/* 288 */       stringBuilder.append((char)i);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void parseBodyAttributes() throws IOException {
/* 294 */     if (this.dataLength > 0) {
/* 295 */       byte[] arrayOfByte = Utils.newBytes(this.dataLength);
/* 296 */       for (int i = 0; i < this.dataLength;) {
/* 297 */         i += this.input.read(arrayOfByte, i, this.dataLength - i);
/*     */       }
/* 299 */       String str = new String(arrayOfByte, StandardCharsets.UTF_8);
/* 300 */       parseAttributes(str);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void parseAttributes(String paramString) {
/* 305 */     trace("data=" + paramString);
/* 306 */     while (paramString != null) {
/* 307 */       int i = paramString.indexOf('=');
/* 308 */       if (i >= 0) {
/* 309 */         String str2, str1 = paramString.substring(0, i);
/* 310 */         paramString = paramString.substring(i + 1);
/* 311 */         i = paramString.indexOf('&');
/*     */         
/* 313 */         if (i >= 0) {
/* 314 */           str2 = paramString.substring(0, i);
/* 315 */           paramString = paramString.substring(i + 1);
/*     */         } else {
/* 317 */           str2 = paramString;
/*     */         } 
/* 319 */         String str3 = StringUtils.urlDecode(str2);
/* 320 */         this.attributes.put(str1, str3);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 325 */     trace(this.attributes.toString());
/*     */   }
/*     */   
/*     */   private boolean parseHeader() throws IOException {
/* 329 */     boolean bool1 = false;
/* 330 */     trace("parseHeader");
/* 331 */     int i = 0;
/* 332 */     this.host = null;
/* 333 */     this.ifModifiedSince = null;
/* 334 */     boolean bool2 = false; String str;
/* 335 */     while ((str = readHeaderLine()) != null) {
/* 336 */       trace(" " + str);
/* 337 */       String str1 = StringUtils.toLowerEnglish(str);
/* 338 */       if (str1.startsWith("host")) {
/* 339 */         this.host = getHeaderLineValue(str); continue;
/* 340 */       }  if (str1.startsWith("if-modified-since")) {
/* 341 */         this.ifModifiedSince = getHeaderLineValue(str); continue;
/* 342 */       }  if (str1.startsWith("connection")) {
/* 343 */         String str2 = getHeaderLineValue(str);
/* 344 */         if ("keep-alive".equals(str2))
/* 345 */           bool1 = true;  continue;
/*     */       } 
/* 347 */       if (str1.startsWith("content-type")) {
/* 348 */         String str2 = getHeaderLineValue(str);
/* 349 */         if (str2.startsWith("multipart/form-data"))
/* 350 */           bool2 = true;  continue;
/*     */       } 
/* 352 */       if (str1.startsWith("content-length")) {
/* 353 */         i = Integer.parseInt(getHeaderLineValue(str));
/* 354 */         trace("len=" + i); continue;
/* 355 */       }  if (str1.startsWith("user-agent")) {
/* 356 */         boolean bool = str1.contains("webkit/");
/* 357 */         if (bool && this.session != null) {
/*     */ 
/*     */           
/* 360 */           this.session.put("frame-border", "1");
/* 361 */           this.session.put("frameset-border", "2");
/*     */         }  continue;
/* 363 */       }  if (str1.startsWith("accept-language")) {
/* 364 */         Locale locale = (this.session == null) ? null : this.session.locale;
/* 365 */         if (locale == null) {
/* 366 */           String str2 = getHeaderLineValue(str);
/* 367 */           StringTokenizer stringTokenizer = new StringTokenizer(str2, ",;");
/* 368 */           while (stringTokenizer.hasMoreTokens()) {
/* 369 */             String str3 = stringTokenizer.nextToken();
/* 370 */             if (!str3.startsWith("q=") && 
/* 371 */               this.server.supportsLanguage(str3)) {
/* 372 */               int j = str3.indexOf('-');
/* 373 */               if (j >= 0) {
/* 374 */                 String str4 = str3.substring(0, j);
/* 375 */                 String str5 = str3.substring(j + 1);
/* 376 */                 locale = new Locale(str4, str5);
/*     */               } else {
/* 378 */                 locale = new Locale(str3, "");
/*     */               } 
/* 380 */               this.headerLanguage = locale.getLanguage();
/* 381 */               if (this.session != null) {
/* 382 */                 this.session.locale = locale;
/* 383 */                 this.session.put("language", this.headerLanguage);
/* 384 */                 this.server.readTranslations(this.session, this.headerLanguage);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         continue;
/*     */       } 
/* 391 */       if (StringUtils.isWhitespaceOrEmpty(str)) {
/*     */         break;
/*     */       }
/*     */     } 
/* 395 */     this.dataLength = 0;
/* 396 */     if (!bool2)
/*     */     {
/* 398 */       if (i > 0)
/* 399 */         this.dataLength = i; 
/*     */     }
/* 401 */     return bool1;
/*     */   }
/*     */   
/*     */   private static String getHeaderLineValue(String paramString) {
/* 405 */     return StringUtils.trimSubstring(paramString, paramString.indexOf(':') + 1);
/*     */   }
/*     */ 
/*     */   
/*     */   protected String adminShutdown() {
/* 410 */     stopNow();
/* 411 */     return super.adminShutdown();
/*     */   }
/*     */   
/*     */   private boolean allow() {
/* 415 */     if (this.server.getAllowOthers()) {
/* 416 */       return true;
/*     */     }
/*     */     try {
/* 419 */       return NetUtils.isLocalAddress(this.socket);
/* 420 */     } catch (UnknownHostException unknownHostException) {
/* 421 */       this.server.traceError(unknownHostException);
/* 422 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void trace(String paramString) {
/* 427 */     this.server.trace(paramString);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\server\web\WebThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */