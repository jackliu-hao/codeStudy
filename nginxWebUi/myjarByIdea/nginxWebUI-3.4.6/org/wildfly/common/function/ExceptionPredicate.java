package org.wildfly.common.function;

@FunctionalInterface
public interface ExceptionPredicate<T, E extends Exception> {
   boolean test(T var1) throws E;

   default ExceptionPredicate<T, E> and(ExceptionPredicate<T, E> other) {
      return (t) -> {
         return this.test(t) && other.test(t);
      };
   }

   default ExceptionPredicate<T, E> or(ExceptionPredicate<T, E> other) {
      return (t) -> {
         return this.test(t) || other.test(t);
      };
   }

   default ExceptionPredicate<T, E> xor(ExceptionPredicate<T, E> other) {
      return (t) -> {
         return this.test(t) != other.test(t);
      };
   }

   default ExceptionPredicate<T, E> not() {
      return (t) -> {
         return !this.test(t);
      };
   }

   default <U> ExceptionBiPredicate<T, U, E> with(ExceptionPredicate<? super U, ? extends E> other) {
      return (t, u) -> {
         return this.test(t) && other.test(u);
      };
   }
}
