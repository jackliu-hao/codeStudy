/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.FormattedHeader;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthScheme;
/*     */ import org.apache.http.auth.AuthSchemeRegistry;
/*     */ import org.apache.http.auth.AuthenticationException;
/*     */ import org.apache.http.auth.MalformedChallengeException;
/*     */ import org.apache.http.client.AuthenticationHandler;
/*     */ import org.apache.http.protocol.HTTP;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Asserts;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public abstract class AbstractAuthenticationHandler
/*     */   implements AuthenticationHandler
/*     */ {
/*  68 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*  70 */   private static final List<String> DEFAULT_SCHEME_PRIORITY = Collections.unmodifiableList(Arrays.asList(new String[] { "Negotiate", "NTLM", "Digest", "Basic" }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<String, Header> parseChallenges(Header[] headers) throws MalformedChallengeException {
/*  85 */     Map<String, Header> map = new HashMap<String, Header>(headers.length);
/*  86 */     for (Header header : headers) {
/*     */       CharArrayBuffer buffer;
/*     */       int pos;
/*  89 */       if (header instanceof FormattedHeader) {
/*  90 */         buffer = ((FormattedHeader)header).getBuffer();
/*  91 */         pos = ((FormattedHeader)header).getValuePos();
/*     */       } else {
/*  93 */         String str = header.getValue();
/*  94 */         if (str == null) {
/*  95 */           throw new MalformedChallengeException("Header value is null");
/*     */         }
/*  97 */         buffer = new CharArrayBuffer(str.length());
/*  98 */         buffer.append(str);
/*  99 */         pos = 0;
/*     */       } 
/* 101 */       while (pos < buffer.length() && HTTP.isWhitespace(buffer.charAt(pos))) {
/* 102 */         pos++;
/*     */       }
/* 104 */       int beginIndex = pos;
/* 105 */       while (pos < buffer.length() && !HTTP.isWhitespace(buffer.charAt(pos))) {
/* 106 */         pos++;
/*     */       }
/* 108 */       int endIndex = pos;
/* 109 */       String s = buffer.substring(beginIndex, endIndex);
/* 110 */       map.put(s.toLowerCase(Locale.ROOT), header);
/*     */     } 
/* 112 */     return map;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<String> getAuthPreferences() {
/* 121 */     return DEFAULT_SCHEME_PRIORITY;
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
/*     */   protected List<String> getAuthPreferences(HttpResponse response, HttpContext context) {
/* 136 */     return getAuthPreferences();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AuthScheme selectScheme(Map<String, Header> challenges, HttpResponse response, HttpContext context) throws AuthenticationException {
/* 145 */     AuthSchemeRegistry registry = (AuthSchemeRegistry)context.getAttribute("http.authscheme-registry");
/*     */     
/* 147 */     Asserts.notNull(registry, "AuthScheme registry");
/* 148 */     Collection<String> authPrefs = getAuthPreferences(response, context);
/* 149 */     if (authPrefs == null) {
/* 150 */       authPrefs = DEFAULT_SCHEME_PRIORITY;
/*     */     }
/*     */     
/* 153 */     if (this.log.isDebugEnabled()) {
/* 154 */       this.log.debug("Authentication schemes in the order of preference: " + authPrefs);
/*     */     }
/*     */ 
/*     */     
/* 158 */     AuthScheme authScheme = null;
/* 159 */     for (String id : authPrefs) {
/* 160 */       Header challenge = challenges.get(id.toLowerCase(Locale.ENGLISH));
/*     */       
/* 162 */       if (challenge != null) {
/* 163 */         if (this.log.isDebugEnabled()) {
/* 164 */           this.log.debug(id + " authentication scheme selected");
/*     */         }
/*     */         try {
/* 167 */           authScheme = registry.getAuthScheme(id, response.getParams());
/*     */           break;
/* 169 */         } catch (IllegalStateException e) {
/* 170 */           if (this.log.isWarnEnabled()) {
/* 171 */             this.log.warn("Authentication scheme " + id + " not supported");
/*     */           }
/*     */           continue;
/*     */         } 
/*     */       } 
/* 176 */       if (this.log.isDebugEnabled()) {
/* 177 */         this.log.debug("Challenge for " + id + " authentication scheme not available");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 182 */     if (authScheme == null)
/*     */     {
/* 184 */       throw new AuthenticationException("Unable to respond to any of these challenges: " + challenges);
/*     */     }
/*     */ 
/*     */     
/* 188 */     return authScheme;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\AbstractAuthenticationHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */