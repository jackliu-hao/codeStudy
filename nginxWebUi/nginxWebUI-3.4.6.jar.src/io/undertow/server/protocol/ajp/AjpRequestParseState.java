/*     */ package io.undertow.server.protocol.ajp;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.BasicSSLSessionInfo;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.Map;
/*     */ import javax.security.cert.CertificateException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class AjpRequestParseState
/*     */ {
/*     */   public static final int BEGIN = 0;
/*     */   public static final int READING_MAGIC_NUMBER = 1;
/*     */   public static final int READING_DATA_SIZE = 2;
/*     */   public static final int READING_PREFIX_CODE = 3;
/*     */   public static final int READING_METHOD = 4;
/*     */   public static final int READING_PROTOCOL = 5;
/*     */   public static final int READING_REQUEST_URI = 6;
/*     */   public static final int READING_REMOTE_ADDR = 7;
/*     */   public static final int READING_REMOTE_HOST = 8;
/*     */   public static final int READING_SERVER_NAME = 9;
/*     */   public static final int READING_SERVER_PORT = 10;
/*     */   public static final int READING_IS_SSL = 11;
/*     */   public static final int READING_NUM_HEADERS = 12;
/*     */   public static final int READING_HEADERS = 13;
/*     */   public static final int READING_ATTRIBUTES = 14;
/*     */   public static final int DONE = 15;
/*     */   int state;
/*     */   byte prefix;
/*     */   int dataSize;
/*  61 */   int numHeaders = 0;
/*     */   
/*     */   HttpString currentHeader;
/*     */   
/*     */   String currentAttribute;
/*     */   
/*     */   Map<String, String> attributes;
/*     */   
/*     */   String remoteAddress;
/*     */   
/*  71 */   int remotePort = -1;
/*  72 */   int serverPort = 80;
/*     */ 
/*     */   
/*     */   String serverAddress;
/*     */ 
/*     */   
/*  78 */   public int stringLength = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   private StringBuilder currentString = new StringBuilder();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   public int currentIntegerPart = -1;
/*     */   
/*     */   boolean containsUrlCharacters = false;
/*  92 */   public int readHeaders = 0;
/*     */   public String sslSessionId;
/*     */   public String sslCipher;
/*     */   public String sslCert;
/*     */   public String sslKeySize;
/*     */   boolean badRequest;
/*     */   public boolean containsUnencodedUrlCharacters;
/*     */   
/*     */   public void reset() {
/* 101 */     this.stringLength = -1;
/* 102 */     this.currentIntegerPart = -1;
/* 103 */     this.readHeaders = 0;
/* 104 */     this.badRequest = false;
/* 105 */     this.currentString.setLength(0);
/* 106 */     this.containsUnencodedUrlCharacters = false;
/*     */   }
/*     */   public boolean isComplete() {
/* 109 */     return (this.state == 15);
/*     */   }
/*     */   
/*     */   BasicSSLSessionInfo createSslSessionInfo() {
/* 113 */     String sessionId = this.sslSessionId;
/* 114 */     String cypher = this.sslCipher;
/* 115 */     String cert = this.sslCert;
/* 116 */     Integer keySize = null;
/* 117 */     if (cert == null && sessionId == null) {
/* 118 */       return null;
/*     */     }
/* 120 */     if (this.sslKeySize != null) {
/*     */       try {
/* 122 */         keySize = Integer.valueOf(Integer.parseUnsignedInt(this.sslKeySize));
/* 123 */       } catch (NumberFormatException e) {
/* 124 */         UndertowLogger.REQUEST_LOGGER.debugf("Invalid sslKeySize %s", this.sslKeySize);
/*     */       } 
/*     */     }
/*     */     try {
/* 128 */       return new BasicSSLSessionInfo(sessionId, cypher, cert, keySize);
/* 129 */     } catch (CertificateException e) {
/* 130 */       return null;
/* 131 */     } catch (CertificateException e) {
/* 132 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   InetSocketAddress createPeerAddress() {
/* 137 */     if (this.remoteAddress == null) {
/* 138 */       return null;
/*     */     }
/* 140 */     int port = (this.remotePort > 0) ? this.remotePort : 0;
/*     */     try {
/* 142 */       InetAddress address = InetAddress.getByName(this.remoteAddress);
/* 143 */       return new InetSocketAddress(address, port);
/* 144 */     } catch (UnknownHostException e) {
/* 145 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   InetSocketAddress createDestinationAddress() {
/* 150 */     if (this.serverAddress == null) {
/* 151 */       return null;
/*     */     }
/* 153 */     return InetSocketAddress.createUnresolved(this.serverAddress, this.serverPort);
/*     */   }
/*     */   
/*     */   public void addStringByte(byte b) {
/* 157 */     this.currentString.append((char)(b & 0xFF));
/*     */   }
/*     */   
/*     */   public String getStringAndClear() throws UnsupportedEncodingException {
/* 161 */     String ret = this.currentString.toString();
/* 162 */     this.currentString.setLength(0);
/* 163 */     return ret;
/*     */   }
/*     */   
/*     */   public int getCurrentStringLength() {
/* 167 */     return this.currentString.length();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\ajp\AjpRequestParseState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */