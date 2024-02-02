/*     */ package org.apache.http.conn.util;
/*     */ 
/*     */ import java.net.IDN;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public final class PublicSuffixMatcher
/*     */ {
/*     */   private final Map<String, DomainType> rules;
/*     */   private final Map<String, DomainType> exceptions;
/*     */   
/*     */   public PublicSuffixMatcher(Collection<String> rules, Collection<String> exceptions) {
/*  56 */     this(DomainType.UNKNOWN, rules, exceptions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PublicSuffixMatcher(DomainType domainType, Collection<String> rules, Collection<String> exceptions) {
/*  64 */     Args.notNull(domainType, "Domain type");
/*  65 */     Args.notNull(rules, "Domain suffix rules");
/*  66 */     this.rules = new ConcurrentHashMap<String, DomainType>(rules.size());
/*  67 */     for (String rule : rules) {
/*  68 */       this.rules.put(rule, domainType);
/*     */     }
/*  70 */     this.exceptions = new ConcurrentHashMap<String, DomainType>();
/*  71 */     if (exceptions != null) {
/*  72 */       for (String exception : exceptions) {
/*  73 */         this.exceptions.put(exception, domainType);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PublicSuffixMatcher(Collection<PublicSuffixList> lists) {
/*  82 */     Args.notNull(lists, "Domain suffix lists");
/*  83 */     this.rules = new ConcurrentHashMap<String, DomainType>();
/*  84 */     this.exceptions = new ConcurrentHashMap<String, DomainType>();
/*  85 */     for (PublicSuffixList list : lists) {
/*  86 */       DomainType domainType = list.getType();
/*  87 */       List<String> rules = list.getRules();
/*  88 */       for (String rule : rules) {
/*  89 */         this.rules.put(rule, domainType);
/*     */       }
/*  91 */       List<String> exceptions = list.getExceptions();
/*  92 */       if (exceptions != null) {
/*  93 */         for (String exception : exceptions) {
/*  94 */           this.exceptions.put(exception, domainType);
/*     */         }
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static DomainType findEntry(Map<String, DomainType> map, String rule) {
/* 101 */     if (map == null) {
/* 102 */       return null;
/*     */     }
/* 104 */     return map.get(rule);
/*     */   }
/*     */   
/*     */   private static boolean match(DomainType domainType, DomainType expectedType) {
/* 108 */     return (domainType != null && (expectedType == null || domainType.equals(expectedType)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDomainRoot(String domain) {
/* 119 */     return getDomainRoot(domain, null);
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
/*     */   public String getDomainRoot(String domain, DomainType expectedType) {
/* 133 */     if (domain == null) {
/* 134 */       return null;
/*     */     }
/* 136 */     if (domain.startsWith(".")) {
/* 137 */       return null;
/*     */     }
/* 139 */     String normalized = DnsUtils.normalize(domain);
/* 140 */     String segment = normalized;
/* 141 */     String result = null;
/* 142 */     while (segment != null) {
/*     */       
/* 144 */       String key = IDN.toUnicode(segment);
/* 145 */       DomainType exceptionRule = findEntry(this.exceptions, key);
/* 146 */       if (match(exceptionRule, expectedType)) {
/* 147 */         return segment;
/*     */       }
/* 149 */       DomainType domainRule = findEntry(this.rules, key);
/* 150 */       if (match(domainRule, expectedType)) {
/* 151 */         if (domainRule == DomainType.PRIVATE) {
/* 152 */           return segment;
/*     */         }
/* 154 */         return result;
/*     */       } 
/*     */       
/* 157 */       int nextdot = segment.indexOf('.');
/* 158 */       String nextSegment = (nextdot != -1) ? segment.substring(nextdot + 1) : null;
/*     */       
/* 160 */       if (nextSegment != null) {
/* 161 */         DomainType wildcardDomainRule = findEntry(this.rules, "*." + IDN.toUnicode(nextSegment));
/* 162 */         if (match(wildcardDomainRule, expectedType)) {
/* 163 */           if (wildcardDomainRule == DomainType.PRIVATE) {
/* 164 */             return segment;
/*     */           }
/* 166 */           return result;
/*     */         } 
/*     */       } 
/* 169 */       result = segment;
/* 170 */       segment = nextSegment;
/*     */     } 
/*     */ 
/*     */     
/* 174 */     if (expectedType == null || expectedType == DomainType.UNKNOWN) {
/* 175 */       return result;
/*     */     }
/*     */ 
/*     */     
/* 179 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(String domain) {
/* 186 */     return matches(domain, null);
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
/*     */   public boolean matches(String domain, DomainType expectedType) {
/* 199 */     if (domain == null) {
/* 200 */       return false;
/*     */     }
/* 202 */     String domainRoot = getDomainRoot(domain.startsWith(".") ? domain.substring(1) : domain, expectedType);
/*     */     
/* 204 */     return (domainRoot == null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\con\\util\PublicSuffixMatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */