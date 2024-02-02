package io.undertow.predicate;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributes;

public class Predicates {
   public static Predicate equals(ExchangeAttribute[] attributes) {
      return new EqualsPredicate(attributes);
   }

   public static Predicate and(Predicate... predicates) {
      return new AndPredicate(predicates);
   }

   public static Predicate or(Predicate... predicates) {
      return new OrPredicate(predicates);
   }

   public static Predicate not(Predicate predicate) {
      return new NotPredicate(predicate);
   }

   public static Predicate path(String path) {
      return new PathMatchPredicate(new String[]{path});
   }

   public static Predicate paths(String... paths) {
      PathMatchPredicate[] predicates = new PathMatchPredicate[paths.length];

      for(int i = 0; i < paths.length; ++i) {
         predicates[i] = new PathMatchPredicate(new String[]{paths[i]});
      }

      return or(predicates);
   }

   public static Predicate suffix(String path) {
      return new PathSuffixPredicate(path);
   }

   public static Predicate suffixes(String... paths) {
      if (paths.length == 1) {
         return suffix(paths[0]);
      } else {
         PathSuffixPredicate[] predicates = new PathSuffixPredicate[paths.length];

         for(int i = 0; i < paths.length; ++i) {
            predicates[i] = new PathSuffixPredicate(paths[i]);
         }

         return or(predicates);
      }
   }

   public static Predicate prefix(String path) {
      return new PathPrefixPredicate(new String[]{path});
   }

   public static Predicate prefixes(String... paths) {
      return new PathPrefixPredicate(paths);
   }

   /** @deprecated */
   @Deprecated
   public static Predicate maxContentSize(long size) {
      return new MaxContentSizePredicate(size);
   }

   /** @deprecated */
   @Deprecated
   public static Predicate minContentSize(long size) {
      return new MinContentSizePredicate(size);
   }

   public static Predicate requestSmallerThan(long size) {
      return new RequestSmallerThanPredicate(size);
   }

   public static Predicate requestLargerThan(long size) {
      return new RequestLargerThanPredicate(size);
   }

   public static Predicate truePredicate() {
      return TruePredicate.instance();
   }

   public static Predicate falsePredicate() {
      return FalsePredicate.instance();
   }

   public static Predicate exists(ExchangeAttribute attribute) {
      return new ExistsPredicate(attribute);
   }

   public static Predicate contains(ExchangeAttribute attribute, String... values) {
      return new ContainsPredicate(attribute, values);
   }

   public static Predicate regex(ExchangeAttribute attribute, String pattern) {
      return new RegularExpressionPredicate(pattern, attribute);
   }

   public static Predicate regex(ExchangeAttribute attribute, String pattern, boolean requireFullMatch) {
      return new RegularExpressionPredicate(pattern, attribute, requireFullMatch);
   }

   public static Predicate regex(String attribute, String pattern, ClassLoader classLoader, boolean requireFullMatch) {
      return new RegularExpressionPredicate(pattern, ExchangeAttributes.parser(classLoader).parse(attribute), requireFullMatch);
   }

   public static Predicate authRequired() {
      return AuthenticationRequiredPredicate.INSTANCE;
   }

   public static Predicate parse(String predicate) {
      return PredicateParser.parse(predicate, Thread.currentThread().getContextClassLoader());
   }

   public static Predicate parse(String predicate, ClassLoader classLoader) {
      return PredicateParser.parse(predicate, classLoader);
   }

   public static Predicate secure() {
      return SecurePredicate.INSTANCE;
   }

   private Predicates() {
   }
}
