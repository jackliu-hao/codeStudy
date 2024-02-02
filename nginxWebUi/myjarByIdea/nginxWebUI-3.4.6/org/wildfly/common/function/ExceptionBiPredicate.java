package org.wildfly.common.function;

@FunctionalInterface
public interface ExceptionBiPredicate<T, U, E extends Exception> {
   boolean test(T var1, U var2) throws E;

   default ExceptionBiPredicate<T, U, E> and(ExceptionBiPredicate<T, U, E> other) {
      return (t, u) -> {
         return this.test(t, u) && other.test(t, u);
      };
   }

   default ExceptionBiPredicate<T, U, E> or(ExceptionBiPredicate<T, U, E> other) {
      return (t, u) -> {
         return this.test(t, u) || other.test(t, u);
      };
   }

   default ExceptionBiPredicate<T, U, E> xor(ExceptionBiPredicate<T, U, E> other) {
      return (t, u) -> {
         return this.test(t, u) != other.test(t, u);
      };
   }

   default ExceptionBiPredicate<T, U, E> not() {
      return (t, u) -> {
         return !this.test(t, u);
      };
   }
}
