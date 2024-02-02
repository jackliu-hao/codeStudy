/*     */ package org.apache.http.impl.auth;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import org.apache.commons.codec.binary.Base64;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.auth.AuthenticationException;
/*     */ import org.apache.http.auth.ChallengeState;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.auth.MalformedChallengeException;
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
/*     */ public class BasicScheme
/*     */   extends RFC2617Scheme
/*     */ {
/*     */   private static final long serialVersionUID = -1931571557597830536L;
/*     */   private boolean complete;
/*     */   
/*     */   public BasicScheme(Charset credentialsCharset) {
/*  63 */     super(credentialsCharset);
/*  64 */     this.complete = false;
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
/*     */   public BasicScheme(ChallengeState challengeState) {
/*  77 */     super(challengeState);
/*     */   }
/*     */   
/*     */   public BasicScheme() {
/*  81 */     this(Consts.ASCII);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSchemeName() {
/*  91 */     return "basic";
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
/* 105 */     super.processChallenge(header);
/* 106 */     this.complete = true;
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
/* 117 */     return this.complete;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConnectionBased() {
/* 127 */     return false;
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
/* 138 */     return authenticate(credentials, request, (HttpContext)new BasicHttpContext());
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
/*     */   public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
/* 159 */     Args.notNull(credentials, "Credentials");
/* 160 */     Args.notNull(request, "HTTP request");
/* 161 */     StringBuilder tmp = new StringBuilder();
/* 162 */     tmp.append(credentials.getUserPrincipal().getName());
/* 163 */     tmp.append(":");
/* 164 */     tmp.append((credentials.getPassword() == null) ? "null" : credentials.getPassword());
/*     */     
/* 166 */     Base64 base64codec = new Base64(0);
/* 167 */     byte[] base64password = base64codec.encode(EncodingUtils.getBytes(tmp.toString(), getCredentialsCharset(request)));
/*     */ 
/*     */     
/* 170 */     CharArrayBuffer buffer = new CharArrayBuffer(32);
/* 171 */     if (isProxy()) {
/* 172 */       buffer.append("Proxy-Authorization");
/*     */     } else {
/* 174 */       buffer.append("Authorization");
/*     */     } 
/* 176 */     buffer.append(": Basic ");
/* 177 */     buffer.append(base64password, 0, base64password.length);
/*     */     
/* 179 */     return (Header)new BufferedHeader(buffer);
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
/*     */   @Deprecated
/*     */   public static Header authenticate(Credentials credentials, String charset, boolean proxy) {
/* 198 */     Args.notNull(credentials, "Credentials");
/* 199 */     Args.notNull(charset, "charset");
/*     */     
/* 201 */     StringBuilder tmp = new StringBuilder();
/* 202 */     tmp.append(credentials.getUserPrincipal().getName());
/* 203 */     tmp.append(":");
/* 204 */     tmp.append((credentials.getPassword() == null) ? "null" : credentials.getPassword());
/*     */     
/* 206 */     byte[] base64password = Base64.encodeBase64(EncodingUtils.getBytes(tmp.toString(), charset), false);
/*     */ 
/*     */     
/* 209 */     CharArrayBuffer buffer = new CharArrayBuffer(32);
/* 210 */     if (proxy) {
/* 211 */       buffer.append("Proxy-Authorization");
/*     */     } else {
/* 213 */       buffer.append("Authorization");
/*     */     } 
/* 215 */     buffer.append(": Basic ");
/* 216 */     buffer.append(base64password, 0, base64password.length);
/*     */     
/* 218 */     return (Header)new BufferedHeader(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 223 */     StringBuilder builder = new StringBuilder();
/* 224 */     builder.append("BASIC [complete=").append(this.complete).append("]");
/*     */     
/* 226 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\BasicScheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */