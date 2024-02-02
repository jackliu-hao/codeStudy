/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.FormattedHeader;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthOption;
/*     */ import org.apache.http.auth.AuthScheme;
/*     */ import org.apache.http.auth.AuthSchemeProvider;
/*     */ import org.apache.http.auth.AuthScope;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.auth.MalformedChallengeException;
/*     */ import org.apache.http.client.AuthCache;
/*     */ import org.apache.http.client.AuthenticationStrategy;
/*     */ import org.apache.http.client.CredentialsProvider;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.config.Lookup;
/*     */ import org.apache.http.protocol.HTTP;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.CharArrayBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ abstract class AuthenticationStrategyImpl
/*     */   implements AuthenticationStrategy
/*     */ {
/*  69 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*  71 */   private static final List<String> DEFAULT_SCHEME_PRIORITY = Collections.unmodifiableList(Arrays.asList(new String[] { "Negotiate", "Kerberos", "NTLM", "CredSSP", "Digest", "Basic" }));
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int challengeCode;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String headerName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AuthenticationStrategyImpl(int challengeCode, String headerName) {
/*  88 */     this.challengeCode = challengeCode;
/*  89 */     this.headerName = headerName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAuthenticationRequested(HttpHost authhost, HttpResponse response, HttpContext context) {
/*  97 */     Args.notNull(response, "HTTP response");
/*  98 */     int status = response.getStatusLine().getStatusCode();
/*  99 */     return (status == this.challengeCode);
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
/*     */   public Map<String, Header> getChallenges(HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
/* 112 */     Args.notNull(response, "HTTP response");
/* 113 */     Header[] headers = response.getHeaders(this.headerName);
/* 114 */     Map<String, Header> map = new HashMap<String, Header>(headers.length);
/* 115 */     for (Header header : headers) {
/*     */       CharArrayBuffer buffer;
/*     */       int pos;
/* 118 */       if (header instanceof FormattedHeader) {
/* 119 */         buffer = ((FormattedHeader)header).getBuffer();
/* 120 */         pos = ((FormattedHeader)header).getValuePos();
/*     */       } else {
/* 122 */         String str = header.getValue();
/* 123 */         if (str == null) {
/* 124 */           throw new MalformedChallengeException("Header value is null");
/*     */         }
/* 126 */         buffer = new CharArrayBuffer(str.length());
/* 127 */         buffer.append(str);
/* 128 */         pos = 0;
/*     */       } 
/* 130 */       while (pos < buffer.length() && HTTP.isWhitespace(buffer.charAt(pos))) {
/* 131 */         pos++;
/*     */       }
/* 133 */       int beginIndex = pos;
/* 134 */       while (pos < buffer.length() && !HTTP.isWhitespace(buffer.charAt(pos))) {
/* 135 */         pos++;
/*     */       }
/* 137 */       int endIndex = pos;
/* 138 */       String s = buffer.substring(beginIndex, endIndex);
/* 139 */       map.put(s.toLowerCase(Locale.ROOT), header);
/*     */     } 
/* 141 */     return map;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract Collection<String> getPreferredAuthSchemes(RequestConfig paramRequestConfig);
/*     */ 
/*     */ 
/*     */   
/*     */   public Queue<AuthOption> select(Map<String, Header> challenges, HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
/* 152 */     Args.notNull(challenges, "Map of auth challenges");
/* 153 */     Args.notNull(authhost, "Host");
/* 154 */     Args.notNull(response, "HTTP response");
/* 155 */     Args.notNull(context, "HTTP context");
/* 156 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/*     */     
/* 158 */     Queue<AuthOption> options = new LinkedList<AuthOption>();
/* 159 */     Lookup<AuthSchemeProvider> registry = clientContext.getAuthSchemeRegistry();
/* 160 */     if (registry == null) {
/* 161 */       this.log.debug("Auth scheme registry not set in the context");
/* 162 */       return options;
/*     */     } 
/* 164 */     CredentialsProvider credsProvider = clientContext.getCredentialsProvider();
/* 165 */     if (credsProvider == null) {
/* 166 */       this.log.debug("Credentials provider not set in the context");
/* 167 */       return options;
/*     */     } 
/* 169 */     RequestConfig config = clientContext.getRequestConfig();
/* 170 */     Collection<String> authPrefs = getPreferredAuthSchemes(config);
/* 171 */     if (authPrefs == null) {
/* 172 */       authPrefs = DEFAULT_SCHEME_PRIORITY;
/*     */     }
/* 174 */     if (this.log.isDebugEnabled()) {
/* 175 */       this.log.debug("Authentication schemes in the order of preference: " + authPrefs);
/*     */     }
/*     */     
/* 178 */     for (String id : authPrefs) {
/* 179 */       Header challenge = challenges.get(id.toLowerCase(Locale.ROOT));
/* 180 */       if (challenge != null) {
/* 181 */         AuthSchemeProvider authSchemeProvider = (AuthSchemeProvider)registry.lookup(id);
/* 182 */         if (authSchemeProvider == null) {
/* 183 */           if (this.log.isWarnEnabled()) {
/* 184 */             this.log.warn("Authentication scheme " + id + " not supported");
/*     */           }
/*     */           
/*     */           continue;
/*     */         } 
/* 189 */         AuthScheme authScheme = authSchemeProvider.create(context);
/* 190 */         authScheme.processChallenge(challenge);
/*     */         
/* 192 */         AuthScope authScope = new AuthScope(authhost, authScheme.getRealm(), authScheme.getSchemeName());
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 197 */         Credentials credentials = credsProvider.getCredentials(authScope);
/* 198 */         if (credentials != null)
/* 199 */           options.add(new AuthOption(authScheme, credentials)); 
/*     */         continue;
/*     */       } 
/* 202 */       if (this.log.isDebugEnabled()) {
/* 203 */         this.log.debug("Challenge for " + id + " authentication scheme not available");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 208 */     return options;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void authSucceeded(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
/* 214 */     Args.notNull(authhost, "Host");
/* 215 */     Args.notNull(authScheme, "Auth scheme");
/* 216 */     Args.notNull(context, "HTTP context");
/*     */     
/* 218 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/*     */     
/* 220 */     if (isCachable(authScheme)) {
/* 221 */       AuthCache authCache = clientContext.getAuthCache();
/* 222 */       if (authCache == null) {
/* 223 */         authCache = new BasicAuthCache();
/* 224 */         clientContext.setAuthCache(authCache);
/*     */       } 
/* 226 */       if (this.log.isDebugEnabled()) {
/* 227 */         this.log.debug("Caching '" + authScheme.getSchemeName() + "' auth scheme for " + authhost);
/*     */       }
/*     */       
/* 230 */       authCache.put(authhost, authScheme);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected boolean isCachable(AuthScheme authScheme) {
/* 235 */     if (authScheme == null || !authScheme.isComplete()) {
/* 236 */       return false;
/*     */     }
/* 238 */     String schemeName = authScheme.getSchemeName();
/* 239 */     return schemeName.equalsIgnoreCase("Basic");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void authFailed(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
/* 245 */     Args.notNull(authhost, "Host");
/* 246 */     Args.notNull(context, "HTTP context");
/*     */     
/* 248 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/*     */     
/* 250 */     AuthCache authCache = clientContext.getAuthCache();
/* 251 */     if (authCache != null) {
/* 252 */       if (this.log.isDebugEnabled()) {
/* 253 */         this.log.debug("Clearing cached auth scheme for " + authhost);
/*     */       }
/* 255 */       authCache.remove(authhost);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\AuthenticationStrategyImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */