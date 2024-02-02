/*     */ package org.apache.http.conn.ssl;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateParsingException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import javax.naming.InvalidNameException;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.directory.Attribute;
/*     */ import javax.naming.directory.Attributes;
/*     */ import javax.naming.ldap.LdapName;
/*     */ import javax.naming.ldap.Rdn;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.util.DnsUtils;
/*     */ import org.apache.http.conn.util.DomainType;
/*     */ import org.apache.http.conn.util.InetAddressUtils;
/*     */ import org.apache.http.conn.util.PublicSuffixMatcher;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public final class DefaultHostnameVerifier
/*     */   implements HostnameVerifier
/*     */ {
/*     */   enum HostNameType
/*     */   {
/*  72 */     IPv4(7), IPv6(7), DNS(2);
/*     */     
/*     */     final int subjectType;
/*     */     
/*     */     HostNameType(int subjectType) {
/*  77 */       this.subjectType = subjectType;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*  82 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*     */   private final PublicSuffixMatcher publicSuffixMatcher;
/*     */   
/*     */   public DefaultHostnameVerifier(PublicSuffixMatcher publicSuffixMatcher) {
/*  87 */     this.publicSuffixMatcher = publicSuffixMatcher;
/*     */   }
/*     */   
/*     */   public DefaultHostnameVerifier() {
/*  91 */     this(null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean verify(String host, SSLSession session) {
/*     */     try {
/*  97 */       Certificate[] certs = session.getPeerCertificates();
/*  98 */       X509Certificate x509 = (X509Certificate)certs[0];
/*  99 */       verify(host, x509);
/* 100 */       return true;
/* 101 */     } catch (SSLException ex) {
/* 102 */       if (this.log.isDebugEnabled()) {
/* 103 */         this.log.debug(ex.getMessage(), ex);
/*     */       }
/* 105 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void verify(String host, X509Certificate cert) throws SSLException {
/* 111 */     HostNameType hostType = determineHostFormat(host);
/* 112 */     List<SubjectName> subjectAlts = getSubjectAltNames(cert);
/* 113 */     if (subjectAlts != null && !subjectAlts.isEmpty()) {
/* 114 */       switch (hostType) {
/*     */         case IPv4:
/* 116 */           matchIPAddress(host, subjectAlts);
/*     */           return;
/*     */         case IPv6:
/* 119 */           matchIPv6Address(host, subjectAlts);
/*     */           return;
/*     */       } 
/* 122 */       matchDNSName(host, subjectAlts, this.publicSuffixMatcher);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 127 */       X500Principal subjectPrincipal = cert.getSubjectX500Principal();
/* 128 */       String cn = extractCN(subjectPrincipal.getName("RFC2253"));
/* 129 */       if (cn == null) {
/* 130 */         throw new SSLException("Certificate subject for <" + host + "> doesn't contain " + "a common name and does not have alternative names");
/*     */       }
/*     */       
/* 133 */       matchCN(host, cn, this.publicSuffixMatcher);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void matchIPAddress(String host, List<SubjectName> subjectAlts) throws SSLException {
/* 138 */     for (int i = 0; i < subjectAlts.size(); i++) {
/* 139 */       SubjectName subjectAlt = subjectAlts.get(i);
/* 140 */       if (subjectAlt.getType() == 7 && 
/* 141 */         host.equals(subjectAlt.getValue())) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     
/* 146 */     throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
/*     */   }
/*     */ 
/*     */   
/*     */   static void matchIPv6Address(String host, List<SubjectName> subjectAlts) throws SSLException {
/* 151 */     String normalisedHost = normaliseAddress(host);
/* 152 */     for (int i = 0; i < subjectAlts.size(); i++) {
/* 153 */       SubjectName subjectAlt = subjectAlts.get(i);
/* 154 */       if (subjectAlt.getType() == 7) {
/* 155 */         String normalizedSubjectAlt = normaliseAddress(subjectAlt.getValue());
/* 156 */         if (normalisedHost.equals(normalizedSubjectAlt)) {
/*     */           return;
/*     */         }
/*     */       } 
/*     */     } 
/* 161 */     throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void matchDNSName(String host, List<SubjectName> subjectAlts, PublicSuffixMatcher publicSuffixMatcher) throws SSLException {
/* 167 */     String normalizedHost = DnsUtils.normalize(host);
/* 168 */     for (int i = 0; i < subjectAlts.size(); i++) {
/* 169 */       SubjectName subjectAlt = subjectAlts.get(i);
/* 170 */       if (subjectAlt.getType() == 2) {
/* 171 */         String normalizedSubjectAlt = DnsUtils.normalize(subjectAlt.getValue());
/* 172 */         if (matchIdentityStrict(normalizedHost, normalizedSubjectAlt, publicSuffixMatcher)) {
/*     */           return;
/*     */         }
/*     */       } 
/*     */     } 
/* 177 */     throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void matchCN(String host, String cn, PublicSuffixMatcher publicSuffixMatcher) throws SSLException {
/* 183 */     String normalizedHost = DnsUtils.normalize(host);
/* 184 */     String normalizedCn = DnsUtils.normalize(cn);
/* 185 */     if (!matchIdentityStrict(normalizedHost, normalizedCn, publicSuffixMatcher)) {
/* 186 */       throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match " + "common name of the certificate subject: " + cn);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean matchDomainRoot(String host, String domainRoot) {
/* 192 */     if (domainRoot == null) {
/* 193 */       return false;
/*     */     }
/* 195 */     return (host.endsWith(domainRoot) && (host.length() == domainRoot.length() || host.charAt(host.length() - domainRoot.length() - 1) == '.'));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean matchIdentity(String host, String identity, PublicSuffixMatcher publicSuffixMatcher, DomainType domainType, boolean strict) {
/* 203 */     if (publicSuffixMatcher != null && host.contains(".") && 
/* 204 */       !matchDomainRoot(host, publicSuffixMatcher.getDomainRoot(identity, domainType))) {
/* 205 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 214 */     int asteriskIdx = identity.indexOf('*');
/* 215 */     if (asteriskIdx != -1) {
/* 216 */       String prefix = identity.substring(0, asteriskIdx);
/* 217 */       String suffix = identity.substring(asteriskIdx + 1);
/* 218 */       if (!prefix.isEmpty() && !host.startsWith(prefix)) {
/* 219 */         return false;
/*     */       }
/* 221 */       if (!suffix.isEmpty() && !host.endsWith(suffix)) {
/* 222 */         return false;
/*     */       }
/*     */       
/* 225 */       if (strict) {
/* 226 */         String remainder = host.substring(prefix.length(), host.length() - suffix.length());
/*     */         
/* 228 */         if (remainder.contains(".")) {
/* 229 */           return false;
/*     */         }
/*     */       } 
/* 232 */       return true;
/*     */     } 
/* 234 */     return host.equalsIgnoreCase(identity);
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean matchIdentity(String host, String identity, PublicSuffixMatcher publicSuffixMatcher) {
/* 239 */     return matchIdentity(host, identity, publicSuffixMatcher, null, false);
/*     */   }
/*     */   
/*     */   static boolean matchIdentity(String host, String identity) {
/* 243 */     return matchIdentity(host, identity, null, null, false);
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean matchIdentityStrict(String host, String identity, PublicSuffixMatcher publicSuffixMatcher) {
/* 248 */     return matchIdentity(host, identity, publicSuffixMatcher, null, true);
/*     */   }
/*     */   
/*     */   static boolean matchIdentityStrict(String host, String identity) {
/* 252 */     return matchIdentity(host, identity, null, null, true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean matchIdentity(String host, String identity, PublicSuffixMatcher publicSuffixMatcher, DomainType domainType) {
/* 258 */     return matchIdentity(host, identity, publicSuffixMatcher, domainType, false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean matchIdentityStrict(String host, String identity, PublicSuffixMatcher publicSuffixMatcher, DomainType domainType) {
/* 264 */     return matchIdentity(host, identity, publicSuffixMatcher, domainType, true);
/*     */   }
/*     */   
/*     */   static String extractCN(String subjectPrincipal) throws SSLException {
/* 268 */     if (subjectPrincipal == null) {
/* 269 */       return null;
/*     */     }
/*     */     try {
/* 272 */       LdapName subjectDN = new LdapName(subjectPrincipal);
/* 273 */       List<Rdn> rdns = subjectDN.getRdns();
/* 274 */       for (int i = rdns.size() - 1; i >= 0; i--) {
/* 275 */         Rdn rds = rdns.get(i);
/* 276 */         Attributes attributes = rds.toAttributes();
/* 277 */         Attribute cn = attributes.get("cn");
/* 278 */         if (cn != null) {
/*     */           try {
/* 280 */             Object value = cn.get();
/* 281 */             if (value != null) {
/* 282 */               return value.toString();
/*     */             }
/* 284 */           } catch (NoSuchElementException ignore) {
/*     */           
/* 286 */           } catch (NamingException ignore) {}
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 291 */       return null;
/* 292 */     } catch (InvalidNameException e) {
/* 293 */       throw new SSLException(subjectPrincipal + " is not a valid X500 distinguished name");
/*     */     } 
/*     */   }
/*     */   
/*     */   static HostNameType determineHostFormat(String host) {
/* 298 */     if (InetAddressUtils.isIPv4Address(host)) {
/* 299 */       return HostNameType.IPv4;
/*     */     }
/* 301 */     String s = host;
/* 302 */     if (s.startsWith("[") && s.endsWith("]")) {
/* 303 */       s = host.substring(1, host.length() - 1);
/*     */     }
/* 305 */     if (InetAddressUtils.isIPv6Address(s)) {
/* 306 */       return HostNameType.IPv6;
/*     */     }
/* 308 */     return HostNameType.DNS;
/*     */   }
/*     */   
/*     */   static List<SubjectName> getSubjectAltNames(X509Certificate cert) {
/*     */     try {
/* 313 */       Collection<List<?>> entries = cert.getSubjectAlternativeNames();
/* 314 */       if (entries == null) {
/* 315 */         return Collections.emptyList();
/*     */       }
/* 317 */       List<SubjectName> result = new ArrayList<SubjectName>();
/* 318 */       for (List<?> entry : entries) {
/* 319 */         Integer type = (entry.size() >= 2) ? (Integer)entry.get(0) : null;
/* 320 */         if (type != null && (
/* 321 */           type.intValue() == 2 || type.intValue() == 7)) {
/* 322 */           Object o = entry.get(1);
/* 323 */           if (o instanceof String) {
/* 324 */             result.add(new SubjectName((String)o, type.intValue())); continue;
/* 325 */           }  if (o instanceof byte[]);
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 331 */       return result;
/* 332 */     } catch (CertificateParsingException ignore) {
/* 333 */       return Collections.emptyList();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String normaliseAddress(String hostname) {
/* 341 */     if (hostname == null) {
/* 342 */       return hostname;
/*     */     }
/*     */     try {
/* 345 */       InetAddress inetAddress = InetAddress.getByName(hostname);
/* 346 */       return inetAddress.getHostAddress();
/* 347 */     } catch (UnknownHostException unexpected) {
/* 348 */       return hostname;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ssl\DefaultHostnameVerifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */