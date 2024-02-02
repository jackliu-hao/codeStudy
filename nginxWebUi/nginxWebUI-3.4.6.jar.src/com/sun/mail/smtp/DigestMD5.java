/*     */ package com.sun.mail.smtp;
/*     */ 
/*     */ import com.sun.mail.util.ASCIIUtility;
/*     */ import com.sun.mail.util.BASE64DecoderStream;
/*     */ import com.sun.mail.util.BASE64EncoderStream;
/*     */ import com.sun.mail.util.MailLogger;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.StreamTokenizer;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Hashtable;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.logging.Level;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DigestMD5
/*     */ {
/*     */   private MailLogger logger;
/*     */   private MessageDigest md5;
/*     */   private String uri;
/*     */   private String clientResponse;
/*     */   
/*     */   public DigestMD5(MailLogger logger) {
/*  65 */     this.logger = logger.getLogger(getClass(), "DEBUG DIGEST-MD5");
/*  66 */     logger.config("DIGEST-MD5 Loaded");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] authClient(String host, String user, String passwd, String realm, String serverChallenge) throws IOException {
/*     */     SecureRandom random;
/*  77 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  78 */     BASE64EncoderStream bASE64EncoderStream = new BASE64EncoderStream(bos, 2147483647);
/*     */ 
/*     */     
/*     */     try {
/*  82 */       random = new SecureRandom();
/*  83 */       this.md5 = MessageDigest.getInstance("MD5");
/*  84 */     } catch (NoSuchAlgorithmException ex) {
/*  85 */       this.logger.log(Level.FINE, "NoSuchAlgorithmException", ex);
/*  86 */       throw new IOException(ex.toString());
/*     */     } 
/*  88 */     StringBuffer result = new StringBuffer();
/*     */     
/*  90 */     this.uri = "smtp/" + host;
/*  91 */     String nc = "00000001";
/*  92 */     String qop = "auth";
/*  93 */     byte[] bytes = new byte[32];
/*     */ 
/*     */     
/*  96 */     this.logger.fine("Begin authentication ...");
/*     */ 
/*     */     
/*  99 */     Hashtable map = tokenize(serverChallenge);
/*     */     
/* 101 */     if (realm == null) {
/* 102 */       String text = (String)map.get("realm");
/* 103 */       realm = (text != null) ? (new StringTokenizer(text, ",")).nextToken() : host;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 108 */     String nonce = (String)map.get("nonce");
/*     */     
/* 110 */     random.nextBytes(bytes);
/* 111 */     bASE64EncoderStream.write(bytes);
/* 112 */     bASE64EncoderStream.flush();
/*     */ 
/*     */     
/* 115 */     String cnonce = bos.toString("iso-8859-1");
/* 116 */     bos.reset();
/*     */ 
/*     */     
/* 119 */     this.md5.update(this.md5.digest(ASCIIUtility.getBytes(user + ":" + realm + ":" + passwd)));
/*     */     
/* 121 */     this.md5.update(ASCIIUtility.getBytes(":" + nonce + ":" + cnonce));
/* 122 */     this.clientResponse = toHex(this.md5.digest()) + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":";
/*     */ 
/*     */ 
/*     */     
/* 126 */     this.md5.update(ASCIIUtility.getBytes("AUTHENTICATE:" + this.uri));
/* 127 */     this.md5.update(ASCIIUtility.getBytes(this.clientResponse + toHex(this.md5.digest())));
/*     */ 
/*     */     
/* 130 */     result.append("username=\"" + user + "\"");
/* 131 */     result.append(",realm=\"" + realm + "\"");
/* 132 */     result.append(",qop=" + qop);
/* 133 */     result.append(",nc=" + nc);
/* 134 */     result.append(",nonce=\"" + nonce + "\"");
/* 135 */     result.append(",cnonce=\"" + cnonce + "\"");
/* 136 */     result.append(",digest-uri=\"" + this.uri + "\"");
/* 137 */     result.append(",response=" + toHex(this.md5.digest()));
/*     */     
/* 139 */     if (this.logger.isLoggable(Level.FINE))
/* 140 */       this.logger.fine("Response => " + result.toString()); 
/* 141 */     bASE64EncoderStream.write(ASCIIUtility.getBytes(result.toString()));
/* 142 */     bASE64EncoderStream.flush();
/* 143 */     return bos.toByteArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean authServer(String serverResponse) throws IOException {
/* 153 */     Hashtable map = tokenize(serverResponse);
/*     */     
/* 155 */     this.md5.update(ASCIIUtility.getBytes(":" + this.uri));
/* 156 */     this.md5.update(ASCIIUtility.getBytes(this.clientResponse + toHex(this.md5.digest())));
/* 157 */     String text = toHex(this.md5.digest());
/* 158 */     if (!text.equals(map.get("rspauth"))) {
/* 159 */       if (this.logger.isLoggable(Level.FINE))
/* 160 */         this.logger.fine("Expected => rspauth=" + text); 
/* 161 */       return false;
/*     */     } 
/* 163 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Hashtable tokenize(String serverResponse) throws IOException {
/* 172 */     Hashtable map = new Hashtable();
/* 173 */     byte[] bytes = serverResponse.getBytes("iso-8859-1");
/* 174 */     String key = null;
/*     */     
/* 176 */     StreamTokenizer tokens = new StreamTokenizer(new InputStreamReader((InputStream)new BASE64DecoderStream(new ByteArrayInputStream(bytes, 4, bytes.length - 4)), "iso-8859-1"));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 185 */     tokens.ordinaryChars(48, 57);
/* 186 */     tokens.wordChars(48, 57); int ttype;
/* 187 */     while ((ttype = tokens.nextToken()) != -1) {
/* 188 */       switch (ttype) {
/*     */         case -3:
/* 190 */           if (key == null) {
/* 191 */             key = tokens.sval;
/*     */           }
/*     */ 
/*     */         
/*     */         case 34:
/* 196 */           if (this.logger.isLoggable(Level.FINE)) {
/* 197 */             this.logger.fine("Received => " + key + "='" + tokens.sval + "'");
/*     */           }
/* 199 */           if (map.containsKey(key)) {
/* 200 */             map.put(key, (new StringBuffer()).append(map.get(key)).append(",").append(tokens.sval).toString());
/*     */           } else {
/* 202 */             map.put(key, tokens.sval);
/*     */           } 
/* 204 */           key = null;
/*     */       } 
/*     */     
/*     */     } 
/* 208 */     return map;
/*     */   }
/*     */   
/* 211 */   private static char[] digits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String toHex(byte[] bytes) {
/* 220 */     char[] result = new char[bytes.length * 2];
/*     */     
/* 222 */     for (int index = 0, i = 0; index < bytes.length; index++) {
/* 223 */       int temp = bytes[index] & 0xFF;
/* 224 */       result[i++] = digits[temp >> 4];
/* 225 */       result[i++] = digits[temp & 0xF];
/*     */     } 
/* 227 */     return new String(result);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\smtp\DigestMD5.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */