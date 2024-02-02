package org.antlr.v4.runtime.misc;

public class Triple<A, B, C> {
   public final A a;
   public final B b;
   public final C c;

   public Triple(A a, B b, C c) {
      this.a = a;
      this.b = b;
      this.c = c;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Triple)) {
         return false;
      } else {
         Triple<?, ?, ?> other = (Triple)obj;
         return ObjectEqualityComparator.INSTANCE.equals(this.a, other.a) && ObjectEqualityComparator.INSTANCE.equals(this.b, other.b) && ObjectEqualityComparator.INSTANCE.equals(this.c, other.c);
      }
   }

   public int hashCode() {
      int hash = MurmurHash.initialize();
      hash = MurmurHash.update(hash, this.a);
      hash = MurmurHash.update(hash, this.b);
      hash = MurmurHash.update(hash, this.c);
      return MurmurHash.finish(hash, 3);
   }

   public String toString() {
      return String.format("(%s, %s, %s)", this.a, this.b, this.c);
   }
}
