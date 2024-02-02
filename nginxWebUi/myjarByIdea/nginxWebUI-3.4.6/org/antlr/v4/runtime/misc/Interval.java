package org.antlr.v4.runtime.misc;

public class Interval {
   public static final int INTERVAL_POOL_MAX_VALUE = 1000;
   public static final Interval INVALID = new Interval(-1, -2);
   static Interval[] cache = new Interval[1001];
   public int a;
   public int b;
   public static int creates = 0;
   public static int misses = 0;
   public static int hits = 0;
   public static int outOfRange = 0;

   public Interval(int a, int b) {
      this.a = a;
      this.b = b;
   }

   public static Interval of(int a, int b) {
      if (a == b && a >= 0 && a <= 1000) {
         if (cache[a] == null) {
            cache[a] = new Interval(a, a);
         }

         return cache[a];
      } else {
         return new Interval(a, b);
      }
   }

   public int length() {
      return this.b < this.a ? 0 : this.b - this.a + 1;
   }

   public boolean equals(Object o) {
      if (o != null && o instanceof Interval) {
         Interval other = (Interval)o;
         return this.a == other.a && this.b == other.b;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int hash = 23;
      hash = hash * 31 + this.a;
      hash = hash * 31 + this.b;
      return hash;
   }

   public boolean startsBeforeDisjoint(Interval other) {
      return this.a < other.a && this.b < other.a;
   }

   public boolean startsBeforeNonDisjoint(Interval other) {
      return this.a <= other.a && this.b >= other.a;
   }

   public boolean startsAfter(Interval other) {
      return this.a > other.a;
   }

   public boolean startsAfterDisjoint(Interval other) {
      return this.a > other.b;
   }

   public boolean startsAfterNonDisjoint(Interval other) {
      return this.a > other.a && this.a <= other.b;
   }

   public boolean disjoint(Interval other) {
      return this.startsBeforeDisjoint(other) || this.startsAfterDisjoint(other);
   }

   public boolean adjacent(Interval other) {
      return this.a == other.b + 1 || this.b == other.a - 1;
   }

   public boolean properlyContains(Interval other) {
      return other.a >= this.a && other.b <= this.b;
   }

   public Interval union(Interval other) {
      return of(Math.min(this.a, other.a), Math.max(this.b, other.b));
   }

   public Interval intersection(Interval other) {
      return of(Math.max(this.a, other.a), Math.min(this.b, other.b));
   }

   public Interval differenceNotProperlyContained(Interval other) {
      Interval diff = null;
      if (other.startsBeforeNonDisjoint(this)) {
         diff = of(Math.max(this.a, other.b + 1), this.b);
      } else if (other.startsAfterNonDisjoint(this)) {
         diff = of(this.a, other.a - 1);
      }

      return diff;
   }

   public String toString() {
      return this.a + ".." + this.b;
   }
}
