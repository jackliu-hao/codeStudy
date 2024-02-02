/*     */ package org.apache.http.conn.ssl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.conn.util.InetAddressUtils;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public abstract class AbstractVerifier
/*     */   implements X509HostnameVerifier
/*     */ {
/*  61 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*  63 */   static final String[] BAD_COUNTRY_2LDS = new String[] { "ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info", "lg", "ne", "net", "or", "org" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  69 */     Arrays.sort((Object[])BAD_COUNTRY_2LDS);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final void verify(String host, SSLSocket ssl) throws IOException {
/*  75 */     Args.notNull(host, "Host");
/*  76 */     SSLSession session = ssl.getSession();
/*  77 */     if (session == null) {
/*     */ 
/*     */ 
/*     */       
/*  81 */       InputStream in = ssl.getInputStream();
/*  82 */       in.available();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 101 */       session = ssl.getSession();
/* 102 */       if (session == null) {
/*     */ 
/*     */         
/* 105 */         ssl.startHandshake();
/*     */ 
/*     */ 
/*     */         
/* 109 */         session = ssl.getSession();
/*     */       } 
/*     */     } 
/*     */     
/* 113 */     Certificate[] certs = session.getPeerCertificates();
/* 114 */     X509Certificate x509 = (X509Certificate)certs[0];
/* 115 */     verify(host, x509);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean verify(String host, SSLSession session) {
/*     */     try {
/* 121 */       Certificate[] certs = session.getPeerCertificates();
/* 122 */       X509Certificate x509 = (X509Certificate)certs[0];
/* 123 */       verify(host, x509);
/* 124 */       return true;
/* 125 */     } catch (SSLException ex) {
/* 126 */       if (this.log.isDebugEnabled()) {
/* 127 */         this.log.debug(ex.getMessage(), ex);
/*     */       }
/* 129 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final void verify(String host, X509Certificate cert) throws SSLException {
/* 136 */     List<SubjectName> allSubjectAltNames = DefaultHostnameVerifier.getSubjectAltNames(cert);
/* 137 */     List<String> subjectAlts = new ArrayList<String>();
/* 138 */     if (InetAddressUtils.isIPv4Address(host) || InetAddressUtils.isIPv6Address(host)) {
/* 139 */       for (SubjectName subjectName : allSubjectAltNames) {
/* 140 */         if (subjectName.getType() == 7) {
/* 141 */           subjectAlts.add(subjectName.getValue());
/*     */         }
/*     */       } 
/*     */     } else {
/* 145 */       for (SubjectName subjectName : allSubjectAltNames) {
/* 146 */         if (subjectName.getType() == 2) {
/* 147 */           subjectAlts.add(subjectName.getValue());
/*     */         }
/*     */       } 
/*     */     } 
/* 151 */     X500Principal subjectPrincipal = cert.getSubjectX500Principal();
/* 152 */     String cn = DefaultHostnameVerifier.extractCN(subjectPrincipal.getName("RFC2253"));
/* 153 */     (new String[1])[0] = cn; verify(host, (cn != null) ? new String[1] : null, (subjectAlts != null && !subjectAlts.isEmpty()) ? subjectAlts.<String>toArray(new String[subjectAlts.size()]) : null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void verify(String host, String[] cns, String[] subjectAlts, boolean strictWithSubDomains) throws SSLException {
/* 163 */     String cn = (cns != null && cns.length > 0) ? cns[0] : null;
/* 164 */     List<String> subjectAltList = (subjectAlts != null && subjectAlts.length > 0) ? Arrays.<String>asList(subjectAlts) : null;
/*     */     
/* 166 */     String normalizedHost = InetAddressUtils.isIPv6Address(host) ? DefaultHostnameVerifier.normaliseAddress(host.toLowerCase(Locale.ROOT)) : host;
/*     */ 
/*     */     
/* 169 */     if (subjectAltList != null) {
/* 170 */       for (String subjectAlt : subjectAltList) {
/* 171 */         String normalizedAltSubject = InetAddressUtils.isIPv6Address(subjectAlt) ? DefaultHostnameVerifier.normaliseAddress(subjectAlt) : subjectAlt;
/*     */         
/* 173 */         if (matchIdentity(normalizedHost, normalizedAltSubject, strictWithSubDomains)) {
/*     */           return;
/*     */         }
/*     */       } 
/* 177 */       throw new SSLException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAltList);
/*     */     } 
/* 179 */     if (cn != null) {
/* 180 */       String normalizedCN = InetAddressUtils.isIPv6Address(cn) ? DefaultHostnameVerifier.normaliseAddress(cn) : cn;
/*     */       
/* 182 */       if (matchIdentity(normalizedHost, normalizedCN, strictWithSubDomains)) {
/*     */         return;
/*     */       }
/* 185 */       throw new SSLException("Certificate for <" + host + "> doesn't match " + "common name of the certificate subject: " + cn);
/*     */     } 
/*     */     
/* 188 */     throw new SSLException("Certificate subject for <" + host + "> doesn't contain " + "a common name and does not have alternative names");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean matchIdentity(String host, String identity, boolean strict) {
/* 194 */     if (host == null) {
/* 195 */       return false;
/*     */     }
/* 197 */     String normalizedHost = host.toLowerCase(Locale.ROOT);
/* 198 */     String normalizedIdentity = identity.toLowerCase(Locale.ROOT);
/*     */ 
/*     */ 
/*     */     
/* 202 */     String[] parts = normalizedIdentity.split("\\.");
/* 203 */     boolean doWildcard = (parts.length >= 3 && parts[0].endsWith("*") && (!strict || validCountryWildcard(parts)));
/*     */     
/* 205 */     if (doWildcard) {
/*     */       boolean match;
/* 207 */       String firstpart = parts[0];
/* 208 */       if (firstpart.length() > 1) {
/* 209 */         String prefix = firstpart.substring(0, firstpart.length() - 1);
/* 210 */         String suffix = normalizedIdentity.substring(firstpart.length());
/* 211 */         String hostSuffix = normalizedHost.substring(prefix.length());
/* 212 */         match = (normalizedHost.startsWith(prefix) && hostSuffix.endsWith(suffix));
/*     */       } else {
/* 214 */         match = normalizedHost.endsWith(normalizedIdentity.substring(1));
/*     */       } 
/* 216 */       return (match && (!strict || countDots(normalizedHost) == countDots(normalizedIdentity)));
/*     */     } 
/* 218 */     return normalizedHost.equals(normalizedIdentity);
/*     */   }
/*     */   
/*     */   private static boolean validCountryWildcard(String[] parts) {
/* 222 */     if (parts.length != 3 || parts[2].length() != 2) {
/* 223 */       return true;
/*     */     }
/* 225 */     return (Arrays.binarySearch((Object[])BAD_COUNTRY_2LDS, parts[1]) < 0);
/*     */   }
/*     */   
/*     */   public static boolean acceptableCountryWildcard(String cn) {
/* 229 */     return validCountryWildcard(cn.split("\\."));
/*     */   }
/*     */   
/*     */   public static String[] getCNs(X509Certificate cert) {
/* 233 */     String subjectPrincipal = cert.getSubjectX500Principal().toString();
/*     */     try {
/* 235 */       String cn = DefaultHostnameVerifier.extractCN(subjectPrincipal);
/* 236 */       (new String[1])[0] = cn; return (cn != null) ? new String[1] : null;
/* 237 */     } catch (SSLException ex) {
/* 238 */       return null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] getDNSSubjectAlts(X509Certificate cert) {
/* 259 */     List<SubjectName> subjectAltNames = DefaultHostnameVerifier.getSubjectAltNames(cert);
/* 260 */     if (subjectAltNames == null) {
/* 261 */       return null;
/*     */     }
/* 263 */     List<String> dnsAlts = new ArrayList<String>();
/* 264 */     for (SubjectName subjectName : subjectAltNames) {
/* 265 */       if (subjectName.getType() == 2) {
/* 266 */         dnsAlts.add(subjectName.getValue());
/*     */       }
/*     */     } 
/* 269 */     return dnsAlts.isEmpty() ? dnsAlts.<String>toArray(new String[dnsAlts.size()]) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int countDots(String s) {
/* 278 */     int count = 0;
/* 279 */     for (int i = 0; i < s.length(); i++) {
/* 280 */       if (s.charAt(i) == '.') {
/* 281 */         count++;
/*     */       }
/*     */     } 
/* 284 */     return count;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ssl\AbstractVerifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */