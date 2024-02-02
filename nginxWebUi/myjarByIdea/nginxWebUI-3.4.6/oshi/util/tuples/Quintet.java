package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Quintet<A, B, C, D, E> {
   private final A a;
   private final B b;
   private final C c;
   private final D d;
   private final E e;

   public Quintet(A a, B b, C c, D d, E e) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
      this.e = e;
   }

   public final A getA() {
      return this.a;
   }

   public final B getB() {
      return this.b;
   }

   public final C getC() {
      return this.c;
   }

   public final D getD() {
      return this.d;
   }

   public final E getE() {
      return this.e;
   }
}
