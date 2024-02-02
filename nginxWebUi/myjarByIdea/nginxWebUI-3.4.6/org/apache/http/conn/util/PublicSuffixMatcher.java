package org.apache.http.conn.util;

import java.net.IDN;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public final class PublicSuffixMatcher {
   private final Map<String, DomainType> rules;
   private final Map<String, DomainType> exceptions;

   public PublicSuffixMatcher(Collection<String> rules, Collection<String> exceptions) {
      this(DomainType.UNKNOWN, rules, exceptions);
   }

   public PublicSuffixMatcher(DomainType domainType, Collection<String> rules, Collection<String> exceptions) {
      Args.notNull(domainType, "Domain type");
      Args.notNull(rules, "Domain suffix rules");
      this.rules = new ConcurrentHashMap(rules.size());
      Iterator i$ = rules.iterator();

      String exception;
      while(i$.hasNext()) {
         exception = (String)i$.next();
         this.rules.put(exception, domainType);
      }

      this.exceptions = new ConcurrentHashMap();
      if (exceptions != null) {
         i$ = exceptions.iterator();

         while(i$.hasNext()) {
            exception = (String)i$.next();
            this.exceptions.put(exception, domainType);
         }
      }

   }

   public PublicSuffixMatcher(Collection<PublicSuffixList> lists) {
      Args.notNull(lists, "Domain suffix lists");
      this.rules = new ConcurrentHashMap();
      this.exceptions = new ConcurrentHashMap();
      Iterator i$ = lists.iterator();

      while(true) {
         DomainType domainType;
         List exceptions;
         do {
            if (!i$.hasNext()) {
               return;
            }

            PublicSuffixList list = (PublicSuffixList)i$.next();
            domainType = list.getType();
            List<String> rules = list.getRules();
            Iterator i$ = rules.iterator();

            while(i$.hasNext()) {
               String rule = (String)i$.next();
               this.rules.put(rule, domainType);
            }

            exceptions = list.getExceptions();
         } while(exceptions == null);

         Iterator i$ = exceptions.iterator();

         while(i$.hasNext()) {
            String exception = (String)i$.next();
            this.exceptions.put(exception, domainType);
         }
      }
   }

   private static DomainType findEntry(Map<String, DomainType> map, String rule) {
      return map == null ? null : (DomainType)map.get(rule);
   }

   private static boolean match(DomainType domainType, DomainType expectedType) {
      return domainType != null && (expectedType == null || domainType.equals(expectedType));
   }

   public String getDomainRoot(String domain) {
      return this.getDomainRoot(domain, (DomainType)null);
   }

   public String getDomainRoot(String domain, DomainType expectedType) {
      if (domain == null) {
         return null;
      } else if (domain.startsWith(".")) {
         return null;
      } else {
         String normalized = DnsUtils.normalize(domain);
         String segment = normalized;

         String result;
         String nextSegment;
         for(result = null; segment != null; segment = nextSegment) {
            String key = IDN.toUnicode(segment);
            DomainType exceptionRule = findEntry(this.exceptions, key);
            if (match(exceptionRule, expectedType)) {
               return segment;
            }

            DomainType domainRule = findEntry(this.rules, key);
            if (match(domainRule, expectedType)) {
               if (domainRule == DomainType.PRIVATE) {
                  return segment;
               }

               return result;
            }

            int nextdot = segment.indexOf(46);
            nextSegment = nextdot != -1 ? segment.substring(nextdot + 1) : null;
            if (nextSegment != null) {
               DomainType wildcardDomainRule = findEntry(this.rules, "*." + IDN.toUnicode(nextSegment));
               if (match(wildcardDomainRule, expectedType)) {
                  if (wildcardDomainRule == DomainType.PRIVATE) {
                     return segment;
                  }

                  return result;
               }
            }

            result = segment;
         }

         if (expectedType != null && expectedType != DomainType.UNKNOWN) {
            return null;
         } else {
            return result;
         }
      }
   }

   public boolean matches(String domain) {
      return this.matches(domain, (DomainType)null);
   }

   public boolean matches(String domain, DomainType expectedType) {
      if (domain == null) {
         return false;
      } else {
         String domainRoot = this.getDomainRoot(domain.startsWith(".") ? domain.substring(1) : domain, expectedType);
         return domainRoot == null;
      }
   }
}
