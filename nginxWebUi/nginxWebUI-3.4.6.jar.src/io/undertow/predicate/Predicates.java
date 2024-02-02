/*     */ package io.undertow.predicate;
/*     */ 
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.attribute.ExchangeAttributes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Predicates
/*     */ {
/*     */   public static Predicate equals(ExchangeAttribute[] attributes) {
/*  38 */     return new EqualsPredicate(attributes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate and(Predicate... predicates) {
/*  46 */     return new AndPredicate(predicates);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate or(Predicate... predicates) {
/*  54 */     return new OrPredicate(predicates);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate not(Predicate predicate) {
/*  62 */     return new NotPredicate(predicate);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate path(String path) {
/*  69 */     return new PathMatchPredicate(new String[] { path });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate paths(String... paths) {
/*  76 */     PathMatchPredicate[] predicates = new PathMatchPredicate[paths.length];
/*  77 */     for (int i = 0; i < paths.length; i++) {
/*  78 */       predicates[i] = new PathMatchPredicate(new String[] { paths[i] });
/*     */     } 
/*  80 */     return or((Predicate[])predicates);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate suffix(String path) {
/*  87 */     return new PathSuffixPredicate(path);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate suffixes(String... paths) {
/*  94 */     if (paths.length == 1) {
/*  95 */       return suffix(paths[0]);
/*     */     }
/*  97 */     PathSuffixPredicate[] predicates = new PathSuffixPredicate[paths.length];
/*  98 */     for (int i = 0; i < paths.length; i++) {
/*  99 */       predicates[i] = new PathSuffixPredicate(paths[i]);
/*     */     }
/* 101 */     return or((Predicate[])predicates);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate prefix(String path) {
/* 108 */     return new PathPrefixPredicate(new String[] { path });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate prefixes(String... paths) {
/* 115 */     return new PathPrefixPredicate(paths);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Predicate maxContentSize(long size) {
/* 126 */     return new MaxContentSizePredicate(size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Predicate minContentSize(long size) {
/* 137 */     return new MinContentSizePredicate(size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate requestSmallerThan(long size) {
/* 146 */     return new RequestSmallerThanPredicate(size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate requestLargerThan(long size) {
/* 154 */     return new RequestLargerThanPredicate(size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate truePredicate() {
/* 161 */     return TruePredicate.instance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate falsePredicate() {
/* 168 */     return FalsePredicate.instance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate exists(ExchangeAttribute attribute) {
/* 177 */     return new ExistsPredicate(attribute);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate contains(ExchangeAttribute attribute, String... values) {
/* 186 */     return new ContainsPredicate(attribute, values);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate regex(ExchangeAttribute attribute, String pattern) {
/* 195 */     return new RegularExpressionPredicate(pattern, attribute);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate regex(ExchangeAttribute attribute, String pattern, boolean requireFullMatch) {
/* 205 */     return new RegularExpressionPredicate(pattern, attribute, requireFullMatch);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate regex(String attribute, String pattern, ClassLoader classLoader, boolean requireFullMatch) {
/* 215 */     return new RegularExpressionPredicate(pattern, ExchangeAttributes.parser(classLoader).parse(attribute), requireFullMatch);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate authRequired() {
/* 224 */     return AuthenticationRequiredPredicate.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate parse(String predicate) {
/* 233 */     return PredicateParser.parse(predicate, Thread.currentThread().getContextClassLoader());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate parse(String predicate, ClassLoader classLoader) {
/* 243 */     return PredicateParser.parse(predicate, classLoader);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Predicate secure() {
/* 251 */     return SecurePredicate.INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\Predicates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */