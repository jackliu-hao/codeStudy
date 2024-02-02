package org.xnio;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import org.xnio._private.Messages;

public final class Sequence<T> extends AbstractList<T> implements List<T>, RandomAccess, Serializable {
   private static final long serialVersionUID = 3042164316147742903L;
   private final Object[] values;
   private static final Object[] empty = new Object[0];
   private static final Sequence<?> EMPTY;

   private Sequence(Object[] values) {
      Object[] realValues = (Object[])values.clone();
      this.values = realValues;
      int i = 0;

      for(int length = realValues.length; i < length; ++i) {
         if (realValues[i] == null) {
            throw Messages.msg.nullArrayIndex("option", i);
         }
      }

   }

   public static <T> Sequence<T> of(T... members) {
      return members.length == 0 ? empty() : new Sequence(members);
   }

   public static <T> Sequence<T> of(Collection<T> members) {
      if (members instanceof Sequence) {
         return (Sequence)members;
      } else {
         Object[] objects = members.toArray();
         return objects.length == 0 ? empty() : new Sequence(objects);
      }
   }

   public <N> Sequence<N> cast(Class<N> newType) throws ClassCastException {
      Object[] var2 = this.values;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object value = var2[var4];
         newType.cast(value);
      }

      return this;
   }

   public static <T> Sequence<T> empty() {
      return EMPTY;
   }

   public Iterator<T> iterator() {
      return Arrays.asList((Object[])this.values).iterator();
   }

   public int size() {
      return this.values.length;
   }

   public boolean isEmpty() {
      return this.values.length == 0;
   }

   public Object[] toArray() {
      return (Object[])this.values.clone();
   }

   public T get(int index) {
      return this.values[index];
   }

   public boolean equals(Object other) {
      return other instanceof Sequence && this.equals((Sequence)other);
   }

   public boolean equals(Sequence<?> other) {
      return this == other || other != null && Arrays.equals(this.values, other.values);
   }

   public int hashCode() {
      return Arrays.hashCode(this.values);
   }

   static {
      EMPTY = new Sequence(empty);
   }
}
