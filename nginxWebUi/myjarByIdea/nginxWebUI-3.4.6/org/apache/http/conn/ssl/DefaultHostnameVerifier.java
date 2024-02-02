package org.apache.http.conn.ssl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.DnsUtils;
import org.apache.http.conn.util.DomainType;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.conn.util.PublicSuffixMatcher;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public final class DefaultHostnameVerifier implements HostnameVerifier {
   private final Log log;
   private final PublicSuffixMatcher publicSuffixMatcher;

   public DefaultHostnameVerifier(PublicSuffixMatcher publicSuffixMatcher) {
      this.log = LogFactory.getLog(this.getClass());
      this.publicSuffixMatcher = publicSuffixMatcher;
   }

   public DefaultHostnameVerifier() {
      this((PublicSuffixMatcher)null);
   }

   public boolean verify(String host, SSLSession session) {
      try {
         Certificate[] certs = session.getPeerCertificates();
         X509Certificate x509 = (X509Certificate)certs[0];
         this.verify(host, x509);
         return true;
      } catch (SSLException var5) {
         if (this.log.isDebugEnabled()) {
            this.log.debug(var5.getMessage(), var5);
         }

         return false;
      }
   }

   public void verify(String host, X509Certificate cert) throws SSLException {
      HostNameType hostType = determineHostFormat(host);
      List<SubjectName> subjectAlts = getSubjectAltNames(cert);
      if (subjectAlts != null && !subjectAlts.isEmpty()) {
         switch (hostType) {
            case IPv4:
               matchIPAddress(host, subjectAlts);
               break;
            case IPv6:
               matchIPv6Address(host, subjectAlts);
               break;
            default:
               matchDNSName(host, subjectAlts, this.publicSuffixMatcher);
         }
      } else {
         X500Principal subjectPrincipal = cert.getSubjectX500Principal();
         String cn = extractCN(subjectPrincipal.getName("RFC2253"));
         if (cn == null) {
            throw new SSLException("Certificate subject for <" + host + "> doesn't contain " + "a common name and does not have alternative names");
         }

         matchCN(host, cn, this.publicSuffixMatcher);
      }

   }

   static void matchIPAddress(String host, List<SubjectName> subjectAlts) throws SSLException {
      for(int i = 0; i < subjectAlts.size(); ++i) {
         SubjectName subjectAlt = (SubjectName)subjectAlts.get(i);
         if (subjectAlt.getType() == 7 && host.equals(subjectAlt.getValue())) {
            return;
         }
      }

      throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
   }

   static void matchIPv6Address(String host, List<SubjectName> subjectAlts) throws SSLException {
      String normalisedHost = normaliseAddress(host);

      for(int i = 0; i < subjectAlts.size(); ++i) {
         SubjectName subjectAlt = (SubjectName)subjectAlts.get(i);
         if (subjectAlt.getType() == 7) {
            String normalizedSubjectAlt = normaliseAddress(subjectAlt.getValue());
            if (normalisedHost.equals(normalizedSubjectAlt)) {
               return;
            }
         }
      }

      throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
   }

   static void matchDNSName(String host, List<SubjectName> subjectAlts, PublicSuffixMatcher publicSuffixMatcher) throws SSLException {
      String normalizedHost = DnsUtils.normalize(host);

      for(int i = 0; i < subjectAlts.size(); ++i) {
         SubjectName subjectAlt = (SubjectName)subjectAlts.get(i);
         if (subjectAlt.getType() == 2) {
            String normalizedSubjectAlt = DnsUtils.normalize(subjectAlt.getValue());
            if (matchIdentityStrict(normalizedHost, normalizedSubjectAlt, publicSuffixMatcher)) {
               return;
            }
         }
      }

      throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
   }

   static void matchCN(String host, String cn, PublicSuffixMatcher publicSuffixMatcher) throws SSLException {
      String normalizedHost = DnsUtils.normalize(host);
      String normalizedCn = DnsUtils.normalize(cn);
      if (!matchIdentityStrict(normalizedHost, normalizedCn, publicSuffixMatcher)) {
         throw new SSLPeerUnverifiedException("Certificate for <" + host + "> doesn't match " + "common name of the certificate subject: " + cn);
      }
   }

   static boolean matchDomainRoot(String host, String domainRoot) {
      if (domainRoot == null) {
         return false;
      } else {
         return host.endsWith(domainRoot) && (host.length() == domainRoot.length() || host.charAt(host.length() - domainRoot.length() - 1) == '.');
      }
   }

   private static boolean matchIdentity(String host, String identity, PublicSuffixMatcher publicSuffixMatcher, DomainType domainType, boolean strict) {
      if (publicSuffixMatcher != null && host.contains(".") && !matchDomainRoot(host, publicSuffixMatcher.getDomainRoot(identity, domainType))) {
         return false;
      } else {
         int asteriskIdx = identity.indexOf(42);
         if (asteriskIdx != -1) {
            String prefix = identity.substring(0, asteriskIdx);
            String suffix = identity.substring(asteriskIdx + 1);
            if (!prefix.isEmpty() && !host.startsWith(prefix)) {
               return false;
            } else if (!suffix.isEmpty() && !host.endsWith(suffix)) {
               return false;
            } else {
               if (strict) {
                  String remainder = host.substring(prefix.length(), host.length() - suffix.length());
                  if (remainder.contains(".")) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return host.equalsIgnoreCase(identity);
         }
      }
   }

   static boolean matchIdentity(String host, String identity, PublicSuffixMatcher publicSuffixMatcher) {
      return matchIdentity(host, identity, publicSuffixMatcher, (DomainType)null, false);
   }

   static boolean matchIdentity(String host, String identity) {
      return matchIdentity(host, identity, (PublicSuffixMatcher)null, (DomainType)null, false);
   }

   static boolean matchIdentityStrict(String host, String identity, PublicSuffixMatcher publicSuffixMatcher) {
      return matchIdentity(host, identity, publicSuffixMatcher, (DomainType)null, true);
   }

   static boolean matchIdentityStrict(String host, String identity) {
      return matchIdentity(host, identity, (PublicSuffixMatcher)null, (DomainType)null, true);
   }

   static boolean matchIdentity(String host, String identity, PublicSuffixMatcher publicSuffixMatcher, DomainType domainType) {
      return matchIdentity(host, identity, publicSuffixMatcher, domainType, false);
   }

   static boolean matchIdentityStrict(String host, String identity, PublicSuffixMatcher publicSuffixMatcher, DomainType domainType) {
      return matchIdentity(host, identity, publicSuffixMatcher, domainType, true);
   }

   static String extractCN(String subjectPrincipal) throws SSLException {
      if (subjectPrincipal == null) {
         return null;
      } else {
         try {
            LdapName subjectDN = new LdapName(subjectPrincipal);
            List<Rdn> rdns = subjectDN.getRdns();

            for(int i = rdns.size() - 1; i >= 0; --i) {
               Rdn rds = (Rdn)rdns.get(i);
               Attributes attributes = rds.toAttributes();
               Attribute cn = attributes.get("cn");
               if (cn != null) {
                  try {
                     Object value = cn.get();
                     if (value != null) {
                        return value.toString();
                     }
                  } catch (NoSuchElementException var8) {
                  } catch (NamingException var9) {
                  }
               }
            }

            return null;
         } catch (InvalidNameException var10) {
            throw new SSLException(subjectPrincipal + " is not a valid X500 distinguished name");
         }
      }
   }

   static HostNameType determineHostFormat(String host) {
      if (InetAddressUtils.isIPv4Address(host)) {
         return DefaultHostnameVerifier.HostNameType.IPv4;
      } else {
         String s = host;
         if (host.startsWith("[") && host.endsWith("]")) {
            s = host.substring(1, host.length() - 1);
         }

         return InetAddressUtils.isIPv6Address(s) ? DefaultHostnameVerifier.HostNameType.IPv6 : DefaultHostnameVerifier.HostNameType.DNS;
      }
   }

   static List<SubjectName> getSubjectAltNames(X509Certificate cert) {
      try {
         Collection<List<?>> entries = cert.getSubjectAlternativeNames();
         if (entries == null) {
            return Collections.emptyList();
         } else {
            List<SubjectName> result = new ArrayList();
            Iterator i$ = entries.iterator();

            while(true) {
               List entry;
               Integer type;
               do {
                  do {
                     if (!i$.hasNext()) {
                        return result;
                     }

                     entry = (List)i$.next();
                     type = entry.size() >= 2 ? (Integer)entry.get(0) : null;
                  } while(type == null);
               } while(type != 2 && type != 7);

               Object o = entry.get(1);
               if (o instanceof String) {
                  result.add(new SubjectName((String)o, type));
               } else if (o instanceof byte[]) {
               }
            }
         }
      } catch (CertificateParsingException var7) {
         return Collections.emptyList();
      }
   }

   static String normaliseAddress(String hostname) {
      if (hostname == null) {
         return hostname;
      } else {
         try {
            InetAddress inetAddress = InetAddress.getByName(hostname);
            return inetAddress.getHostAddress();
         } catch (UnknownHostException var2) {
            return hostname;
         }
      }
   }

   static enum HostNameType {
      IPv4(7),
      IPv6(7),
      DNS(2);

      final int subjectType;

      private HostNameType(int subjectType) {
         this.subjectType = subjectType;
      }
   }
}
