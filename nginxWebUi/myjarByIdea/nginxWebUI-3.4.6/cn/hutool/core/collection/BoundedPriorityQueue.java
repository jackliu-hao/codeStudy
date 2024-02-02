package cn.hutool.core.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class BoundedPriorityQueue<E> extends PriorityQueue<E> {
   private static final long serialVersionUID = 3794348988671694820L;
   private final int capacity;
   private final Comparator<? super E> comparator;

   public BoundedPriorityQueue(int capacity) {
      this(capacity, (Comparator)null);
   }

   public BoundedPriorityQueue(int capacity, Comparator<? super E> comparator) {
      super(capacity, (o1, o2) -> {
         int cResult;
         if (comparator != null) {
            cResult = comparator.compare(o1, o2);
         } else {
            Comparable<E> o1c = (Comparable)o1;
            cResult = o1c.compareTo(o2);
         }

         return -cResult;
      });
      this.capacity = capacity;
      this.comparator = comparator;
   }

   public boolean offer(E e) {
      if (this.size() >= this.capacity) {
         E head = this.peek();
         if (this.comparator().compare(e, head) <= 0) {
            return true;
         }

         this.poll();
      }

      return super.offer(e);
   }

   public boolean addAll(E[] c) {
      return this.addAll(Arrays.asList(c));
   }

   public ArrayList<E> toList() {
      ArrayList<E> list = new ArrayList(this);
      list.sort(this.comparator);
      return list;
   }

   public Iterator<E> iterator() {
      return this.toList().iterator();
   }
}
