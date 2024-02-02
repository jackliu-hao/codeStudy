package org.antlr.v4.runtime.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class OrderedHashSet<T> extends LinkedHashSet<T> {
   protected ArrayList<T> elements = new ArrayList();

   public T get(int i) {
      return this.elements.get(i);
   }

   public T set(int i, T value) {
      T oldElement = this.elements.get(i);
      this.elements.set(i, value);
      super.remove(oldElement);
      super.add(value);
      return oldElement;
   }

   public boolean remove(int i) {
      T o = this.elements.remove(i);
      return super.remove(o);
   }

   public boolean add(T value) {
      boolean result = super.add(value);
      if (result) {
         this.elements.add(value);
      }

      return result;
   }

   public boolean remove(Object o) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      this.elements.clear();
      super.clear();
   }

   public int hashCode() {
      return this.elements.hashCode();
   }

   public boolean equals(Object o) {
      if (!(o instanceof OrderedHashSet)) {
         return false;
      } else {
         boolean same = this.elements != null && this.elements.equals(((OrderedHashSet)o).elements);
         return same;
      }
   }

   public Iterator<T> iterator() {
      return this.elements.iterator();
   }

   public List<T> elements() {
      return this.elements;
   }

   public Object clone() {
      OrderedHashSet<T> dup = (OrderedHashSet)super.clone();
      dup.elements = new ArrayList(this.elements);
      return dup;
   }

   public Object[] toArray() {
      return this.elements.toArray();
   }

   public String toString() {
      return this.elements.toString();
   }
}
