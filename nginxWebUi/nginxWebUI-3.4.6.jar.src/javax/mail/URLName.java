/*     */ package javax.mail;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.BitSet;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class URLName
/*     */ {
/*     */   protected String fullURL;
/*     */   private String protocol;
/*     */   private String username;
/*     */   private String password;
/*     */   private String host;
/*     */   private InetAddress hostAddress;
/*     */   private boolean hostAddressKnown = false;
/* 103 */   private int port = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String file;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String ref;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 118 */   private int hashCode = 0;
/*     */ 
/*     */   
/*     */   private static boolean doEncode = true;
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*     */     try {
/* 127 */       doEncode = !Boolean.getBoolean("mail.URLName.dontencode");
/* 128 */     } catch (Exception ex) {}
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
/*     */   public URLName(String protocol, String host, int port, String file, String username, String password) {
/* 148 */     this.protocol = protocol;
/* 149 */     this.host = host;
/* 150 */     this.port = port;
/*     */     int refStart;
/* 152 */     if (file != null && (refStart = file.indexOf('#')) != -1) {
/* 153 */       this.file = file.substring(0, refStart);
/* 154 */       this.ref = file.substring(refStart + 1);
/*     */     } else {
/* 156 */       this.file = file;
/* 157 */       this.ref = null;
/*     */     } 
/* 159 */     this.username = doEncode ? encode(username) : username;
/* 160 */     this.password = doEncode ? encode(password) : password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URLName(URL url) {
/* 167 */     this(url.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URLName(String url) {
/* 175 */     parseString(url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 182 */     if (this.fullURL == null) {
/*     */       
/* 184 */       StringBuffer tempURL = new StringBuffer();
/* 185 */       if (this.protocol != null) {
/* 186 */         tempURL.append(this.protocol);
/* 187 */         tempURL.append(":");
/*     */       } 
/*     */       
/* 190 */       if (this.username != null || this.host != null) {
/*     */         
/* 192 */         tempURL.append("//");
/*     */ 
/*     */ 
/*     */         
/* 196 */         if (this.username != null) {
/* 197 */           tempURL.append(this.username);
/*     */           
/* 199 */           if (this.password != null) {
/* 200 */             tempURL.append(":");
/* 201 */             tempURL.append(this.password);
/*     */           } 
/*     */           
/* 204 */           tempURL.append("@");
/*     */         } 
/*     */ 
/*     */         
/* 208 */         if (this.host != null) {
/* 209 */           tempURL.append(this.host);
/*     */         }
/*     */ 
/*     */         
/* 213 */         if (this.port != -1) {
/* 214 */           tempURL.append(":");
/* 215 */           tempURL.append(Integer.toString(this.port));
/*     */         } 
/* 217 */         if (this.file != null) {
/* 218 */           tempURL.append("/");
/*     */         }
/*     */       } 
/*     */       
/* 222 */       if (this.file != null) {
/* 223 */         tempURL.append(this.file);
/*     */       }
/*     */ 
/*     */       
/* 227 */       if (this.ref != null) {
/* 228 */         tempURL.append("#");
/* 229 */         tempURL.append(this.ref);
/*     */       } 
/*     */ 
/*     */       
/* 233 */       this.fullURL = tempURL.toString();
/*     */     } 
/*     */     
/* 236 */     return this.fullURL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void parseString(String url) {
/* 245 */     this.protocol = this.file = this.ref = this.host = this.username = this.password = null;
/* 246 */     this.port = -1;
/*     */     
/* 248 */     int len = url.length();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 253 */     int protocolEnd = url.indexOf(':');
/* 254 */     if (protocolEnd != -1) {
/* 255 */       this.protocol = url.substring(0, protocolEnd);
/*     */     }
/*     */     
/* 258 */     if (url.regionMatches(protocolEnd + 1, "//", 0, 2)) {
/*     */       int portindex;
/* 260 */       String fullhost = null;
/* 261 */       int fileStart = url.indexOf('/', protocolEnd + 3);
/* 262 */       if (fileStart != -1)
/* 263 */       { fullhost = url.substring(protocolEnd + 3, fileStart);
/* 264 */         if (fileStart + 1 < len) {
/* 265 */           this.file = url.substring(fileStart + 1);
/*     */         } else {
/* 267 */           this.file = "";
/*     */         }  }
/* 269 */       else { fullhost = url.substring(protocolEnd + 3); }
/*     */ 
/*     */       
/* 272 */       int i = fullhost.indexOf('@');
/* 273 */       if (i != -1) {
/* 274 */         String fulluserpass = fullhost.substring(0, i);
/* 275 */         fullhost = fullhost.substring(i + 1);
/*     */ 
/*     */         
/* 278 */         int passindex = fulluserpass.indexOf(':');
/* 279 */         if (passindex != -1) {
/* 280 */           this.username = fulluserpass.substring(0, passindex);
/* 281 */           this.password = fulluserpass.substring(passindex + 1);
/*     */         } else {
/* 283 */           this.username = fulluserpass;
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 289 */       if (fullhost.length() > 0 && fullhost.charAt(0) == '[') {
/*     */         
/* 291 */         portindex = fullhost.indexOf(':', fullhost.indexOf(']'));
/*     */       } else {
/* 293 */         portindex = fullhost.indexOf(':');
/*     */       } 
/* 295 */       if (portindex != -1) {
/* 296 */         String portstring = fullhost.substring(portindex + 1);
/* 297 */         if (portstring.length() > 0) {
/*     */           try {
/* 299 */             this.port = Integer.parseInt(portstring);
/* 300 */           } catch (NumberFormatException nfex) {
/* 301 */             this.port = -1;
/*     */           } 
/*     */         }
/*     */         
/* 305 */         this.host = fullhost.substring(0, portindex);
/*     */       } else {
/* 307 */         this.host = fullhost;
/*     */       }
/*     */     
/* 310 */     } else if (protocolEnd + 1 < len) {
/* 311 */       this.file = url.substring(protocolEnd + 1);
/*     */     } 
/*     */     
/*     */     int refStart;
/*     */     
/* 316 */     if (this.file != null && (refStart = this.file.indexOf('#')) != -1) {
/* 317 */       this.ref = this.file.substring(refStart + 1);
/* 318 */       this.file = this.file.substring(0, refStart);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPort() {
/* 327 */     return this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getProtocol() {
/* 335 */     return this.protocol;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFile() {
/* 343 */     return this.file;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRef() {
/* 351 */     return this.ref;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHost() {
/* 359 */     return this.host;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUsername() {
/* 367 */     return doEncode ? decode(this.username) : this.username;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPassword() {
/* 375 */     return doEncode ? decode(this.password) : this.password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URL getURL() throws MalformedURLException {
/* 382 */     return new URL(getProtocol(), getHost(), getPort(), getFile());
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
/*     */   
/*     */   public boolean equals(Object obj) {
/* 407 */     if (!(obj instanceof URLName))
/* 408 */       return false; 
/* 409 */     URLName u2 = (URLName)obj;
/*     */ 
/*     */     
/* 412 */     if (u2.protocol == null || !u2.protocol.equals(this.protocol)) {
/* 413 */       return false;
/*     */     }
/*     */     
/* 416 */     InetAddress a1 = getHostAddress(), a2 = u2.getHostAddress();
/*     */     
/* 418 */     if (a1 != null && a2 != null) {
/* 419 */       if (!a1.equals(a2)) {
/* 420 */         return false;
/*     */       }
/* 422 */     } else if (this.host != null && u2.host != null) {
/* 423 */       if (!this.host.equalsIgnoreCase(u2.host)) {
/* 424 */         return false;
/*     */       }
/* 426 */     } else if (this.host != u2.host) {
/* 427 */       return false;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 432 */     if (this.username != u2.username && (this.username == null || !this.username.equals(u2.username)))
/*     */     {
/* 434 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 440 */     String f1 = (this.file == null) ? "" : this.file;
/* 441 */     String f2 = (u2.file == null) ? "" : u2.file;
/*     */     
/* 443 */     if (!f1.equals(f2)) {
/* 444 */       return false;
/*     */     }
/*     */     
/* 447 */     if (this.port != u2.port) {
/* 448 */       return false;
/*     */     }
/*     */     
/* 451 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 458 */     if (this.hashCode != 0)
/* 459 */       return this.hashCode; 
/* 460 */     if (this.protocol != null)
/* 461 */       this.hashCode += this.protocol.hashCode(); 
/* 462 */     InetAddress addr = getHostAddress();
/* 463 */     if (addr != null) {
/* 464 */       this.hashCode += addr.hashCode();
/* 465 */     } else if (this.host != null) {
/* 466 */       this.hashCode += this.host.toLowerCase(Locale.ENGLISH).hashCode();
/* 467 */     }  if (this.username != null)
/* 468 */       this.hashCode += this.username.hashCode(); 
/* 469 */     if (this.file != null)
/* 470 */       this.hashCode += this.file.hashCode(); 
/* 471 */     this.hashCode += this.port;
/* 472 */     return this.hashCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized InetAddress getHostAddress() {
/* 481 */     if (this.hostAddressKnown)
/* 482 */       return this.hostAddress; 
/* 483 */     if (this.host == null)
/* 484 */       return null; 
/*     */     try {
/* 486 */       this.hostAddress = InetAddress.getByName(this.host);
/* 487 */     } catch (UnknownHostException ex) {
/* 488 */       this.hostAddress = null;
/*     */     } 
/* 490 */     this.hostAddressKnown = true;
/* 491 */     return this.hostAddress;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 522 */   static BitSet dontNeedEncoding = new BitSet(256); static {
/*     */     int i;
/* 524 */     for (i = 97; i <= 122; i++) {
/* 525 */       dontNeedEncoding.set(i);
/*     */     }
/* 527 */     for (i = 65; i <= 90; i++) {
/* 528 */       dontNeedEncoding.set(i);
/*     */     }
/* 530 */     for (i = 48; i <= 57; i++) {
/* 531 */       dontNeedEncoding.set(i);
/*     */     }
/*     */     
/* 534 */     dontNeedEncoding.set(32);
/* 535 */     dontNeedEncoding.set(45);
/* 536 */     dontNeedEncoding.set(95);
/* 537 */     dontNeedEncoding.set(46);
/* 538 */     dontNeedEncoding.set(42);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final int caseDiff = 32;
/*     */ 
/*     */ 
/*     */   
/*     */   static String encode(String s) {
/* 548 */     if (s == null) {
/* 549 */       return null;
/*     */     }
/* 551 */     for (int i = 0; i < s.length(); i++) {
/* 552 */       int c = s.charAt(i);
/* 553 */       if (c == 32 || !dontNeedEncoding.get(c))
/* 554 */         return _encode(s); 
/*     */     } 
/* 556 */     return s;
/*     */   }
/*     */   
/*     */   private static String _encode(String s) {
/* 560 */     int maxBytesPerChar = 10;
/* 561 */     StringBuffer out = new StringBuffer(s.length());
/* 562 */     ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
/* 563 */     OutputStreamWriter writer = new OutputStreamWriter(buf);
/*     */     
/* 565 */     for (int i = 0; i < s.length(); i++) {
/* 566 */       int c = s.charAt(i);
/* 567 */       if (dontNeedEncoding.get(c)) {
/* 568 */         if (c == 32) {
/* 569 */           c = 43;
/*     */         }
/* 571 */         out.append((char)c);
/*     */       } else {
/*     */         
/*     */         try {
/* 575 */           writer.write(c);
/* 576 */           writer.flush();
/* 577 */         } catch (IOException e) {
/* 578 */           buf.reset();
/*     */         } 
/*     */         
/* 581 */         byte[] ba = buf.toByteArray();
/* 582 */         for (int j = 0; j < ba.length; j++) {
/* 583 */           out.append('%');
/* 584 */           char ch = Character.forDigit(ba[j] >> 4 & 0xF, 16);
/*     */ 
/*     */           
/* 587 */           if (Character.isLetter(ch)) {
/* 588 */             ch = (char)(ch - 32);
/*     */           }
/* 590 */           out.append(ch);
/* 591 */           ch = Character.forDigit(ba[j] & 0xF, 16);
/* 592 */           if (Character.isLetter(ch)) {
/* 593 */             ch = (char)(ch - 32);
/*     */           }
/* 595 */           out.append(ch);
/*     */         } 
/* 597 */         buf.reset();
/*     */       } 
/*     */     } 
/*     */     
/* 601 */     return out.toString();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String decode(String s) {
/* 635 */     if (s == null)
/* 636 */       return null; 
/* 637 */     if (indexOfAny(s, "+%") == -1) {
/* 638 */       return s;
/*     */     }
/* 640 */     StringBuffer sb = new StringBuffer();
/* 641 */     for (int i = 0; i < s.length(); i++) {
/* 642 */       char c = s.charAt(i);
/* 643 */       switch (c) {
/*     */         case '+':
/* 645 */           sb.append(' ');
/*     */           break;
/*     */         case '%':
/*     */           try {
/* 649 */             sb.append((char)Integer.parseInt(s.substring(i + 1, i + 3), 16));
/*     */           }
/* 651 */           catch (NumberFormatException e) {
/* 652 */             throw new IllegalArgumentException("Illegal URL encoded value: " + s.substring(i, i + 3));
/*     */           } 
/*     */ 
/*     */           
/* 656 */           i += 2;
/*     */           break;
/*     */         default:
/* 659 */           sb.append(c);
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 664 */     String result = sb.toString();
/*     */     try {
/* 666 */       byte[] inputBytes = result.getBytes("8859_1");
/* 667 */       result = new String(inputBytes);
/* 668 */     } catch (UnsupportedEncodingException e) {}
/*     */ 
/*     */     
/* 671 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int indexOfAny(String s, String any) {
/* 681 */     return indexOfAny(s, any, 0);
/*     */   }
/*     */   
/*     */   private static int indexOfAny(String s, String any, int start) {
/*     */     try {
/* 686 */       int len = s.length();
/* 687 */       for (int i = start; i < len; i++) {
/* 688 */         if (any.indexOf(s.charAt(i)) >= 0)
/* 689 */           return i; 
/*     */       } 
/* 691 */       return -1;
/* 692 */     } catch (StringIndexOutOfBoundsException e) {
/* 693 */       return -1;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\URLName.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */