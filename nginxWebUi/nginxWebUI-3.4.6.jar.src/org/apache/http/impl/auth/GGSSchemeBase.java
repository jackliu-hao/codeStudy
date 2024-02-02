/*     */ package org.apache.http.impl.auth;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import org.apache.commons.codec.binary.Base64;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.auth.AuthenticationException;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.auth.InvalidCredentialsException;
/*     */ import org.apache.http.auth.KerberosCredentials;
/*     */ import org.apache.http.auth.MalformedChallengeException;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.message.BufferedHeader;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.CharArrayBuffer;
/*     */ import org.ietf.jgss.GSSContext;
/*     */ import org.ietf.jgss.GSSCredential;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.GSSManager;
/*     */ import org.ietf.jgss.GSSName;
/*     */ import org.ietf.jgss.Oid;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class GGSSchemeBase
/*     */   extends AuthSchemeBase
/*     */ {
/*     */   enum State
/*     */   {
/*  63 */     UNINITIATED,
/*  64 */     CHALLENGE_RECEIVED,
/*  65 */     TOKEN_GENERATED,
/*  66 */     FAILED;
/*     */   }
/*     */   
/*  69 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*     */   private final Base64 base64codec;
/*     */   
/*     */   private final boolean stripPort;
/*     */   
/*     */   private final boolean useCanonicalHostname;
/*     */   
/*     */   private State state;
/*     */   
/*     */   private byte[] token;
/*     */ 
/*     */   
/*     */   GGSSchemeBase(boolean stripPort, boolean useCanonicalHostname) {
/*  83 */     this.base64codec = new Base64(0);
/*  84 */     this.stripPort = stripPort;
/*  85 */     this.useCanonicalHostname = useCanonicalHostname;
/*  86 */     this.state = State.UNINITIATED;
/*     */   }
/*     */   
/*     */   GGSSchemeBase(boolean stripPort) {
/*  90 */     this(stripPort, true);
/*     */   }
/*     */   
/*     */   GGSSchemeBase() {
/*  94 */     this(true, true);
/*     */   }
/*     */   
/*     */   protected GSSManager getManager() {
/*  98 */     return GSSManager.getInstance();
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] generateGSSToken(byte[] input, Oid oid, String authServer) throws GSSException {
/* 103 */     return generateGSSToken(input, oid, authServer, (Credentials)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] generateGSSToken(byte[] input, Oid oid, String authServer, Credentials credentials) throws GSSException {
/*     */     GSSCredential gssCredential;
/* 112 */     GSSManager manager = getManager();
/* 113 */     GSSName serverName = manager.createName("HTTP@" + authServer, GSSName.NT_HOSTBASED_SERVICE);
/*     */ 
/*     */     
/* 116 */     if (credentials instanceof KerberosCredentials) {
/* 117 */       gssCredential = ((KerberosCredentials)credentials).getGSSCredential();
/*     */     } else {
/* 119 */       gssCredential = null;
/*     */     } 
/*     */     
/* 122 */     GSSContext gssContext = createGSSContext(manager, oid, serverName, gssCredential);
/* 123 */     return (input != null) ? gssContext.initSecContext(input, 0, input.length) : gssContext.initSecContext(new byte[0], 0, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   GSSContext createGSSContext(GSSManager manager, Oid oid, GSSName serverName, GSSCredential gssCredential) throws GSSException {
/* 133 */     GSSContext gssContext = manager.createContext(serverName.canonicalize(oid), oid, gssCredential, 0);
/*     */     
/* 135 */     gssContext.requestMutualAuth(true);
/* 136 */     return gssContext;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected byte[] generateToken(byte[] input, String authServer) throws GSSException {
/* 143 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] generateToken(byte[] input, String authServer, Credentials credentials) throws GSSException {
/* 153 */     return generateToken(input, authServer);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isComplete() {
/* 158 */     return (this.state == State.TOKEN_GENERATED || this.state == State.FAILED);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
/* 170 */     return authenticate(credentials, request, (HttpContext)null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
/*     */     String tokenstr;
/*     */     CharArrayBuffer buffer;
/* 178 */     Args.notNull(request, "HTTP request");
/* 179 */     switch (this.state) {
/*     */       case UNINITIATED:
/* 181 */         throw new AuthenticationException(getSchemeName() + " authentication has not been initiated");
/*     */       case FAILED:
/* 183 */         throw new AuthenticationException(getSchemeName() + " authentication has failed");
/*     */       case CHALLENGE_RECEIVED:
/*     */         try {
/* 186 */           HttpHost host; String authServer; HttpRoute route = (HttpRoute)context.getAttribute("http.route");
/* 187 */           if (route == null) {
/* 188 */             throw new AuthenticationException("Connection route is not available");
/*     */           }
/*     */           
/* 191 */           if (isProxy()) {
/* 192 */             host = route.getProxyHost();
/* 193 */             if (host == null) {
/* 194 */               host = route.getTargetHost();
/*     */             }
/*     */           } else {
/* 197 */             host = route.getTargetHost();
/*     */           } 
/*     */           
/* 200 */           String hostname = host.getHostName();
/*     */           
/* 202 */           if (this.useCanonicalHostname) {
/*     */             
/*     */             try {
/*     */ 
/*     */ 
/*     */               
/* 208 */               hostname = resolveCanonicalHostname(hostname);
/* 209 */             } catch (UnknownHostException ignore) {}
/*     */           }
/*     */           
/* 212 */           if (this.stripPort) {
/* 213 */             authServer = hostname;
/*     */           } else {
/* 215 */             authServer = hostname + ":" + host.getPort();
/*     */           } 
/*     */           
/* 218 */           if (this.log.isDebugEnabled()) {
/* 219 */             this.log.debug("init " + authServer);
/*     */           }
/* 221 */           this.token = generateToken(this.token, authServer, credentials);
/* 222 */           this.state = State.TOKEN_GENERATED;
/* 223 */         } catch (GSSException gsse) {
/* 224 */           this.state = State.FAILED;
/* 225 */           if (gsse.getMajor() == 9 || gsse.getMajor() == 8)
/*     */           {
/* 227 */             throw new InvalidCredentialsException(gsse.getMessage(), gsse);
/*     */           }
/* 229 */           if (gsse.getMajor() == 13) {
/* 230 */             throw new InvalidCredentialsException(gsse.getMessage(), gsse);
/*     */           }
/* 232 */           if (gsse.getMajor() == 10 || gsse.getMajor() == 19 || gsse.getMajor() == 20)
/*     */           {
/*     */             
/* 235 */             throw new AuthenticationException(gsse.getMessage(), gsse);
/*     */           }
/*     */           
/* 238 */           throw new AuthenticationException(gsse.getMessage());
/*     */         } 
/*     */       case TOKEN_GENERATED:
/* 241 */         tokenstr = new String(this.base64codec.encode(this.token));
/* 242 */         if (this.log.isDebugEnabled()) {
/* 243 */           this.log.debug("Sending response '" + tokenstr + "' back to the auth server");
/*     */         }
/* 245 */         buffer = new CharArrayBuffer(32);
/* 246 */         if (isProxy()) {
/* 247 */           buffer.append("Proxy-Authorization");
/*     */         } else {
/* 249 */           buffer.append("Authorization");
/*     */         } 
/* 251 */         buffer.append(": Negotiate ");
/* 252 */         buffer.append(tokenstr);
/* 253 */         return (Header)new BufferedHeader(buffer);
/*     */     } 
/* 255 */     throw new IllegalStateException("Illegal state: " + this.state);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void parseChallenge(CharArrayBuffer buffer, int beginIndex, int endIndex) throws MalformedChallengeException {
/* 263 */     String challenge = buffer.substringTrimmed(beginIndex, endIndex);
/* 264 */     if (this.log.isDebugEnabled()) {
/* 265 */       this.log.debug("Received challenge '" + challenge + "' from the auth server");
/*     */     }
/* 267 */     if (this.state == State.UNINITIATED) {
/* 268 */       this.token = Base64.decodeBase64(challenge.getBytes());
/* 269 */       this.state = State.CHALLENGE_RECEIVED;
/*     */     } else {
/* 271 */       this.log.debug("Authentication already attempted");
/* 272 */       this.state = State.FAILED;
/*     */     } 
/*     */   }
/*     */   
/*     */   private String resolveCanonicalHostname(String host) throws UnknownHostException {
/* 277 */     InetAddress in = InetAddress.getByName(host);
/* 278 */     String canonicalServer = in.getCanonicalHostName();
/* 279 */     if (in.getHostAddress().contentEquals(canonicalServer)) {
/* 280 */       return host;
/*     */     }
/* 282 */     return canonicalServer;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\GGSSchemeBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */