/*     */ package org.apache.http.impl.auth;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Formatter;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.auth.AuthenticationException;
/*     */ import org.apache.http.auth.ChallengeState;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.auth.MalformedChallengeException;
/*     */ import org.apache.http.message.BasicHeaderValueFormatter;
/*     */ import org.apache.http.message.BasicNameValuePair;
/*     */ import org.apache.http.message.BufferedHeader;
/*     */ import org.apache.http.protocol.BasicHttpContext;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.CharArrayBuffer;
/*     */ import org.apache.http.util.EncodingUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DigestScheme
/*     */   extends RFC2617Scheme
/*     */ {
/*     */   private static final long serialVersionUID = 3883908186234566916L;
/*  84 */   private static final char[] HEXADECIMAL = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */   
/*     */   private boolean complete;
/*     */   
/*     */   private static final int QOP_UNKNOWN = -1;
/*     */   
/*     */   private static final int QOP_MISSING = 0;
/*     */   
/*     */   private static final int QOP_AUTH_INT = 1;
/*     */   
/*     */   private static final int QOP_AUTH = 2;
/*     */   
/*     */   private String lastNonce;
/*     */   
/*     */   private long nounceCount;
/*     */   
/*     */   private String cnonce;
/*     */   
/*     */   private String a1;
/*     */   
/*     */   private String a2;
/*     */   
/*     */   public DigestScheme(Charset credentialsCharset) {
/* 107 */     super(credentialsCharset);
/* 108 */     this.complete = false;
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
/*     */   @Deprecated
/*     */   public DigestScheme(ChallengeState challengeState) {
/* 121 */     super(challengeState);
/*     */   }
/*     */   
/*     */   public DigestScheme() {
/* 125 */     this(Consts.ASCII);
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
/*     */   public void processChallenge(Header header) throws MalformedChallengeException {
/* 139 */     super.processChallenge(header);
/* 140 */     this.complete = true;
/* 141 */     if (getParameters().isEmpty()) {
/* 142 */       throw new MalformedChallengeException("Authentication challenge is empty");
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
/*     */   public boolean isComplete() {
/* 154 */     String s = getParameter("stale");
/* 155 */     return "true".equalsIgnoreCase(s) ? false : this.complete;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSchemeName() {
/* 165 */     return "digest";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConnectionBased() {
/* 175 */     return false;
/*     */   }
/*     */   
/*     */   public void overrideParamter(String name, String value) {
/* 179 */     getParameters().put(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
/* 190 */     return authenticate(credentials, request, (HttpContext)new BasicHttpContext());
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
/*     */   public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
/* 213 */     Args.notNull(credentials, "Credentials");
/* 214 */     Args.notNull(request, "HTTP request");
/* 215 */     if (getParameter("realm") == null) {
/* 216 */       throw new AuthenticationException("missing realm in challenge");
/*     */     }
/* 218 */     if (getParameter("nonce") == null) {
/* 219 */       throw new AuthenticationException("missing nonce in challenge");
/*     */     }
/*     */     
/* 222 */     getParameters().put("methodname", request.getRequestLine().getMethod());
/* 223 */     getParameters().put("uri", request.getRequestLine().getUri());
/* 224 */     String charset = getParameter("charset");
/* 225 */     if (charset == null) {
/* 226 */       getParameters().put("charset", getCredentialsCharset(request));
/*     */     }
/* 228 */     return createDigestHeader(credentials, request);
/*     */   }
/*     */ 
/*     */   
/*     */   private static MessageDigest createMessageDigest(String digAlg) throws UnsupportedDigestAlgorithmException {
/*     */     try {
/* 234 */       return MessageDigest.getInstance(digAlg);
/* 235 */     } catch (Exception e) {
/* 236 */       throw new UnsupportedDigestAlgorithmException("Unsupported algorithm in HTTP Digest authentication: " + digAlg);
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
/*     */   private Header createDigestHeader(Credentials credentials, HttpRequest request) throws AuthenticationException {
/*     */     MessageDigest digester;
/* 252 */     String digestValue, uri = getParameter("uri");
/* 253 */     String realm = getParameter("realm");
/* 254 */     String nonce = getParameter("nonce");
/* 255 */     String opaque = getParameter("opaque");
/* 256 */     String method = getParameter("methodname");
/* 257 */     String algorithm = getParameter("algorithm");
/*     */     
/* 259 */     if (algorithm == null) {
/* 260 */       algorithm = "MD5";
/*     */     }
/*     */     
/* 263 */     Set<String> qopset = new HashSet<String>(8);
/* 264 */     int qop = -1;
/* 265 */     String qoplist = getParameter("qop");
/* 266 */     if (qoplist != null) {
/* 267 */       StringTokenizer tok = new StringTokenizer(qoplist, ",");
/* 268 */       while (tok.hasMoreTokens()) {
/* 269 */         String variant = tok.nextToken().trim();
/* 270 */         qopset.add(variant.toLowerCase(Locale.ROOT));
/*     */       } 
/* 272 */       if (request instanceof HttpEntityEnclosingRequest && qopset.contains("auth-int")) {
/* 273 */         qop = 1;
/* 274 */       } else if (qopset.contains("auth")) {
/* 275 */         qop = 2;
/*     */       } 
/*     */     } else {
/* 278 */       qop = 0;
/*     */     } 
/*     */     
/* 281 */     if (qop == -1) {
/* 282 */       throw new AuthenticationException("None of the qop methods is supported: " + qoplist);
/*     */     }
/*     */     
/* 285 */     String charset = getParameter("charset");
/* 286 */     if (charset == null) {
/* 287 */       charset = "ISO-8859-1";
/*     */     }
/*     */     
/* 290 */     String digAlg = algorithm;
/* 291 */     if (digAlg.equalsIgnoreCase("MD5-sess")) {
/* 292 */       digAlg = "MD5";
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 297 */       digester = createMessageDigest(digAlg);
/* 298 */     } catch (UnsupportedDigestAlgorithmException ex) {
/* 299 */       throw new AuthenticationException("Unsuppported digest algorithm: " + digAlg);
/*     */     } 
/*     */     
/* 302 */     String uname = credentials.getUserPrincipal().getName();
/* 303 */     String pwd = credentials.getPassword();
/*     */     
/* 305 */     if (nonce.equals(this.lastNonce)) {
/* 306 */       this.nounceCount++;
/*     */     } else {
/* 308 */       this.nounceCount = 1L;
/* 309 */       this.cnonce = null;
/* 310 */       this.lastNonce = nonce;
/*     */     } 
/* 312 */     StringBuilder sb = new StringBuilder(256);
/* 313 */     Formatter formatter = new Formatter(sb, Locale.US);
/* 314 */     formatter.format("%08x", new Object[] { Long.valueOf(this.nounceCount) });
/* 315 */     formatter.close();
/* 316 */     String nc = sb.toString();
/*     */     
/* 318 */     if (this.cnonce == null) {
/* 319 */       this.cnonce = createCnonce();
/*     */     }
/*     */     
/* 322 */     this.a1 = null;
/* 323 */     this.a2 = null;
/*     */     
/* 325 */     if (algorithm.equalsIgnoreCase("MD5-sess")) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 331 */       sb.setLength(0);
/* 332 */       sb.append(uname).append(':').append(realm).append(':').append(pwd);
/* 333 */       String checksum = encode(digester.digest(EncodingUtils.getBytes(sb.toString(), charset)));
/* 334 */       sb.setLength(0);
/* 335 */       sb.append(checksum).append(':').append(nonce).append(':').append(this.cnonce);
/* 336 */       this.a1 = sb.toString();
/*     */     } else {
/*     */       
/* 339 */       sb.setLength(0);
/* 340 */       sb.append(uname).append(':').append(realm).append(':').append(pwd);
/* 341 */       this.a1 = sb.toString();
/*     */     } 
/*     */     
/* 344 */     String hasha1 = encode(digester.digest(EncodingUtils.getBytes(this.a1, charset)));
/*     */     
/* 346 */     if (qop == 2) {
/*     */       
/* 348 */       this.a2 = method + ':' + uri;
/* 349 */     } else if (qop == 1) {
/*     */       
/* 351 */       HttpEntity entity = null;
/* 352 */       if (request instanceof HttpEntityEnclosingRequest) {
/* 353 */         entity = ((HttpEntityEnclosingRequest)request).getEntity();
/*     */       }
/* 355 */       if (entity != null && !entity.isRepeatable()) {
/*     */         
/* 357 */         if (qopset.contains("auth")) {
/* 358 */           qop = 2;
/* 359 */           this.a2 = method + ':' + uri;
/*     */         } else {
/* 361 */           throw new AuthenticationException("Qop auth-int cannot be used with a non-repeatable entity");
/*     */         } 
/*     */       } else {
/*     */         
/* 365 */         HttpEntityDigester entityDigester = new HttpEntityDigester(digester);
/*     */         try {
/* 367 */           if (entity != null) {
/* 368 */             entity.writeTo(entityDigester);
/*     */           }
/* 370 */           entityDigester.close();
/* 371 */         } catch (IOException ex) {
/* 372 */           throw new AuthenticationException("I/O error reading entity content", ex);
/*     */         } 
/* 374 */         this.a2 = method + ':' + uri + ':' + encode(entityDigester.getDigest());
/*     */       } 
/*     */     } else {
/* 377 */       this.a2 = method + ':' + uri;
/*     */     } 
/*     */     
/* 380 */     String hasha2 = encode(digester.digest(EncodingUtils.getBytes(this.a2, charset)));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 385 */     if (qop == 0) {
/* 386 */       sb.setLength(0);
/* 387 */       sb.append(hasha1).append(':').append(nonce).append(':').append(hasha2);
/* 388 */       digestValue = sb.toString();
/*     */     } else {
/* 390 */       sb.setLength(0);
/* 391 */       sb.append(hasha1).append(':').append(nonce).append(':').append(nc).append(':').append(this.cnonce).append(':').append((qop == 1) ? "auth-int" : "auth").append(':').append(hasha2);
/*     */ 
/*     */       
/* 394 */       digestValue = sb.toString();
/*     */     } 
/*     */     
/* 397 */     String digest = encode(digester.digest(EncodingUtils.getAsciiBytes(digestValue)));
/*     */     
/* 399 */     CharArrayBuffer buffer = new CharArrayBuffer(128);
/* 400 */     if (isProxy()) {
/* 401 */       buffer.append("Proxy-Authorization");
/*     */     } else {
/* 403 */       buffer.append("Authorization");
/*     */     } 
/* 405 */     buffer.append(": Digest ");
/*     */     
/* 407 */     List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(20);
/* 408 */     params.add(new BasicNameValuePair("username", uname));
/* 409 */     params.add(new BasicNameValuePair("realm", realm));
/* 410 */     params.add(new BasicNameValuePair("nonce", nonce));
/* 411 */     params.add(new BasicNameValuePair("uri", uri));
/* 412 */     params.add(new BasicNameValuePair("response", digest));
/*     */     
/* 414 */     if (qop != 0) {
/* 415 */       params.add(new BasicNameValuePair("qop", (qop == 1) ? "auth-int" : "auth"));
/* 416 */       params.add(new BasicNameValuePair("nc", nc));
/* 417 */       params.add(new BasicNameValuePair("cnonce", this.cnonce));
/*     */     } 
/*     */     
/* 420 */     params.add(new BasicNameValuePair("algorithm", algorithm));
/* 421 */     if (opaque != null) {
/* 422 */       params.add(new BasicNameValuePair("opaque", opaque));
/*     */     }
/*     */     
/* 425 */     for (int i = 0; i < params.size(); i++) {
/* 426 */       BasicNameValuePair param = params.get(i);
/* 427 */       if (i > 0) {
/* 428 */         buffer.append(", ");
/*     */       }
/* 430 */       String name = param.getName();
/* 431 */       boolean noQuotes = ("nc".equals(name) || "qop".equals(name) || "algorithm".equals(name));
/*     */       
/* 433 */       BasicHeaderValueFormatter.INSTANCE.formatNameValuePair(buffer, (NameValuePair)param, !noQuotes);
/*     */     } 
/* 435 */     return (Header)new BufferedHeader(buffer);
/*     */   }
/*     */   
/*     */   String getCnonce() {
/* 439 */     return this.cnonce;
/*     */   }
/*     */   
/*     */   String getA1() {
/* 443 */     return this.a1;
/*     */   }
/*     */   
/*     */   String getA2() {
/* 447 */     return this.a2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String encode(byte[] binaryData) {
/* 458 */     int n = binaryData.length;
/* 459 */     char[] buffer = new char[n * 2];
/* 460 */     for (int i = 0; i < n; i++) {
/* 461 */       int low = binaryData[i] & 0xF;
/* 462 */       int high = (binaryData[i] & 0xF0) >> 4;
/* 463 */       buffer[i * 2] = HEXADECIMAL[high];
/* 464 */       buffer[i * 2 + 1] = HEXADECIMAL[low];
/*     */     } 
/*     */     
/* 467 */     return new String(buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String createCnonce() {
/* 477 */     SecureRandom rnd = new SecureRandom();
/* 478 */     byte[] tmp = new byte[8];
/* 479 */     rnd.nextBytes(tmp);
/* 480 */     return encode(tmp);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 485 */     StringBuilder builder = new StringBuilder();
/* 486 */     builder.append("DIGEST [complete=").append(this.complete).append(", nonce=").append(this.lastNonce).append(", nc=").append(this.nounceCount).append("]");
/*     */ 
/*     */ 
/*     */     
/* 490 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\DigestScheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */