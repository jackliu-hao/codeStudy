package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Quartet<A, B, C, D> {
   private final A a;
   private final B b;
   private final C c;
   private final D d;

   public Quartet(A a, B b, C c, D d) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
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
}
